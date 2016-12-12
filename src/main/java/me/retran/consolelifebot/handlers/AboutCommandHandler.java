package me.retran.consolelifebot.handlers;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.SentMessageCallback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AboutCommandHandler extends PredefinedMessageCommandHandler {
    @Inject
    public AboutCommandHandler(Configuration configuration, SentMessageCallback callback) {
        super(configuration, callback, "predefined/about.txt", "/about", "");
    }
}
