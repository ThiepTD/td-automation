package com.treasuredata.tdautomation.pluginautomation.sfdc;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

/*
 * Instead of uploading data to Salesforce manually, we would like to do so via sfdc export plugin
 * The flow of this test case is used to verify sfdc input with the following steps:
 *  1 Create csv test data by calling Python code with an input.py as test data we want to generate
 *  2 Fresh import output.csv as our test data to TD from_csv
 *  3 Export test data from TD source_table to Salesforce
 *  5 Run td connector:issue with a yml file to bring data from Salesforce to TD from_3rd_party
 *  6 Export TD from_csv into output.csv
 *  7 Export TD from_3rd_party into target.csv
 *  8 Compare data b/w output.csv and target.csv
 */

@Listeners(E2eSample.class)

public class E2eSample extends TestCaseBase {

    public static String ID = "id";
    public static String NAME = "name";
    public static String AGE = "age";
    public static String EMAIL = "email";
    public static String DATE = "date";
    public static String TIME = "mytime";
    public static String TD_SOURCE_TABLE = "from_csv";
    public static String TD_TARGET_TABLE = "from_3rd_party";

    public static String CSV_SOURCE_TEST_DATA = String.format("%spluginautomation/common/csv/output.csv", Constant.RESOURCE_PATH);
    public static String CSV_TARGET_TEST_DATA = String.format("%spluginautomation/common/csv/target.csv", Constant.RESOURCE_PATH);
    public static String CSV_TEST_DATA_FOLDER = String.format("%spluginautomation/common/csv/", Constant.RESOURCE_PATH);

    public static String EXPORT_URI = "sfdc://sfdc_test%40treasure-data.com:treasure123P8zjJIyM00X5X36xw3Qk0uTE9@login.salesforce.com/TestObject__c?mode=append";

    public static String SF_TO_TD = "";

    public static String QUERY_TARGET_TABLE = String.format("select %s__c as %s, %s__c as %s, %s__c as %s, %s__c as %s, TD_TIME_FORMAT(TD_TIME_PARSE(%s__c), 'yyyy-MM-dd HH:mm:ss') as %s, TD_TIME_FORMAT(TD_TIME_PARSE(%s__c), 'HH:mm:ss') as %s from %s",
                                                            ID, ID, NAME, NAME, EMAIL, EMAIL, AGE, AGE, DATE, DATE, TIME, TIME, TD_TARGET_TABLE);

    public static String QUERY_SOURCE_TABLE = String.format("select %s as %s__c, %s as %s__c, %s as %s__c, %s as %s__c, %s as %s__c, %s as %s__c  from %s",
                                                            ID, ID, NAME, NAME, EMAIL, EMAIL, AGE, AGE, DATE, DATE, TIME, TIME, TD_SOURCE_TABLE);

    @Parameters("env")
    @BeforeSuite
    public void beforeSuite(@Optional("null") String env) {
        LOGGER = LogManager.getLogger(E2eSample.class.getName());
        LOGGER.info("---------------- Before Suite -------------");
        DELAY_TIME = 15;

        TD_CONF = (env == null) ? Constant.RESOURCE_PATH + "pluginautomation/configuration/td.conf" : env;
        getTdUser();
        TD_DB = TD_USER + "_sfdc";

        // number of row: 5, delimiter: ",", number of column: 6, headers, headers' types
        PYTHON_INPUT = String.format(PYTHON_INPUT, 5, ",", 6,
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", ID, NAME, EMAIL, AGE, DATE, TIME),
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", "id", "name", "email", "number,1,100", "date,2015-01-01,2018-01-01", "time"));

        //v2Table = String.format("%s_%s", v2Table, String.valueOf(util.getCurrentUnixTimeStamp()));
        //tdTargetTable = String.format("%s_%s", tdTargetTable, String.valueOf(util.getCurrentUnixTimeStamp()));
    }

    @Test
    public void createTestData() {
        String result = createTestData(CSV_TEST_DATA_FOLDER);
        assertTrue(result.contains("output.csv was created"));
    }

    // Create database if it is NOT exists
    // Delete table if it is exists
    // Create table
    // Call td query to import csv test data to Treasure data
    @Test(dependsOnMethods = {"createTestData"})
    public void freshImportTestDataToTd() {
        String result = CommonSteps.freshImportTestDataToTd(TD_CONF, TD_DB, TD_SOURCE_TABLE, "csv", CSV_SOURCE_TEST_DATA, 0, 0);
        assertTrue(result.contains(": SUCCESS"));
    }

    // Call td query (for a certain table in TD)with an URL to export data to Salesforce
    @Test(dependsOnMethods = {"freshImportTestDataToTd"}, retryAnalyzer = TestCaseBase.class)
    public void exportDataToSalesforce() {
        String result = CommonSteps.exportTdDataTo3rdParty(TD_CONF, TD_DB, "presto", EXPORT_URI, QUERY_SOURCE_TABLE);
        assertTrue(result.contains(": success"));
    }

    @Test(dependsOnMethods = {"exportDataToSalesforce"}, retryAnalyzer = TestCaseBase.class)
    public void callDataTransferToTd() {
        String result = CommonSteps.transferDataToTd(TD_CONF, Constant.RESOURCE_PATH + "pluginautomation/sample/yml/loadv2.yml", TD_DB, TD_TARGET_TABLE);
        assertTrue(result.contains(": success"));
    }

    @Test (dependsOnMethods = {"callDataTransferToTd"}, retryAnalyzer = TestCaseBase.class)
    public void exportSourceDataToCsv() {
        QUERY_SOURCE_TABLE = String.format("select %s, %s, %s, %s, TD_TIME_FORMAT(TD_TIME_PARSE(%s), 'yyyy-MM-dd HH:mm:ss') as %s, TD_TIME_FORMAT(TD_TIME_PARSE(%s), 'HH:mm:ss') as %s from %s", ID, NAME, EMAIL, AGE, DATE, DATE, TIME, TIME, TD_SOURCE_TABLE);
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", CSV_SOURCE_TEST_DATA, QUERY_SOURCE_TABLE);
        assertTrue(result.contains(": success"));
    }

    @Test (dependsOnMethods = {"exportSourceDataToCsv"}, retryAnalyzer = TestCaseBase.class)
    public void exportTargetDataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", CSV_TARGET_TEST_DATA, QUERY_TARGET_TABLE);
        assertTrue(result.contains(": success"));
    }

    @Test(dependsOnMethods = {"exportTargetDataToCsv"}, retryAnalyzer = TestCaseBase.class)
    public void compareData() {
        boolean result = CsvUtil.compareCsv(CSV_SOURCE_TEST_DATA, CSV_TARGET_TEST_DATA, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        assertTrue(result);
    }

    //@AfterSuite
    public void cleanup(){
        CommonSteps.deleteTable(TD_CONF, TD_DB, TD_SOURCE_TABLE);
        CommonSteps.deleteTable(TD_CONF, TD_DB, TD_TARGET_TABLE);
    }
}


