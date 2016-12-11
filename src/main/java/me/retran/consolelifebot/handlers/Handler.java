package me.retran.consolelifebot.handlers;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;

public abstract class Handler {
    public abstract boolean canHandle(Message message);

    public abstract void handle(AbsSender sender, Message message);
}
