package me.retran.consolelifebot.youtube;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.Helpers;
import me.retran.consolelifebot.common.TelegramClient;

@Singleton
public class YouTubePoller extends Thread {
    private final Configuration configuration;
    private final TelegramClient telegramClient;
    private LocalDateTime lastPolledAt;
    private YouTube youTube;

    @Inject
    public YouTubePoller(Configuration configuration, TelegramClient telegramClient) {
        this.configuration = configuration;
        this.telegramClient = telegramClient;
        this.lastPolledAt = LocalDateTime.now();

        HttpRequestInitializer initializer = request -> {
        };
        this.youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), initializer)
                .setYouTubeRequestInitializer(new YouTubeRequestInitializer(configuration.youtubeApiKey()))
                .setApplicationName("Console Life Bot").build();
    }

    public void run() {
        while (true) {
            for (YouTubeEntry entry : fetchNewVideos(lastPolledAt)) {
                SendMessage sendMessage = new SendMessage().setChatId("@consolenote").setParseMode("HTML")
                        .setText(entry.getText());
                try {
                    telegramClient.sendMessage(sendMessage);
                    Thread.sleep(1000);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lastPolledAt = LocalDateTime.now();
            Helpers.sleep(30 * 60 * 1000);
        }
    }

    private ArrayList<YouTubeEntry> fetchNewVideos(LocalDateTime after) {
        ArrayList<YouTubeEntry> results = new ArrayList<>();
        try {
            YouTube.Channels.List channelsListRequest = youTube.channels().list("snippet, contentDetails")
                    .setId(configuration.channels());
            ChannelListResponse channelListResponse = channelsListRequest.execute();
            Boolean full = false;
            for (Channel channel : channelListResponse.getItems()) {
                Boolean finished = false;
                String page = null;
                while (!finished) {
                    YouTube.PlaylistItems.List playlistItemsRequest = youTube.playlistItems().list("snippet")
                            .setPlaylistId(channel.getContentDetails().getRelatedPlaylists().getUploads())
                            .setMaxResults((long) 50);
                    if (page != null) {
                        playlistItemsRequest.setPageToken(page);
                    }
                    PlaylistItemListResponse playlistItemListResponse = playlistItemsRequest.execute();
                    if (playlistItemListResponse.getNextPageToken() != null && full) {
                        page = playlistItemListResponse.getNextPageToken();
                    } else {
                        finished = true;
                    }
                    for (PlaylistItem item : playlistItemListResponse.getItems()) {
                        LocalDateTime publishedAt = new Date(item.getSnippet().getPublishedAt().getValue()).toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDateTime();
                        if (publishedAt.compareTo(after) >= 0) {
                            results.add(new YouTubeEntry(item.getSnippet().getTitle(), channel.getSnippet().getTitle(),
                                    String.format("https://www.youtube.com/watch?v=%s",
                                            item.getSnippet().getResourceId().getVideoId())));
                        }
                    }
                    Helpers.sleep(1000);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
}
