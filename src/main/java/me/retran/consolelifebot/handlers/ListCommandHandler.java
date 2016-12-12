package me.retran.consolelifebot.handlers;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.SentMessageCallback;

@Singleton
public class ListCommandHandler extends PredefinedMessageCommandHandler {
    @Inject
    public ListCommandHandler(Configuration configuration, SentMessageCallback callback) {
        super(configuration, callback, "predefined/list.txt", "/list", "");
    }
}
