package com.treasuredata.tdautomation.pluginautomation.sfdc;

import com.treasuredata.tdautomation.pluginautomation.commontestscenarios.InputPluginBackwardCompatible;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.Listeners;

/*
 * Extended from InputPluginBackwardCompatible since it has the same scenario
 * Just need to define parameters: 2 csv output files, 2 yml file2, db and 2 table names, query
 */

@Listeners(InputPluginBackwardCompatible.class)

public class SfdcInputBackwardCompatibleDeletedRecord extends InputPluginBackwardCompatible {
    public static String NAME = "deletedRecord";

    public static String V1_YML_FILE = String.format("pluginautomation/sfdc/yml/v1%s.yml", NAME);
    public static String V2_YML_FILE = String.format("pluginautomation/sfdc/yml/v2%s.yml", NAME);
    public static String V1_EXPORTED_FILE = String.format("pluginautomation/sfdc/csv/v1%s.csv", NAME);
    public static String V2_EXPORTED_FILE = String.format("pluginautomation/sfdc/csv/v2%s.csv", NAME);
    public static String V1_TABLE = "v1_table_1";
    public static String V2_TABLE = "v2_table_2";

    public static String QUERY = "select myid__c, percent__c, double__c, checkbox__c, date__c, datetime__c, time__c, currency__c, phone__c, email__c, text_long__c, integer__c, long__c, address__c from %s";

    public static String DB_NAME = "_sfdc";
    public SfdcInputBackwardCompatibleDeletedRecord(){
        this.setYml(V1_YML_FILE)
                .setSourceData(V1_EXPORTED_FILE)
                .setTargetData(V2_EXPORTED_FILE)
                .setTdTable(V1_TABLE)
                .setTdQuery(QUERY)
                .setDb(DB_NAME);
        this.setV2Table(V2_TABLE).setV2Yml(V2_YML_FILE);
        LOGGER = LogManager.getLogger(SfdcInputBackwardCompatibleDeletedRecord.class.getName());
    }
}


