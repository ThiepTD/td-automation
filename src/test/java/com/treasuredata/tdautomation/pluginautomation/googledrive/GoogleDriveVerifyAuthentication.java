package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import org.testng.annotations.*;
import java.io.IOException;

import static org.testng.Assert.assertTrue;


/** 3.28 Verify that authentication created using one account can not access the files in the other google account **/
@Listeners(TestCaseBase.class)

public class GoogleDriveVerifyAuthentication extends GoogleDriveInputRefactor2 {


    public static String yml = String.format("%spluginautomation/googledrive/yml/load_csv.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveVerifyAuthentication(){
        super(null,yml,sourceTestData,targetTestData,"output.csv");
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Initialize google api with different account
     * Exporting csv file to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        googleDriveApi = new Quickstart("token");
        fileId = googleDriveApi.uploadFile("upload_csv.csv","text/csv",sourceTestData);
        assertTrue(fileId != null);
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Replacing the file Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job log contains error message
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + fileId);
        result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
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
    public void compareData() {}

    /**
     * Overriding cleanupBefore method from GoogleDriveInputRefactor2
     * Delete connection session
     * Delete the file in Google Drive
     */
    @AfterTest
    @Override
    public void cleanupBefore() {
        googleDriveApi.deleteFile(fileId);
        cleanupYmlFile(yml);
    }

}
