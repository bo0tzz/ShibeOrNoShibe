package me.bo0tzz.shibeornoshibe.engine;

import me.bo0tzz.shibeornoshibe.ShibeOrNoShibe;
import me.bo0tzz.shibeornoshibe.bean.ShibeResult;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.PhotoMessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShibeListener implements Listener {
    private final ShibeOrNoShibe main;
    private final ShibeTester shibeTester;
    private final Map<String, ShibeResult> cachedResults;
    private final static String OUTPUT = "*Shiba*: %.2f%%\n*Doggo:* %.2f%%\n*Random*: %.2f%%";

    public ShibeListener(ShibeOrNoShibe main) {
        this.main = main;
        this.shibeTester = new ShibeTester();
        this.cachedResults = new HashMap<>();
    }

    public void onPhotoMessageReceived(PhotoMessageReceivedEvent event) {
        ShibeResult cached = cachedResults.get(event.getContent().getContent()[0].getFileId());
        if (cached != null) {
            HashMap<String, Float> prediction = cached.getPrediction();
            String out = String.format(OUTPUT,
                    prediction.get("shiba")*100,
                    prediction.get("doggo")*100,
                    prediction.get("random")*100);
            event.getChat().sendMessage(SendableTextMessage.markdown(out).build());
            return;
        }

        File image;
        try {
            image = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
            event.getContent().getContent()[0].downloadFile(main.getBot(),image);
        } catch (IOException e) {
            UUID uuid = UUID.randomUUID();
            System.out.println("Error happened: " + uuid);
            e.printStackTrace();
            event.getChat().sendMessage("Unfortunately something went wrong while processing your image. Please contact @bo0tzz and mention error code " + uuid);
            return;
        }

        ShibeResult confidence = shibeTester.shibeCertainty(image);
        if (confidence == null) {
            event.getChat().sendMessage("Something has gone wrong while checking if your image is a shibe! If this keeps happening, please contact @bo0tzz");
            return;
        }

        cachedResults.put(event.getContent().getContent()[0].getFileId(), confidence);

        HashMap<String, Float> prediction = confidence.getPrediction();
        String out = String.format(OUTPUT,
                prediction.get("shiba")*100,
                prediction.get("doggo")*100,
                prediction.get("random")*100);
        event.getChat().sendMessage(SendableTextMessage.markdown(out).build());
    }
}
