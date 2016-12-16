package me.retran.consolelifebot.handlers;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.SentMessageCallback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RulesCommandHandler extends PredefinedMessageCommandHandler {
    @Inject
    public RulesCommandHandler(Configuration configuration, SentMessageCallback callback) {
        super(configuration, callback, "predefined/rules.txt", "/rules", "");
    }
}
