package me.retran.consolelifebot.quiz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.Utils;
import me.retran.consolelifebot.giantbomb.GameEntry;

@Singleton
public class GameState {
    public final static int Idle = 0;
    public final static int Playing = 1;
    public final static int AwaitingAnswers = 2;

    private Configuration configuration;

    private int status = Idle;
    private GameEntry game = null;
    private LocalDateTime stateChangedAt;
    private Map<String, Integer> scores;
    private List<Answer> answers;
    private Object object = new Object();

    @Inject
    public GameState(Configuration configuration) {
        stateChangedAt = LocalDateTime.now();
        answers = new ArrayList<Answer>();
        this.configuration = configuration;
        loadScores();
    }

    @SuppressWarnings({ "unchecked" })
    private void loadScores() {
        try {
            synchronized (object) {
                if (new File(configuration.topFilename()).exists()) {
                    FileInputStream fis = null;
                    ObjectInputStream ois = null;
                    try {
                        fis = new FileInputStream(configuration.topFilename());
                        ois = new ObjectInputStream(fis);
                        scores = (HashMap<String, Integer>) ois.readObject();
                    } finally {
                        if (ois != null) {
                            ois.close();
                        }
                        if (fis != null) {
                            fis.close();
                        }
                    }
                } else {
                    scores = new HashMap<String, Integer>();
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Can't load scores.", e);
        } catch (IOException e) {
            throw new RuntimeException("Can't load scores.", e);
        } catch (ClassNotFoundException e) {
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
        int baseEstimate = Utils.distance(game.name().toLowerCase(), answer.toLowerCase());
        String[] words = game.name().split(" ");
        String[] answerWords = answer.split(" ");
        boolean flag = false;
        for (int j = 0; j < answerWords.length; j++) {
            for (int i = 0; i < words.length; i++) {
                int est = Utils.distance(answerWords[j].toLowerCase(), words[i].toLowerCase());
                System.out.println(words[i] + " " + answerWords[j]);
                System.out.println(words[i].length() / 2);
                System.out.println(est);

                if (est <= words[i].length() / 2) {
                    System.out.println("right");
                    flag = true;
                }
            }
        }
        if (!flag) {
            baseEstimate = 10000;
        }
        answers.add(new Answer(user, answer, baseEstimate));
    }

    public void clearAnswers() {
        answers.clear();
    }

    static private class ScoreEntry {
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
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(e -> new ScoreEntry(e.getKey(), e.getValue())).collect(Collectors.toList())) {
            sb.append(String.format("%d. @%s - %d%n", i, entry.getName(), entry.getScore()));
            i++;
        }
        return sb.toString();
    }

    public void incrementScore(String user) {
        if (!scores.containsKey(user)) {
            scores.put(user, 0);
        }
        scores.put(user, scores.get(user) + 1);
        saveScores();
    }

    private void saveScores() {
        synchronized (object) {
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                try {
                    fos = new FileOutputStream(configuration.topFilename());
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(scores);
                } finally {
                    if (oos != null) {
                        oos.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Answer getBestAnswer() {
        return answers.stream().min((a, b) -> {
            int result = Integer.compare(a.getEstimate(), b.getEstimate());
            if (result == 0) {
                result = a.getStamp().compareTo(b.getStamp());
            }
            return result;
        }).orElse(null);
    }
}
