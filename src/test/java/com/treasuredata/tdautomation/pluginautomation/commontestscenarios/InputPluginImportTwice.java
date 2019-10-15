package com.treasuredata.tdautomation.pluginautomation.commontestscenarios;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

/*
 * Extended from TestCaseBase
 * initialize method to get necessary parameters
 *  1 Transfer data from third party data platform to TD based on defined yml file
 *  2 Again, transfer data from third party data platform to TD base on defined yml file
 *  2 Export imported data from TD to csv
 *  3 Do data comparison b/w expected csv file and target csv file
 */

@Listeners(InputPluginImportTwice.class)

public class InputPluginImportTwice extends TestCaseBase {

    // WARNING: Remove the default constructor make TestNg fail to run our test suite/script

    public InputPluginImportTwice(){}

    public InputPluginImportTwice(String ymlFile, String targetFile, String expectedFile, String database, String targetTable, String tdQuery){
        super(ymlFile, targetFile, expectedFile, database, targetTable, tdQuery);
    }

    @Parameters({"sourceTestData", "ymlFile"})
    @BeforeTest
    public void initialize(@Optional("null") String expectedFile, @Optional("null") String ymlFile){
        TD_DB = TD_USER + dbName;
        sourceTestData = (expectedFile == null) ? sourceTestData : expectedFile;
        this.yml = (ymlFile == null) ? this.yml : ymlFile;
        LOGGER.info(String.format("\nExpected test data file %s \nYml file %s", sourceTestData, this.yml));

        // get sfdc info from .bash_profile
        String [] ymlValues = getEnvInfo();

        // override yml file content
        ymlContent = FileUtil.updateFileContent(yml, ymlValues);

        CommonSteps.TD_CONF = TD_CONF;
        CommonSteps.createTable(TD_DB, tdTargetTable);
    }

    @Test(retryAnalyzer = TestCaseBase.class, timeOut = 30000)
    public void firstTimeDataImport() {
        String result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        transferDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"firstTimeDataImport"}, retryAnalyzer = TestCaseBase.class, timeOut = 30000)
    public void secondTimeDataImport() {
        String result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        transferDataToTdAssertion(result);
    }

    @Test(dependsOnMethods = {"secondTimeDataImport"}, retryAnalyzer = TestCaseBase.class, timeOut = 30000)
    public void exportTargetDataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_DB, "presto", "csv", targetTestData, tdQuery);
        exportDataFromTdToCsvAssertion(result);
    }

    @Test(dependsOnMethods = {"exportTargetDataToCsv"}, timeOut = 300000)
    public void compareData() {
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        compareDataAssertion(result);
    }
}


