package me.retran.consolelifebot.me.retran.consoleliftbot.handlers;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.Helpers;
import me.retran.consolelifebot.common.SentMessageCallback;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

public abstract class PredefinedTextCommandHandler extends CommandHandler {
    private final SentMessageCallback callback;
    protected String reply;

    public PredefinedTextCommandHandler(Configuration configuration, SentMessageCallback callback, String filename, String template, String description) {
        super(configuration, template, description);
        this.callback = callback;
        reply = Helpers.getPredefinedMessage(filename);
    }

    @Override
    public void handle(AbsSender sender, Message message) {
        BotLogger.info(Helpers.getDisplayName(message.getFrom()), message.getText());
        SendMessage sendMessage = new SendMessage()
                .setText(reply)
                .setChatId(message.getChatId())
                .setReplyToMessageId(message.getMessageId());
        try {
            sender.sendMessageAsync(sendMessage, callback);
        } catch (TelegramApiException e) {
            BotLogger.severe(this.getTemplate(), e);
        }
    }
}
