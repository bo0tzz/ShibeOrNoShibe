package me.bo0tzz.shibeornoshibe;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ShibeTester {

    private static final String API_URI = "http://shiba.vil.so/";

    public ShibeTester() {
    }

    public ShibeResult shibeCertainty(InputStream image) {
        HttpResponse<String> response;

        try {
            File tmp = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
            Files.copy(image, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            response = Unirest.post(API_URI)
                    .field("image", tmp)
                    .asString();
        } catch (UnirestException|IOException e) {
            e.printStackTrace();
            return ShibeResult.nullResult();
        }

        System.out.println("Printing body " + response.getBody());
        ShibeResult results = new Gson().fromJson(response.getBody(), ShibeResult.class);
        System.out.println("Printing results: \n" + results);
        return results;
    }

}
