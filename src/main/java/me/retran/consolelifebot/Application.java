package me.retran.consolelifebot;

import dagger.Component;

import me.retran.consolelifebot.common.MessagesHandler;
import me.retran.consolelifebot.quiz.GameProcess;
import me.retran.consolelifebot.youtube.YouTubePoller;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.inject.Singleton;

@Singleton
@Component(modules = ApplicationModule.class)
interface Dependencies {
    MessagesHandler messagesHandler();
    YouTubePoller youTubePoller();
    GameProcess gameProcess();
}

public class Application {
    public static void main(String[] args) {
        Dependencies injector = DaggerDependencies.create();
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            YouTubePoller poller = injector.youTubePoller();
            poller.start();
            GameProcess process = injector.gameProcess();
            process.start();
            telegramBotsApi.registerBot(injector.messagesHandler());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
