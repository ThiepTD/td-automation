package com.treasuredata.tdautomation.pluginautomation.commontestscenarios;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

/**
 * The InputPluginBackwardCompatible extends from TestCaseBase
 * to support a test scenario for backward compatible
 * Step order"" transfer data to td, export data from td for both version of plugin then do data comparison
 * @author  Thiep Truong
 * @version 1.0
 * @since   2019-08-08
 */

@Listeners(InputPluginBackwardCompatible.class)

public class InputPluginBackwardCompatible extends TestCaseBase {

    public String v2Table;
    public String v2Yml;
    public String v2YmlContent;

    public InputPluginBackwardCompatible(){}

    // tdTargetTable now is used for SFDCv1 connector
    public InputPluginBackwardCompatible(String v1Yml, String v2Yml, String v1TargetFile, String v2TargetFile, String database, String v1Table, String v2Table, String tdQuery){
        super(v1Yml, v1TargetFile, v2TargetFile, database, v1Table, tdQuery);
        this.v2Yml = v2Yml;
        this.v2Table = v2Table;
    }

    // Following are setter for object builder
    public InputPluginBackwardCompatible setV2Yml(String v2Yml){
        this.v2Yml = Constant.RESOURCE_PATH + v2Yml;
        return this;
    }

    public InputPluginBackwardCompatible setV2Table(String v2Table){
        this.v2Table = v2Table;
        return this;
    }

    @Parameters({"v1YmlFile", "v2YmlFile", "query", "v1Table", "v2Table"})
    @BeforeTest
    public void initialize(@Optional("null") String v1YmlFile, @Optional("null") String v2YmlFile, @Optional("null") String query, @Optional("null") String v1Table, @Optional("null") String v2Table){

        // Overwrite values from constructor in case we run from xml file
        TD_DB = TD_USER + dbName;
        yml = (v1YmlFile == null) ? yml : v1YmlFile;
        v2Yml = (v2YmlFile == null) ? v2Yml : v2YmlFile;
        tdQuery = (query == null) ? tdQuery : query;
        tdTargetTable = (v1Table == null) ? tdTargetTable.toLowerCase() : v1Table.toLowerCase();
        this.v2Table = (v2Table == null) ? this.v2Table.toLowerCase() : v2Table.toLowerCase();

        // get sfdc info from .bash_profile
        String [] ymlValues = getEnvInfo();

        // override yml file content
        ymlContent = FileUtil.updateFileContent(yml, ymlValues);
        v2YmlContent = FileUtil.updateFileContent(v2Yml, ymlValues);

        CommonSteps.TD_CONF = TD_CONF;
        CommonSteps.createTable(TD_DB, this.v2Table);
        CommonSteps.createTable(TD_DB, tdTargetTable);
    }

    @Test(retryAnalyzer = TestCaseBase.class)
    public void transferDataToTdV1() {
        String result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        transferDataToTdAssertion(result);
    }

    @Test(retryAnalyzer = TestCaseBase.class)
    public void transferDataToTdV2() {
        String result = CommonSteps.transferDataToTd(v2Yml, TD_DB, v2Table);
        transferDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"transferDataToTdV1"}, retryAnalyzer = TestCaseBase.class)
    public void exportV1DataToCsv() {
        String queryV1Table = String.format(tdQuery, tdTargetTable);
        String result = CommonSteps.exportTdDataToFile(TD_DB, "presto", "csv", sourceTestData, queryV1Table);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"transferDataToTdV2"}, retryAnalyzer = TestCaseBase.class)
    public void exportV2DataToCsv() {
        String queryV2Table = String.format(tdQuery, v2Table);
        String result = CommonSteps.exportTdDataToFile(TD_DB, "presto", "csv", targetTestData, queryV2Table);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"exportV2DataToCsv", "exportV2DataToCsv"}, timeOut = 300000)
    public void compareData() {
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        compareDataAssertion(result);
    }

    //@Test(dependsOnMethods = {"transferDataToTdV1"})
    public void testGetRecordCount(){
        LOGGER.info("------------- Start running testGetRecordCount -------------");
        String query = "select count(address__c) from " + tdTargetTable;
        int result = CommonSteps.getRecordCount(TD_DB, null, query);
        getRecordCountAssertion(result);
    }

    @AfterTest
    public void resetFileContent(){
        super.resetFileContent();
        FileUtil.writeFile(v2Yml, v2YmlContent);
    }
}


