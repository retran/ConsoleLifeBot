package me.retran.consolelifebot;

import com.google.inject.Inject;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.logging.BotLogger;

public class CommandsHandler extends TelegramLongPollingBot {
    public static final String LOGTAG = "COMMANDSHANDLER";

    private final Configuration configuration;
    private final SentMessageCallback sentMessageCallback;
    private final CommandRepository commands;

    @Inject
    CommandsHandler(Configuration configuration, CommandRepository commands, SentMessageCallback sentMessageCallback) {
        this.configuration = configuration;
        this.sentMessageCallback = sentMessageCallback;
        this.commands = commands;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            BotLogger.info(Helpers.getDisplayName(update.getMessage().getFrom()), update.getMessage().getText());
            Command c = commands.getCommand(update.getMessage().getText());
            if (c != null) {
                c.Handle(this, update.getMessage());
            } else {
                BotLogger.warning(LOGTAG, "invalid command");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return this.configuration.telegramUserName();
    }

    @Override
    public String getBotToken() {
        return this.configuration.telegramToken();
    }
}
