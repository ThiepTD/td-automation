package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase2;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 *   GoogleDriveIncrementalFolder is a base class for all Google Drive folder Incremental Test Scenarios
 *   The TestCaseBase implements an iTestListener and IRetryAnalyzer
 *   to support automatically rerun failure TestNg tests
 */
public class GoogleDriveIncreamentalFolder extends TestCaseBase {

    public static String ID = "id";
    public static String NAME = "name";
    public static String AGE = "age";
    public static String EMAIL = "email";
    public static String DATE = "date";
    public static String TIME = "mytime";
    public static String BOOLEAN = "mybool";
    public static String DECIMAL = "mydecimal";
    public static String SPECIAL = "special";


    public static String CSV_SOURCE_TEST_DATA2= String.format("%spluginautomation/googledrive/csv/output2.csv", Constant.RESOURCE_PATH);
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/csv/2_output.csv", Constant.RESOURCE_PATH);
    public static String file3 = String.format("%spluginautomation/googledrive/csv/3_output.csv", Constant.RESOURCE_PATH);

    public String folderId, fileId,fileId1, fileId2,fileId3,fileName = "output.csv";
    public String CONNECTOR_SESSION = "v2_hourly";
    public Quickstart googleDriveApi;

    /**
     * Defining Constructors for intializing variables
     */

    public GoogleDriveIncreamentalFolder() {
        getInstance();
    }

    public GoogleDriveIncreamentalFolder(String pythonInput, String ymlFile, String sourceTestData, String targetTestData) {
        getInstance();
        if (ymlFile != null) yml = ymlFile;
        if (pythonInput != null) PYTHON_INPUT = pythonInput;
        if (sourceTestData != null) this.sourceTestData = sourceTestData;
        if (targetTestData != null) this.targetTestData = targetTestData;

    }



    public void getInstance() {
        yml = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_folder_append.yml";
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
    public void initialize(@Optional("null") String sourceFile, @Optional("null") String targetFile, @Optional("null") String ymlFile){
        LOGGER = LogManager.getLogger(GoogleDriveFileIncrementalAppend.class.getName());
        LOGGER.info("---------------- Before Suite -------------");

        sourceTestData = (sourceFile == null) ? sourceTestData : sourceFile;
        targetTestData = (targetFile  == null) ? targetTestData : targetFile;
        this.yml = (ymlFile == null) ? this.yml : ymlFile;
        this.fileName = (fileName == null) ? this.fileName : fileName;
        TD_DB = TD_USER + "_google_db";

        CommonSteps.TD_CONF = TD_CONF;
        CommonSteps.createTable(TD_DB, tdTargetTable);

        PYTHON_INPUT = String.format(PYTHON_INPUT, 5, ",", 6,
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", ID, NAME, EMAIL, AGE, DATE, TIME),
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", "id", "name", "email", "number,1,100", "date,2015-01-01,2018-01-01", "time"),"output.csv");
        googleDriveApi = new Quickstart("tokens");
        setupYmlFile(yml);
    }

    /**
     * Generating Python input file to be read by data generator
     */
    public void createPythonInputFile() {
        String[] lines = {PYTHON_INPUT};
        FileUtil.writeFile(PYTHON_INPUT_FILE, lines);
    }

    /** Generating test data and storing in 2 csv files
     * Asserting that there is no error
     */
    @Test(dependsOnMethods = {"initialize"},retryAnalyzer = TestCaseBase.class)
    public void firstCreateTestData() {
        createPythonInputFile();
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/1_", Constant.RESOURCE_PATH));
        String result2 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/2_", Constant.RESOURCE_PATH));
        assertTrue(!result1.contains("error") && !result2.contains("error"));
    }

    /** Creating a folder in Google drive using api call
     * Exporting csv files to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Test(dependsOnMethods = {"firstCreateTestData"}, retryAnalyzer = TestCaseBase.class)
    public void firstExportDataToGoogleDrive() throws IOException {
        folderId = googleDriveApi.createFolder("FolderIncrementAppend");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.csv","text/csv",file1);
        fileId2 = googleDriveApi.uploadFileInFolder(folderId,"file2.csv","text/csv",file2);
        assertTrue(folderId != null);
    }

    /**
     * Replacing the folder Id generated in previous step in yml file
     * Create a new Connector session
     * Asserting that there is no error
     */
    @Test(dependsOnMethods = {"firstExportDataToGoogleDrive"}, retryAnalyzer = TestCaseBase.class)
    public void createConnectorSessionV2() {
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + folderId);
        String result = CommonSteps.createConnectionSession(CONNECTOR_SESSION,null,TD_DB, tdTargetTable, yml);
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
        String result = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/3_", Constant.RESOURCE_PATH));
        assertTrue(!result.contains("error"));
    }

    /** Upload another csv file in google drive folder using google drive api
     * Asserting that file Id is not null
     */
    @Test(dependsOnMethods = {"secondCreateTestData"}, retryAnalyzer = TestCaseBase.class)
    public void uploadAnotherFileInGdFolder() throws IOException{
        String fileId3 = googleDriveApi.uploadFileInFolder(folderId,"file3.csv","text/csv",file3);
        assertTrue(fileId3 != null);
    }

    /**
     * Run the Connector session
     * Asserting that there is no error
     */
    @Test(dependsOnMethods = {"uploadAnotherFileInGdFolder"}, retryAnalyzer = TestCaseBase.class)
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
        new GoogleDriveUtil().mergeFiles(file1,file2,CSV_SOURCE_TEST_DATA2);
        new GoogleDriveUtil().mergeFiles(CSV_SOURCE_TEST_DATA2,file3,sourceTestData);
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
        googleDriveApi.deleteFile(folderId);
        System.out.println("Folder clean up ran");
    }


    @Override
    public void resetFileContent(){

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