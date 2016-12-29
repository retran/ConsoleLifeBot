package me.retran.consolelifebot.giantbomb;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameEntry {
    String name;
    ImageEntry[] images;

    public List<ImageEntry> images() {
        return Arrays.asList(this.images);
    }

    public String name() {
        return this.name;
    }

    public Boolean hasScreenshots() {
         return images().stream().anyMatch(i -> i.tags().contains("screenshot"));
    }

    public String randomScreenshot() {
        ImageEntry[] screenshots =
            images().stream()
            .filter(i -> i.tags().contains("screenshot"))
            .toArray(ImageEntry[]::new);
        Random rnd = new Random();
        return screenshots[rnd.nextInt(screenshots.length)].url();
    }
}
