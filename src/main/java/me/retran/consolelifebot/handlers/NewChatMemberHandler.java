package me.retran.consolelifebot.handlers;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import me.retran.consolelifebot.common.SentMessageCallback;
import me.retran.consolelifebot.common.Utils;

@Singleton
public class NewChatMemberHandler extends Handler {
    private final SentMessageCallback callback;
    private String welcome;

    @Inject
    public NewChatMemberHandler(SentMessageCallback callback) {
        this.callback = callback;
        welcome = Utils.getPredefinedMessage("predefined/welcome.txt");
    }

    @Override
    public boolean canHandle(Message message) {
        return message.getNewChatMember() != null;
    }

    @Override
    public void handle(AbsSender sender, Message message) {
        BotLogger.info(Utils.getDisplayName(message.getNewChatMember()), "enter");
        SendMessage sendMessage = new SendMessage().setChatId(message.getChatId()).disableNotification()
                .disableWebPagePreview().enableHtml(true).setReplyToMessageId(message.getMessageId()).setText(welcome);
        try {
            sender.sendMessageAsync(sendMessage, callback);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        
        sendMessage = new SendMessage().setChatId(message.getChatId()).disableNotification()
                .disableWebPagePreview().enableHtml(true).setReplyToMessageId(message.getMessageId())
                .setText("Скриптонита слушаешь? Консоли есть? А если найдем?");
        try {
            sender.sendMessageAsync(sendMessage, callback);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
