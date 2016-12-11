package me.retran.consolelifebot.handlers;

import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;
import com.google.inject.Inject;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Arrays;
import java.util.List;

public class HandlersRepository {
    private final DefaultHandler defaultHandler;
    private List<Handler> handlers;

    @Inject
    public HandlersRepository(RulesCommandHandler rulesCommandHandler,
                              ListCommandHandler listCommandHandler,
                              AboutCommandHandler aboutCommandHandler,
                              NewChatMemberHandler newChatMemberHandler,
                              DefaultHandler defaultHandler) {
        handlers = Arrays.asList(rulesCommandHandler,
                listCommandHandler,
                aboutCommandHandler,
                newChatMemberHandler);
        this.defaultHandler = defaultHandler;
    }

    public Handler getHandler(Message message) {
        return handlers.stream().filter(handler -> handler.canHandle(message))
                .findFirst().orElse(defaultHandler);
    }
}
