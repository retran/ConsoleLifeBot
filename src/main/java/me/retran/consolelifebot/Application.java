
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
import me.retran.consolelifebot.messaging.Sender;
import me.retran.consolelifebot.quiz.GameProcess;
import me.retran.consolelifebot.youtube.LegacyYouTubePoller;
import scala.concurrent.duration.Duration;

public class Application {
    private static final Dependencies injector = DaggerDependencies.create();
    private static final Configuration configuration = injector.configuration();
    private static final ActorSystem actorSystem = ActorSystem.create("consolelifebot");

    public static void main(String[] args) {
        final ActorRef messageSender = actorSystem.actorOf(Props.create(MessageSender.class, injector.telegramMessagingService()), "messageSender");
        final ActorRef messageThrottler = actorSystem.actorOf(Props.create(TimerBasedThrottler.class,
                new Throttler.Rate(2, Duration.create(30, TimeUnit.SECONDS))));
        messageThrottler.tell(new Throttler.SetTarget(messageSender), null);
        
        if (!configuration.telegramToken().isEmpty()) {
            if (!configuration.youtubeApiKey().isEmpty() && !configuration.channels().isEmpty()) {
                // YouTubePoller poller = injector.youTubePoller();
                // poller.start();
            }

            if (!configuration.giantbombApiKey().isEmpty() && configuration.giantbombPlatforms().length > 0) {
                GameProcess process = injector.gameProcess();
                process.start();
            }

            try {
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
                telegramBotsApi.registerBot(injector.messagesHandler());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
    }
}
