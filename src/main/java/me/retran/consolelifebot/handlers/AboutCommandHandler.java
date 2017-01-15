package me.retran.consolelifebot.handlers;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.retran.consolelifebot.Configuration;
import me.retran.consolelifebot.common.SentMessageCallback;

@Singleton
public class AboutCommandHandler extends PredefinedMessageCommandHandler {
    @Inject
    public AboutCommandHandler(Configuration configuration, SentMessageCallback callback) {
        super(configuration, callback, "predefined/about.txt", "/about", "");
    }
}
