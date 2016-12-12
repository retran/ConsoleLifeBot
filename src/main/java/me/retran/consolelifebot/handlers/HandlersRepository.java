package me.retran.consolelifebot.handlers;

import javax.inject.Inject;
import com.google.inject.Singleton;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Arrays;
import java.util.List;

@javax.inject.Singleton
public class HandlersRepository {
    private final DefaultHandler defaultHandler;
    private List<Handler> handlers;

    @Inject
    public HandlersRepository(RulesCommandHandler rulesCommandHandler,
                              ListCommandHandler listCommandHandler,
                              AboutCommandHandler aboutCommandHandler,
                              NewChatMemberHandler newChatMemberHandler,
                              RomCommandHandler romCommandHandler,
                              GetRomCommandHandler getRomCommandHandler,
                              DefaultHandler defaultHandler) {
        handlers = Arrays.asList(rulesCommandHandler,
                listCommandHandler,
                aboutCommandHandler,
                newChatMemberHandler,
                romCommandHandler,
                getRomCommandHandler);
        this.defaultHandler = defaultHandler;
    }

    public Handler getHandler(Message message) {
        return handlers.stream().filter(handler -> handler.canHandle(message))
                .findFirst().orElse(defaultHandler);
    }
}
