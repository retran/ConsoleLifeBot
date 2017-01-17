
package me.retran.consolelifebot;

import java.util.concurrent.TimeUnit;

import org.telegram.telegrambots.api.methods.send.SendMessage;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import akka.util.Timeout;
import scala.concurrent.duration.FiniteDuration;

public class Application {
    public static void main(String[] args) {
        final Dependencies injector = DaggerDependencies.create();
        final Configuration configuration = injector.configuration();

        final ActorSystem system = ActorSystem.create("consolelifebot");
        final Materializer materializer = ActorMaterializer.create(system);

        final ActorRef telegramPublisher = system.actorOf(TelegramPublisher.props(injector.telegramService()),
                "telegramPublisher");
        final ActorRef updateHandler = system
                .actorOf(UpdateHandler.props(
                        configuration.telegramUserName(),
                        injector.library(),
                        injector.gameState()), "updateHandler");

        Timeout askTimeout = Timeout.apply(5, TimeUnit.SECONDS);
        Source<Object, NotUsed> updates = Source
                .fromGraph(new TelegramPollingSource(new FiniteDuration(500, TimeUnit.MILLISECONDS),
                        injector.telegramService()))
                .async().mapAsync(1, i -> PatternsCS.ask(updateHandler, i, askTimeout))
                .filter(i -> !(i instanceof String));

        Source<Object, NotUsed> youtubeEntries = Source
                .fromGraph(new YouTubePollingSource(new FiniteDuration(15, TimeUnit.MINUTES),
                        configuration.youtubeApiKey(), configuration.channels()))
                .async().map(i -> new SendMessage().setChatId(configuration.channelName()).setText(i.getText())
                        .setParseMode("HTML"));

        updates.merge(youtubeEntries).throttle(30, new FiniteDuration(1, TimeUnit.SECONDS), 30, ThrottleMode.shaping())
                .to(Sink.actorRefWithAck(telegramPublisher, "init", "ack", "done", null)).run(materializer);
        
         GameProcess process = injector.gameProcess();
         process.start();
    }
}
