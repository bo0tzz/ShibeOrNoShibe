package me.bo0tzz.shibeornoshibe.bean;

import java.util.Map;

public enum Category {

    SHIBE("shiba"),
    DOGGO("doggo"),
    RANDOM("random");

    public String predictionMapping() {
        return predictionMapping;
    }

    private final String predictionMapping;

    Category(String predictionMapping) {
        this.predictionMapping = predictionMapping;
    }

    public static Category fromPredictionMap(Map<String, Float> predictionMap) {
        String category = null;
        float maxVal = 0f;
        for (Map.Entry<String, Float> e : predictionMap.entrySet()) {
            if (e.getValue() > maxVal) {
                maxVal = e.getValue();
                category = e.getKey();
            }
        }
        return fromString(category);
    }

    public static Category fromString(String s) {
        if (s == null) return null;
        for (Category c : Category.values()) {
            if (c.predictionMapping().equals(s)) {
                return c;
            }
        }
        return null;
    }
}
