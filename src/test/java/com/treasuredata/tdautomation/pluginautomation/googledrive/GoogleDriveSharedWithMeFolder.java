package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.testng.annotations.*;
import java.io.IOException;
import static org.testng.Assert.assertTrue;

/** 3.5 Verify import data from shared with me folder **/
@Listeners(TestCaseBase.class)
public class GoogleDriveSharedWithMeFolder extends GoogleDriveInputRefactor2 {


    public static String yml = String.format("%spluginautomation/googledrive/yml/load_folder.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/csv/2_output.csv", Constant.RESOURCE_PATH);
    String folderId,fileId1,fileId2;
    private String email;
   /**
    * Defining Constructors for intializing variables
    */
   @Parameters({"email"})
    public GoogleDriveSharedWithMeFolder(@Optional String email){
        super(null,yml,sourceTestData,targetTestData,"output.csv");
        this.email = (email == null) ? "parul.saran@treasure-data.com" : email;
    }

   /** Overriding createTestData method from GoogleDriveInputRefactor2
    * Generating two csv test files
    * Asserting that there is no error
    */
   @Override
    public void firstCreateTestData() {
        createPythonInputFile();
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/1_", Constant.RESOURCE_PATH));
        String result2 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/2_", Constant.RESOURCE_PATH));
        assertTrue(!result1.contains("error") && !result2.contains("error"));
    }

   /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
    * Create a folder in Google drive using api
    * Use a different account to upload file(Personal account) in google drive folder
    * Exporting csv file to Google Drive using google drive Api
    * Change the folder metadata to share with the other account using api
    * Storing the file Id returned by api call
    * Asserting that folder id is not null
    */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        googleDriveApi = new Quickstart("token");
        folderId = googleDriveApi.createFolder("SharedFolder");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.csv","text/csv",file1);
        fileId2 = googleDriveApi.uploadFileInFolder(folderId,"file2.csv","text/csv",file2);
        googleDriveApi.createPermissionForEmail(folderId,email);
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

   /** Overriding compareData method from GoogleDriveInputRefactor2
    * Merge file1 and file2 in source file for comparision
    * Compare the Source file and Target file
    * Assert that the comparision returns True
    */
    @Override
    public void compareData() {
        new GoogleDriveUtil().mergeFiles(file1,file2,sourceTestData);
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null);
        assertTrue(result);
    }

   /**
    * Overriding cleanupBefore method from GoogleDriveInputRefactor2
    * Delete the file in Google Drive
    */

   @Override
    public void cleanupBefore() {
        googleDriveApi.deleteFile(folderId);
       cleanupYmlFile(yml);
    }
}
