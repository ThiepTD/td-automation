package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Listeners;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/** 3.11 Import from a specific file which doesn’t contain any records
 */
@Listeners(TestCaseBase.class)

public class GoogleDriveEmptyFolder extends GoogleDriveInputRefactor2{


    public static String YML = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_empty_folder.yml";
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);

    private String folderId;

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveEmptyFolder(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * No need to generate any test data
     */

    @Override
    public void firstCreateTestData() {}

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Create a folder in google drive using google drive api
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {

        folderId = googleDriveApi.createFolder("EmptyFolder");
        assertTrue(folderId != null);
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Replacing the folder Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job is successful
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().setFileIdInYml(YML, "  id:", "  id: " + folderId);
        String result = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /** Overriding exportDataFromSourceTd method from GoogleDriveInputRefactor2
     * Querying the TD table to select all column values except time column
     * Store the result in a target file
     */
    @Override
    public void exportDataFromTdToCsv() {
        result = CommonSteps.exportTdDataToFileWithoutHeaders(TD_DB, "presto", "csv", targetTestData, tdQuery);
        assertTrue(!(result.contains("error") || result.contains("killed")) && result.contains("0 rows"));
    }

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Source file and target file length should be 0
     */
    @Override
    public void compareData() {
        boolean result =  new File(targetTestData).length() == 0;
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



