package me.retran.consolelifebot.quiz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.giantbomb.GameEntry;
import me.retran.consolelifebot.utils.Levenstein;

@Singleton
public class GameState {
	public final static int Idle = 0;
	public final static int Playing = 1;
	public final static int AwaitingAnswers = 2;
	
	private int status = Idle;	
	private GameEntry game = null;
	private LocalDateTime stateChangedAt;
	private Map<String, Integer> scores;
	
	private List<Answer> answers;
	private Configuration configuration;
	
	@Inject
	public GameState(Configuration configuration) {
		stateChangedAt = LocalDateTime.now();
		answers = new ArrayList<Answer>();
		this.configuration = configuration;
		loadScores();
	}

	private void loadScores() {
		try
		{
			if (new File(configuration.topFilename()).exists()) {
				FileInputStream fis = new FileInputStream(configuration.topFilename());
				ObjectInputStream ois = new ObjectInputStream(fis);
				scores = (HashMap<String, Integer>) ois.readObject();
				ois.close();
				fis.close();
			} else {
				scores = new HashMap<String, Integer>();
			}			
		} catch(Exception e) {
			throw new RuntimeException("Can't load scores.", e);
		}
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
		stateChangedAt = LocalDateTime.now();
	}

	public GameEntry getGame() {
		return game;
	}

	public void setGame(GameEntry game) {
		this.game = game;
	}

	public LocalDateTime getStateChangedAt() {
		return stateChangedAt;
	}

	public boolean userAnswered(String user) {
		return answers.stream().anyMatch(a -> a.getUser().equalsIgnoreCase(user));
	}
	
	public boolean hasAnswer(String answer) {
		return answers.stream().anyMatch(a -> a.getAnswer().equalsIgnoreCase(answer));
	}

	public void addAnswer(String user, String answer) {
		answers.add(new Answer(user, answer, 
				Levenstein.distance(game.name().toLowerCase(), answer.toLowerCase())));
	}

	public void clearAnswers() {
		answers.clear();		
	}
	
	private class ScoreEntry {
		private String name;
		private int score;
		
		public ScoreEntry(String name, int score) {
			this.name = name;
			this.score = score;
		}

		public String getName() {
			return name;
		}

		public int getScore() {
			return score;
		}
	}
	
	public String getScores() {
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (ScoreEntry entry : scores.entrySet().stream()
			.sorted(Map.Entry.<String, Integer>comparingByValue())
			.map(e -> new ScoreEntry(e.getKey(), e.getValue()))
			.collect(Collectors.toList())) {
			sb.append(String.format("%d. @%s - %d\n", i, entry.getName(), entry.getScore()));
			i++;			
		}			
		return sb.toString();
	}
	
	public void incrementScore(String user) {
		if (!scores.containsKey(user))
		{
			scores.put(user, 0);
		}
		
		scores.put(user, scores.get(user) + 1);
		saveScores();
	}

	private void saveScores() {
		try
		{
			FileOutputStream fos = new FileOutputStream(configuration.topFilename());
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(scores);
			oos.close();
			fos.close();
		} catch(IOException e) {
	       e.printStackTrace();
		}		
	}

	public Answer getBestAnswer() {
		return answers.stream()
				.min((a, b) -> {
					int result = Integer.compare(a.getEstimate(), b.getEstimate());
					if (result == 0) {
						result = a.getStamp().compareTo(b.getStamp());
					}
					return result;
				})
				.orElse(null);
	}
}
