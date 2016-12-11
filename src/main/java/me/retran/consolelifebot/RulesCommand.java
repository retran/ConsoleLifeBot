package me.retran.consolelifebot;

import com.google.inject.Inject;

public class RulesCommand extends PredefinedTextCommand {
    @Inject
    public RulesCommand(SentMessageCallback callback) {
        super(callback, "rules.txt", "/rules", "");
    }
}
