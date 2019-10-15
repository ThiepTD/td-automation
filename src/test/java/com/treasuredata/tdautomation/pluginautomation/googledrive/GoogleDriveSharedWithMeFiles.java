package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import org.testng.annotations.*;
import java.io.IOException;

import static org.testng.Assert.assertTrue;


/** 3.6 Verify import data from Shared Files **/
@Listeners(TestCaseBase.class)

public class GoogleDriveSharedWithMeFiles extends GoogleDriveInputRefactor2 {


    public static String YML = String.format("%spluginautomation/googledrive/yml/load_csv.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    private String email;
    /**
     * Defining Constructors for intializing variables
     */
    @Parameters({"email"})
    public GoogleDriveSharedWithMeFiles(@Optional String email){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
        this.email = (email == null) ? "parul.saran@treasure-data.com" : email;

    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Use a different account to upload file(Personal account) in google drive
     * Exporting csv file to Google Drive using google drive Api
     * Change the file metadata to share with the other account using api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        googleDriveApi = new Quickstart("token");

        fileId = googleDriveApi.uploadFile("shared_file.csv","text/csv",sourceTestData);
        googleDriveApi.createPermissionForEmail(fileId,email);
        assertTrue(fileId != null);
    }
}
