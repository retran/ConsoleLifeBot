
package me.retran.consolelifebot;

import java.util.concurrent.TimeUnit;

import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.contrib.throttle.Throttler;
import akka.contrib.throttle.TimerBasedThrottler;
import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.messaging.MessageSender;
import me.retran.consolelifebot.quiz.GameProcess;
import scala.concurrent.duration.Duration;

public class Application {
    public static void main(String[] args) {
        final Dependencies injector = DaggerDependencies.create();
        final Configuration configuration = injector.configuration();
        final ActorSystem system = ActorSystem.create("consolelifebot");
        final ActorRef messageSender = system.actorOf(MessageSender.props(injector.telegramService()));
        final ActorRef messageThrottler = system.actorOf(
                Props.create(TimerBasedThrottler.class,
                        new Throttler.Rate(MessageSender.messagesPerSecond, Duration.create(1, TimeUnit.SECONDS))),
                "messageSender");
        messageThrottler.tell(new Throttler.SetTarget(messageSender), null);
        
        
        
//        if (!configuration.telegramToken().isEmpty()) {
//            if (!configuration.youtubeApiKey().isEmpty() && !configuration.channels().isEmpty()) {
//                // YouTubePoller poller = injector.youTubePoller();
//                // poller.start();
//            }
//
//            if (!configuration.giantbombApiKey().isEmpty() && configuration.giantbombPlatforms().length > 0) {
//                GameProcess process = injector.gameProcess();
//                process.start();
//            }
//
//            try {
//                TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
//                telegramBotsApi.registerBot(injector.messagesHandler());
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
