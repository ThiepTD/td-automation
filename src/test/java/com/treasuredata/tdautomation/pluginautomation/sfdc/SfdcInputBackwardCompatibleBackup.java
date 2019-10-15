package com.treasuredata.tdautomation.pluginautomation.sfdc;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/*
 * Instead of uploading data to Salesforce manually, we would like to do so via sfdc export plugin
 * The flow of this test case is used to verify sfdc input with the following steps:
 *  1 Assume that we have test data available in Salesforce already
 *  2 Use td connector:issue with the first yml file to import data from Salesforce to v1_table
 *  3 Use td connector:issue with the second yml file to import data from Salesforce to v2_table
 *  4 Export TD v1_table into output.csv
 *  5 Export TD v2_table into target.csv
 *  8 Compare data b/w output.csv and target.csv
 */

@Listeners(SfdcInputBackwardCompatibleBackup.class)

public class SfdcInputBackwardCompatibleBackup extends TestCaseBase {

    public static String ID = "id";
    public static String NAME = "name";
    public static String AGE = "age";
    public static String EMAIL = "email";
    public static String DATE = "date";
    public static String TIME = "mytime";
    public static String TD_SOURCE_TABLE = "v1_table";
    public static String TD_TARGET_TABLE = "v2_table";

    public static String YMLv1 = Constant.RESOURCE_PATH + "pluginautomation/sfdc/yml/loadv1.yml";
    public static String YMLv2 = Constant.RESOURCE_PATH + "pluginautomation/sfdc/yml/loadv2.yml";

    public static String CSV_SOURCE_TEST_DATA = String.format("%spluginautomation/sfdc/csv/output.csv", Constant.RESOURCE_PATH);
    public static String CSV_TARGET_TEST_DATA = String.format("%spluginautomation/sfdc/csv/target.csv", Constant.RESOURCE_PATH);

    public static String TD_QUERY_TABLE = String.format("select %s__c as %s, %s__c as %s, %s__c as %s, %s__c as %s, %s__c as %s, %s__c as %s from %s",
            ID, ID, NAME, NAME, EMAIL, EMAIL, AGE, AGE, DATE, DATE, TIME, TIME, "%s");

    @Parameters({"v1YmlFile", "v2YmlFile"})
    @Test
    public void initialize(@Optional("csv file") String v1YmlFile, @Optional("yml file") String v2YmlFile){
        LOGGER = LogManager.getLogger(SfdcInputBackwardCompatibleBackup.class.getName());
        TD_DB = TD_USER + "_sfdc";
        YMLv1 = (!v1YmlFile.contains(".yml")) ? YMLv2 : v1YmlFile;
        YMLv2 = (!v2YmlFile.contains(".yml")) ? YMLv2 : v2YmlFile;
        CommonSteps.createTable(TD_CONF, TD_DB, TD_SOURCE_TABLE);
        CommonSteps.createTable(TD_CONF, TD_DB, TD_TARGET_TABLE);
    }

    @Test(dependsOnMethods = {"initialize"}, retryAnalyzer = TestCaseBase.class)
    public void callDataTransferToTdV1() {
        String result = CommonSteps.transferDataToTd(TD_CONF, YMLv1, TD_DB, TD_SOURCE_TABLE);
        assertTrue(result.contains(": success"));
    }

    @Test(dependsOnMethods = {"initialize"}, retryAnalyzer = TestCaseBase.class)
    public void callDataTransferToTdV2() {
        String result = CommonSteps.transferDataToTd(TD_CONF, YMLv2, TD_DB, TD_TARGET_TABLE);
        assertTrue(result.contains(": success"));
    }

    @Test(dependsOnMethods = {"callDataTransferToTdV1", "callDataTransferToTdV2"}, retryAnalyzer = TestCaseBase.class)
    public void exportSourceDataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", CSV_SOURCE_TEST_DATA,
                String.format(TD_QUERY_TABLE, TD_SOURCE_TABLE));
        assertTrue(result.contains(": success"));
    }

    @Test(dependsOnMethods = {"exportSourceDataToCsv"}, retryAnalyzer = TestCaseBase.class)
    public void exportTargetDataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", CSV_TARGET_TEST_DATA,
                String.format(TD_QUERY_TABLE, TD_TARGET_TABLE));
        assertTrue(result.contains(": success"));
    }

    @Test(dependsOnMethods = {"exportTargetDataToCsv"})
    public void compareData() {
        boolean result = CsvUtil.compareCsv(CSV_SOURCE_TEST_DATA, CSV_TARGET_TEST_DATA, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        assertTrue(result);
    }

    @Test(dependsOnMethods = {"compareData"})
    public void testGetRecordCount(){
        LOGGER.info("------------- Start running testGetRecordCount -------------");
        String query = "select count(name__c) from v1_table";
        int result = CommonSteps.getRecordCount(TD_CONF, TD_DB, null, query);
        assertTrue(result >= 0);
    }
}


