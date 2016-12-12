package me.retran.consolelifebot.handlers;

import javax.inject.Inject;
import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.SentMessageCallback;

public class ListCommandHandler extends PredefinedMessageCommandHandler {
    @Inject
    public ListCommandHandler(Configuration configuration, SentMessageCallback callback) {
        super(configuration, callback, "predefined/list.txt", "/list", "");
    }
}
