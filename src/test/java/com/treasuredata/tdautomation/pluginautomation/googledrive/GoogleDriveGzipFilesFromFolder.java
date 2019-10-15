package com.treasuredata.tdautomation.pluginautomation.googledrive;


import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;
 import java.io.File;

/** 3.3 Verify import data from folders containing only tsv files
This class extends basic test scenario from GoogleDriveInputRefactor2
 *   Generate 2 csv test data files
 *   Compress the test files using Google Drive Utility class
 *   Use the Api to create a folder and import compressed test file to Google Drive in that particular folder
 *   Call td connector:issue with load_csv.yml to import data from Google Drive to Treasure Data
 *   Export data from tables in TD to a csv files
 *   Make data comparison
 *   Print out the result
 */

@Listeners(GoogleDriveGzipFilesFromFolder.class)

public class GoogleDriveGzipFilesFromFolder extends GoogleDriveInputRefactor2 {

    /** Defining variables **/
    public static String YML = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_gz_folder.yml";

    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/csv/2_output.csv", Constant.RESOURCE_PATH);
    public static String file1_gz = String.format("%spluginautomation/googledrive/Gzip/1_output.gzip", Constant.RESOURCE_PATH);
    public static String file2_gz = String.format("%spluginautomation/googledrive/Gzip/2_output.gzip", Constant.RESOURCE_PATH);

    private String folderId,fileId1,fileId2;

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveGzipFilesFromFolder(){
        super(null,YML,null,null,"output.csv");
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
     * Compress the csv files to get  gzip files
     * Exporting gzip files to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        folderId = googleDriveApi.createFolder("GzipFilesFolder");
        new GoogleDriveUtil().compressGZIP(new File (file1), new File(file1_gz));
        new GoogleDriveUtil().compressGZIP(new File (file2), new File(file2_gz));

        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.csv.gz","application/x-gzip",file1_gz);
        fileId2 = googleDriveApi.uploadFileInFolder(folderId,"file2.csv.gz","application/x-gzip",file2_gz);
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

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Merge file1 and file2 in source file for comparision
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
   @Override
    public void compareData() {
        mergeCsvFiles(file1,file2);
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
        cleanupYmlFile(yml);
    }
    
   
    
}


