package me.bo0tzz.shibeornoshibe.engine;

import me.bo0tzz.shibeornoshibe.ShibeOrNoShibe;
import me.bo0tzz.shibeornoshibe.bean.Category;
import me.bo0tzz.shibeornoshibe.bean.ShibeResult;
import me.bo0tzz.shibeornoshibe.bean.ShibeUser;
import me.bo0tzz.shibeornoshibe.db.ShibeMorphia;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageEditReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.PhotoMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShibeListener implements Listener {
    private final ShibeOrNoShibe main;
    private final ShibeMorphia morphia;
    private final static String OUTPUT_TAG = "A new %s image was sent! Tagging: %s";
    private final static String OUTPUT = "*Shiba*: %.2f%%\n*Doggo:* %.2f%%\n*Random*: %.2f%%";

    public ShibeListener(ShibeOrNoShibe main, ShibeMorphia morphia) {
        this.main = main;
        this.morphia = morphia;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("Received a message from chat " + event.getChat().getName());
        User user = event.getMessage().getSender();

        if (user != null) {
            morphia.updateUserName(user.getId(), user.getUsername());
            System.out.println("Updated DB with user " + user.getFullName());
        }
    }

    @Override
    public void onMessageEditReceived(MessageEditReceivedEvent event) {
        System.out.println("Received a message edit from chat " + event.getChat().getName());
        User user = event.getMessage().getSender();

        if (user != null) {
            morphia.updateUserName(user.getId(), user.getUsername());
            System.out.println("Updated DB with user " + user.getFullName());
        }
    }

    @Override
    public void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
        System.out.println("Received a join event from chat " + event.getChat().getName());
        User user = event.getParticipant();

        if (user != null) {
            morphia.updateUserName(user.getId(), user.getUsername());
            System.out.println("Updated DB with user " + user.getFullName());
        }
    }

    public void onPhotoMessageReceived(PhotoMessageReceivedEvent event) {
        System.out.println("Received a photo message from chat " + event.getChat().getName());
        ShibeResult confidence = morphia.fromCache(event.getContent().getContent()[0].getFileId());

        if (confidence == null) {
            System.out.println("Could not retrieve shibe from cache");
            File image;
            UUID uuid = UUID.randomUUID();
            try {
                image = File.createTempFile(uuid.toString(), ".tmp");
                event.getContent().getContent()[0].downloadFile(main.getBot(), image);
            } catch (IOException e) {
                System.out.println("Error happened: " + uuid);
                e.printStackTrace();
                event.getChat().sendMessage("Unfortunately something went wrong while processing your image. Please contact @bo0tzz and mention error code " + uuid);
                return;
            }
            confidence = ShibeResult.shibeResult(image, event.getContent().getContent()[0].getFileId());
            if (confidence == null) {
                event.getChat().sendMessage("Something has gone wrong while checking if your image is a shibe! If this keeps happening, please contact @bo0tzz");
                return;
            }
            morphia.cacheShibe(event.getContent().getContent()[0].getFileId(), confidence);
            System.out.println("Created and cached new shibe");
        }

        String out;

        System.out.println("Switching through chat types for " + event.getChat().getType());
        switch (event.getChat().getType()) {
            case GROUP:
            case SUPERGROUP:
                System.out.println("Chat is a group type!");
                if (confidence.getCategory() == Category.RANDOM) return;
                List<ShibeUser> users = morphia.getUsersToTag(event.getChat(), confidence.getCategory());
                if (users == null || users.isEmpty()) {
                    event.getChat().sendMessage(String.format(OUTPUT_TAG, confidence.getCategory().toString().toLowerCase(), "Nobody :("));
                    return;
                }
                StringJoiner joiner = new StringJoiner(" ", "", "");
                for (ShibeUser u : users) {
                    joiner.add(u.getUsername());
                }
                out = String.format(OUTPUT_TAG, confidence.getCategory().toString().toLowerCase(), joiner.toString());
                break;
            case PRIVATE:
            case CHANNEL:
                System.out.println("Chat is not a group type!");
                out = formatShibeOutput(OUTPUT, confidence.getPrediction());
                break;
            default:
                System.out.println("Chat was nothing!");
                return;
        }

        event.getChat().sendMessage(SendableTextMessage.markdown(out).build());
    }

    public String formatShibeOutput(String format, Map<String, Float> from) {
        return String.format(format,
                from.get("shiba")*100,
                from.get("doggo")*100,
                from.get("random")*100);
    }
}
