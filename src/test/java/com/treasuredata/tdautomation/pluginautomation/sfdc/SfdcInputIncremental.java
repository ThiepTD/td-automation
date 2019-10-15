package com.treasuredata.tdautomation.pluginautomation.sfdc;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.apache.logging.log4j.LogManager;
import org.testng.ITestContext;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

/*
 * Instead of uploading data to Salesforce manually, we would like to do so via sfdc export plugin
 * The flow of this test case is used to verify sfdc input with the following steps:
 *  1 Create csv test data by calling Python code with an input.py as test data we want to generate
 *  2 Fresh import output.csv as our test data to TD source_table
 *  3 Export test data from TD source_table to Salesforce
 *  4 Create a connector session so that we can run incremental load with it
 *  5 Run above connector session to bring data from Salesforce to TD v2_table
 *  6 Export TD source_table into output.csv
 *  7 Export TD v2_table into target.csv
 *  8 Compare data b/w output.csv and target.csv
 *  9 Create test data second times into output.csv
 *  10 Import output.csv as our test data to TD source_table second time
 *  11 Export test data from TD source_table to Salesforce second time
 *  12 Run above connector session to bring data from Salesforce to TD v2_table second time
 *  13 Export TD source_table into output.csv second time
 *  14 Export TD v2_table into target.csv second time
 *  15 Compare data b/w output.csv and target.csv
 *  Incremental column is Name as it is defined in yml file
 */

@Listeners(SfdcInputIncremental.class)

public class SfdcInputIncremental extends TestCaseBase {

    public static String ID = "id";
    public static String NAME = "name";
    public static String AGE = "age";
    public static String EMAIL = "email";
    public static String DATE = "date";
    public static String TIME = "mytime";
    public static String TD_V2_TABLE = "v2_table";
    public static String TD_SOURCE_TABLE = "source_table";

    public static String YMLv2 = Constant.RESOURCE_PATH + "pluginautomation/sample/yml/incremental_loadv2.yml";

    public static String CSV_SOURCE_TEST_DATA = String.format("%spluginautomation/common/csv/output.csv", Constant.RESOURCE_PATH);
    public static String CSV_TARGET_TEST_DATA = String.format("%spluginautomation/common/csv/target.csv", Constant.RESOURCE_PATH);

    public static String EXPORT_URI = "sfdc://sfdc_test%40treasure-data.com:treasure123P8zjJIyM00X5X36xw3Qk0uTE9@login.salesforce.com/TestObject__c?mode=append";

    public static String QUERY_TARGET_TABLE = String.format("select %s__c, %s__c, %s__c, %s__c, TD_TIME_FORMAT(TD_TIME_PARSE(%s__c), 'yyyy-MM-dd') as %s__c, TD_TIME_FORMAT(TD_TIME_PARSE(%s__c), 'HH:mm:ss') as %s__c from %s",
            ID, NAME, EMAIL, AGE, DATE, DATE, TIME, TIME, TD_V2_TABLE);

    // when we upload test data to Salesforce column names always contain __c
    public static String QUERY_SOURCE_TABLE = String.format("select %s as %s__c, %s as %s__c, %s as %s__c, %s as %s__c, %s as %s__c, %s as %s__c from %s",
            ID, ID, NAME, NAME, EMAIL, EMAIL, AGE, AGE, DATE, DATE, TIME, TIME, TD_SOURCE_TABLE);

    public static String CONNECTOR_SESSION_V2 = "v2_hourly";

    @Parameters("env")
    @BeforeSuite
    public void beforeSuite(@Optional("null") String env) {
        LOGGER = LogManager.getLogger(SfdcInputIncremental.class.getName());
        LOGGER.info("---------------- Before Suite -------------");
        DELAY_TIME = 15;
        RETRY_LIMIT = 3;
        TD_CONF = (env == null) ? Constant.RESOURCE_PATH + "pluginautomation/configuration/td.conf" : env;
        getTdUser();
        TD_DB = TD_USER + "_sfdc";

        // number of row: 10, delimiter: ",", number of column: 5, headers, headers' types
        PYTHON_INPUT = String.format(PYTHON_INPUT, 5, ",", 6,
                                        String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", ID, NAME, EMAIL, AGE, DATE, TIME),
                                        String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", "id", "name", "email", "number,1,100", "date,2015-01-01,2018-01-01", "time"));

    }

// ---------------------            First time data transfer        ---------------------
    @Test(retryAnalyzer = TestCaseBase.class)
    public void firstCreateTestData() {
        String result = createTestData(String.format("%spluginautomation/common/csv/", Constant.RESOURCE_PATH));
        createTestDataAssertion(result);
    }

    @Test(dependsOnMethods = {"firstCreateTestData"}, retryAnalyzer = TestCaseBase.class)
    public void firstImportTestDataToTd() {
        String result = CommonSteps.freshImportTestDataToTd(TD_CONF, TD_DB, TD_SOURCE_TABLE, "csv", CSV_SOURCE_TEST_DATA, 0, 0);
        importTestDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"firstImportTestDataToTd"}, retryAnalyzer = TestCaseBase.class)
    public void firstExportDataToSalesforce() {
        String result = CommonSteps.exportTdDataTo3rdParty(TD_CONF, TD_DB, "presto", EXPORT_URI, QUERY_SOURCE_TABLE);
        exportDataFromTdAssertion(result);
    }

    @Test(dependsOnMethods = {"firstExportDataToSalesforce"}, retryAnalyzer = TestCaseBase.class)
    public void createConnectorSessionV2() {
        CommonSteps.createTable(TD_CONF, TD_DB, TD_V2_TABLE);
        String result = CommonSteps.createConnectionSession(CONNECTOR_SESSION_V2, TD_CONF, null, TD_V2_TABLE, YMLv2);
        createConnectorSessionAssertion(result);
    }

    @Test(dependsOnMethods = {"createConnectorSessionV2"}, retryAnalyzer = TestCaseBase.class)
    public void firstCallDataTransferToTdV2() {
        String result = CommonSteps.runConnectionSession(TD_CONF, CONNECTOR_SESSION_V2);
        transferDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"firstCallDataTransferToTdV2"}, retryAnalyzer = TestCaseBase.class)
    public void firstExportSourceDataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", CSV_SOURCE_TEST_DATA, QUERY_SOURCE_TABLE);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"firstExportSourceDataToCsv"}, retryAnalyzer = TestCaseBase.class)
    public void firstExportTargetDataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", CSV_TARGET_TEST_DATA, QUERY_TARGET_TABLE);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"firstExportTargetDataToCsv"})
    public void firstCompareData() {
        boolean result = CsvUtil.compareCsv(CSV_SOURCE_TEST_DATA, CSV_TARGET_TEST_DATA, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        compareDataAssertion(result);
    }

// ---------------------            Second time data transfer        ---------------------

    @Test(dependsOnMethods = {"firstCompareData"}, retryAnalyzer = TestCaseBase.class)
    public void secondCreateTestData() {
        String result = createTestData(String.format("%spluginautomation/common/csv/", Constant.RESOURCE_PATH));
        createTestDataAssertion(result);
    }

    @Test(dependsOnMethods = {"secondCreateTestData"}, retryAnalyzer = TestCaseBase.class)
    public void secondImportTestDataToTd() {
        String result = CommonSteps.importTestDataToTd(TD_CONF, CSV_SOURCE_TEST_DATA, TD_DB, TD_SOURCE_TABLE, "csv", 0, 0);
        importTestDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"secondImportTestDataToTd"}, retryAnalyzer = TestCaseBase.class)
    public void secondExportDataToSalesforce() {
        QUERY_SOURCE_TABLE = String.format(QUERY_SOURCE_TABLE + " where time >= %d", CURRENT_UNIX_TIMESTAMP);
        String result = CommonSteps.exportTdDataTo3rdParty(TD_CONF, TD_DB, "presto", EXPORT_URI, QUERY_SOURCE_TABLE);
        exportDataFromTdAssertion(result);
    }

    @Test(dependsOnMethods = {"secondExportDataToSalesforce"}, retryAnalyzer = TestCaseBase.class)
    public void secondCallDataTransferToTdV2() {
        String result = CommonSteps.runConnectionSession(TD_CONF, CONNECTOR_SESSION_V2);
        transferDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"secondCallDataTransferToTdV2"}, retryAnalyzer = TestCaseBase.class)
    public void secondExportSourceDataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", CSV_SOURCE_TEST_DATA, QUERY_SOURCE_TABLE);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"secondExportSourceDataToCsv"}, retryAnalyzer = TestCaseBase.class)
    public void secondExportTargetDataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", CSV_TARGET_TEST_DATA, QUERY_TARGET_TABLE);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"secondExportTargetDataToCsv"})
    public void secondCompareData() {
        boolean result = CsvUtil.compareCsv(CSV_SOURCE_TEST_DATA, CSV_TARGET_TEST_DATA, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }

    @Override
    public void onFinish(ITestContext Result){
        LOGGER.info("---------------- ON FINISH -------------");
        CommonSteps.deleteConnectionSession(TD_CONF, CONNECTOR_SESSION_V2);
    }
}


