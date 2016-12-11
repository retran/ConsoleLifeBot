package me.retran.consolelifebot;

import com.google.inject.Inject;

public class AboutCommand extends PredefinedTextCommand {
    @Inject
    public AboutCommand(SentMessageCallback callback) {
        super(callback, "about.txt", "/about", "");
    }
}
