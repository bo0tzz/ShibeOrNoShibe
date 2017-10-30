package me.bo0tzz.shibeornoshibe;

import lombok.Data;

import java.util.HashMap;

@Data
public class ShibeResult {

    private final boolean success;
    private final HashMap<String, Float> prediction;

    public static ShibeResult nullResult() {
        return new ShibeResult(false, null);
    }

}
