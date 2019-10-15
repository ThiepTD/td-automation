package com.treasuredata.tdautomation.pluginautomation.commontestscenarios;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

/**
 * The BasicInputPlugin extends TestCaseBase
 * It is a basic scenario for input plugin
 * It basically has 1 steps to initialize necessary parameters and three other steps to
 *      - Transfer data from third party data platform to TD based on provided yml file
 *      - Export from TD to csv file
 *      - Compare exported csv file with our expected csv data file
 * @author  Thiep Truong
 * @version 1.0
 * @since   2019-07-23
 */

@Listeners(BasicInputPlugin.class)

public class BasicInputPlugin extends TestCaseBase {

    // WARNING: Removing the default constructor makes TestNg fail to run our test suite/script
    public BasicInputPlugin(){}

    /**
     * This is the constructor for the class.
     * @param ymlFile yml file - full path.
     * @param targetFile target csv file - full path, normally we use to store exported data from TD.
     * @param expectedFile our expected csv - full path, our expected data which will be used to compare with the target csv file
     * @param database a part of database name. Ex: in td configuration file, user name is thiep.truong@treasure-data.com
     *                 and database name is sfdc then full database name will be thiep_truong_sfdc
     * @param targetTable TD table name which will store data we got from third party data platform.
     * @param tdQuery our sql query to query above target TD table.
     */
    public BasicInputPlugin(String ymlFile, String targetFile, String expectedFile, String database, String targetTable, String tdQuery){
        super(ymlFile, targetFile, expectedFile, database, targetTable, tdQuery);
    }

    /**
     * This is the first step to initialize sourceTestData and ymlFile
     * If we run this test class from the code directly sourceTestData and ymlFile will have default values defined inside this method
     * If we run test from TestNg xml files, sourceTestData and ymlFile will be assigned to values defined in xml file
     * It create a TD table in advance
     * @param ymlFile yml file - full path.
     * @param sourceTestData expected csv test data file - full path
     */
    @Parameters({"sourceTestData", "ymlFile"})
    @BeforeTest
    public void initialize(@Optional("null") String sourceTestData, @Optional("null") String ymlFile){
        TD_DB = TD_USER + dbName;
        this.sourceTestData = (sourceTestData == null) ? this.sourceTestData : sourceTestData;
        this.yml = (ymlFile == null) ? this.yml : ymlFile;
        LOGGER.info(String.format("\nExpected test data file %s \nYml file %s", this.sourceTestData, this.yml));

        // get sfdc info from .bash_profile
        String [] ymlValues = getEnvInfo();

        // override yml file content
        ymlContent = FileUtil.updateFileContent(yml, ymlValues);

        CommonSteps.TD_CONF = TD_CONF;
        CommonSteps.createTable(TD_DB, tdTargetTable);
    }

    /**
     * This is the step to transfer data from 3rd data platform to TD based on defined yml file
     * Transferring data will be executed by calling td cli
     * Output of td cli command will be captured by result variable
     * Call transferDataToTdAssertion to evaluate the result
     * This step will be skipped if above step initialize is failed
     */
    @Test(retryAnalyzer = TestCaseBase.class, timeOut = 60000)
    public void transferDataToTd() {
        String result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        transferDataToTdAssertion(result);
    }

    /**
     * This is the step to export data from a certain TD table to csv file
     * Exporting data will be executed by calling td cli
     * Output of td cli command will be captured by result variable
     * Call exportDataFromTdToCsvAssertion to evaluate the result
     * This step will be skipped if above step transferDataToTd is failed
     */
    @Test(dependsOnMethods = {"transferDataToTd"}, retryAnalyzer = TestCaseBase.class, timeOut = 60000)
    public void exportDataFromTdToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_DB, "presto", "csv", targetTestData, tdQuery);
        exportDataFromTdToCsvAssertion(result);
    }

    /**
     * This is the step to compare exported data from TD with our predefined csv file
     * Call compareDataAssertion to evaluate the result
     * This step will be skipped if above step exportDataFromSourceTd is failed
     */
    @Test(dependsOnMethods = {"exportDataFromTdToCsv"}, timeOut = 300000)
    public void compareData() {
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        compareDataAssertion(result);
    }

}


