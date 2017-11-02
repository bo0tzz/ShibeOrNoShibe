package me.bo0tzz.shibeornoshibe.db;

import com.mongodb.MongoClient;
import me.bo0tzz.shibeornoshibe.bean.CachedShibeResult;
import me.bo0tzz.shibeornoshibe.bean.ShibeGroup;
import me.bo0tzz.shibeornoshibe.bean.ShibeResult;
import me.bo0tzz.shibeornoshibe.bean.ShibeUser;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.GroupChat;

import java.util.List;
import java.util.stream.Collectors;

public class ShibeMorphia {

    private final Morphia morphia;
    private final Datastore datastore;

    public ShibeMorphia() {
        this.morphia = new Morphia();
        morphia.mapPackage("me.bo0tzz.shibeornoshibe.bean");
        MongoClient mongo = new MongoClient(System.getenv("MONGO_IP")); //if env var does not exist, default 'localhost' will be used
        this.datastore = morphia.createDatastore(mongo, "shibe");
        datastore.ensureIndexes();
    }

    public void cacheShibe(String fileID, ShibeResult result) {
        cacheShibe(new CachedShibeResult(fileID, result));
    }

    public void cacheShibe(CachedShibeResult result) {
        datastore.save(result);
    }

    public CachedShibeResult fromCache(String fileID) {
        return datastore.createQuery(CachedShibeResult.class)
                .field("_id").equal(fileID)
                .get();
    }

    public List<ShibeUser> getUsersToTag(Chat chat, String category) {
        ShibeGroup group = getShibeGroup(chat);
        if (group == null) return null;
        return group.getUsers().stream().filter(user -> user.pingFor(category)).collect(Collectors.toList());
    }

    public ShibeGroup getShibeGroup (Chat chat) {
        ShibeGroup group = datastore.createQuery(ShibeGroup.class)
                .field("groupID").equal(chat.getId())
                .get();

        if (group == null && chat instanceof GroupChat) {
            group = new ShibeGroup((GroupChat)chat);
        }

        return group;
    }

    public void saveShibeGroup(ShibeGroup group) {
        datastore.save(group);
    }

    public void updateUserName(long UID, String username) {
        Query<ShibeGroup> query = datastore.find(ShibeGroup.class)
                .field("users.UID").equal(UID);
        UpdateOperations<ShibeGroup> update = datastore.createUpdateOperations(ShibeGroup.class)
                .set("users.$.username", username);
        datastore.update(query, update);
    }

    public void updateUser(ShibeUser user) {
        Query<ShibeGroup> query = datastore.find(ShibeGroup.class)
                .field("users.UID").equal(user.getUID());
        UpdateOperations<ShibeGroup> update = datastore.createUpdateOperations(ShibeGroup.class)
                .set("users.$.username", user.getUsername())
                .set("users.$.pingShibe", user.isPingShibe())
                .set("users.$.pingDoggo", user.isPingDoggo());
        datastore.update(query, update);
    }
}
