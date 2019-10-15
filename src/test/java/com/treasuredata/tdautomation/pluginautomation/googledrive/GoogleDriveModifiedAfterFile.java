package com.treasuredata.tdautomation.pluginautomation.googledrive;

import static org.testng.Assert.assertTrue;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import org.testng.annotations.*;

/**3.14 Verify file upload using “Modified After” in new source dialog **/
@Listeners(TestCaseBase.class)

public class GoogleDriveModifiedAfterFile extends GoogleDriveInputRefactor2 {


    public static String YML = String.format("%spluginautomation/googledrive/yml/load_modified_file.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);

    public GoogleDriveModifiedAfterFile(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Set the modified time in yml file
     * Wait and replace the file Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job is successful
     */
    @Override
    public void importDataFromGoogleDriveToTd() {
        new YmlFileUtil().replaceModifiedTimeInYml(YML);
        new YmlFileUtil().setFileIdInYml(YML, "  id:", "  id: " + fileId);
        try {
            Thread.sleep(60000);
        }
        catch(InterruptedException ex){
            System.out.println(ex);
        }
        result = CommonSteps.transferDataToTd(YML, TD_DB, tdTargetTable);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /** Overriding exportDataFromSourceTd method from GoogleDriveInputRefactor2
     * Querying the TD table to select all column values except time column
     * Store the result in a target file
     * Asert that the result contains 0 rows
     */
    @Override
    public void exportDataFromTdToCsv() {
        result = CommonSteps.exportTdDataToFile(TD_DB, "presto", "csv", targetTestData, tdQuery);
        assertTrue(result.contains("0 rows") );
    }

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * No need to compare files
     */
    @Override
    public void compareData() {}
}