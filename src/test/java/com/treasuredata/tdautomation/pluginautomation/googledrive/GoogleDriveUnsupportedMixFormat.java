package com.treasuredata.tdautomation.pluginautomation.googledrive;


import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/** 3.7 Verify import from a folder having files with different schema unsupported mix format

 *   This class extends basic test scenario from GoogleDriveInputRefactor2
 *   Generate 1 Csv and 1 existing png test data files
 *   Use the Api to create a folder and  import generated test files to Google Drive in that particular folder
 *   Call td connector:issue with load_csv.yml to import data from Google Drive to Treasure Data
 *   Expect that only csv files will be uploaded
 *
 */
@Listeners(TestCaseBase.class)
public class GoogleDriveUnsupportedMixFormat extends GoogleDriveInputRefactor2 {


    public static String YML = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_mix_formats.yml";

    public static String sourceTestData;
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/png/image.png", Constant.RESOURCE_PATH);
    public static String targetTestData = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);

    private String folderId, fileId1,fileId2;

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveUnsupportedMixFormat(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }

    /**
     * Generating Python input file to be read by data generator
     * Generating test data and storing in csv file
     * Asserting that there is no error
     */
    @Override
    public void firstCreateTestData() {
        createPythonInputFile();
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/1_", Constant.RESOURCE_PATH));
        assertTrue(!result1.contains("error") );
    }

    /** Creating a folder using google drive Api
     * Exporting csv file and png file to Google Drive using google drive Api to specific folder
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        folderId = googleDriveApi.createFolder("UnsupportedFilesFolder");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.csv","text/csv",file1);
        fileId2 = googleDriveApi.uploadFileInFolder(folderId,"image.png","image/png",file2);
        assertTrue(folderId != null );
    }

    /**
     * Replacing the folder Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job is successful
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().setFileIdInYml(YML, "  id:", "  id: " + folderId);
        result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        assertTrue(result.contains("success"));
    }

    /**
     * Compare the Source file and Target file
     * Only csv file should be imported
     * Assert that the comparision returns True
     */
    @Override
    public void compareData() {
        sourceTestData = file1;
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null);
        assertTrue(result);
    }

    /**
     * Delete the file in Google Drive
     * Delete the Database and Table
     */
    @AfterTest
    @Override
    public void cleanupBefore() {
        googleDriveApi.deleteFile(folderId);
        cleanupYmlFile(YML);
    }



}