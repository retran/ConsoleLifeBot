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

import com.google.inject.util.Types;
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

    public InputStream getFile(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .build();
        Response response = client.newCall(request).execute();
        return response.body().byteStream();
    }

    public int getGameCountForPlatform(int id) throws IOException {
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
        Response response = client.newCall(request).execute();
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GiantBombResponse<GameListEntry>> jsonAdapter 
        	= moshi.adapter(Types.newParameterizedType(GiantBombResponse.class,  GameEntry.class));
        GiantBombResponse<GameListEntry> result = null;
        result = jsonAdapter.fromJson(response.body().string());
        return result.total();
    }

    public GameEntry getRandomGame() throws IOException {
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
        GiantBombResponse<GameListEntry> result = null;
        Response response = client.newCall(request).execute();
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GiantBombResponse<GameListEntry>> jsonAdapter 
        	= moshi.adapter(Types.newParameterizedType(GiantBombResponse.class,  GameEntry.class));
        result = jsonAdapter.fromJson(response.body().string());
        return getGame(result.results().id());
    }

    public GameEntry getGame(int id) throws IOException {
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
        Response response = client.newCall(request).execute();
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GiantBombResponse<GameEntry>> jsonAdapter = 
        		moshi.adapter(Types.newParameterizedType(GiantBombResponse.class,  GameEntry.class));
        GiantBombResponse<GameEntry> result = null;
        result = jsonAdapter.fromJson(response.body().string());
        return result.results();
    }
}
