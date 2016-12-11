package me.retran.consolelifebot.me.retran.consoleliftbot.handlers;

import com.google.inject.Inject;
import me.retran.consolelifebot.common.Helpers;
import me.retran.consolelifebot.common.SentMessageCallback;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import static me.retran.consolelifebot.common.MessagesHandler.LOGTAG;

public class NewChatMemberHandler extends Handler {
    private final SentMessageCallback callback;
    private String welcome;

    @Inject
    public NewChatMemberHandler(SentMessageCallback callback) {
        this.callback = callback;
        welcome = Helpers.getPredefinedMessage("predefined/welcome.txt");
    }

    @Override
    public boolean canHandle(Message message) {
        return message.getNewChatMember() != null;
    }

    @Override
    public void handle(AbsSender sender, Message message) {
        BotLogger.info(Helpers.getDisplayName(message.getNewChatMember()), "enter");
        SendMessage sendMessage = new SendMessage()
                .setChatId(message.getChatId())
                .setReplyToMessageId(message.getMessageId())
                .setText(welcome);
        try {
            sender.sendMessageAsync(sendMessage, callback);
        } catch (TelegramApiException e) {
            BotLogger.severe(LOGTAG, e);
        }
    }
}
