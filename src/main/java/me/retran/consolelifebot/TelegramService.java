package me.retran.consolelifebot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.ApiConstants;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class TelegramService extends DefaultAbsSender {
    static {
        ApiContextInitializer.init();
    }

    private final Configuration configuration;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private CloseableHttpClient httpclient;
    private RequestConfig requestConfig;
    
    @Inject
    public TelegramService(Configuration configuration) {
        super(ApiContext.getInstance(DefaultBotOptions.class));
        this.configuration = configuration;
        
        httpclient = HttpClientBuilder.create()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setConnectionTimeToLive(70, TimeUnit.SECONDS)
                .setMaxConnTotal(100)
                .build();

        requestConfig = ApiContext.getInstance(DefaultBotOptions.class)
                .getRequestConfig();
    }

    @Override
    public String getBotToken() {
        return this.configuration.telegramToken();
    }
    
    public List<Update> getUpdates(int lastReceivedUpdate) {
        GetUpdates request = new GetUpdates();
        request.setLimit(100);
        request.setTimeout(1);
        request.setOffset(lastReceivedUpdate + 1);
        String url = ApiConstants.BASE_URL + getBotToken() + "/" + GetUpdates.PATH;
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("charset", StandardCharsets.UTF_8.name());
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                HttpEntity ht = response.getEntity();
                BufferedHttpEntity buf = new BufferedHttpEntity(ht);
                String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);
                List<Update> updates = request.deserializeResponse(responseContent);
                updates.removeIf(x -> x.getUpdateId() < lastReceivedUpdate);
                return updates;
            } catch (IOException | TelegramApiRequestException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (UnsupportedCharsetException | JsonProcessingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return new LinkedList<Update>();
    }
}
