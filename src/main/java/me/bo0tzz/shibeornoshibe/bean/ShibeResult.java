package me.bo0tzz.shibeornoshibe.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Builder;
import lombok.Value;
import me.bo0tzz.shibeornoshibe.gson.ShibeResultDeserializer;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.File;
import java.util.Map;

@Entity("cache")
@Builder(toBuilder = true)
@Value
public class ShibeResult {

    private static final String API_URI = "http://shiba.vil.so/";
    private static final Gson gson;
    private final boolean success;
    private final Map<String, Float> prediction;
    @Id
    private final String fileID;
    private final Category category;

    static {
        gson = new GsonBuilder().registerTypeAdapter(ShibeResult.class, new ShibeResultDeserializer()).create();
    }

    public static ShibeResult from(File image, String fileID) {
        HttpResponse<String> response;

        try {
            response = Unirest.post(API_URI)
                    .field("image", image)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }

        if (response.getStatus() != 200) {
            System.out.println("Got incorrect response!\n" + response.getStatusText() + "\n" + response.getBody());
            return null;
        }

        ShibeResult result =  gson.fromJson(response.getBody(), ShibeResult.class);
        return result.with(fileID);
    }

    public ShibeResult with(String fileID) {
        return toBuilder().fileID(fileID).build();
    }

    public boolean isShibe() {
        return category == Category.SHIBE;
    }

    public boolean isDoggo() {
        return category == Category.DOGGO;
    }

}
