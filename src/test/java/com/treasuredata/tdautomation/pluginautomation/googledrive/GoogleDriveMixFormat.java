package com.treasuredata.tdautomation.pluginautomation.googledrive;

import static org.testng.Assert.assertTrue;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import org.testng.annotations.*;
import java.io.*;


/** 3.8 Verify import from folder having files in different formats

 *   This class extends basic test scenario from GoogleDriveInputRefactor2
 *   Generate 1 Csv and 1 tsv test data files
 *   Use the Api to create a folder and  import generated test files to Google Drive in that particular folder
 *   Call td connector:issue with load_csv.yml to import data from Google Drive to Treasure Data
 *   Expect the output log will show the error message
 *
 */
@Listeners(TestCaseBase.class)
public class GoogleDriveMixFormat extends GoogleDriveInputRefactor2 {


    /** Defining variables **/

    public static String YML = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_mix_formats.yml";

    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/tsv/2_output.tsv", Constant.RESOURCE_PATH);
    public static String targetTestData = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);

    private String folderId, fileId1,fileId2;

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveMixFormat(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Generating 1 csv test file and 1 tsv test file
     * Asserting that there is no error
     */
    @Override
    public void firstCreateTestData() {
        createPythonInputFile();
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/1_", Constant.RESOURCE_PATH));
        String result2 = createTsvTestData();
        assertTrue(!result1.contains("error") && !result2.contains("error") );
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Exporting csv and tsv files to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        folderId = googleDriveApi.createFolder("FileInFolder");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.csv","text/csv",file1);
        fileId2 = googleDriveApi.uploadFileInFolder(folderId,"file2.tsv","text/tab-separated-values",file2);
       assertTrue(folderId != null);
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Replacing the folder Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that it shows error message
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().setFileIdInYml(YML, "  id:", "  id: " + folderId);
        String result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        assertTrue(result.contains("This folder must contain files of the same type. Currently, multiple file types are contained in this folder."));
    }

    /** Overriding exportDataFromSourceTd method from GoogleDriveInputRefactor2
     *  Do not need to export data to csv
     */
    @Override
    public void exportDataFromTdToCsv() {}

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Do not need to compare data
     */
    @Override
    public void compareData() {
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