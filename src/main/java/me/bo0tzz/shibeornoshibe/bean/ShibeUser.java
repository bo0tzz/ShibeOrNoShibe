package me.bo0tzz.shibeornoshibe.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import pro.zackpollard.telegrambot.api.user.User;

@Entity("user")
@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ShibeUser {

    @Id
    private final long UID;
    private final String username;
    private final boolean pingShibe;
    private final boolean pingDoggo;

    public static ShibeUser from(User user) {
        return builder().UID(user.getId()).username(user.getUsername()).build();
    }

    public ShibeUser updateUsername(String username) {
        return toBuilder().username(username).build();
    }

    public ShibeUser setPingShibe(boolean pingShibe) {
        return toBuilder().pingShibe(pingShibe).build();
    }

    public ShibeUser setPingDoggo(boolean pingDoggo) {
        return toBuilder().pingDoggo(pingDoggo).build();
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

}
