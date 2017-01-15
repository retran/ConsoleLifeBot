package me.retran.consolelifebot;

import akka.stream.Outlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import akka.stream.Attributes;
import akka.stream.SourceShape;
import akka.stream.stage.*;
import scala.concurrent.duration.FiniteDuration;

public class YouTubePollingSource extends GraphStage<SourceShape<YouTubeEntry>> {
    private final Outlet<YouTubeEntry> out = Outlet.create("YouTubePollingSource.out");
    private final SourceShape<YouTubeEntry> shape = SourceShape.of(out);
    
    private final FiniteDuration interval;
    private final String channels;
    private final YouTube youTube;
    
    @Override
    public SourceShape<YouTubeEntry> shape() {
        return shape;
    }

    public YouTubePollingSource(FiniteDuration interval, String apiKey, String channels) {
        super();
        this.interval = interval;
        this.channels = channels;
        
        HttpRequestInitializer initializer = request -> {
        };
        this.youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), initializer)
                .setYouTubeRequestInitializer(new YouTubeRequestInitializer(apiKey))
                .setApplicationName("Console Life Bot").build();
    }

    @Override
    public GraphStageLogic createLogic(Attributes attributes) throws Exception {
        return new TimerGraphStageLogic(shape) {
            private Queue<YouTubeEntry> buffer = new ArrayDeque<>();
            private LocalDateTime lastPolledAt;
            
            {
                this.lastPolledAt = LocalDateTime.now();
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
                    LocalDateTime startedPollingAt = LocalDateTime.now();
                    buffer.addAll(fetchNewVideos(lastPolledAt));
                    lastPolledAt = startedPollingAt;
                }
                
                if (!buffer.isEmpty()) {
                    push(out, buffer.poll());
                } else {
                    scheduleOnce("poll", interval);                    
                }                    
            }
        };
    }
    
    private List<YouTubeEntry> fetchNewVideos(LocalDateTime after) {
        ArrayList<YouTubeEntry> results = new ArrayList<>();
        try {
            YouTube.Channels.List channelsListRequest = youTube.channels().list("snippet, contentDetails")
                    .setId(channels);
            ChannelListResponse channelListResponse = channelsListRequest.execute();
            for (Channel channel : channelListResponse.getItems()) {
                YouTube.PlaylistItems.List playlistItemsRequest = youTube.playlistItems().list("snippet")
                        .setPlaylistId(channel.getContentDetails().getRelatedPlaylists().getUploads())
                        .setMaxResults((long) 10);
                PlaylistItemListResponse playlistItemListResponse = playlistItemsRequest.execute();
                for (PlaylistItem item : playlistItemListResponse.getItems()) {
                    LocalDateTime publishedAt = new Date(item.getSnippet().getPublishedAt().getValue()).toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
                    if (publishedAt.compareTo(after) >= 0) {
                        results.add(new YouTubeEntry(item.getSnippet().getTitle(), channel.getSnippet().getTitle(),
                                String.format("https://www.youtube.com/watch?v=%s",
                                        item.getSnippet().getResourceId().getVideoId())));
                        System.out.println(item.getSnippet().getTitle());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
}
