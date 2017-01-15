
package me.retran.consolelifebot;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.ApiConstants;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.api.objects.Update;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import me.retran.consolelifebot.common.MessagesHandler;
import me.retran.consolelifebot.quiz.GameProcess;
import scala.concurrent.duration.Duration;
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
            .map(i -> new SendMessage()
                .setChatId("@consolenote")
                .setText(i.getText()))            
            .throttle(30, new FiniteDuration(1, TimeUnit.SECONDS), 30, ThrottleMode.shaping())
            .to(Sink.actorRef(telegramPublisher, null))
            .run(materializer);
        
        MessagesHandler messageHandler = injector.messagesHandler();
        Source.fromGraph(new TelegramPollingSource(new FiniteDuration(500, TimeUnit.MILLISECONDS),
                injector.telegramService()))
            .runForeach(i -> messageHandler.onUpdateReceived(i), materializer);
        
        GameProcess process = injector.gameProcess();
        process.start();                
    }
}
