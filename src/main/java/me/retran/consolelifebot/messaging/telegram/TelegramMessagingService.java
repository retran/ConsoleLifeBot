package me.retran.consolelifebot.messaging.telegram;

import static akka.dispatch.Futures.future;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.messaging.Message;
import me.retran.consolelifebot.messaging.MessagingService;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

@Singleton
public class TelegramMessagingService extends DefaultAbsSender implements MessagingService {
    static {
        ApiContextInitializer.init();
    }

    private final Configuration configuration;

    @Inject
    public TelegramMessagingService(Configuration configuration) {
        super(ApiContext.getInstance(DefaultBotOptions.class));
        this.configuration = configuration;
    }

    @Override
    public String getBotToken() {
        return this.configuration.telegramToken();
    }

    @Override
    public Message send(Message message) {
        // TODO Auto-generated method stub
        return null;
    }
}
