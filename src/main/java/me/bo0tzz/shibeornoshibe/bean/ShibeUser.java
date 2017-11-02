package me.bo0tzz.shibeornoshibe.bean;

import org.mongodb.morphia.annotations.Id;
import pro.zackpollard.telegrambot.api.user.User;

public class ShibeUser {

    @Id
    private final long UID;
    private String username;
    private boolean pingShibe;
    private boolean pingDoggo;

    public long getUID() {
        return UID;
    }

    public String getUsername() {
        return username;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public boolean isPingShibe() {
        return pingShibe;
    }

    public boolean isPingDoggo() {
        return pingDoggo;
    }

    public void setPingShibe(boolean pingShibe) {
        this.pingShibe = pingShibe;
    }

    public void setPingDoggo(boolean pingDoggo) {
        this.pingDoggo = pingDoggo;
    }

    public boolean pingFor(Category c) {
        switch (c) {
            case DOGGO:
                return  pingDoggo;
            case SHIBE:
                return pingShibe;
            default:
                return false;
        }
    }

    public ShibeUser(User user, boolean pingShibe, boolean pingDoggo) {
        this.UID = user.getId();
        this.username = user.getUsername();
        this.pingShibe = pingShibe;
        this.pingDoggo = pingDoggo;
    }

}
