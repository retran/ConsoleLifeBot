package me.retran.consolelifebot.messaging;

import static akka.dispatch.Futures.future;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.contrib.throttle.Throttler;
import akka.contrib.throttle.TimerBasedThrottler;
import akka.japi.Creator;
import scala.concurrent.duration.Duration;

public class MessageSender extends UntypedActor {
    static public final int messagesPerSecond = 30;
    static private final int messagesToResourcePerSecond = 2;

    static public class SendMessage {
        String to;

        public String to() {
            return to;
        }

        public SendMessage to(String to) {
            this.to = to;
            return this;
        }
    }

    static private class Sender extends UntypedActor {
        private TelegramService telegramService;

        public Sender(TelegramService telegramService) {
            super();
            this.telegramService = telegramService;
        }

        @Override
        public void onReceive(Object arg0) throws Throwable {
            if (arg0 instanceof SendMessage) {
                future(new Callable<SendMessage>() {
                    public SendMessage call() {
                        // return messagingService.send((Message) arg0);
                        return null;
                    }
                }, getContext().dispatcher());
            }
            unhandled(arg0);
        }

        public static Props props(TelegramService telegramService) {
            return Props.create(new Creator<Sender>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Sender create() throws Exception {
                    return new Sender(telegramService);
                }
            });
        }
    }

    private Map<String, ActorRef> throttlers;
    private TelegramService telegramService;

    public MessageSender(TelegramService telegramService) {
        this.telegramService = telegramService;
        throttlers = new HashMap<>();
    }

    @Override
    public void onReceive(Object arg0) throws Throwable {
        if (arg0 instanceof SendMessage) {
            SendMessage message = (SendMessage) arg0;
            ActorRef throttler = throttlers.get(message.to());
            if (throttler == null) {
                ActorRef sender = this.getContext().actorOf(Sender.props(telegramService));
                throttler = this.getContext()
                        .actorOf(Props.create(TimerBasedThrottler.class,
                                new Throttler.Rate(messagesToResourcePerSecond, Duration.create(1, TimeUnit.SECONDS))),
                                "senderTo" + message.to());
                throttler.tell(new Throttler.SetTarget(sender), null);
                throttlers.put(message.to(), throttler);
            }
            throttler.tell(arg0, null);
        }
        unhandled(arg0);
    }
    
    public static Props props(TelegramService telegramService) {
        return Props.create(new Creator<MessageSender>() {
            private static final long serialVersionUID = 1L;

            @Override
            public MessageSender create() throws Exception {
                return new MessageSender(telegramService);
            }
        });
    }
}
