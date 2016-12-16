package me.retran.consolelifebot.library;

import java.io.File;

public class Entry {
    private static long count = 0;
    private long id;
    private String filename;
    private String path;
    private String platform;

    public Entry(String path) {
        count++;
        File f = new File(path);
        this.id = count;
        this.path = f.getAbsolutePath();
        this.filename = f.getName();
        this.platform = f.getParentFile().getName();
    }

    public static void reset() {
        count = 0;
    }

    public String getFilename() {
        return filename;
    }

    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getPlatform() {
        return platform;
    }
}
