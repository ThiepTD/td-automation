package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/** 3.3 Verify import data from folders containing only csv files

 *   This class extends basic test scenario from GoogleDriveInputRefactor2
 *   Generate 2 Csv test data files
 *   Use the Api to create a folder and  import generated test files to Google Drive in that particular folder
 *   Call td connector:issue with load_csv.yml to import data from Google Drive to Treasure Data
 *   Export data from tables in TD to a csv files
 *   Make data comparison
 *   Print out the result
 */

@Listeners(TestCaseBase.class)

public class GoogleDriveCsvFilesFromFolder extends GoogleDriveInputRefactor2{

   /** Defining variables to store yml,source and target files **/
    public static String YML = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_folder.yml";
    public static String file1 = String.format("%spluginautomation/googledrive/csv/1_output.csv", Constant.RESOURCE_PATH);
    public static String file2 = String.format("%spluginautomation/googledrive/csv/2_output.csv", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);

    private String folderId,fileId1,fileId2;

    /**
     * Defining Constructors for intializing variables
     */
   public GoogleDriveCsvFilesFromFolder(){
       super(null,YML,sourceTestData,targetTestData,"output.csv");
   }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Generating two csv test files
     * Asserting that there is no error
     */
   @Override
    public void firstCreateTestData() {
        String result1 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/1_", Constant.RESOURCE_PATH));
        String result2 = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/2_", Constant.RESOURCE_PATH));
        assertTrue(!result1.contains("error") && !result2.contains("error"));
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Create a folder using google drive Api
     * Exporting csv files to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {

        folderId = googleDriveApi.createFolder("FileInFolder");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.csv","text/csv",file1);
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


