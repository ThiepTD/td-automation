package com.treasuredata.tdautomation.util;

import java.util.Map;

public class Constant {

    public static final String PATH = System.getProperty("user.dir");
    public static final String RESOURCE_PATH = PATH + "/src/main/resources/";
    public static final String PYTHON_MAIN = String.format("%s/src/main/python/data_generator/main.py", PATH);
    public static final String TD_CONF_FOLDER = System.getProperty("user.home") + "/.td/";
    public static final String DEV = "dev";
    public static final String DEV_EU01 = "dev-eu01";
    public static final String STAG = "stag";
    public static final String PROD = "prod";
    public static final String TD_USERNAME = "user";
    public static final String TD_APIKEY = "apikey";
    public static final String TD_ENDPOINT = "endpoint";
    public static Map<String, String> ENV = System.getenv();
}
