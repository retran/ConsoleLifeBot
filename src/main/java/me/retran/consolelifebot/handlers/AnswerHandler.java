package me.retran.consolelifebot.handlers;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import me.retran.consolelifebot.common.Helpers;
import me.retran.consolelifebot.common.SentMessageCallback;
import me.retran.consolelifebot.quiz.GameState;

@Singleton
public class AnswerHandler extends Handler {

	private GameState state;
	private SentMessageCallback callback;

	@Inject
	public AnswerHandler(GameState state, SentMessageCallback callback) {
		this.state = state;
		this.callback = callback;
	}
	
	@Override
	public boolean canHandle(Message message) {
		return state.getStatus() == GameState.AwaitingAnswers
				&& message.getText().trim().startsWith("!");
	}

	@Override
	public void handle(AbsSender sender, Message message) {
		String answer = message.getText().trim().replaceFirst("!", "").trim();
		String user = Helpers.getDisplayName(message.getFrom());
        BotLogger.info(user, answer);
        String reply = null;
		if (state.hasAnswer(answer)) {
			reply = "Такой ответ уже был, попробуй другой.";
		}
		else if (state.userAnswered(user)) {
			reply = "Ты уже отвечал :(";
		}
		else {
			state.addAnswer(user, answer);
		}
		if (reply != null) {
	        SendMessage sendMessage = new SendMessage()
	                .setText(reply)
	                .disableNotification()
	                .disableWebPagePreview()
	                .enableHtml(true)
	                .setChatId(message.getChatId())
	                .setReplyToMessageId(message.getMessageId());
	        try {
	            sender.sendMessageAsync(sendMessage, callback);
	        } catch (TelegramApiException e) {
	            BotLogger.severe("answer", e);
	        }
		}
	}
}
