package me.retran.consolelifebot;

import com.google.inject.Inject;

public class ListCommand extends PredefinedTextCommand {
    @Inject
    public ListCommand(SentMessageCallback callback) {
        super(callback, "list.txt", "/list", "");
    }
}
