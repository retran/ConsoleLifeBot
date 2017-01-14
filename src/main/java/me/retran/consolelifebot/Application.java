package me.retran.consolelifebot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.quiz.GameProcess;
import me.retran.consolelifebot.youtube.YouTubePoller;

public class Application {
    public static void main(String[] args) {
        Dependencies injector = DaggerDependencies.create();
        Configuration configuration = injector.configuration();

        if (!configuration.telegramToken().isEmpty()) {
            try {
                ApiContextInitializer.init();
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
                telegramBotsApi.registerBot(injector.messagesHandler());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            if (!configuration.youtubeApiKey().isEmpty() && !configuration.channels().isEmpty()) {
                YouTubePoller poller = injector.youTubePoller();
                poller.start();
            }

            if (!configuration.giantbombApiKey().isEmpty() && configuration.giantbombPlatforms().length > 0) {
                GameProcess process = injector.gameProcess();
                process.start();
            }
        }
    }
}
