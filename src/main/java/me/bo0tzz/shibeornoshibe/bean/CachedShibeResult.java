package me.bo0tzz.shibeornoshibe.bean;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("cache")
public class CachedShibeResult extends ShibeResult {

    @Id
    private final String fileID;

    public CachedShibeResult(String fileID, ShibeResult parent) {
        super(parent);
        this.fileID = fileID;
    }

}
