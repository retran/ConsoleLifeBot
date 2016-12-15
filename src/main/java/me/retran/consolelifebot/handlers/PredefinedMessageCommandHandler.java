package me.retran.consolelifebot.handlers;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.Helpers;
import me.retran.consolelifebot.common.SentMessageCallback;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

public abstract class PredefinedMessageCommandHandler extends CommandHandler {
    private final SentMessageCallback callback;
    protected String reply;

    public PredefinedMessageCommandHandler(Configuration configuration, SentMessageCallback callback,
                                           String filename, String template, String description) {
        super(configuration, template, description);
        this.callback = callback;
        reply = Helpers.getPredefinedMessage(filename);
    }

    @Override
    public void handle(AbsSender sender, Message message) {
        BotLogger.info(Helpers.getDisplayName(message.getFrom()), message.getText());
        SendMessage sendMessage = new SendMessage()
                .setText(reply)
                .disableNotification()
                .disableWebPagePreview()
                .enableHtml(true)
                .setChatId(message.getChatId())
                .setReplyToMessageId(message.getMessageId());
        try {
            sender.sendMessageAsync(sendMessage, callback);
        } catch (TelegramApiException e) {
            BotLogger.severe(this.getTemplate(), e);
        }
    }
}
