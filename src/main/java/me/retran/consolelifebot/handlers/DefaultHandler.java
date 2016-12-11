package me.retran.consolelifebot.handlers;

import com.google.inject.Inject;
import me.retran.consolelifebot.common.Helpers;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.logging.BotLogger;

public class DefaultHandler extends Handler {
    @Inject
    public DefaultHandler() {
        // nothing
    }

    @Override
    public boolean canHandle(Message message) {
        return true;
    }

    @Override
    public void handle(AbsSender sender, Message message) {
        BotLogger.info(Helpers.getDisplayName(message.getFrom()), message.getText());
    }
}
