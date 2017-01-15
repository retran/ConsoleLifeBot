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
import akka.japi.Creator;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

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
                            telegramService.sendMessage(sendMessage);
                        } catch (TelegramApiException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        return null;
                    }
                }, getContext().dispatcher());
            }
            unhandled(arg0);
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
        if (arg0 instanceof SendMessage) {
            SendMessage sendMessage = (SendMessage) arg0;
            ActorRef channel = channels.get(sendMessage.getChatId());
            if (channel == null) {
                ActorRef publisher = this.getContext().actorOf(ChannelPublisher.props(telegramService));
                channel = this.getContext()
                        .actorOf(Props.create(TimerBasedThrottler.class,
                                new Throttler.Rate(2, Duration.create(1, TimeUnit.SECONDS))),
                                "channel:" + sendMessage.getChatId());
                channel.tell(new Throttler.SetTarget(publisher), null);
                channels.put(sendMessage.getChatId(), channel);
            }
            channel.tell(arg0, null);
        }
        unhandled(arg0);
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
