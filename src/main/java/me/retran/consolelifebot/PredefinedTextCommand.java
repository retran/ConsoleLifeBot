package me.retran.consolelifebot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.InputStream;
import java.util.Scanner;
import java.util.StringJoiner;

public abstract class PredefinedTextCommand extends Command {
    private final SentMessageCallback callback;
    protected String reply;

    public PredefinedTextCommand(SentMessageCallback callback, String filename, String template, String description) {
        super(template, description);
        this.callback = callback;
        reply = Helpers.getPredefinedMessage(filename);
    }

    @Override
    public void Handle(AbsSender sender, Message message) {
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
