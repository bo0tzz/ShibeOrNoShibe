package me.bo0tzz.shibeornoshibe.gson;

import com.google.gson.*;
import me.bo0tzz.shibeornoshibe.bean.Category;
import me.bo0tzz.shibeornoshibe.bean.ShibeResult;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ShibeResultDeserializer implements JsonDeserializer<ShibeResult> {

    @Override
    public ShibeResult deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject object = jsonElement.getAsJsonObject();
        boolean success = object.get("success").getAsBoolean();
        Map<String, Float> prediction = new HashMap<>();
        for (JsonElement o : object.get("prediction").getAsJsonArray()) {
            for (Map.Entry<String, JsonElement> e : o.getAsJsonObject().entrySet()) {
                prediction.put(
                        e.getKey(),
                        e.getValue().getAsFloat()
                );
            }
        }
        Category category = Category.fromPredictionMap(prediction);

        return ShibeResult.builder()
                .success(success)
                .prediction(prediction)
                .category(category)
                .build();

    }
}
