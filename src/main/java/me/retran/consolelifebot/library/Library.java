package me.retran.consolelifebot.library;

import javax.inject.Inject;
import me.retran.consolelifebot.common.Configuration;

import java.io.File;
import java.util.ArrayList;

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
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                indexDir(file.getPath());
            } else {
                entries.add(new Entry(file.getAbsolutePath()));
            }
        }
    }

    public Entry[] search(String pattern) {
        final String p = pattern.toLowerCase().trim();
        return entries.stream().filter(e -> e.getFilename().contains(p)).toArray(size -> new Entry[size]);
    }

    public Entry getEntry(long id) {
        return entries.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }
}
