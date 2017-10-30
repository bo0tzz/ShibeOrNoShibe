package me.bo0tzz.shibeornoshibe;

import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.PhotoMessageReceivedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public class ShibeListener implements Listener {
    private final ShibeOrNoShibe main;
    private final ShibeTester shibeTester;
    private final static String OUTPUT = "*Shiba*: %.2f%%\n*Doggo:* %.2f%%\n*Random*: %.2f%%";

    public ShibeListener(ShibeOrNoShibe main) {
        this.main = main;
        this.shibeTester = new ShibeTester();
    }

    public void onPhotoMessageReceived(PhotoMessageReceivedEvent event) {
        String url = event.getContent().getContent()[0].getFileDownloadLink(main.getBot());
        InputStream photo;
        try {
            photo = new URL(url).openStream();
        } catch (IOException e) {
            UUID uuid = UUID.randomUUID();
            System.out.println("Error happened: " + uuid);
            e.printStackTrace();
            event.getChat().sendMessage("Unfortunately something went wrong while processing your image. Please contact @bo0tzz and mention error code " + uuid);
            return;
        }

        ShibeResult confidence = shibeTester.shibeCertainty(photo);
        if (confidence == null) {
            event.getChat().sendMessage("Something has gone wrong while checking if your image is a shibe! If this keeps happening, please contact @bo0tzz");
            return;
        }
        HashMap<String, Float> prediction = confidence.getPrediction();
        String out = String.format(OUTPUT,
                prediction.get("shiba")*100,
                prediction.get("doggo")*100,
                prediction.get("random")*100);
        event.getChat().sendMessage(SendableTextMessage.markdown(out).build());
    }
}
