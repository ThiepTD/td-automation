package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import org.testng.ITestContext;
import org.testng.annotations.*;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/** 3.19 Verify Incremental Loading when a file moved from one folder to another folder@Listeners(GoogleDriveMovedFileFolderIncremental.class) **/
@Listeners(TestCaseBase.class)

public class GoogleDriveMovedFileFolderIncremental extends GoogleDriveIncreamentalFolder {

    public static String yml = String.format("%spluginautomation/googledrive/yml/load_folder_append.yml", Constant.RESOURCE_PATH);
    public String folderId1, folderId2, fileId, fileId4;

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveMovedFileFolderIncremental() {
        super(null, yml, null, null);
    }

    /** Overriding createTestData method from GoogleDriveIncreamentalFolder
     * Generating test data and storing in csv file
     * Asserting that there is no error
     */
    @Override
    public void firstCreateTestData() {
        createPythonInputFile();
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/1_", Constant.RESOURCE_PATH));
        assertTrue(!result1.contains("error"));
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveIncreamentalFolder
     * Creating a folder in google drive using api
     * Exporting csv file to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        folderId1 = googleDriveApi.createFolder("Moved_Folder1");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId1, "file1.csv", "text/csv", file1);
        assertTrue(folderId1 != null);
    }

    /** Overriding createConnectorSessionV2 method from GoogleDriveIncreamentalFolder
     * Replacing the folder Id generated in previous step in yml file
     * Create a new Connector session
     * Asserting that there is no error
     */
    @Override
    public void createConnectorSessionV2() {
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + folderId1);
        String result = CommonSteps.createConnectionSession(CONNECTOR_SESSION, null,TD_DB, tdTargetTable, yml);
        assertTrue(result.contains("Config Diff"));
    }

    /** Overriding secondCreateTestData method from GoogleDriveIncreamentalFolder
     * Generating two other test  csv files
     * Asserting that there is no error
     */
    @Override
    public void secondCreateTestData() {
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/2_", Constant.RESOURCE_PATH));
        String result2 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/3_", Constant.RESOURCE_PATH));
        assertTrue(!result1.contains("error") && !result2.contains("error"));
    }

    /** Overriding uploadAnotherFileInGdFolder method from GoogleDriveIncreamentalFolder
     * Create a folder in Google Drive using api
     * Upload the csv files in google drive folder using google drive api
     * Asserting that there is no error
     */
    @Override
    public void uploadAnotherFileInGdFolder() throws IOException {
        folderId2 = googleDriveApi.createFolder("Moved_Folder22");
        fileId2 = googleDriveApi.uploadFileInFolder(folderId2, "file2.csv", "text/csv", file2);
        fileId3 = googleDriveApi.uploadFileInFolder(folderId2, "file3.csv", "text/csv", file3);
        assertTrue(fileId3 != null);
    }

    /** Overriding secondCallDataTransferToTdV2 method from GoogleDriveIncreamentalFolder
     * Move the file 2 from folder2 to folder1 using api
     * Run the Connector session
     * Asserting that there is no error
     */
    @Override
    public void secondCallDataTransferToTdV2() {
        String result = "";
        try {
            googleDriveApi.moveFileBetweenFolders(fileId2, folderId1);
            Thread.sleep(60000);
            result = CommonSteps.runConnectionSession(TD_CONF, CONNECTOR_SESSION);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        assertTrue(result.contains(": success"));
    }

    /** Overriding secondCompareData method from GoogleDriveIncreamentalFolder
     * Merge file1 and file2 in source file for comparision
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void secondCompareData() {
        new GoogleDriveUtil().mergeFiles(file1, file2, sourceTestData);
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }

    /**
     * Overriding cleanupBefore method from GoogleDriveIncreamentalFolder
     * Delete connection session
     * Delete the file in Google Drive
     */
    @AfterTest
    @Override
    public void onFinish(ITestContext Result) {
        LOGGER.info("---------------- ON FINISH -------------");
        CommonSteps.deleteConnectionSession(TD_CONF, CONNECTOR_SESSION);
        cleanupYmlFile(yml);
        googleDriveApi.deleteFile(folderId1);
        googleDriveApi.deleteFile(folderId2);
    }
}

