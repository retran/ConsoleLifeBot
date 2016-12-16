package me.retran.consolelifebot.youtube;

public class YouTubeEntry {
    private String title;
    private String channelTitle;
    private String url;

    public YouTubeEntry(String title, String channelTitle, String url) {
        this.title = title;
        this.channelTitle = channelTitle;
        this.url = url;
    }

    public String getText() {
        return String.format("<a href=\"%s\">%s</a> (%s)", url, title, channelTitle);
    }
}
