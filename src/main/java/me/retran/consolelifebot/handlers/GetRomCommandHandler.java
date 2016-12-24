package me.retran.consolelifebot.handlers;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.library.Entry;
import me.retran.consolelifebot.library.Library;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Singleton
public class GetRomCommandHandler extends CommandHandler {
    private final Library library;

    @Inject
    public GetRomCommandHandler(Library library, Configuration configuration) {
        super(configuration, "/r", "");
        this.library = library;
    }


    @Override
    public boolean canHandle(Message message) {
        String text = message.getText();
        if (text == null || text.isEmpty()) {
            return false;
        }

        text = text.toLowerCase().trim().split(" ")[0];
        if (text.contains("@")) {
            text = text.replace("@" + configuration.telegramUserName(), "");
        }

        return text.startsWith(template);
    }

    @Override
    public void handle(AbsSender sender, Message message) {
        String text = message.getText().toLowerCase().trim().split(" ")[0];
        if (text.contains("@")) {
            text = text.replace("@" + configuration.telegramUserName(), "");
        }
        text = text.replace(getTemplate(), "");
        long id = Long.parseLong(text);
        Entry entry = library.getEntry(id);
        if (entry != null) {
            try {
                SendDocument sendDocument = new SendDocument()
                        .setNewDocument(entry.getFilename(), new FileInputStream(entry.getPath()))
                        .setChatId(message.getChatId())
                        .setReplyToMessageId(message.getMessageId());
                sender.sendDocument(sendDocument);
            } catch (FileNotFoundException e) {
                BotLogger.severe(this.getTemplate(), e);
            } catch (TelegramApiException e) {
                BotLogger.severe(this.getTemplate(), e);
            }
        }
    }
}
