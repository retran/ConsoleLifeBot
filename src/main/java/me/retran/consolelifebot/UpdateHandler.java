package me.retran.consolelifebot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

public class UpdateHandler extends UntypedActor {
    private String botUserName = "";
    private Library library;
    private GameState gameState;

    public UpdateHandler(String botUserName, Library library, GameState gameState) {
        super();
        this.botUserName = botUserName;
        this.library = library;
        this.gameState = gameState;
    }

    public SendMessage replyWith(Message message, String resource) {
        return reply(message, Utils.getPredefinedMessage(resource));
    }

    public SendMessage reply(Message message, String text) {
        return new SendMessage().setChatId(message.getChatId()).disableNotification().disableWebPagePreview()
                .enableHtml(true).setReplyToMessageId(message.getMessageId()).setText(text).setParseMode("HTML");
    }

    public SendMessage send(Message message, String text) {
        return new SendMessage().setChatId(message.getChatId()).disableNotification().disableWebPagePreview()
                .enableHtml(true).setText(text).setParseMode("HTML");
    }
    
    public void onReceive(Object arg0) throws Throwable {
        if (arg0 instanceof Update) {
            Object reply = null;
            Message message = ((Update) arg0).getMessage();
            if (message != null && message.getText() != null && !message.getText().isEmpty()) {
                if (message.getText().startsWith("/")) {
                    String command = message.getText().replace("@" + botUserName, "").trim();
                    if (command.compareToIgnoreCase("/about") == 0) {
                        reply = replyWith(message, "predefined/about.txt");
                    } else if (command.compareToIgnoreCase("/list") == 0) {
                        reply = replyWith(message, "predefined/list.txt");
                    } else if (command.compareToIgnoreCase("/rules") == 0) {
                        reply = replyWith(message, "predefined/rules.txt");
                    } else if (command.compareToIgnoreCase("/startgame") == 0) {
                      if (gameState.getStatus() == GameState.Idle) {
                          gameState.setStatus(GameState.Playing);
                      }
                    } else if (command.compareToIgnoreCase("/top") == 0) {
                        reply = send(message, gameState.getScores());
                    } else if (command.toLowerCase().startsWith("/rom")) {
                        String arg = message.getText().trim().replace("/rom", "").trim();
                        if (arg.isEmpty()) {
                            reply = reply(message, "Укажи строку для поиска после команды.");
                        } else {
                            Entry[] entries = library.search(arg);
                            if (entries.length == 0) {
                                reply = reply(message, "Ничего не найдено :(");
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (Entry e : entries) {
                                    sb.append(String.format("%s %s (/r%d)%n", e.getPlatform(), e.getFilename(),
                                            e.getId()));
                                }
                                reply = reply(message, sb.toString());
                            }
                        }
                    } else if (command.toLowerCase().startsWith("/r")) {
                        command = command.replace("/r", "");
                        long id = Long.parseLong(command);
                        Entry entry = library.get(id);
                        if (entry != null) {
                            try {
                                reply = new SendDocument()
                                        .setNewDocument(entry.getFilename(), new FileInputStream(entry.getPath()))
                                        .setChatId(message.getChatId()).setReplyToMessageId(message.getMessageId());
                            } catch (FileNotFoundException e) {
                            }
                        }
                    }
                } else if (message.getText().startsWith("!")) {
                    if (gameState.getStatus() == GameState.AwaitingAnswers) {
                        String answer = message.getText().trim().replaceFirst("!", "").trim();
                        String user = Utils.getDisplayName(message.getFrom());
                        if (gameState.hasAnswer(answer)) {
                            reply = reply(message, "Такой ответ уже был, попробуй другой.");
                        } else if (gameState.userAnswered(user)) {
                            reply = reply(message, "Ты уже отвечал :(");
                        } else {
                            gameState.addAnswer(user, answer);
                        }
                    }
                }
            } else {
                if (message != null && message.getNewChatMember() != null) {
                    reply = replyWith(message, "predefined/welcome.txt");
                }
            }
            if (reply != null) {
                getSender().tell(reply, getSelf());
            } else {
                getSender().tell("null", getSelf());
            }
        } else {
            unhandled(arg0);
        }
    }

    public static Props props(String botUserName, Library library, GameState gameState) {
        return Props.create(new Creator<UpdateHandler>() {
            private static final long serialVersionUID = 1L;

            @Override
            public UpdateHandler create() throws Exception {
                return new UpdateHandler(botUserName, library, gameState);
            }
        });
    }
}
