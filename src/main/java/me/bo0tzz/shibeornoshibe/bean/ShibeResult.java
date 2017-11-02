package me.bo0tzz.shibeornoshibe.bean;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.File;
import java.util.Map;

@Entity("cache")
public class ShibeResult {

    private static final String API_URI = "http://shiba.vil.so/";
    private boolean success;
    private Map<String, Float> prediction;
    @Id
    private String fileID;
    private Category category;

    public ShibeResult(boolean success, Map<String, Float> prediction) {
        this.success = success;
        this.prediction = prediction;
    }

    public ShibeResult(ShibeResult parent, String fileID) {
        this(parent);
        this.fileID = fileID;
    }

    public ShibeResult(ShibeResult from) {
        this.success = from.isSuccess();
        this.prediction = from.getPrediction();
    }

    public ShibeResult() {
        //Method to allow Morphia to instantiate class
    }

    public static ShibeResult nullResult() {
        return new ShibeResult(false,null);
    }

    public static ShibeResult shibeResult(File image, String fileID) {
        HttpResponse<String> response;

        try {
            response = Unirest.post(API_URI)
                    .field("image", image)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
            return nullResult();
        }

        if (response.getStatus() != 200) {
            System.out.println("Got incorrect response!\n" + response.getStatusText() + "\n" + response.getBody());
            return nullResult();
        }

        ShibeResult result =  new Gson().fromJson(response.getBody(), ShibeResult.class);
        result.fileID = fileID;
        return result;
    }

    public boolean isShibe() {
        return category == Category.SHIBE;
    }

    public boolean isDoggo() {
        return category == Category.DOGGO;
    }

    public Category getCategory() {
        if (category == null) {
            category = Category.fromPredictionMap(prediction);
        }
        return category;
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<String, Float> getPrediction() {
        return prediction;
    }

}
