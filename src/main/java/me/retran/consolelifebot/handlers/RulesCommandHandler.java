package me.retran.consolelifebot.handlers;

import com.google.inject.Inject;
import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.SentMessageCallback;

public class RulesCommandHandler extends PredefinedMessageCommandHandler {
    @Inject
    public RulesCommandHandler(Configuration configuration, SentMessageCallback callback) {
        super(configuration, callback, "predefined/rules.txt", "/rules", "");
    }
}
