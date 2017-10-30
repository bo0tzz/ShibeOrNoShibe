package me.bo0tzz.shibeornoshibe;

import com.google.gson.Gson;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class ShibeTester {

    private static final String API_URI = "https://shiba.vil.so/";
    private final HttpClient httpClient;

    public ShibeTester() {
        httpClient = HttpClient.newHttpClient();
    }

    public ShibeResult shibeCertainty(InputStream image) {
        HttpResponse<String> response;
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(API_URI))
                    .POST(HttpRequest.BodyProcessor.fromInputStream(() -> image))
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandler.asString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return ShibeResult.nullResult();
        }
        ShibeResult results = new Gson().fromJson(response.body(), ShibeResult.class);
        return results;
    }

}
