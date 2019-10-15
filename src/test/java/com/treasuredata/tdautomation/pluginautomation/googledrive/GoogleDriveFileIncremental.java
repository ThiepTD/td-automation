package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase2;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import org.apache.logging.log4j.LogManager;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 *   GoogleDriveFileIncremental is a base class for all Google Drive file Incremental  Test Scenarios
 *   The TestCaseBase implements an iTestListener and IRetryAnalyzer
 *   to support automatically rerun failure TestNg tests
 */

public class GoogleDriveFileIncremental extends TestCaseBase {

    /** Declaring all variables **/
    public static String ID = "id";
    public static String NAME = "name";
    public static String AGE = "age";
    public static String EMAIL = "email";
    public static String DATE = "date";
    public static String TIME = "mytime";
    public static String BOOLEAN = "mybool";
    public static String DECIMAL = "mydecimal";
    public static String SPECIAL = "special";


    public static String yml = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_append.yml";

    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    //public static String CSV_SOURCE_TEST_DATA2= String.format("%spluginautomation/googledrive/csv/output2.csv", Constant.RESOURCE_PATH);
    public static String targetTestData = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/csv/2_output.csv", Constant.RESOURCE_PATH);
    public static String file3 = String.format("%spluginautomation/googledrive/csv/3_output.csv", Constant.RESOURCE_PATH);

   // public static String TD_TO_CSV_TARGET_CMD;
    public String folderId, fileId,fileId1, fileId2,fileName="output.csv";

    public static String TD_TABLE = "from_google_drive";
    public String CONNECTOR_SESSION = "v2_hourly";
    public static String database = "google_drive_db";
    public Quickstart googleDriveApi;

    /**
     * Defining Constructors for intializing variables
     */

    public GoogleDriveFileIncremental() {
        getInstance();
    }

    public GoogleDriveFileIncremental(String pythonInput, String ymlFile, String sourceTestData, String targetTestData) {
        getInstance();
        if (ymlFile != null) yml = ymlFile;
        if (pythonInput != null) PYTHON_INPUT = pythonInput;
        if (sourceTestData != null) this.sourceTestData = sourceTestData;
        if (targetTestData != null) this.targetTestData = targetTestData;

    }



    public void getInstance() {
        yml = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_append.yml";
        sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
        targetTestData = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
        tdTargetTable = "from_google_drive";
        dbName = "_google_drive_db";
        tdQuery = String.format("Select id,name,age,email,TD_TIME_FORMAT(TD_TIME_PARSE(date), 'yyyy-MM-dd') as date,mytime from %s", tdTargetTable);
        PYTHON_INPUT = "input = {\n" +
                "  \"num_of_row\": %d,\n" +
                "  \"delimiter\": \"%s\",\n" +
                "  \"num_of_column\": %d,\n" +
                "  \"headers\": %s,\n" +
                "  \"column_types\": %s,\n" +
                "  \"fileName\": \"%s\"\n" +
                "}";
    }

    /**
     * Defining initialize method to set variables through xml file
     * Create Database and Table
     * Create instance of Google drive api class
     */
    @Parameters({"sourceFile","targetFile", "ymlFile"})
    @Test
    public void initialize(@Optional("null") String sourceFile, @Optional("null") String targetFile, @Optional("null") String ymlFile) {
        LOGGER = LogManager.getLogger(GoogleDriveFileIncrementalAppend.class.getName());
        LOGGER.info("---------------- Before Suite -------------");

        sourceTestData = (sourceFile == null) ? sourceTestData : sourceFile;
        targetTestData = (targetFile  == null) ? targetTestData : targetFile;
        this.yml = (ymlFile == null) ? this.yml : ymlFile;
        this.fileName = (fileName == null) ? this.fileName : fileName;
        TD_DB = TD_USER + "_google_db";
        CommonSteps.TD_CONF = TD_CONF;
        CommonSteps.createTable(TD_DB, tdTargetTable);
        googleDriveApi = new Quickstart("tokens");
        setupYmlFile(yml);
    }


    /** Generating test data and storing in csv file
     * Asserting that there is no error
     */
    @Test(dependsOnMethods = {"initialize"},retryAnalyzer = TestCaseBase.class)
    public void firstCreateTestData() {
        createPythonInputFile();
        String result = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/1_", Constant.RESOURCE_PATH));
        assertTrue(!result.contains("error"));
    }

    /**
     * Exporting csv file to Google Drive using google drive Api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Test(dependsOnMethods = {"firstCreateTestData"}, retryAnalyzer = TestCaseBase.class)
    public void firstExportDataToGoogleDrive() throws IOException {
        fileId = googleDriveApi.uploadFile("append_test.csv","text/csv",file1);
        assertTrue(fileId != null);
    }

    /**
     * Replacing the file Id generated in previous step in yml file
     * Create a new Connector session
     * Asserting that there is no error
     */
    @Test(dependsOnMethods = {"firstExportDataToGoogleDrive"}, retryAnalyzer = TestCaseBase.class)
    public void createConnectorSessionV2() {
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + fileId);

        String result =  CommonSteps.createConnectionSession(CONNECTOR_SESSION,TD_CONF,null,TD_DB, TD_TABLE, yml);
        assertTrue(result.contains("Config Diff"));
    }

    /**
     * Run the Connector session
     * Asserting that there is no error
     */
    @Test(dependsOnMethods = {"createConnectorSessionV2"}, retryAnalyzer = TestCaseBase.class)
    public void firstCallDataTransferToTdV2() {
        String result = CommonSteps.runConnectionSession(TD_CONF, CONNECTOR_SESSION);
        assertTrue(result.contains(": success"));
    }

    /** Generating another test data and storing in csv file
     * Asserting that there is no error
     */
    @Test(dependsOnMethods = {"firstCallDataTransferToTdV2"},retryAnalyzer = TestCaseBase.class)
    public void secondCreateTestData() {
        String result = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/2_", Constant.RESOURCE_PATH));
        assertTrue(!result.contains("error"));
    }

    /** Updating the csv file in google drive using google drive api
     * Asserting that there is no error
     */
    @Test(dependsOnMethods = {"secondCreateTestData"}, retryAnalyzer = TestCaseBase.class)
    public void updateFileContentInGd() {
        fileId = googleDriveApi.updateFile(fileId,"append_test.csv","text/csv",file2);
        assertTrue(fileId != null);
    }

    /**
     * Run the Connector session
     * Asserting that there is no error
     */
    @Test(dependsOnMethods = {"updateFileContentInGd"}, retryAnalyzer = TestCaseBase.class)
    public void secondCallDataTransferToTdV2() {
        String result = CommonSteps.runConnectionSession(TD_CONF, CONNECTOR_SESSION);
        assertTrue(result.contains(": success"));
    }

    /**
     * Querying the TD table to select all column values except time column
     * Store the result in a target file
     */
    @Test(dependsOnMethods = {"secondCallDataTransferToTdV2"}, retryAnalyzer = TestCaseBase.class)
    public void secondExportTargetDataToCsv() {
        String result = CommonSteps.exportTdDataToFile(TD_DB, "presto", "csv", targetTestData, tdQuery);
        exportDataFromTdToCsvAssertion(result);
    }

    /**
     * Merge file1 and file2 in source file for comparision
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Test(dependsOnMethods = {"secondExportTargetDataToCsv"})
    public void secondCompareData() {
        new GoogleDriveUtil().mergeFiles(file1, file2, sourceTestData);
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }

    /**
     * Overriding cleanupBefore method from GoogleDriveInputRefactor2
     * Delete connection session
     * Delete the file in Google Drive
     */
    @AfterTest
    @Override
    public void onFinish(ITestContext Result){
        LOGGER.info("---------------- ON FINISH -------------");
        CommonSteps.deleteConnectionSession(TD_CONF, CONNECTOR_SESSION);
        cleanupYmlFile(yml);
        //System.out.println("file cleanup ran");
    }

    @Override
    public void resetFileContent(){

    }

    /**
     * Delete a file in Google Drive using api call
     */
    public void deleteFileInGd() {
        googleDriveApi.deleteFile(fileId);
    }

    /**
     * This method returns a input file for data generator
     */

    public void createPythonInputFile() {
        PYTHON_INPUT = "input = {\n" +
                "  \"num_of_row\": %d,\n" +
                "  \"delimiter\": \"%s\",\n" +
                "  \"num_of_column\": %d,\n" +
                "  \"headers\": %s,\n" +
                "  \"column_types\": %s,\n" +
                "  \"fileName\": \"%s\"\n" +
                "}";

        PYTHON_INPUT = String.format(PYTHON_INPUT, 5, ",", 6,
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", ID, NAME, EMAIL, AGE, DATE, TIME),
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", "id", "name", "email", "number,1,100", "date,2015-01-01,2018-01-01", "time"),this.fileName);

        String[] lines = {PYTHON_INPUT};
        FileUtil.writeFile(PYTHON_INPUT_FILE, lines);
    }

    /**
     *
     * Setting the required parameters in config file from environment variables
     */
    protected static void setupYmlFile(String yml){
        new YmlFileUtil().setRefreshToken(yml," refresh_token:","  refresh_token: "+System.getenv("GD_REFRESH_TOKEN"));
        new YmlFileUtil().setRefreshToken(yml," client_id:","  client_id: "+System.getenv("GD_CLIENT_ID"));
        new YmlFileUtil().setRefreshToken(yml," client_secret:","  client_secret: "+System.getenv("GD_CLIENT_SECRET"));
    }

    /**
     *
     * Removing the required parameters in config file from environment variables
     */
    protected static void cleanupYmlFile(String yml){
        new YmlFileUtil().setRefreshToken(yml,"  refresh_token: "+System.getenv("GD_REFRESH_TOKEN"),"  refresh_token: "+"");
        new YmlFileUtil().setRefreshToken(yml," client_id: "+System.getenv("GD_CLIENT_ID"),"  client_id: "+"");
        new YmlFileUtil().setRefreshToken(yml," client_secret: "+System.getenv("GD_CLIENT_SECRET"),"  client_secret: "+"");
    }
}