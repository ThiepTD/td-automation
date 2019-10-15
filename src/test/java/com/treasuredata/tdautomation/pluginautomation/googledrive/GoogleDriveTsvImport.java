package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.*;
import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/** 3.4 Verify import data from specific file (tsv file)
 *   Call td connector:issue with load_tsv.yml to import data from Google Drive to Treasure Data
 *   Export data from tables in TD to a csv files
 *   Make data comparison
 *   Print out the result
 */
@Listeners(TestCaseBase.class)

public class GoogleDriveTsvImport extends GoogleDriveInputRefactor2 {



    public static String YML = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_tsv.yml";
    public static String sourceTestData = String.format("%spluginautomation/googledrive/tsv/output.tsv", Constant.RESOURCE_PATH);
    public static String targetTestData = String.format("%spluginautomation/googledrive/tsv/target.tsv", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveTsvImport(){super(null,YML,sourceTestData,targetTestData,"output.tsv");}

    /**
     * Overriding initialize method from GoogleDriveInputRefactor2 to set variables through xml file
     * Create Database and Table
     * Defining input file for python data generator to generate tsv file
     * Create instance of Google drive api class
     */
    @Parameters({"sourceFile","targetFile", "ymlFile","fileName"})
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
        setupYmlFile(YML);
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Generating Python input file to be read by data generator
     * Generating test data and storing in tsv file
     * Asserting that there is no error
     */

    @Override
    public void firstCreateTestData() {
        createTsvPythonInputFile();
        String result = CmdUtil.generateData(String.format("%spluginautomation/googledrive/tsv/", Constant.RESOURCE_PATH));
        assertTrue(!result.contains("error"));
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Exporting tsv file to Google Drive using google drive Api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        fileId = googleDriveApi.uploadFile("upload_tsv.tsv","text/tab-separated-values",sourceTestData);
        assertTrue(fileId != null);
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Replacing the file Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job is successful
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().setFileIdInYml(YML, "  id:", "  id: " + fileId);
        result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
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
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void compareData() {
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, '\t');
        assertTrue(result);
    }

    /**
     * Overriding cleanupBefore method from GoogleDriveInputRefactor2
     * Delete the file in Google Drive
     * Delete the Database and Table
     */
    @AfterTest
    @Override
    public void cleanupBefore() {
        System.out.println("cleanup ran");
        CommonSteps.deleteTable(TD_DB, tdTargetTable);
        cleanupYmlFile(YML);
        googleDriveApi.deleteFile(fileId);
    }
    
}


