package me.bo0tzz.shibeornoshibe.bean;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import pro.zackpollard.telegrambot.api.chat.GroupChat;

import java.util.ArrayList;
import java.util.List;

@Entity("group")
public class ShibeGroup {

    @Id
    private final String groupID;
    @Embedded
    private List<ShibeUser> users;

    public ShibeGroup(GroupChat group, List<ShibeUser> users) {
        this.groupID = group.getId();
        this.users = users;
    }

    public ShibeGroup(GroupChat group) {
        this.groupID = group.getId();
        this.users = new ArrayList<>();
    }

    public void addUser(ShibeUser user) {
        users.add(user);
    }

    public List<ShibeUser> getUsers() {
        return users;
    }

    public String getGroupID() {
        return groupID;
    }
}
