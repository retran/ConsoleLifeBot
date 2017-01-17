package me.retran.consolelifebot;

import static akka.dispatch.Futures.future;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.contrib.throttle.Throttler;
import akka.contrib.throttle.TimerBasedThrottler;
import akka.dispatch.OnComplete;
import akka.japi.Creator;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class TelegramPublisher extends UntypedActor {
    static private class ChannelPublisher extends UntypedActor {
        private TelegramService telegramService;

        public ChannelPublisher(TelegramService telegramService) {
            super();
            this.telegramService = telegramService;
        }

        @Override
        public void onReceive(Object arg0) throws Throwable {
            if (arg0 instanceof SendMessage) {
                SendMessage sendMessage = (SendMessage) arg0;
                Future<Message> f = future(new Callable<Message>() {
                    public Message call() {
                        try {
                            return telegramService.sendMessage(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                }, getContext().dispatcher());

                ActorRef sender = getSender();
                f.onComplete(new OnComplete<Message>() {
                    @Override
                    public final void onComplete(Throwable failure, Message message) {
                        if (failure != null) {
                            failure.printStackTrace();
                        }
                        sender.tell(message, getSelf());
                    }
                }, getContext().dispatcher());
            } else {
                unhandled(arg0);
            }
        }

        public static Props props(TelegramService telegramService) {
            return Props.create(new Creator<ChannelPublisher>() {
                private static final long serialVersionUID = 1L;

                @Override
                public ChannelPublisher create() throws Exception {
                    return new ChannelPublisher(telegramService);
                }
            });
        }
    }

    private Map<String, ActorRef> channels;
    private TelegramService telegramService;

    public TelegramPublisher(TelegramService telegramService) {
        this.telegramService = telegramService;
        channels = new HashMap<>();
    }

    @Override
    public void onReceive(Object arg0) throws Throwable {
        if (arg0 instanceof String && ((String) arg0).compareTo("init") == 0) {
            getSender().tell("ack", getSelf());
        } else if (arg0 instanceof SendMessage) {
            SendMessage sendMessage = (SendMessage) arg0;
            ActorRef channel = channels.get(sendMessage.getChatId());
            if (channel == null) {
                ActorRef publisher = this.getContext().actorOf(ChannelPublisher.props(telegramService));
                channel = this.getContext().actorOf(
                        Props.create(TimerBasedThrottler.class,
                                new Throttler.Rate(1, Duration.create(1, TimeUnit.SECONDS))),
                        "channel:" + sendMessage.getChatId());
                channel.tell(new Throttler.SetTarget(publisher), null);
                channels.put(sendMessage.getChatId(), channel);
            }
            Future<Object> f = Patterns.ask(channel, sendMessage,
                    new Timeout(new FiniteDuration(60, TimeUnit.SECONDS)));
            ActorRef sender = getSender();
            f.onComplete(new OnComplete<Object>() {
                @Override
                public final void onComplete(Throwable failure, Object message) {
                    sender.tell("ack", getSelf());
                }
            }, getContext().dispatcher());
        } else {
            unhandled(arg0);
        }
    }

    public static Props props(TelegramService telegramService) {
        return Props.create(new Creator<TelegramPublisher>() {
            private static final long serialVersionUID = 1L;

            @Override
            public TelegramPublisher create() throws Exception {
                return new TelegramPublisher(telegramService);
            }
        });
    }
}
