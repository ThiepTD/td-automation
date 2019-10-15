package com.treasuredata.tdautomation.pluginautomation.sfdc;

import com.treasuredata.tdautomation.pluginautomation.commontestscenarios.InputPluginBackwardCompatible;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

/*
 * Extended from InputPluginBackwardCompatible since it has the same scenario
 * Just need to define parameters: 2 csv output files, 2 yml file2, db and 2 table names, query
 */

@Listeners(InputPluginBackwardCompatible.class)

public class SfdcInputBackwardCompatible extends InputPluginBackwardCompatible {
    public static String NAME = "load";

    public static String V1_YML_FILE = String.format("pluginautomation/sfdc/yml/v1%s.yml", NAME);
    public static String V2_YML_FILE = String.format("pluginautomation/sfdc/yml/v2%s.yml", NAME);
    public static String V1_EXPORTED_FILE = String.format("pluginautomation/sfdc/csv/v1%s.csv", NAME);
    public static String V2_EXPORTED_FILE = String.format("pluginautomation/sfdc/csv/v2%s.csv", NAME);
    public static String V1_TABLE = "v1_table";
    public static String V2_TABLE = "v2_table";

    public static String QUERY = "select myid__c, percent__c, double__c, checkbox__c, geolocation__c, date__c, datetime__c, time__c, currency__c, phone__c, email__c, text_long__c, integer__c, long__c, address__c from %s";

    public static String DB_NAME = "_sfdc";
    public SfdcInputBackwardCompatible(){
        this.setDb(DB_NAME)
                .setSourceData(V1_EXPORTED_FILE)
                .setTargetData(V2_EXPORTED_FILE)
                .setTdTable(V1_TABLE)
                .setYml(V1_YML_FILE)
                .setTdQuery(QUERY);
        this.setV2Yml(V2_YML_FILE).setV2Table(V2_TABLE);

        // add skipped tests for example purpose
/*
        switch (RUNNING_TEST_NAME){
            case "SfdcInputLastRecord":
                LOGGER.info("-------> SfdcInputLastRecord");
                this.addSkippedTest("abc");
                break;
            case "SfdcInputDeletedRecord":
                LOGGER.info("-------> SfdcInputDeletedRecord");
                this.addSkippedTest("xyz");
                break;
        }
*/
        LOGGER = LogManager.getLogger(SfdcInputBackwardCompatible.class.getName());
    }

    // For demo/sharing purpose
/*
    @Override
    public void transferDataToTdAssertion(Object result){
        switch (RUNNING_TEST_NAME){
            case "SfdcInputLastRecord":
                LOGGER.info("-------> SfdcInputLastRecord");
                assertTrue(result.toString().contains(": success"));
                break;
            case "SfdcInputDeletedRecord":
                LOGGER.info("-------> SfdcInputDeletedRecord");
                assertTrue(result.toString().contains(": success"));
                break;
            case "SfdcInputUnsupportedKeyword":
                LOGGER.info("-------> SfdcInputUnsupportedKeyword");
                assertTrue(result.toString().contains(": success"));
                break;
            case "SfdcInputUnsupportedObject":
                LOGGER.info("-------> SfdcInputUnsupportedObject");
                assertTrue(result.toString().contains(": success"));
                break;
            default:
                LOGGER.info("-------> SfdcInputStandardObject");
                assertTrue(result.toString().contains(": success"));
                break;
        }
    }
*/
}


