package me.retran.consolelifebot;

import javax.inject.Singleton;

import dagger.Component;
import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.MessagesHandler;
import me.retran.consolelifebot.messaging.TelegramService;
import me.retran.consolelifebot.quiz.GameProcess;
import me.retran.consolelifebot.youtube.LegacyYouTubePoller;

@Singleton
@Component(modules = ApplicationModule.class)
interface Dependencies {
    MessagesHandler messagesHandler();

    LegacyYouTubePoller youTubePoller();

    GameProcess gameProcess();

    Configuration configuration();

    TelegramService telegramService();
}