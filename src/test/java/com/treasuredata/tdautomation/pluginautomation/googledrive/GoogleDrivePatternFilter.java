package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;


/** 3.9 Verify file upload using “Filename Pattern” in New Source dialog **/
@Listeners(TestCaseBase.class)

public class GoogleDrivePatternFilter extends GoogleDriveInputRefactor2 {


    public static String YML = String.format("%spluginautomation/googledrive/yml/load_pattern.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/csv/2_output.csv", Constant.RESOURCE_PATH);
    public String folderId,fileId1,fileId2;

    public GoogleDrivePatternFilter(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Generating 2 csv files
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
     * creating a folder in Google Drive using api call
     * Exporting csv files to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException{
        folderId = googleDriveApi.createFolder("FileInFolder");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file_one.csv","text/csv",file1);
        fileId2 = googleDriveApi.uploadFileInFolder(folderId,"file_two.csv","text/csv",file2);
        System.out.println("This is folder id:"+folderId);
        assertTrue(folderId != null);
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Replacing the file Id generated in previous step in yml file
     * Replacing the filename pattern  in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job is successful
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().setFileIdInYml(YML, "  id:", "  id: " + folderId);
        new YmlFileUtil().setFileIdInYml(YML, "  filename_pattern:", "  filename_pattern: " + "one");
        String result = CommonSteps.transferDataToTd(YML, TD_DB, tdTargetTable);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Target file should contain the contents of file1 only
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void compareData() {
        sourceTestData = file1;
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null);
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
        cleanupYmlFile(YML);
    }

}

