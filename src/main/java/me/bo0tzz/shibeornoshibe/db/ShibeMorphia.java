package me.bo0tzz.shibeornoshibe.db;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import me.bo0tzz.shibeornoshibe.bean.Category;
import me.bo0tzz.shibeornoshibe.bean.ShibeGroup;
import me.bo0tzz.shibeornoshibe.bean.ShibeResult;
import me.bo0tzz.shibeornoshibe.bean.ShibeUser;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
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
        cacheShibe(new ShibeResult(result, fileID));
    }

    public void cacheShibe(ShibeResult result) {
        datastore.save(result);
    }

    public ShibeResult fromCache(String fileID) {
        return datastore.createQuery(ShibeResult.class)
                .field("_id").equal(fileID)
                .get();
    }

    public List<ShibeUser> getUsersToTag(Chat chat, Category category) {
        ShibeGroup group = getShibeGroup(chat);
        if (group == null) return null;
        return group.getUsers().stream().filter(user -> user.pingFor(category)).collect(Collectors.toList());
    }

    public ShibeGroup getShibeGroup (Chat chat) {
        ShibeGroup group = datastore.createQuery(ShibeGroup.class)
                .field("_id").equal(chat.getId())
                .get();

        if (group == null && chat instanceof GroupChat) {
            group = new ShibeGroup((GroupChat)chat);
        }

        return group;
    }

    public boolean updateShibeGroup(ShibeGroup group) {
        Query<ShibeGroup> query = datastore.find(ShibeGroup.class)
                .field("_id").equal(group.getGroupID());
        UpdateOperations<ShibeGroup> update = datastore.createUpdateOperations(ShibeGroup.class)
                .set("users", group.getUsers());
        UpdateResults r = datastore.update(query, update);
        return r.getUpdatedCount() + r.getInsertedCount() > 0;
    }

    public void saveShibeGroup(ShibeGroup group) {
        datastore.save(group);
    }

    public boolean updateUserName(long UID, String username) {
        Query<ShibeGroup> query = datastore.find(ShibeGroup.class)
                .field("users").elemMatch(
                        datastore.createQuery(ShibeUser.class)
                        .field("_id").equal(UID));
        UpdateOperations<ShibeGroup> update = datastore.createUpdateOperations(ShibeGroup.class)
                .set("users.$.username", username);
        UpdateResults r = datastore.update(query, update);
        return r.getUpdatedCount() > 0;
    }

    public boolean updateUser(ShibeUser user) {
        Query<ShibeGroup> query = datastore.find(ShibeGroup.class)
                .field("users").equal(new BasicDBObject("UID", user.getUID()));
        UpdateOperations<ShibeGroup> update = datastore.createUpdateOperations(ShibeGroup.class)
                .set("users.$.username", user.getUsername())
                .set("users.$.pingShibe", user.isPingShibe())
                .set("users.$.pingDoggo", user.isPingDoggo());
        UpdateResults r = datastore.update(query, update);
        return r.getUpdatedCount() > 0;
    }
}
