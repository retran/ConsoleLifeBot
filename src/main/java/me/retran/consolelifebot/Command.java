package me.retran.consolelifebot;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;

public abstract class Command {
    protected String template;
    protected String description;

    public Command(String template, String description) {
        this.template = template;
        this.description = description;
    }

    public String getTemplate() {
        return template;
    }

    public String getDescription() {
        return description;
    }

    public abstract void Handle(AbsSender sender, Message message);
}
