package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertTrue;

/** 3.4 Verify import data from an unsupported file (png file)
 *   Uploading a png file to Google Drive
 *   Importing png file from Google Drive to Td and Expecting error message
 */
@Listeners(GoogleDrivePngImport.class)
public class GoogleDrivePngImport extends GoogleDriveInputRefactor2{


    public static String yml = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_csv.yml";
    /** Declaring a variable to store png file **/
    public static String sourceTestData = String.format("%spluginautomation/googledrive/png/image.png", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDrivePngImport(){
        super(null,yml,sourceTestData,null,"output.csv");
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Not generating any test data
     */

    @Override
    public void firstCreateTestData(){}

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Exporting png file to Google Drive using google drive Api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        fileId = googleDriveApi.uploadFile("upload_image.png","image/png",sourceTestData);
        assertTrue(fileId != null);
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Replacing the file Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that result shows the error message
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + fileId);
        String result  = CommonSteps.transferDataToTd(yml, TD_DB, tdTargetTable);
        assertTrue(result.contains("The specified file type for source import is invalid. Supported file types: csv, tsv, gzip of tsv, gzip of csv."));
    }

    /** Overriding exportDataFromSourceTd method from GoogleDriveInputRefactor2
     * Do not need to export data to csv
     */
    @Override
    public void exportDataFromTdToCsv() {}

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Do not need to compare data
     */
    @Override
    public void compareData() {}

    /**
     * Overriding cleanupBefore method from GoogleDriveInputRefactor2
     * Delete the file in Google Drive
     */
    @AfterTest
    @Override
    public void cleanupBefore() {
        googleDriveApi.deleteFile(fileId);
        cleanupYmlFile(yml);
    }
    
   
    
}


