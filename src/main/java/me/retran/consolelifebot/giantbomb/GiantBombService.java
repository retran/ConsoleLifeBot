package me.retran.consolelifebot.giantbomb;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import me.retran.consolelifebot.common.Configuration;

@Singleton
public class GiantBombService {
    private Configuration configuration;
    private HttpUrl baseUri;
    private OkHttpClient client;

    @Inject
    public GiantBombService(Configuration configuration) {
        this.configuration = configuration;
        this.client = new OkHttpClient();
        this.baseUri = HttpUrl.parse("http://www.giantbomb.com/api/");
    }

    public GameEntry getGame(int id) {
        HttpUrl url = this.baseUri.newBuilder()
            .addPathSegment("game")
            .addPathSegment(Integer.toString(id))
            .addQueryParameter("api_key", this.configuration.giantbombApiKey())
            .addQueryParameter("format", "json")
            .build();

        Request request = new Request.Builder()
            .url(url)
            .build();

        try {
            Response response = client.newCall(request).execute();

            return null;
        }
        catch (IOException e) {
            return null;
        }
    }
}
