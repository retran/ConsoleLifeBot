package me.retran.consolelifebot;

import javax.inject.Singleton;

import dagger.Component;
import me.retran.consolelifebot.common.MessagesHandler;
import me.retran.consolelifebot.quiz.GameProcess;

@Singleton
@Component(modules = ApplicationModule.class)
interface Dependencies {
    MessagesHandler messagesHandler();

    GameProcess gameProcess();

    Configuration configuration();

    TelegramService telegramService();
}