package me.retran.consolelifebot;

import dagger.Component;
import me.retran.consolelifebot.common.MessagesHandler;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.inject.Singleton;

@Singleton
@Component(modules = ApplicationModule.class)
interface Dependencies {
    MessagesHandler messagesHandler();
}

public class Application {

    public static void main(String[] args) {
        Dependencies injector = DaggerDependencies.create();
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(injector.messagesHandler());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
