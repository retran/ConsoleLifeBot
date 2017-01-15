package me.retran.consolelifebot.handlers;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import me.retran.consolelifebot.Configuration;
import me.retran.consolelifebot.common.SentMessageCallback;
import me.retran.consolelifebot.library.Entry;
import me.retran.consolelifebot.library.Library;

@Singleton
public class RomCommandHandler extends CommandHandler {
    private final SentMessageCallback callback;
    private final Library library;

    @Inject
    public RomCommandHandler(Library library, Configuration configuration, SentMessageCallback callback) {
        super(configuration, "/rom", "");
        this.callback = callback;
        this.library = library;
    }

    @Override
    public void handle(AbsSender sender, Message message) {
        String arg = message.getText().trim().replace("@slonikdendy_bot", "").replace("/rom", "").trim();
        SendMessage sendMessage = new SendMessage().setChatId(message.getChatId())
                .setReplyToMessageId(message.getMessageId());
        if (arg.isEmpty()) {
            sendMessage.setText("Укажи строку для поиска после команды.");
        } else {
            Entry[] entries = library.search(arg);
            if (entries.length == 0) {
                sendMessage.setText("Ничего не найдено :(");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Entry e : entries) {
                    sb.append(String.format("%s %s (/r%d)%n", e.getPlatform(), e.getFilename(), e.getId()));
                }
                sendMessage.setText(sb.toString());
            }
        }

        try {
            sender.sendMessageAsync(sendMessage, callback);
        } catch (TelegramApiException e) {
        }
    }
}
