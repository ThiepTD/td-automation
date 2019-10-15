package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.testng.annotations.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/** /3.13 Import  files containing records with empty cells **/
@Listeners(TestCaseBase.class)

public class GoogleDriveRandomCellsImport extends GoogleDriveInputRefactor2 {


    public static String YML = String.format("%spluginautomation/googledrive/yml/empty_cells.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);
    public static String CSV_TARGET_RANDOM_EMPTYCELL_DATA = String.format("%spluginautomation/googledrive/csv/empty_cells.csv", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveRandomCellsImport(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveInputRefactor2
     * Replace some cells in generated test file with empty value
     * Exporting csv file to Google Drive using google drive Api
     * Storing the file Id returned by api call
     * Asserting that file id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException {
        GoogleDriveUtil.replaceFileWithRandomEmptyCells(sourceTestData," ",CSV_TARGET_RANDOM_EMPTYCELL_DATA);
        fileId = googleDriveApi.uploadFile("empty_cells.csv","text/csv",CSV_TARGET_RANDOM_EMPTYCELL_DATA);
        assertTrue(fileId != null);
    }

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Replace null values in target file with empty values
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void compareData(){
        boolean result = false;
        try {
            GoogleDriveUtil.replaceEmptyWithNull(targetTestData, null, " ", sourceTestData);
            result = CsvUtil.compareCsv(CSV_TARGET_RANDOM_EMPTYCELL_DATA, sourceTestData, null, null);
        }catch(FileNotFoundException ex){
            System.out.println(ex);
        }catch(IOException ex){
            System.out.println(ex);
        }
        assertTrue(result);
    }
}



