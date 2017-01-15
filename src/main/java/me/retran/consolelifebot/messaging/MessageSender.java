package me.retran.consolelifebot.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.contrib.throttle.Throttler;
import akka.contrib.throttle.TimerBasedThrottler;
import scala.concurrent.duration.Duration;

public class MessageSender extends UntypedActor  {
    private Map<String, ActorRef> throttlers;
    private MessagingService messagingService;
    
    public MessageSender(MessagingService messagingService) {
        this.messagingService = messagingService;
        throttlers = new HashMap<>();
    }
    
    @Override
    public void onReceive(Object arg0) throws Throwable {
        if (arg0 instanceof Message) {
            Message message = (Message)arg0;
            ActorRef throttler = throttlers.get(message.to());
            if (throttler == null) {
                ActorRef sender = this.getContext().actorOf(Sender.props(messagingService), "sender:" + message.to());
                throttler = this.getContext().actorOf(Props.create(TimerBasedThrottler.class,
                        new Throttler.Rate(2, Duration.create(1, TimeUnit.SECONDS))));
                throttler.tell(new Throttler.SetTarget(sender), null);
                throttlers.put(message.to(), throttler);
            }
            throttler.tell(arg0, null);
        }
        unhandled(arg0);   
    }
}
