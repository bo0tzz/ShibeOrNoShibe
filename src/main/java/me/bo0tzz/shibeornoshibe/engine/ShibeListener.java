package me.bo0tzz.shibeornoshibe.engine;

import me.bo0tzz.shibeornoshibe.ShibeOrNoShibe;
import me.bo0tzz.shibeornoshibe.bean.Category;
import me.bo0tzz.shibeornoshibe.bean.ShibeGroup;
import me.bo0tzz.shibeornoshibe.bean.ShibeResult;
import me.bo0tzz.shibeornoshibe.bean.ShibeUser;
import me.bo0tzz.shibeornoshibe.db.ShibeMorphia;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageEditReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageEvent;
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
        updateDatabase(event.getChat(), event.getMessage().getSender());
    }

    @Override
    public void onMessageEditReceived(MessageEditReceivedEvent event) {
        updateDatabase(event.getChat(), event.getMessage().getSender());
    }

    @Override
    public void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
        updateDatabase(event.getChat(), event.getParticipant());
    }

    public void updateDatabase(Chat chat, User user) {
        if (user != null) {
            boolean success = morphia.updateUserName(user.getId(), user.getUsername());
            if (!success) {
                ShibeGroup g = morphia.getShibeGroup(chat);
                if (g == null) return; //I'm a retard
                g.addUser(new ShibeUser(user, false, false));
                morphia.updateShibeGroup(g);
            }
        }
    }

    public void onPhotoMessageReceived(PhotoMessageReceivedEvent event) {
        ShibeResult confidence = morphia.fromCache(event.getContent().getContent()[0].getFileId());

        if (confidence == null) {
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
        }

        String out;
        switch (event.getChat().getType()) {
            case GROUP:
            case SUPERGROUP:
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
                out = formatShibeOutput(OUTPUT, confidence.getPrediction());
                break;
            default:
                return;
        }

        event.getChat().sendMessage(SendableTextMessage.markdown(out).build());
        morphia.cacheShibe(event.getContent().getContent()[0].getFileId(), confidence);
        updateDatabase(event.getChat(), event.getMessage().getSender());

    }

    public String formatShibeOutput(String format, Map<String, Float> from) {
        return String.format(format,
                from.get("shiba")*100,
                from.get("doggo")*100,
                from.get("random")*100);
    }
}
