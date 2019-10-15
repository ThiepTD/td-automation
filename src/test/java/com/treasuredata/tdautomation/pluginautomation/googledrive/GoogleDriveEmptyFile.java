package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import java.io.File;
import org.testng.annotations.*;
import java.io.IOException;
import static org.testng.Assert.assertTrue;

/**3.11 Import from a specific file which doesn’t contain any records **/
@Listeners(TestCaseBase.class)

public class GoogleDriveEmptyFile extends GoogleDriveInputRefactor2 {


    public static String YML = String.format("%spluginautomation/googledrive/yml/load_empty_file.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target_empty.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/source_empty.csv", Constant.RESOURCE_PATH);
    private File file;

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveEmptyFile(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Generating an empty csv test files using File class
     * Asserting that there is no error
     */
    @Override
    public void firstCreateTestData() {
        file = new File(sourceTestData);
        try {
            file.createNewFile();
        }
        catch(IOException ex){
            System.out.println(ex);
        }
        boolean result = (file.length() == 0);
        assertTrue(result);
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Exporting empty file to Google Drive using google drive Api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive()  throws IOException {
        fileId = googleDriveApi.uploadFile("empty_csv.csv","text/csv",sourceTestData);
        assertTrue(fileId != null);
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
     * Make sure the source and target file length is 0
     * Assert that the comparision returns True
     */
    @Override
    public void compareData() {
        boolean result = file.length()  == new File(targetTestData).length();
        assertTrue(result);
    }

}