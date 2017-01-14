package me.retran.consolelifebot.common;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updateshandlers.SentCallback;

@Singleton
public class SentMessageCallback implements SentCallback<Message> {
    @Inject
    public SentMessageCallback() {
    }

    @Override
    public void onResult(BotApiMethod<Message> method, Message response) {
    }

    @Override
    public void onError(BotApiMethod<Message> method, TelegramApiRequestException apiException) {
    }

    @Override
    public void onException(BotApiMethod<Message> method, Exception exception) {
    }
}
