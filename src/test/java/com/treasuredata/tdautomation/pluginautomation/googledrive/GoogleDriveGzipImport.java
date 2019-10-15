package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**  3.4 Verify import data from specific file (gzip file)
 *   Call td connector:issue with load_gz.yml to import data from Google Drive to Treasure Data
 *   Export data from tables in TD to a csv files
 *   Make data comparison
 *   Print out the result
 */

@Listeners(TestCaseBase.class)

public class GoogleDriveGzipImport extends GoogleDriveInputRefactor2 {


    public static String yml = Constant.RESOURCE_PATH + "pluginautomation/googledrive/yml/load_gz.yml";
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    public static String targetTestData = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);

    /** Declaring a variable to store compressed file **/
    public static String CSV_SOURCE_TEST_DATA_GZIP = String.format("%spluginautomation/googledrive/Gzip/output.gzip", Constant.RESOURCE_PATH);


    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveGzipImport(){
        super(null,yml,sourceTestData,sourceTestData,"output.csv");
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Generating a gzip file using GoogleDriveUtil compressGZIP method
     * Exporting gzip file to Google Drive using google drive Api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        new GoogleDriveUtil().compressGZIP(new File (sourceTestData), new File(CSV_SOURCE_TEST_DATA_GZIP));
        fileId = googleDriveApi.uploadFile("upload_gzip.csv.gz","application/x-gzip",CSV_SOURCE_TEST_DATA_GZIP);
        assertTrue(fileId != null);
    }

    /**
     * Overriding cleanupBefore method from GoogleDriveInputRefactor2
     * Delete the file in Google Drive
     */
    @AfterTest
    @Override
    public void cleanupBefore() {
        cleanupYmlFile(yml);
        googleDriveApi.deleteFile(fileId);

    }
    
   
    
}


