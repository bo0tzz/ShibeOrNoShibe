package me.bo0tzz.shibeornoshibe.db;

import com.mongodb.MongoClient;
import me.bo0tzz.shibeornoshibe.bean.CachedShibeResult;
import me.bo0tzz.shibeornoshibe.bean.ShibeResult;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

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

}
