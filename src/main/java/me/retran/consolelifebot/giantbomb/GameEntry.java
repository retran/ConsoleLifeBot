package me.retran.consolelifebot.giantbomb;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameEntry {
    String name;
    GameImage[] images;

    public List<GameImage> images() {
        return Arrays.asList(this.images);
    }

    public String name() {
        return this.name;
    }

    public Boolean hasScreenshots() {
        //    return true;
         return images().stream().anyMatch(i -> i.tags().contains("screenshot"));
    }

    public String randomScreenshot() {
        GameImage[] screenshots =
            //this.images;
            images().stream()
            .filter(i -> i.tags().contains("screenshot"))
            .toArray(GameImage[]::new);
        Random rnd = new Random();
        return screenshots[rnd.nextInt(screenshots.length)].url();
    }
}
