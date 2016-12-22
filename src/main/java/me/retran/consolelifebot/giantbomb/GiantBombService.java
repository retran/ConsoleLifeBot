package me.retran.consolelifebot.giantbomb;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import me.retran.consolelifebot.common.Configuration;

@Singleton
public class GiantBombService {
    private Configuration configuration;
    private final String baseUri = "http://www.giantbomb.com/api";

    @Inject
    public GiantBombService(Configuration configuration) {
        this.configuration = configuration;
    }

    public GameEntry getGame(int id) {
        try {
            HttpResponse<JsonNode> response = Unirest
                .get(baseUri + "/game/" + Integer.toString(id))
                .queryString("api_key", this.configuration.giantbombApiKey())
                .queryString("format", "json")
                .asJson();
            return null;
        }
        catch (UnirestException e) {
            return null;
        }
    }
}
