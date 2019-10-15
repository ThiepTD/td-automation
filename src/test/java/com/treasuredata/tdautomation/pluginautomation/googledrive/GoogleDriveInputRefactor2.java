package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase2;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;


/**
 *   GoogleDriveInputRefactor2 is a base class for all Google Drive Test Scenarios
 *   The TestCaseBase implements an iTestListener and IRetryAnalyzer
 *   to support automatically rerun failure TestNg tests
 */


@Listeners(TestCaseBase.class)

public class GoogleDriveInputRefactor2 extends TestCaseBase {

    public static String ID = "id";
    public static String NAME = "name";
    public static String AGE = "age";
    public static String EMAIL = "email";
    public static String DATE = "date";
    public static String TIME = "mytime";
    public static String PHONE = "phone";


    public String fileId,fileName;
    public Quickstart googleDriveApi;
    public String result;

    /**
     * Defining Constructors for intializing variables
     */

    public GoogleDriveInputRefactor2() {
        getInstance();
    }

    public GoogleDriveInputRefactor2(String pythonInput, String ymlFile, String sourceTestData, String targetTestData,String fileName) {
        getInstance();
        if (ymlFile != null) yml = ymlFile;
        if (pythonInput != null) PYTHON_INPUT = pythonInput;
        if (sourceTestData != null) this.sourceTestData = sourceTestData;
        if (targetTestData != null) this.targetTestData = targetTestData;
        if (fileName != null) this.fileName = fileName;
    }

    public void getInstance() {
        yml = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_csv.yml";
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
    @Parameters({"sourceFile","targetFile", "ymlFile","fileName"})
    @Test
    public void initialize(@Optional("null") String sourceFile, @Optional("null") String targetFile, @Optional("null") String ymlFile,@Optional("null") String fileName) {
        LOGGER = LogManager.getLogger(TestCaseBase2.class.getName());
        LOGGER.info("---------------- Before Suite -------------");

        sourceTestData = (sourceFile == null) ? sourceTestData : sourceFile;
        targetTestData = (targetFile  == null) ? targetTestData : targetFile;
        this.yml = (ymlFile == null) ? this.yml : ymlFile;
        this.fileName = (fileName == null) ? this.fileName : fileName;

        getTdUser();
        TD_DB = TD_USER + dbName;
        CommonSteps.TD_CONF = TD_CONF;
        CommonSteps.createTable(TD_DB, tdTargetTable);
        googleDriveApi = new Quickstart("tokens");
        setupYmlFile(yml);
    }

    /**
     * Generating Python input file to be read by data generator
     * Generating test data and storing in csv file
     * Asserting that there is no error
     */
  @Test(dependsOnMethods = {"initialize"}, retryAnalyzer = TestCaseBase.class)
    public void firstCreateTestData() {
        createPythonInputFile();
        String result = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/", Constant.RESOURCE_PATH));
        assertTrue(!result.contains("error"));
    }

    /**
     * Exporting csv file to Google Drive using google drive Api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Test(dependsOnMethods = {"firstCreateTestData"}, retryAnalyzer = TestCaseBase.class)
    public void firstExportDataToGoogleDrive() throws IOException,InterruptedException {
        fileId = googleDriveApi.uploadFile("upload_csv.csv","text/csv",sourceTestData);
        assertTrue(fileId != null);
    }

    /**
     * Replacing the file Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job is successful
     */
    @Test(dependsOnMethods = {"firstExportDataToGoogleDrive"}, retryAnalyzer = TestCaseBase.class)
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + fileId);
        result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /**
     * Querying the TD table to select all column values except time column
     * Store the result in a target file
     */
    @Test (dependsOnMethods = {"importDataFromGoogleDriveToTd"}, retryAnalyzer = TestCaseBase.class)
    public void exportDataFromTdToCsv() {
        result = CommonSteps.exportTdDataToFile(TD_DB, "presto", "csv", targetTestData, tdQuery);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /**
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Test(dependsOnMethods = {"exportDataFromTdToCsv"}, retryAnalyzer = TestCaseBase.class)
    public void compareData() {
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null);
        assertTrue(result);
    }

    @Override
    public void resetFileContent(){

    }
    /**
     * Delete the file in Google Drive
     * Delete the Database and Table
     */
    @AfterTest
    public void cleanupBefore() {
        //super.afterSuite();
        cleanupYmlFile(yml);
        googleDriveApi.deleteFile(fileId);
    }

    /**
     * Generating Python input file to be read by data generator
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
     * Merging Files from a folder to combine in single file for data comparision
     */
    public void mergeCsvFiles(String file1, String file2) {
        new GoogleDriveUtil().mergeFiles(file1, file2, sourceTestData);
    }

    /**
     * Generating another Python input file with different schema to be read by data generator
     */
   public void createSecondPythonInputFile() {
        PYTHON_INPUT = "input = {\n" +
                "  \"num_of_row\": %d,\n" +
                "  \"delimiter\": \"%s\",\n" +
                "  \"num_of_column\": %d,\n" +
                "  \"headers\": %s,\n" +
                "  \"column_types\": %s,\n" +
                "  \"fileName\": \"%s\"\n" +
                "}";
       PYTHON_INPUT = String.format(PYTHON_INPUT, 5, ",", 7,
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", ID, NAME, EMAIL, AGE, PHONE,DATE, TIME),
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", "id", "name", "email", "number,1,100","number,1000000000,9999999999" ,"date,2015-01-01,2018-01-01", "time"),this.fileName);


        String[]  lines = {PYTHON_INPUT};
        FileUtil.writeFile(PYTHON_INPUT_FILE, lines);
    }

    /**
     * Generating a test file with different schema
     */
    public String createDifferentSchemaTestData() {
        createSecondPythonInputFile();
        String result2 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/2_", Constant.RESOURCE_PATH));
        return result2;
    }

    /**
     * Generating another Python input file in tsv format to be read by data generator
     */
    public  void createTsvPythonInputFile() {
        this.fileName = "output.tsv";
         PYTHON_INPUT = "input = {\n" +
                "  \"num_of_row\": %d,\n" +
                "  \"delimiter\": \"%s\",\n" +
                "  \"num_of_column\": %d,\n" +
                "  \"headers\": %s,\n" +
                "  \"column_types\": %s,\n" +
                "  \"fileName\": \"%s\"\n" +
                "}";

        PYTHON_INPUT  = String.format(PYTHON_INPUT, 5, "\t", 6,
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", ID, NAME, EMAIL, AGE, DATE, TIME),
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\",  \"%s\"]", "id", "name", "email", "number,1,100", "date,2015-01-01,2018-01-01", "time"),
                fileName);


        String[]  lines = {PYTHON_INPUT};
        FileUtil.writeFile(PYTHON_INPUT_FILE, lines);
    }

    /**
     * Generating a test file in tsv format
     */
    public String createTsvTestData() {
        createTsvPythonInputFile();
        String result2 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/tsv/2_", Constant.RESOURCE_PATH));
        return result2;
    }

    /**
     * Setting the required variables in yml file from environment variables
     */
    protected static void setupYmlFile(String yml){
        new YmlFileUtil().setRefreshToken(yml," refresh_token:","  refresh_token: "+System.getenv("GD_REFRESH_TOKEN"));
        new YmlFileUtil().setRefreshToken(yml," client_id:","  client_id: "+System.getenv("GD_CLIENT_ID"));
        new YmlFileUtil().setRefreshToken(yml," client_secret:","  client_secret: "+System.getenv("GD_CLIENT_SECRET"));
    }
    /**
     * Removing the required variables in yml file from environment variables
     */
    protected static void cleanupYmlFile(String yml){
        new YmlFileUtil().setRefreshToken(yml,"  refresh_token: "+System.getenv("GD_REFRESH_TOKEN"),"  refresh_token: "+"");
        new YmlFileUtil().setRefreshToken(yml," client_id: "+System.getenv("GD_CLIENT_ID"),"  client_id: "+"");
        new YmlFileUtil().setRefreshToken(yml," client_secret: "+System.getenv("GD_CLIENT_SECRET"),"  client_secret: "+"");
    }
}


