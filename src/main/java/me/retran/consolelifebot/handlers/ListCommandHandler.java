package me.retran.consolelifebot.me.retran.consoleliftbot.handlers;

import com.google.inject.Inject;
import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.SentMessageCallback;

public class ListCommandHandler extends PredefinedTextCommandHandler {
    @Inject
    public ListCommandHandler(Configuration configuration, SentMessageCallback callback) {
        super(configuration, callback, "predefined/list.txt", "/list", "");
    }
}
