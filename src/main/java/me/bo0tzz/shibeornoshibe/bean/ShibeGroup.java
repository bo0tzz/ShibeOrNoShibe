package me.bo0tzz.shibeornoshibe.bean;

import lombok.*;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import pro.zackpollard.telegrambot.api.chat.GroupChat;

import java.util.ArrayList;
import java.util.List;

@Entity("group")
@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ShibeGroup {

    @Id
    private final String groupID;

    @Reference
    @Singular
    private final List<ShibeUser> users;

    public static ShibeGroup from(GroupChat chat) {
        return builder().groupID(chat.getId()).users(new ArrayList<>()).build();
    }

    public ShibeGroup addUser(ShibeUser user) {
        return toBuilder().user(user).build();
    }
}
