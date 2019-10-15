package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;


/** 3.27 Verify Reuse of a File Id
 Of a deleted file for import **/
@Listeners(TestCaseBase.class)

public class GoogleDriveDeletedFileImport extends GoogleDriveInputRefactor2 {


    public static String yml = String.format("%spluginautomation/googledrive/yml/load_csv.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveDeletedFileImport(){
        super(null,yml,sourceTestData,targetTestData,"output.csv");
    }


    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Delete the file uploaded in google drive using api call
     * Replacing the file Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job log contains error message
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        googleDriveApi.deleteFile(fileId);
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + fileId);
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
        cleanupYmlFile(yml);

    }
}

