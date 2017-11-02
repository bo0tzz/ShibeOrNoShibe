package me.bo0tzz.shibeornoshibe.db;

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
        morphia.getMapper().getOptions().setObjectFactory(new CustomMorphiaObjectFactory());
        morphia.mapPackage("me.bo0tzz.shibeornoshibe.bean");
        MongoClient mongo = new MongoClient(System.getenv("MONGO_IP")); //if env var does not exist, default 'localhost' will be used
        this.datastore = morphia.createDatastore(mongo, "shibe");
        datastore.ensureIndexes();
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
        ShibeGroup group = getShibeGroup(chat.getId());

        if (group == null && chat instanceof GroupChat) {
            group = ShibeGroup.from((GroupChat)chat);
        }

        return group;
    }

    public ShibeGroup getShibeGroup(String chatID) {
        return datastore.createQuery(ShibeGroup.class)
                .field("_id").equal(chatID)
                .get();
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

    public ShibeUser getShibeUser(long UID) {
        return datastore.find(ShibeUser.class)
                .field("_id").equal(UID).get();
    }

    public void saveShibeUser(ShibeUser user) {
        datastore.save(user);
    }

    public boolean updateUserName(long UID, String username) {
        Query<ShibeUser> query = datastore.find(ShibeUser.class)
                .field("_id").equal(UID);

        UpdateOperations<ShibeUser> update = datastore.createUpdateOperations(ShibeUser.class)
                .set("username", username);

        UpdateResults r = datastore.update(query, update);
        return r.getUpdatedCount() > 0;
    }

    public boolean updateUser(ShibeUser user) {
        Query<ShibeUser> query = datastore.find(ShibeUser.class)
                .field("_id").equal(user.getUID());
        UpdateOperations<ShibeUser> update = datastore.createUpdateOperations(ShibeUser.class)
                .set("username", user.getUsername())
                .set("pingShibe", user.isPingShibe())
                .set("pingDoggo", user.isPingDoggo());
        UpdateResults r = datastore.update(query, update);
        return r.getUpdatedCount() > 0;
    }
}
