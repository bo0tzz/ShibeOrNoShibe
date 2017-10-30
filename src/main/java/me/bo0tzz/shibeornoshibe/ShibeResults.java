package me.bo0tzz.shibeornoshibe;

import lombok.Data;

import java.util.HashMap;

@Data
public class ShibeResults {

    private final boolean success;
    private final HashMap<String, Float> prediction;

    public static ShibeResults nullResult() {
        return new ShibeResults(false, null);
    }

}
