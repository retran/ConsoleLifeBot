package me.retran.consolelifebot;

import akka.stream.Outlet;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import akka.stream.Attributes;
import akka.stream.SourceShape;
import akka.stream.stage.*;
import scala.concurrent.duration.FiniteDuration;

public class RssPollingSource extends GraphStage<SourceShape<SyndEntry>> {
    private final Outlet<SyndEntry> out = Outlet.create("RssPollingSource.out");
    private final SourceShape<SyndEntry> shape = SourceShape.of(out);

    private final FiniteDuration interval;
    private String url;
    private String category;

    @Override
    public SourceShape<SyndEntry> shape() {
        return shape;
    }

    public RssPollingSource(FiniteDuration interval, String url, String category) {
        super();
        this.interval = interval;
        this.url = url;
        this.category = category;
    }

    @Override
    public GraphStageLogic createLogic(Attributes attributes) throws Exception {
        return new TimerGraphStageLogic(shape) {
            private Queue<SyndEntry> buffer = new ArrayDeque<>();
            private LocalDateTime lastPolledAt;

            {
                this.lastPolledAt = LocalDateTime.now().minusHours(1);
                // this.lastPolledAt = LocalDateTime.of(2017, 1, 27, 18, 0);
                setHandler(out, new AbstractOutHandler() {
                    @Override
                    public void onPull() {
                        poll();
                    }
                });
            }

            @Override
            public void onTimer(Object timerKey) {
                poll();
            }

            private void poll() {
                if (buffer.isEmpty()) {
                    List<SyndEntry> entries = getNewRecords(lastPolledAt);
                    if (entries.stream().count() > 0) {
                        SyndEntry lastEntry = entries.stream().findFirst().get();
                        buffer.addAll(entries);
                        lastPolledAt = LocalDateTime.ofInstant(lastEntry.getPublishedDate().toInstant(),
                                ZoneId.systemDefault());
                    }
                }

                if (!buffer.isEmpty()) {
                    push(out, buffer.poll());
                } else {
                    scheduleOnce("poll", interval);
                }
            }

            private List<SyndEntry> getNewRecords(LocalDateTime lastPolledAt) {
                System.out.println(url);

                SyndFeed feed;
                try {
                    feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
                    if (feed != null && feed.getEntries() != null) {
                        return ((List<SyndEntry>) feed.getEntries()).stream().filter(entry -> entry != null)
                                .filter(entry -> LocalDateTime
                                        .ofInstant(entry.getPublishedDate().toInstant(), ZoneId.systemDefault())
                                        .compareTo(lastPolledAt) > 0)
                                .filter(entry -> category == null || category == ""
                                        || ((List<SyndCategory>) entry.getCategories()).stream().anyMatch(
                                                c -> c.getName().toLowerCase().equals(category.toLowerCase())))
                                // .filter(entry -> {
                                // System.err.println("YEAH!");
                                // System.err.println(entry.getTitle());
                                // return true;
                                // })
                                .collect(Collectors.toList());
                    }
                } catch (IllegalArgumentException | FeedException | IOException e) {
                    e.printStackTrace();
                }
                return new ArrayList<SyndEntry>();
            }
        };
    }
}
