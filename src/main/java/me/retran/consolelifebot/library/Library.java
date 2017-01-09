package me.retran.consolelifebot.library;

import me.retran.consolelifebot.common.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;

@Singleton
public class Library {
    private final ArrayList<Entry> entries;
    private final String path;

    @Inject
    public Library(Configuration configuration) {
        path = configuration.library();
        entries = new ArrayList<Entry>();
        index();
    }

    private void index() {
        Entry.reset();
        entries.clear();
        indexDir(path);
    }

    private void indexDir(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    indexDir(file.getPath());
                } else {
                    entries.add(new Entry(file.getAbsolutePath()));
                }
            }
        }
    }

    public Entry[] search(String pattern) {
        final String p = pattern.toLowerCase().trim();
        return entries.stream().filter(e -> e.getFilename().toLowerCase().contains(p)).toArray(size -> new Entry[size]);
    }

    public Entry getEntry(long id) {
        return entries.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }
}
