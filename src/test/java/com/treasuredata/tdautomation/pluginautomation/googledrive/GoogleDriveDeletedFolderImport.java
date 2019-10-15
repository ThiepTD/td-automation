package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**  3.27 Verify Reuse of a Folder Id Of a deleted folder for import **/
@Listeners(TestCaseBase.class)

public class GoogleDriveDeletedFolderImport extends GoogleDriveInputRefactor2 {


    public static String yml = String.format("%spluginautomation/googledrive/yml/load_folder.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/csv/2_output.csv", Constant.RESOURCE_PATH);
    public String folderId;

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveDeletedFolderImport(){
        super(null,yml,sourceTestData,targetTestData,"output.csv");
    }

    /**
     * Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Creating a folder in google drive using api call
     * Exporting csv file to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        folderId = googleDriveApi.createFolder("FolderIncrementAppend");
        fileId = googleDriveApi.uploadFileInFolder(folderId,"file1.csv","text/csv",sourceTestData);
        assertTrue(folderId != null);
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Delete the folder uploaded in google drive using api call
     * Replacing the folder Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job log contains error message
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        googleDriveApi.deleteFile(folderId);
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + folderId);
        String result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        assertTrue(result.contains("The specified Folder ID or File ID for source import cannot be found."));
    }

    /**
     * Overriding exportDataFromSourceTd method from GoogleDriveInputRefactor2
     * Do not need to export data to csv
     */
    @Override
    public void exportDataFromTdToCsv() {}

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Do not need to perform comparision
     */
    @Override
    public void compareData() {

    }
}

