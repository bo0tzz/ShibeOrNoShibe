package me.bo0tzz.shibeornoshibe;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


import java.io.*;

public class ShibeTester {

    private static final String API_URI = "http://shiba.vil.so/";

    public ShibeTester() {
    }

    public ShibeResult shibeCertainty(File image) {
        HttpResponse<String> response;

        try {
            response = Unirest.post(API_URI)
                    .field("image", image)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
            return ShibeResult.nullResult();
        }

        if (response.getStatus() != 200) {
            System.out.println("Got incorrect response!\n" + response.getStatusText() + "\n" + response.getBody());
            return ShibeResult.nullResult();
        }

        ShibeResult results = new Gson().fromJson(response.getBody(), ShibeResult.class);
        return results;
    }

}
