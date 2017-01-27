
package me.retran.consolelifebot;

import java.util.concurrent.TimeUnit;

import org.telegram.telegrambots.api.methods.send.SendMessage;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import me.retran.consolelifebot.common.MessagesHandler;
import me.retran.consolelifebot.quiz.GameProcess;
import scala.concurrent.duration.FiniteDuration;

public class Application {
    public static void main(String[] args) {
        final Dependencies injector = DaggerDependencies.create();
        final Configuration configuration = injector.configuration();

        final ActorSystem system = ActorSystem.create("consolelifebot");
        final Materializer materializer = ActorMaterializer.create(system);

        final ActorRef telegramPublisher = system.actorOf(TelegramPublisher.props(injector.telegramService()),
                "telegramPublisher");

        Source.fromGraph(new YouTubePollingSource(new FiniteDuration(15, TimeUnit.MINUTES),
                configuration.youtubeApiKey(), configuration.channels()))
                .map(i -> new SendMessage().setChatId("@consolenote").setText(i.getText()).setParseMode("HTML"))
                .throttle(30, new FiniteDuration(1, TimeUnit.SECONDS), 30, ThrottleMode.shaping())
                .to(Sink.actorRefWithAck(telegramPublisher, "init", "ack", "done", null)).run(materializer);

        Source.fromGraph(
                new RssPollingSource(new FiniteDuration(15, TimeUnit.MINUTES), "https://hi-news.ru/games/feed", null))
                .merge(Source.fromGraph(new RssPollingSource(new FiniteDuration(15, TimeUnit.MINUTES),
                        "http://gamemag.ru/rss/feed", null)))
                .merge(Source.fromGraph(new RssPollingSource(new FiniteDuration(15, TimeUnit.MINUTES),
                        "http://feeds.feedburner.com/devicebox?format=xml", "Игровые консоли")))
                .map(i -> new SendMessage().setChatId("@consolenote")
                        .setText(String.format("<a href=\"%s\">%s</a>", i.getLink(), i.getTitle()))
                        .setParseMode("HTML"))
                .throttle(30, new FiniteDuration(1, TimeUnit.SECONDS), 30, ThrottleMode.shaping())
                .to(Sink.actorRefWithAck(telegramPublisher, "init", "ack", "done", null)).run(materializer);

        MessagesHandler messageHandler = injector.messagesHandler();
        Source.fromGraph(
                new TelegramPollingSource(new FiniteDuration(500, TimeUnit.MILLISECONDS), injector.telegramService()))
                .runForeach(i -> messageHandler.onUpdateReceived(i), materializer);

        GameProcess process = injector.gameProcess();
        process.start();
    }
}
