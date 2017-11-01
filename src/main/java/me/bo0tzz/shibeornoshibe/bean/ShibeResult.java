package me.bo0tzz.shibeornoshibe.bean;

import java.util.HashMap;

public class ShibeResult {

    private final boolean success;
    private final HashMap<String, Float> prediction;

    public ShibeResult(boolean success, HashMap<String, Float> prediction) {
        this.success = success;
        this.prediction = prediction;
    }

    public ShibeResult(ShibeResult from) {
        this.success = from.isSuccess();
        this.prediction = from.getPrediction();
    }

    public boolean isSuccess() {
        return success;
    }

    public HashMap<String, Float> getPrediction() {
        return prediction;
    }

    public static ShibeResult nullResult() {
        return new ShibeResult(false,null);
    }

}
