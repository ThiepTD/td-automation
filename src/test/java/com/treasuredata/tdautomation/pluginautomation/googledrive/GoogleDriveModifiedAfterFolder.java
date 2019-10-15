package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**3.14 Verify file upload using “Modified After” in new source dialog **/
@Listeners(TestCaseBase.class)

public class GoogleDriveModifiedAfterFolder extends GoogleDriveInputRefactor2 {


    public static String YML = String.format("%spluginautomation/googledrive/yml/load_modified_folder.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/csv/2_output.csv", Constant.RESOURCE_PATH);
    public String folderId,fileId1,fileId2;

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveModifiedAfterFolder(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Generating 2 test csv files
     * Asserting that there is no error
     */
    @Override
    public void firstCreateTestData() {
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/1_", Constant.RESOURCE_PATH));
        String result2 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/2_", Constant.RESOURCE_PATH));
        assertTrue(!result1.contains("error") && !result2.contains("error"));
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Exporting csv file to Google Drive folder using google drive Api
     * Set the modified time in yml file
     * Export another csv file to google drive folder
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException{

        folderId = googleDriveApi.createFolder("FileInFolder");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.csv","text/csv",file1);
        new YmlFileUtil().replaceModifiedTimeInYml(yml);
        try {
            Thread.sleep(60000);
        }
        catch(InterruptedException ex){
            System.out.println(ex);
        }
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
        String result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * target file should only contains content of file2
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void compareData() {
        sourceTestData = file2;
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null);
        assertTrue(result);
    }

    /**
     * Overriding cleanupBefore method from GoogleDriveInputRefactor2
     * Delete the folder in Google Drive
     */
    @AfterTest
    @Override
    public void cleanupBefore() {
        googleDriveApi.deleteFile(folderId);
        cleanupYmlFile(YML);
    }

}