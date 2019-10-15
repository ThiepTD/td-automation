package com.treasuredata.tdautomation.pluginautomation.td2td;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.pluginautomation.common.TdTable;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.testng.Assert.assertTrue;

/**
 * The Td2TdOutput extends TestCaseBase
 * It is a basic scenario for td2td output plugin
 * It basically has 1 steps (before method) to initialize necessary parameters and other steps to
 *      - Transfer data from source table to target table using firstQuery
 *      - Export data from source table using firstQuery - csv1
 *      - Create source headers which are retrieved from above step
 *      - Create another query (from source headers) to query target table then export to csv - csv2
 *      - Compare csv
 *      - Second time transfer data from source table to target table using secondQuery
 *      - Export data from source table using secondQuery - csv3
 *      - Update source headers with columns from (csv3) above step
 *      - Create another query from source headers to query target table then export to csv - csv4
 *      - Compare data b/w (csv2 & csv3) and csv4
 *      - Verify schema
 * @author  Thiep Truong
 * @version 1.0
 * @since   2019-10-02
 */

@Listeners(Td2TdOutput.class)

public class Td2TdOutput extends TestCaseBase {

    public String targetDb;
    public String apiKey;
    public String endPoint;
    public String mode;
    public String timeValue;
    public String targetEnv;
    public String tdSourceTable;
    public String secondQuery;
    public String [] envInfo;
    public ArrayList<String> sourceHeaders;
    public String URL = FileUtil.read(Constant.RESOURCE_PATH + "pluginautomation/td2td/targetTd.json");
    public String tmpConf = String.format("%spluginautomation/tmp/tmp.conf", Constant.RESOURCE_PATH);
    public static String ENGINE = "presto";
    public static String TMP_CSV = Constant.RESOURCE_PATH + "pluginautomation/td2td/csv/tempCsv" +
            "";

    // WARNING: Removing the default constructor makes TestNg fail to run our test suite/script
    public Td2TdOutput(){}

    public Td2TdOutput(String ymlFile, String targetFile, String expectedFile, String database, String targetTable, String tdQuery){
        super(ymlFile, targetFile, expectedFile, database, targetTable, tdQuery);
    }

    public Td2TdOutput setTargetDb(String targetDb){
        this.targetDb = targetDb;
        return this;
    }

    public Td2TdOutput setSourceTable(String sourceTable){
        this.tdSourceTable = sourceTable;
        return this;
    }

    public Td2TdOutput setApiKey(String envName){
        this.apiKey = envName;
        return this;
    }

    public Td2TdOutput setEndPoint(String envName){
        this.endPoint = envName;
        return this;
    }

    public Td2TdOutput setMode(String mode){
        this.mode = mode;
        return this;
    }

    public Td2TdOutput setTimeValue(String timeValue){
        this.timeValue = timeValue;
        return this;
    }

    public Td2TdOutput setSecondQuery(String secondQuery){
        this.secondQuery = secondQuery;
        return this;
    }

    public Td2TdOutput setTargetEnv(String targetEnv){
        this.targetEnv = targetEnv;
        return this;
    }
    @Parameters({"targetDb", "firstQuery", "secondQuery","tdSourceTable", "tdTargetTable", "mode", "timeValue", "engine", "targetEnv"})
    @BeforeTest
    public void initialize(@Optional("null") String targetDb,
                           @Optional("null") String firstQuery,
                           @Optional("null") String secondQuery,
                           @Optional("null") String tdSourceTable,
                           @Optional("null") String tdTargetTable,
                           @Optional("null") String mode,
                           @Optional("null") String timeValue,
                           @Optional("null") String engine,
                           @Optional("null") String targetEnv){
        TD_DB = TD_USER + dbName;
        if (targetDb != null)
            this.targetDb = targetDb.trim().toLowerCase();

        if (firstQuery != null)
            this.tdQuery = firstQuery;

        if (secondQuery != null)
            this.secondQuery = secondQuery;

        if (tdSourceTable != null)
            this.tdSourceTable = tdSourceTable.trim().toLowerCase();

        if (tdTargetTable != null)
            this.tdTargetTable = tdTargetTable.trim().toLowerCase();

        if (mode != null)
            this.mode = mode;

        if (timeValue != null)
            this.timeValue = timeValue;

        if (targetEnv != null)
            this.targetEnv = targetEnv.trim().toLowerCase();

        if (engine.trim().toLowerCase().equalsIgnoreCase("hive"))
            ENGINE = "hive";

        // get sfdc info from .bash_profile
        envInfo = getEnvInfo();
        setApiKey(envInfo[1]);
        setEndPoint(envInfo[2].replace("https://", ""));

        // target env td configuration file
        FileUtil.writeFile(tmpConf, TD_CONF_CONTENT);
        FileUtil.updateFileContent(tmpConf, envInfo);

        URL = "'" + String.format(URL, this.apiKey, this.endPoint, this.targetDb, this.mode, this.timeValue, "td_v2", tdTargetTable) + "'";

        CommonSteps.TD_CONF = TD_CONF;
    }

    /*
    * This function will read environment variables based on environment names which is one of three values: dev, dev-eu01 and stag
    * */
    @Override
    public String [] getEnvInfo(){
        String [] envInfo;
        switch (targetEnv.trim().toLowerCase()){
            case "dev-eu01":
                envInfo = new String [] {Constant.ENV.get(Td2TdUtil.TD2TD_DEV_EU01_USER), Constant.ENV.get(Td2TdUtil.TD2TD_DEV_EU01_API_KEY), Constant.ENV.get(Td2TdUtil.TD2TD_DEV_EU01_END_POINT)};
                break;
            case "stag":
                envInfo = new String [] {Constant.ENV.get(Td2TdUtil.TD2TD_STAG_USER), Constant.ENV.get(Td2TdUtil.TD2TD_STAG_API_KEY), Constant.ENV.get(Td2TdUtil.TD2TD_STAG_END_POINT)};
                break;
            default:
                envInfo = new String [] {Constant.ENV.get(Td2TdUtil.TD2TD_DEV_USER), Constant.ENV.get(Td2TdUtil.TD2TD_DEV_API_KEY), Constant.ENV.get(Td2TdUtil.TD2TD_DEV_END_POINT)};
                break;
        }
        return envInfo;
    }

    /*
     * This function will create table tdTargetTable under database tdTargetDb of environment define in tmpConf
     * If the table is ready exist, it will be deleted then create a new one with empty value
     * */
    public void createTable(){
        // Create table in target env
        CommonSteps.createTable(tmpConf, this.targetDb, tdTargetTable);
    }

    /*
     * This function will first time export data from TdSourceTable to tdTargetTable which is defined in URL info
     * It firstly create table in target environment then export data to target environment then verify the output of td cli
     * */
    @Test(retryAnalyzer = TestCaseBase.class)
    public void firstTimeExportDataToTargetTd() {
        createTable();
        String result = CommonSteps.exportTdDataTo3rdParty(TD_DB, ENGINE, URL, String.format(tdQuery, tdSourceTable));
        transferDataToTdAssertion(result);
    }

    /*
     * This function will do the following steps:
     *  - Empty TMP_CSV file
     *  - Export sourceTable data with query tdQuery and make an assertion to make exporting is successful
     *  - Get headers of TMP_CSV file and store them in sourceHeaders
     *  - Create query queryTargetData based on sourceHeaders
     *  - Export tdTargetTable data using query queryTargetTable and make an assertion to make exporting is successful
     * */
    @Test(dependsOnMethods = {"firstTimeExportDataToTargetTd"}, retryAnalyzer = TestCaseBase.class)
    public void firstTimeExportData() {

        // Export data from source table using tdQuery
        FileUtil.writeFile(TMP_CSV, "");
        String result = CommonSteps.exportTdDataToFile(TD_DB, ENGINE, "csv", TMP_CSV, String.format(tdQuery, tdSourceTable));
        exportDataFromTdToCsvAssertion(result);

        //Add header for sourceHeaders
        sourceHeaders = new ArrayList<String>(Arrays.asList(CsvUtil.getHeaders(TMP_CSV, null)));

        // Export data from target table
        targetTestData = String.format(targetTestData, RUNNING_TEST_NAME);
        String queryForTargetData = CsvUtil.createQueryFromHeaders(sourceHeaders);
        result = CommonSteps.exportTdDataToFile(tmpConf, targetDb, ENGINE, "csv", targetTestData, String.format(queryForTargetData, tdTargetTable));
        exportDataFromTdToCsvAssertion(result);
    }

    /*
     * This function will first time compare data b/w TMP_CSV and targetTestData
     * */
    @Test(dependsOnMethods = {"firstTimeExportData"})
    public void firstTimeCompareData() {
        boolean result = CsvUtil.compareCsv(TMP_CSV, targetTestData, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        compareDataAssertion(result);
    }

    /*
     * This function will second time export data from TdSourceTable using secondQuery to tdTargetTable which is defined in URL info
     * Then verify the output of td cli
     * */
    @Test(dependsOnMethods = {"firstTimeCompareData"}, retryAnalyzer = TestCaseBase.class)
    public void secondTimeExportDataToTargetTd() {
        String result = CommonSteps.exportTdDataTo3rdParty(TD_DB, ENGINE, URL, String.format(secondQuery, tdSourceTable));
        transferDataToTdAssertion(result);
    }

    /*
     * This function will do the following steps:
     *  - Export sourceTable data with query secondQuery and make an assertion to make exporting is successful
     *  - Get headers of sourceTestData file and add them into sourceHeaders
     *  - Create query queryTargetData based on sourceHeaders
     *  - Export tdTargetTable data using query queryTargetTable and make an assertion to make exporting is successful
     * */
    @Test(dependsOnMethods = {"secondTimeExportDataToTargetTd"}, retryAnalyzer = TestCaseBase.class)
    public void secondTimeExportData() {

        sourceTestData = String.format(sourceTestData, RUNNING_TEST_NAME);
        String result = CommonSteps.exportTdDataToFile(TD_DB, ENGINE, "csv", sourceTestData, String.format(secondQuery, tdSourceTable));
        exportDataFromTdToCsvAssertion(result);

        //Update sourceHeaders
        CsvUtil.mergeHeaders(sourceHeaders, CsvUtil.getHeaders(sourceTestData, null));

        // Export data from target table
        targetTestData = String.format(targetTestData, RUNNING_TEST_NAME);
        String queryForTargetData = CsvUtil.createQueryFromHeaders(sourceHeaders);

        result = CommonSteps.exportTdDataToFile(tmpConf, targetDb, ENGINE, "csv", targetTestData, String.format(queryForTargetData, tdTargetTable));
        exportDataFromTdToCsvAssertion(result);
    }

    /*
     * This function will second time compare data b/w TMP_CSV, sourceTestData and targetTestData
     * TMP_CSV will be merged with sourceTestData before doing data comparison
     * */
    @Test(dependsOnMethods = {"secondTimeExportData"})
    public void secondTimeCompareData() {
        boolean result = CsvUtil.compareCsv(TMP_CSV, sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        compareDataAssertion(result);
    }

    /*
     * This function will do the following steps:
     *  - Create sourceTable object by providing database TD_DB and table tdSourceTable
     *  - Create expected schema based on the original schema of tdSourceTable and sourceHeaders
     *  - Create targetTable object by providing database targetDb and table tdTargetTable
     *  - Get schema of targetTable by calling getTableInfo function
     *  - Make an assertion to make sure targetTable's schema contains expected schema
     *  - Make an assertion to make sure number of columns in targetTable is the same as number columns in sourceHeaders
     * */
    @Test(dependsOnMethods = {"secondTimeCompareData"})
    public void verifySchema(){
        TdTable sourceTable = TdTable.getInstance(TD_DB, tdSourceTable);
        HashMap<String, String> expectedSchema = sourceTable.getTableInfo(TD_CONF).getSubSchema(sourceHeaders);

        TdTable targetTable = TdTable.getInstance(targetDb, tdTargetTable).getTableInfo(tmpConf);
        assertTrue(targetTable.doesSchemaContain(expectedSchema));
        int numOfCol = targetTable.getColumns();
        assertTrue(numOfCol == sourceHeaders.size());
    }

    @AfterTest
    public void afterTest(){
        FileUtil.writeFile(tmpConf, TD_CONF_CONTENT);
    }
}


