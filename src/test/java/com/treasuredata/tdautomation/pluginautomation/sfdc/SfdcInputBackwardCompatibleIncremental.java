package com.treasuredata.tdautomation.pluginautomation.sfdc;

import com.treasuredata.tdautomation.pluginautomation.commontestscenarios.InputPluginBackwardCompatible;
import com.treasuredata.tdautomation.util.Constant;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.Listeners;

/*
 * Extended from InputPluginBackwardCompatible since it has the same scenario
 * Just need to define parameters: 2 csv output files, 2 yml file2, db and 2 table names, query
 */

@Listeners(InputPluginBackwardCompatible.class)

public class SfdcInputBackwardCompatibleIncremental extends InputPluginBackwardCompatible {
    public static String NAME = "Incremental";

    public static String V1_YML_FILE = String.format("%spluginautomation/sfdc/yml/v1%s.yml", Constant.RESOURCE_PATH, NAME);
    public static String V2_YML_FILE = String.format("%spluginautomation/sfdc/yml/v2%s.yml", Constant.RESOURCE_PATH, NAME);
    public static String V1_EXPORTED_FILE = String.format("%spluginautomation/sfdc/csv/v1%s.csv", Constant.RESOURCE_PATH, NAME);
    public static String V2_EXPORTED_FILE = String.format("%spluginautomation/sfdc/csv/v2%s.csv", Constant.RESOURCE_PATH, NAME);
    public static String V1_TABLE = "v1_table";
    public static String V2_TABLE = "v2_table";

    public static String QUERY = "select myid__c, percent__c, double__c, checkbox__c, date__c, datetime__c, time__c, currency__c, phone__c, email__c, text_long__c, integer__c, long__c, address__c from %s";

    public static String DB_NAME = "_sfdc";
    public SfdcInputBackwardCompatibleIncremental(){
        super(V1_YML_FILE, V2_YML_FILE, V1_EXPORTED_FILE, V2_EXPORTED_FILE, DB_NAME, V1_TABLE, V2_TABLE, QUERY);
        LOGGER = LogManager.getLogger(SfdcInputBackwardCompatibleIncremental.class.getName());
    }
}


