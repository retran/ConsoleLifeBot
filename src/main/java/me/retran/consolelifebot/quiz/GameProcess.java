package me.retran.consolelifebot.quiz;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.Helpers;
import me.retran.consolelifebot.common.TelegramClient;
import me.retran.consolelifebot.giantbomb.GameEntry;
import me.retran.consolelifebot.giantbomb.GiantBombService;

@Singleton
public class GameProcess extends Thread {
    private GameState state;
    private GiantBombService giantbombService;
    private TelegramClient client;
    private int time;
    private Configuration configuration;

    @Inject
    public GameProcess(Configuration configuration, GameState state, GiantBombService giantbombService,
            TelegramClient client) {
        this.state = state;
        this.giantbombService = giantbombService;
        this.client = client;
        this.configuration = configuration;
    }

    public void run() {
        while (true) {
            int status = state.getStatus();
            switch (status) {
            case GameState.Idle:
                time = 5;
                break;
            case GameState.Playing:
                makeQuestion();
                break;
            case GameState.AwaitingAnswers:
                processAnswers();
                break;
            default:
                break;
            }
            Helpers.sleep(1000);
        }
    }

    private void processAnswers() {
        if (ChronoUnit.SECONDS.between(state.getStateChangedAt(), LocalDateTime.now()) >= 20) {
            Answer best = state.getBestAnswer();
            try {
                sendMessage(
                        String.format("Время закончилось. Все было просто, игра называется - <a href=\"%s\">%s</a>.",
                                state.getGame().detailUrl(), state.getGame().name()));
                if (best == null) {
                    sendMessage(
                            "Никто не хочет со мной играть :( Игра остановлена. Чтобы запустить ее снова - напишите /startgame");
                    state.setStatus(GameState.Idle);
                } else {
                    if (best.getEstimate() > 1000) {
                        sendMessage("Ну что ж так, ни одного правильного ответа ;(");
                    } else {
                        state.incrementScore(best.getUser());
                        sendMessage(String.format("Лучший ответ - <b>%s (@%s)</b>. Молодец!", best.getAnswer(),
                                best.getUser()));
                    }
                    state.setStatus(GameState.Playing);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setText(text).disableWebPagePreview().enableHtml(true)
                .enableNotification().setChatId(configuration.chatName());
        client.sendMessage(sendMessage);
    }

    private void makeQuestion() {
        if (ChronoUnit.SECONDS.between(state.getStateChangedAt(), LocalDateTime.now()) >= time) {
            time = 40;
            try {
                state.clearAnswers();
                GameEntry game = null;
                while (game == null || !game.hasScreenshots()) {
                    game = giantbombService.getRandomGame();
                }
                state.setGame(game);
                sendScreenshot(game.randomScreenshot());
                sendMessage(
                        "Ну что? Сможете угадать игру по скриншоту? У вас 20 секунд на ответ.\n(отвечайте сообщениями типа \"!названиеигры\")");
                state.setStatus(GameState.AwaitingAnswers);
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendScreenshot(String url) throws IOException, TelegramApiException {
        InputStream inputStream = giantbombService.getFile(url);
        SendPhoto sendPhoto = new SendPhoto().setChatId(configuration.chatName()).setNewPhoto("photo.jpg", inputStream);
        client.sendPhoto(sendPhoto);
    }
}
