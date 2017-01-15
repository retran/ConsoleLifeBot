package me.retran.consolelifebot.messaging;

import java.util.concurrent.Callable;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import static akka.dispatch.Futures.future;

public class Sender extends UntypedActor {
    private MessagingService messagingService;
    
    public Sender(MessagingService messagingService) {
        super();
        this.messagingService = messagingService;
    }

    @Override
    public void onReceive(Object arg0) throws Throwable {
        if (arg0 instanceof Message) {
            future(new Callable<Message>() {
                public Message call() {
                    return messagingService.send((Message)arg0);                    
                }
            }, getContext().dispatcher());
        }
        unhandled(arg0);
    }
    
    public static Props props(MessagingService messagingService) {
        return Props.create(new Creator<Sender>() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public Sender create() throws Exception {
                return new Sender(messagingService);
            }
        });
    }
}
