package me.retran.consolelifebot.messaging;

public class Message {
    String to;
    
    public String to() {
        return to;
    }
    
    public Message to(String to) {
        this.to = to;
        return this;
    }
}
