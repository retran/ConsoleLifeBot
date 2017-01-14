package me.retran.consolelifebot.giantbomb;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.telegram.telegrambots.logging.BotLogger;

public class GameEntry {
    String name = "";
    String site_detail_url = "";
    int number_of_user_reviews = 0;
    ImageEntry[] images = new ImageEntry[0];

    public List<ImageEntry> images() {
        return Arrays.asList(this.images);
    }

    public String name() {
        return this.name;
    }

    public String detailUrl() {
        return site_detail_url;
    }

    public Boolean hasScreenshots() {
        BotLogger.info("hasScreenshots", Integer.toString(images.length));
        return images().stream().anyMatch(i -> i.tags().contains("screenshot"));
    }

    public String randomScreenshot() {
        ImageEntry[] screenshots = images().stream().filter(i -> i.tags().contains("screenshot"))
                .toArray(ImageEntry[]::new);
        Random rnd = new Random();
        return screenshots[rnd.nextInt(screenshots.length)].url();
    }
}
