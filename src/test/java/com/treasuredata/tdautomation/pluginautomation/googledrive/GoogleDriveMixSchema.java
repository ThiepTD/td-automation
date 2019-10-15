package com.treasuredata.tdautomation.pluginautomation.googledrive;

import static org.testng.Assert.assertTrue;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import org.testng.annotations.*;

import java.io.*;


/** 3.7 Verify import from a folder having files with different schema

 *   This class extends basic test scenario from GoogleDriveInputRefactor2
 *   Generate 1 Csv with default schema and 1 csv file with different schema
 *   Use the Api to create a folder and  import generated test files to Google Drive in that particular folder
 *   Call td connector:issue with load_csv.yml to import data from Google Drive to Treasure Data
 *   Expect that connector uploads data from file which matches the schema defined in Yml file
 *
 */
@Listeners(TestCaseBase.class)
public class GoogleDriveMixSchema extends GoogleDriveInputRefactor2 {


   /** Defining variables **/
    public static String yml = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_folder.yml";

    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/csv/2_output.csv", Constant.RESOURCE_PATH);
    public static String targetTestData = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    private String folderId, fileId1,fileId2;

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveMixSchema(){
        super(null,yml,sourceTestData,targetTestData,"output.csv");
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Generating two csv test files having different schema
     * Asserting that there is no error
     */
    @Override
    public void firstCreateTestData() {
        createPythonInputFile();
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/1_", Constant.RESOURCE_PATH));
        try {
            Thread.sleep(6000);
        }
        catch(InterruptedException ex){
            System.out.println(ex);
        }
        String result2 = createDifferentSchemaTestData();
        assertTrue(!result1.contains("error") && !result2.contains("error"));
    }


    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Create a folder using Google drive api
     * Exporting csv files to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        folderId = googleDriveApi.createFolder("FileInFolder");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.csv","text/csv",file1);
        fileId2 = googleDriveApi.uploadFileInFolder(folderId,"file2.csv","text/csv",file2);
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
        result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }


    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Assert that the target file only contains data from file having schema same as defined in yml file
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void compareData() {
        boolean result = CsvUtil.compareCsv(file1, targetTestData, null, null);
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


