package com.treasuredata.tdautomation.pluginautomation.sfdc;

import com.treasuredata.tdautomation.pluginautomation.commontestscenarios.InputPluginImportTwice;
import com.treasuredata.tdautomation.util.Constant;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.Listeners;

/*
 * Extended from BasicInputPlugin since it has the same scenario
 * Just need to define parameters: csvExpected file, yml file, db and target table names, query
 */

@Listeners(InputPluginImportTwice.class)

public class SfdcInputImportTwice extends InputPluginImportTwice {

    public SfdcInputImportTwice(){
        super(String.format("%spluginautomation/sfdc/yml/replaceMode.yml", Constant.RESOURCE_PATH),
                String.format("%spluginautomation/sfdc/csv/target.csv", Constant.RESOURCE_PATH),
                String.format("%spluginautomation/sfdc/csv/replaceMode.csv", Constant.RESOURCE_PATH),
                "_sfdc", "v2_table",
                String.format("select %s__c as %s, %s__c as %s, %s__c as %s, %s__c as %s, %s__c as %s, TD_TIME_FORMAT(TD_TIME_PARSE(%s__c), 'HH:mm:ss') as %s from %s",
                        "id", "id", "name", "name", "email", "email", "age", "age", "date", "date", "mytime", "mytime", "v2_table"));
        LOGGER = LogManager.getLogger(SfdcInputImportTwice.class.getName());
    }
}


