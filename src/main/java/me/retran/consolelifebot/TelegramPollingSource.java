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

import org.telegram.telegrambots.api.objects.Update;

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

public class TelegramPollingSource extends GraphStage<SourceShape<Update>> {
    private final Outlet<Update> out = Outlet.create("YouTubePollingSource.out");
    private final SourceShape<Update> shape = SourceShape.of(out);
    
    private final FiniteDuration interval;
    private TelegramService telegramService;
    
    @Override
    public SourceShape<Update> shape() {
        return shape;
    }

    public TelegramPollingSource(FiniteDuration interval, TelegramService telegramService) {
        super();
        this.interval = interval;
        this.telegramService = telegramService;
    }

    @Override
    public GraphStageLogic createLogic(Attributes attributes) throws Exception {
        return new TimerGraphStageLogic(shape) {
            private Queue<Update> buffer = new ArrayDeque<>();
            private int lastReceivedUpdate = 0;
            
            {                
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
                    List<Update> updates = telegramService.getUpdates(lastReceivedUpdate);
                    lastReceivedUpdate = updates.parallelStream()
                            .map(Update::getUpdateId)
                            .max(Integer::compareTo)
                            .orElse(0);
                    buffer.addAll(updates);
                }                
                
                if (!buffer.isEmpty()) {
                    push(out, buffer.poll());
                } else {
                    scheduleOnce("poll", interval);                    
                }                    
            }
        };
    }
}