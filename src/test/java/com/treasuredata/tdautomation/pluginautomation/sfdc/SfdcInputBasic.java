package com.treasuredata.tdautomation.pluginautomation.sfdc;

import com.treasuredata.tdautomation.pluginautomation.commontestscenarios.BasicInputPlugin;
import org.apache.logging.log4j.LogManager;
import org.testng.SkipException;
import org.testng.annotations.Listeners;

/*
 * Extended from BasicInputPlugin since it has the same scenario
 * Just need to define parameters: csvExpected file, yml file, db and target table names, query
 */

@Listeners(BasicInputPlugin.class)

public class SfdcInputBasic extends BasicInputPlugin {

    public SfdcInputBasic(){
        this.setDb("_sfdc")
                .setSourceData("pluginautomation/sfdc/csv/basic.csv")
                .setTargetData("pluginautomation/sfdc/csv/target.csv")
                .setTdTable("v2_table")
                .setYml("pluginautomation/sfdc/yml/replaceMode.yml")
                .addSkippedTest("exportDataFromSourceTd")
                .setTdQuery(String.format("select %s__c as %s, %s__c as %s, %s__c as %s, %s__c as %s, %s__c as %s, TD_TIME_FORMAT(TD_TIME_PARSE(%s__c), 'HH:mm:ss') as %s from %s",
                        "id", "id", "name", "name", "email", "email", "age", "age", "date", "date", "mytime", "mytime", "v2_table"));

        LOGGER = LogManager.getLogger(SfdcInputBasic.class.getName());
    }

    @Override
    public void exportDataFromTdToCsv(){
        throw new SkipException("This step has been skipped");
    }
}


