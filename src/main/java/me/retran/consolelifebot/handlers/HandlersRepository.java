package me.retran.consolelifebot.handlers;

import org.telegram.telegrambots.api.objects.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Arrays;
import java.util.List;

@Singleton
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
                              LeftChatMemberHandler leftChatMemberHandler,
                              TestCommandHandler testCommandHandler,
                              AnswerCommandHandler answerCommandHandler,
                              DefaultHandler defaultHandler) {
        handlers = Arrays.asList(rulesCommandHandler,
                                 testCommandHandler,
                                 answerCommandHandler,
                                 listCommandHandler,
                                 aboutCommandHandler,
                                 newChatMemberHandler,
                                 romCommandHandler,
                                 leftChatMemberHandler,
                                 getRomCommandHandler);
        this.defaultHandler = defaultHandler;
    }

    public Handler getHandler(Message message) {
        return handlers.stream().filter(handler -> handler.canHandle(message))
                .findFirst().orElse(defaultHandler);
    }
}
