package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/** 3.3 Verify import data from folders containing only tsv files

 *   This class extends basic test scenario from GoogleDriveInputRefactor2
 *   Generate 2 tsv test data files
 *   Use the Api to create a folder and import generated test file to Google Drive in that particular folder
 *   Call td connector:issue with load_csv.yml to import data from Google Drive to Treasure Data
 *   Export data from tables in TD to a csv files
 *   Make data comparison
 *   Print out the result
 */

@Listeners(TestCaseBase.class)

public class GoogleDriveTSVFilesFromFolder extends GoogleDriveInputRefactor2 {

    /** Defining variables **/
    public static String yml = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_folder_tsv.yml";
    public static String sourceTestData = String.format("%spluginautomation/googledrive/tsv/output.tsv", Constant.RESOURCE_PATH);
    public static String targetTestData = String.format("%spluginautomation/googledrive/tsv/target.tsv", Constant.RESOURCE_PATH);

    public static String file1 = String.format("%spluginautomation/googledrive/tsv/1_output.tsv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/tsv/2_output.tsv", Constant.RESOURCE_PATH);
    private String folderId,fileId1,fileId2;

    /**
     * Defining Constructors for intializing variables
     */

    public GoogleDriveTSVFilesFromFolder(){
        super(null,yml,sourceTestData,targetTestData,"output.tsv");
    }

    /**
     * Overriding initialize method from GoogleDriveInputRefactor2 to set variables through xml file
     * Create Database and Table
     * Defining input file for python data generator to generate tsv file
     * Create instance of Google drive api class
     */
    @Override
    public void initialize(@Optional("null") String sourceFile, @Optional("null") String targetFile, @Optional("null") String ymlFile,@Optional("null") String fileName) {
        LOGGER = LogManager.getLogger(GoogleDriveInputRefactor2.class.getName());
        LOGGER.info("---------------- Before Suite -------------");

        sourceTestData = (sourceFile == null) ? sourceTestData : sourceFile;
        targetTestData = (targetFile  == null) ? targetTestData : targetFile;
        this.yml = (ymlFile == null) ? this.yml : ymlFile;
        this.fileName = (fileName == null) ? this.fileName : ymlFile;

        getTdUser();
        TD_DB = TD_USER + dbName;
        CommonSteps.TD_CONF = TD_CONF;
        CommonSteps.createTable(TD_DB, tdTargetTable);
        googleDriveApi = new Quickstart("tokens");
        setupYmlFile(yml);
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Generating two tsv test files
     * Asserting that there is no error
     */
    @Override
    public void firstCreateTestData() {
        createTsvPythonInputFile();
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/tsv/1_", Constant.RESOURCE_PATH));
        String result2 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/tsv/2_", Constant.RESOURCE_PATH));
        assertTrue(!result1.contains("error") && !result2.contains("error"));
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Create a folder using google drive Api
     * Exporting tsv files to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        folderId = googleDriveApi.createFolder("TsvFilesFolder");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.tsv","text/tab-separated-values",file1);
        fileId2 = googleDriveApi.uploadFileInFolder(folderId,"file2.tsv","text/tab-separated-values",file2);
        assertTrue(folderId != null);
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Replacing the folder Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job is successful
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + folderId);
        String result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /** Overriding exportDataFromSourceTd method from GoogleDriveInputRefactor2
     * Querying the TD table to select all column values except time column
     * Store the result in a target file
     */
    @Override
    public void exportDataFromTdToCsv() {
        result = CommonSteps.exportTdDataToFile(TD_DB, "presto", "tsv", targetTestData, tdQuery);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Merge file1 and file2 in source file for comparision
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void compareData() {
        mergeCsvFiles(file1,file2);
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, '\t');
        assertTrue(result);
    }

    /**
     * Overriding cleanupBefore method from GoogleDriveInputRefactor2
     * Delete the file in Google Drive
     */
    @AfterTest
    @Override
    public void cleanupBefore() {
        googleDriveApi.deleteFile(folderId);
        cleanupYmlFile(yml);
    }
    
   
    
}


