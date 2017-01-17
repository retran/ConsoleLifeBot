package me.retran.consolelifebot;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Library {
    private final ArrayList<Entry> entries;
    private final String path;
    private final Object lock = new Object();

    @Inject
    public Library(Configuration configuration) {
        path = configuration.library();
        entries = new ArrayList<Entry>();
        index();
    }

    private void index() {
        Entry.reset();
        entries.clear();
        index(path);
    }

    private void index(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    index(file.getPath());
                } else {
                    entries.add(new Entry(file.getAbsolutePath()));
                }
            }
        }
    }

    public Entry[] search(String pattern) {
        synchronized (lock) {
            final String p = pattern.toLowerCase().trim();
            return entries.stream().filter(e -> e.getFilename().toLowerCase().contains(p))
                    .toArray(size -> new Entry[size]);
        }
    }

    public Entry get(long id) {
        synchronized (lock) {
            return entries.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        }
    }
}
