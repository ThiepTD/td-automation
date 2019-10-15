package com.treasuredata.tdautomation.restassured.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class Database {
    public static final String UPDATED_AT = "updated_at";
    public static final String NAME = "name";
    public static final String CREATED_AT = "created_at";
    public static final String COUNT = "count";
    public static final String ORGANIZATION = "organization";
    public static final String PERMISSION = "permission";
    public static final String DETECT_PROTECTED = "delete_protected";

    static Logger LOGGER = LogManager.getLogger(Database.class.getName());

    private HashMap<String, Object> db = new HashMap<String, Object>();

    public Database(){};

    public Database(HashMap<String, Object> hm){
        db.put(UPDATED_AT, hm.get(UPDATED_AT));
        db.put(NAME, hm.get(NAME));
        db.put(CREATED_AT, hm.get(CREATED_AT));
        db.put(COUNT, hm.get(COUNT));
        db.put(ORGANIZATION, hm.get(ORGANIZATION));
        db.put(PERMISSION, hm.get(PERMISSION));
        db.put(DETECT_PROTECTED, hm.get(DETECT_PROTECTED));
    }

    public boolean isEqual(Database targetDb){
        HashMap<String, Object> targetMap = targetDb.getDb();
        for(String key: db.keySet())
            if(!db.containsKey(key) || !db.get(key).equals(targetMap.get(key)))
                return false;
        return true;
    }

    public boolean contains(Database targetDb){
        HashMap<String, Object> targetMap = targetDb.getDb();
        for(String key: targetMap.keySet())
            if(!db.containsKey(key) || !db.get(key).equals(targetMap.get(key)))
                return false;
        return true;
    }

    public boolean attrContains(String keyName, Object value){
        if(db.containsKey(keyName) && db.get(keyName).equals(value))
            return true;
        return false;
    }

    public HashMap<String, Object> getDb() {
        return db;
    }

    public void put(String keyName, Object value){
        db.put(keyName, value);
    }

    public void print(){
        if (db == null) {
            LOGGER.info("No object returned !");
        }

        for(String key: db.keySet()) {
            Object value = db.get(key);
            if (value != null)
                LOGGER.info(String.format("%s: %s", key, value.toString()));
            else
                LOGGER.info(String.format("%s: null", key));
        }
    }
}
