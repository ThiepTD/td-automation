package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import org.testng.annotations.*;


/** 3.4 Verify import data from specific file (csv file)
 * This class extends basic test scenario from GoogleDriveInputRefactor2
 *   Generate Csv test data file
 *   Use the Api to import generated test file to Google Drive
 *   Call td connector:issue with load_csv.yml to import data from Google Drive to Treasure Data
 *   Export data from tables in TD to a csv files
 *   Make data comparison
 *   Print out the result
 */

@Listeners(TestCaseBase.class)

public class GoogleDriveCsvImport extends GoogleDriveInputRefactor2 {


    public static String YML = String.format("%spluginautomation/googledrive/yml/load_csv.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);

    public GoogleDriveCsvImport(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }


}


