package me.retran.consolelifebot.quiz;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import me.retran.consolelifebot.common.TelegramClient;
import me.retran.consolelifebot.giantbomb.GameEntry;
import me.retran.consolelifebot.giantbomb.GiantBombService;

@Singleton
public class GameProcess extends Thread {
	private GameState state;
	private GiantBombService giantbombService;
	private TelegramClient client;

	@Inject
	public GameProcess(GameState state, GiantBombService giantbombService,
			TelegramClient client) {
		this.state = state;
		this.giantbombService = giantbombService;
		this.client = client;
	}
	
	public void run() {
		while (true) {
			int status = state.getStatus();
			
			switch (status) {
			case GameState.Idle:
				// do nothing
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
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void processAnswers() {
		if (ChronoUnit.MINUTES.between(state.getStateChangedAt(), LocalDateTime.now()) >= 1) {
			Answer best = state.getBestAnswer();
			try {
				sendMessage(
						String.format("Раунд закончен. Правильный ответ - <a href=\"%s\">%s</a>.", state.getGame().detailUrl(), state.getGame().name()));
				if (best == null) {
					sendMessage("Не принято ни одного ответа :(");
					state.setStatus(GameState.Idle);	
				} else {
					if (state.getGame().name().length() > 3 && best.getEstimate() >= state.getGame().name().length() - 3) {
						sendMessage(
								String.format("Правильный ответ не дал никто.", best.getAnswer(), best.getUser()));						
					} else {
						state.incrementScore(best.getUser());
						sendMessage(
								String.format("Лучший ответ - <b>%s (@%s)</b>.", best.getAnswer(), best.getUser()));
					}
					state.setStatus(GameState.Playing);	
				}
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	private void sendMessage(String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage()
                .setText(text)
                .disableWebPagePreview()
                .enableHtml(true)
                .enableNotification()
                .setChatId("@clbottest");
        client.sendMessage(sendMessage);
	}

	private void makeQuestion() {
		try {
			state.clearAnswers();
			GameEntry game = null;
			while (game == null || !game.hasScreenshots()) {
				game = giantbombService.getRandomGame();
			}
            BotLogger.info("makeQuestion", game.name());	

			state.setGame(game);
            BotLogger.info("makeQuestion", "send screenshot");	

			sendScreenshot(game.randomScreenshot());
            BotLogger.info("makeQuestion", "send message");	
			sendMessage("Что это за игра? У вас одна минута на ответ.\n(отвечайте сообщениями типа \"!названиеигры\")");
			state.setStatus(GameState.AwaitingAnswers);
		} catch (IOException | TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendScreenshot(String url) throws IOException, TelegramApiException {
		InputStream inputStream = giantbombService.getFile(url);
		SendPhoto sendPhoto = new SendPhoto()
				.setChatId("@clbottest")
				.setNewPhoto("photo.jpg", inputStream);
		client.sendPhoto(sendPhoto);
	}
}
