package me.retran.consolelifebot.giantbomb;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.telegram.telegrambots.logging.BotLogger;

import com.google.inject.util.Types;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import me.retran.consolelifebot.common.Configuration;
import me.retran.consolelifebot.common.Helpers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class GiantBombService {
    private Configuration configuration;
    private HttpUrl baseUri;
    private OkHttpClient client;
    private Random random;
    private String[] platforms;
    private Map<String, Integer> counts;
    private final Object countsLock = new Object();
    private final Object randomLock = new Object();

    @Inject
    public GiantBombService(Configuration configuration) {
        this.configuration = configuration;
        this.client = new OkHttpClient();
        this.baseUri = HttpUrl.parse("http://www.giantbomb.com/api/");
        this.random = new Random();
        this.platforms = configuration.giantbombPlatforms();
        this.counts = new HashMap<String, Integer>();
    }

    public InputStream getFile(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .build();
        Response response = client.newCall(request).execute();
        return response.body().byteStream();
    }

    private int getGameCountForPlatform(String id) throws IOException {
        boolean hasCount = false;
        synchronized (countsLock) {
            hasCount = counts.containsKey(id);
        }
        if (!hasCount) {
            HttpUrl url = this.baseUri.newBuilder()
                .addPathSegment("games")
                .addQueryParameter("api_key", this.configuration.giantbombApiKey())
                .addQueryParameter("format", "json")
                .addQueryParameter("limit", "1")
                .addQueryParameter("platforms", id)
                .addQueryParameter("fieldList", "id")
                .build();
            Request request = new Request.Builder()
                .url(url)
                .build();
            Response response = client.newCall(request).execute();
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<GiantBombResponse<GameListEntry[]>> jsonAdapter
                = moshi.adapter(Types.newParameterizedType(GiantBombResponse.class,  GameListEntry[].class));
            GiantBombResponse<GameListEntry[]> result = null;
            result = jsonAdapter.fromJson(response.body().string());
            synchronized (countsLock) {
                counts.put(id, result.total());
            }
        }
        synchronized (countsLock) {
            return counts.get(id);
        }
    }

    public GameEntry getRandomGame() throws IOException {
        String platform = null;
        int count = 0;
        int offset = 0;
        synchronized (randomLock) {
            BotLogger.info("randomGame", Integer.toString(platforms.length));	
            platform = platforms[random.nextInt(platforms.length)];
            count = getGameCountForPlatform(platform);
            BotLogger.info("randomGame", Integer.toString(count));	            
            offset = random.nextInt(count);
        }
        HttpUrl url = this.baseUri.newBuilder()
            .addPathSegment("games")
            .addQueryParameter("api_key", this.configuration.giantbombApiKey())
            .addQueryParameter("format", "json")
            .addQueryParameter("limit", "1")
            .addQueryParameter("offset", Integer.toString(offset))
            .addQueryParameter("platforms", platform)
            .addQueryParameter("field_list", "id")
            .build();
        Request request = new Request.Builder()
            .url(url)
            .build();
        GiantBombResponse<GameListEntry[]> result = null;
        Response response = client.newCall(request).execute();
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GiantBombResponse<GameListEntry[]>> jsonAdapter 
        	= moshi.adapter(Types.newParameterizedType(GiantBombResponse.class,  GameListEntry[].class));
        result = jsonAdapter.fromJson(response.body().string());
        return getGame(result.results()[0].id());
    }

    private GameEntry getGame(int id) throws IOException {
        HttpUrl url = this.baseUri.newBuilder()
            .addPathSegment("game")
            .addPathSegment(Integer.toString(id))
            .addQueryParameter("api_key", this.configuration.giantbombApiKey())
            .addQueryParameter("format", "json")
            .addQueryParameter("field_list", "id,name,site_detail_url,images")
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
