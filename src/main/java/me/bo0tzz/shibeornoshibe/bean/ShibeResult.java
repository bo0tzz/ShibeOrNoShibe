package me.bo0tzz.shibeornoshibe.bean;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.File;
import java.util.Map;

public class ShibeResult {

    private static final String API_URI = "http://shiba.vil.so/";
    private final boolean success;
    private final Map<String, Float> prediction;
    private String category = "none";

    public ShibeResult(boolean success, Map<String, Float> prediction) {
        this.success = success;
        this.prediction = prediction;
    }

    public ShibeResult(ShibeResult from) {
        this.success = from.isSuccess();
        this.prediction = from.getPrediction();
    }

    public static ShibeResult nullResult() {
        return new ShibeResult(false,null);
    }

    public static ShibeResult shibeResult(File image) {
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

        return new Gson().fromJson(response.getBody(), ShibeResult.class);
    }

    public boolean isShibe() {
        return category.equals("shiba");
    }

    public boolean isDoggo() {
        return category.equals("doggo");
    }

    public String getCategory() {
        if (category.equals("none")) {
            float maxVal = 0f;
            prediction.forEach((s, f) -> category = f > maxVal ? s : category);
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
