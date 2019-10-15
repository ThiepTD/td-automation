package com.treasuredata.tdautomation.pluginautomation.td2td;

import com.treasuredata.tdautomation.util.CsvUtil;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.Listeners;

import static org.testng.Assert.assertTrue;

@Listeners(Td2TdOutput.class)

public class Td2tdOutputBasic extends Td2TdOutput {
    public static String SOURCE_EXPORTED_FILE = "pluginautomation/td2td/csv/source_%s.csv";
    public static String DESTINATION_EXPORTED_FILE = "pluginautomation/td2td/csv/target_%s.csv";
    public static String TARGET_TABLE = "target_table_%s";
    public static String SOURCE_TABLE = "source_table";

    public static String QUERY = "select myid__c, percent__c, double__c, checkbox__c, geolocation__c, date__c, datetime__c, time__c, currency__c, phone__c, email__c, text_long__c, integer__c, long__c, address__c from %s";
    public static String SECOND_QUERY = "select myid__c, percent__c, double__c, checkbox__c, geolocation__c, date__c, datetime__c, time__c, currency__c, phone__c, email__c, text_long__c, integer__c, long__c, address__c from %s";

    public static String DB_NAME = "_sfdc";
    public static String TARTGET_DB = "_td2td";
    public Td2tdOutputBasic(){
        this.setDb(DB_NAME)
                .setSourceData(SOURCE_EXPORTED_FILE)
                .setTargetData(DESTINATION_EXPORTED_FILE)
                .setTdTable(TARGET_TABLE)
                .setTdQuery(QUERY);
        this.setTargetEnv("dev").setTargetDb(TARTGET_DB).setSourceTable(SOURCE_TABLE).setSecondQuery(SECOND_QUERY);

        // add skipped tests for example purpose
/*
        switch (RUNNING_TEST_NAME){
            case "Td2Td-Replace":
                LOGGER.info("-------> Td2Td-Replace");
                this.addSkippedTest("abc");
                break;
            case "Td2Td-Append":
                LOGGER.info("-------> Td2Td-Append");
                this.addSkippedTest("xyz");
                break;
        }
*/
        LOGGER = LogManager.getLogger(Td2tdOutputBasic.class.getName());
    }

    @Override
    public void compareDataAssertion(Object result){
        switch (RUNNING_TEST_NAME){
            case "Td2Td-Replace":
                LOGGER.info("-------> Td2Td-Replace");
                assertTrue((boolean)result);
                break;
            default:
                LOGGER.info("-------> {}", RUNNING_TEST_NAME);
                assertTrue((boolean)result);
                break;
        }
    }

    @Override
    public void secondTimeCompareData(){
        boolean result;
        switch (RUNNING_TEST_NAME){
            case "Td2Td-Append":
                result = CsvUtil.compareCsv(TMP_CSV, sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
                compareDataAssertion(result);
                break;
            default:
                result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
                compareDataAssertion(result);
                break;
        }
    }

}


