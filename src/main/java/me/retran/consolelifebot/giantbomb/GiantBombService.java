package me.retran.consolelifebot.giantbomb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.telegram.telegrambots.logging.BotLogger;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

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

    public InputStream getFile(String url) {
        Request request = new Request.Builder()
            .url(url)
            .build();
        
        try {
            Response response = client.newCall(request).execute();
            return response.body().byteStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    public int getGameCountForPlatform(int id) {
        HttpUrl url = this.baseUri.newBuilder()
            .addPathSegment("games")
            .addQueryParameter("api_key", this.configuration.giantbombApiKey())
            .addQueryParameter("format", "json")
            .addQueryParameter("limit", "1")
            .addQueryParameter("platforms", Integer.toString(id))
            .addQueryParameter("fieldList", "id")
            .build();
        Request request = new Request.Builder()
            .url(url)
            .build();
        try {
            Response response = client.newCall(request).execute();
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<GamesResult> jsonAdapter = moshi.adapter(GamesResult.class);
            GamesResult result = null;
            result = jsonAdapter.fromJson(response.body().string());
            return result.total();
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public GameEntry getRandomGame() {
        int platform = 146;
        int count = getGameCountForPlatform(platform);
        Random rnd = new Random();
        int offset = rnd.nextInt(count);
        HttpUrl url = this.baseUri.newBuilder()
            .addPathSegment("games")
            .addQueryParameter("api_key", this.configuration.giantbombApiKey())
            .addQueryParameter("format", "json")
            .addQueryParameter("limit", "1")
            .addQueryParameter("offset", Integer.toString(offset))
            .addQueryParameter("platforms", Integer.toString(platform))
            .addQueryParameter("field_list", "id")
            .build();
        Request request = new Request.Builder()
            .url(url)
            .build();
        GamesResult result = null;
        try {
            Response response = client.newCall(request).execute();
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<GamesResult> jsonAdapter = moshi.adapter(GamesResult.class);
            result = jsonAdapter.fromJson(response.body().string());
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return getGame(result.id());
    }

    public GameEntry getGame(int id) {

        BotLogger.info("giantbomb", Integer.toString(id));

        HttpUrl url = this.baseUri.newBuilder()
            .addPathSegment("game")
            .addPathSegment(Integer.toString(id))
            .addQueryParameter("api_key", this.configuration.giantbombApiKey())
            .addQueryParameter("format", "json")
            .addQueryParameter("field_list", "id,name,images")
            .build();

        Request request = new Request.Builder()
            .url(url)
            .build();
        BotLogger.info("giantbomb", url.toString());

        try {
            Response response = client.newCall(request).execute();
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Result> jsonAdapter = moshi.adapter(Result.class);
            Result result = null;

            result = jsonAdapter.fromJson(response.body().string());
            return result.results();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
