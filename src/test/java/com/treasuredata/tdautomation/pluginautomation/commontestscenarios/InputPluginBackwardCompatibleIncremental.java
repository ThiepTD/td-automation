package com.treasuredata.tdautomation.pluginautomation.commontestscenarios;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import org.testng.annotations.*;

/*
 * Extended from TestCaseBase
 * initialize method to get necessary parameters
 *  1 Transfer data from third party data platform to the first TD table based on the first defined yml file
 *  2 Transfer data from third party data platform to the second TD table based on the second defined yml file
 *  3 Export imported data from the first TD table to csv
 *  4 Export imported data from the second TD table to csv
 *  5 Do data comparison b/w the first and second csv files
 */

@Listeners(InputPluginBackwardCompatibleIncremental.class)

public class InputPluginBackwardCompatibleIncremental extends TestCaseBase {

    public String v2Table;
    public String v2Yml;
    public String v1ConnectionSession;
    public String v2ConnectionSession;

    public InputPluginBackwardCompatibleIncremental(){}

    // tdTargetTable now is used for SFDCv1 connector
    public InputPluginBackwardCompatibleIncremental(String v1Yml, String v2Yml, String v1TargetFile, String v2TargetFile, String database, String v1Table, String v2Table, String tdQuery){
        super(v1Yml, v1TargetFile, v2TargetFile, database, v1Table, tdQuery);
        this.v2Yml = v2Yml;
        this.v2Table = v2Table;
    }

    @Parameters({"v1YmlFile", "v2YmlFile"})
    @Test
    public void initialize(@Optional("first yml file") String v1YmlFile, @Optional("second yml file") String v2YmlFile){
        TD_DB = TD_USER + dbName;
        yml = (!v1YmlFile.contains(".yml")) ? yml : v1YmlFile;
        v2Yml = (!v2YmlFile.contains(".yml")) ? v2Yml : v2YmlFile;
        v1ConnectionSession = "v1Connection";
        v2ConnectionSession = "v2Connection";
        CommonSteps.createTable(TD_CONF, TD_DB, v2Table);
        CommonSteps.createTable(TD_CONF, TD_DB, tdTargetTable);
    }

    @Test(dependsOnMethods = {"initialize"}, retryAnalyzer = TestCaseBase.class)
    public void createV1ConnectionSession(){
        String result = CommonSteps.createConnectionSession(v1ConnectionSession, TD_CONF, null, tdTargetTable, yml);
        createConnectorSessionAssertion(result);
    }

    @Test(dependsOnMethods = {"initialize"}, retryAnalyzer = TestCaseBase.class)
    public void createV2ConnectionSession(){
        String result = CommonSteps.createConnectionSession(v2ConnectionSession, TD_CONF, null, v2Table, v2Yml);
        createConnectorSessionAssertion(result);
    }

    @Test(dependsOnMethods = {"createV1ConnectionSession"}, retryAnalyzer = TestCaseBase.class)
    public void transferDataToTdV1() {
        String result = CommonSteps.runConnectionSession(TD_CONF, v1ConnectionSession);
        transferDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"createV2ConnectionSession"}, retryAnalyzer = TestCaseBase.class)
    public void transferDataToTdV2() {
        String result = CommonSteps.runConnectionSession(TD_CONF, v2ConnectionSession);
        transferDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"transferDataToTdV1"}, retryAnalyzer = TestCaseBase.class)
    public void exportV1DataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", sourceTestData, tdTargetTable);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"transferDataToTdV2"}, retryAnalyzer = TestCaseBase.class)
    public void exportV2DataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", targetTestData, v2Table);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"exportV2DataToCsv","exportV1DataToCsv" })
    public void compareData() {
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        compareDataAssertion(result);
    }

    @Test(dependsOnMethods = {"compareData"})
    public void updateYmlFiles(){
        FileUtil.replaceLine(yml, "  where:", "  where: myid__c <= 10");
        FileUtil.replaceLine(v2Yml, "  where:", "  where: myid__c <= 10");
    }

    @Test(dependsOnMethods = {"updateYmlFiles"}, retryAnalyzer = TestCaseBase.class)
    public void secondTimeTransferDataToTdV1() {
        String result = CommonSteps.runConnectionSession(TD_CONF, v1ConnectionSession);
        transferDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"updateYmlFiles"}, retryAnalyzer = TestCaseBase.class)
    public void secondTimeTransferDataToTdV2() {
        String result = CommonSteps.runConnectionSession(TD_CONF, v2ConnectionSession);
        transferDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"transferDataToTdV1"}, retryAnalyzer = TestCaseBase.class)
    public void secondTimeExportV1DataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", sourceTestData, tdTargetTable);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"transferDataToTdV2"}, retryAnalyzer = TestCaseBase.class)
    public void secondTimeExportV2DataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", targetTestData, v2Table);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"secondTimeExportV2DataToCsv", "secondTimeExportV1DataToCsv"})
    public void secondTimeCompareData() {
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        compareDataAssertion(result);
    }

    @Override
    @AfterSuite
    public void afterSuite(){
        LOGGER.info("------------- Start running afterSuite -------------");

        // Get back to original yml files
        FileUtil.replaceLine(yml, "  where:", "  where: myid__c <= 5");
        FileUtil.replaceLine(v2Yml, "  where:", "  where: myid__c <= 5");

        // Delete connection Sessions
        CommonSteps.deleteConnectionSession(TD_CONF, v1ConnectionSession);
        CommonSteps.deleteConnectionSession(TD_CONF, v2ConnectionSession);
        //BEFORE_SUITE_IS_EXECUTED = false;
    }
}


