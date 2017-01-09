package me.retran.consolelifebot.handlers;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.Helpers;
import me.retran.consolelifebot.common.SentMessageCallback;
import me.retran.consolelifebot.quiz.GameState;

@Singleton
public class TopCommandHandler extends CommandHandler {
	private GameState state;
	private SentMessageCallback callback;

	@Inject
	public TopCommandHandler(Configuration configuration, GameState state, SentMessageCallback callback) {
		super(configuration, "/top", "");
		this.state = state;
		this.callback = callback;
	}

	@Override
	public void handle(AbsSender sender, Message message) {
        BotLogger.info(Helpers.getDisplayName(message.getFrom()), message.getText());
		String text = state.getScores();
		if (!text.isEmpty()) {
	        SendMessage sendMessage = new SendMessage()
	                .setText(text)
	                .disableNotification()
	                .disableWebPagePreview()
	                .enableHtml(true)
	                .setChatId(message.getChatId())
	                .setReplyToMessageId(message.getMessageId());
	        try {
	            sender.sendMessageAsync(sendMessage, callback);
	        } catch (TelegramApiException e) {
	            BotLogger.severe(this.getTemplate(), e);
	        }
		}
	}

}
