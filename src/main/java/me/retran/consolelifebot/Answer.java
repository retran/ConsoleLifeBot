package me.retran.consolelifebot;

import java.time.LocalDateTime;

public class Answer {
    private String answer;
    private String user;
    private LocalDateTime stamp;
    private int estimate;

    public Answer(String user, String answer, int estimate) {
        this.answer = answer;
        this.user = user;
        this.estimate = estimate;
        this.stamp = LocalDateTime.now();
    }

    public String getAnswer() {
        return answer;
    }

    public String getUser() {
        return user;
    }

    public LocalDateTime getStamp() {
        return stamp;
    }

    public int getEstimate() {
        return this.estimate;
    }
}
