package com.treasuredata.tdautomation.pluginautomation.onedrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.*;
import com.treasuredata.tdautomation.util.Constant;
import static org.testng.Assert.assertTrue;

@Listeners(OneDriveInputBackup.class)

public class OneDriveInputBackup extends TestCaseBase {

    public static String QUERIES = "Queries";
    public static String CLICKS = "Clicks";
    public static String IMPRESSIONS = "Impressions";
    public static String CTR = "CTR";
    public static String POSITION = "Position";
    public static String TIME = "Time";
    public static String TD_TARGET_TABLE = "from_3rd_party";

    public static String CSV_SOURCE_TEST_DATA = String.format("%spluginautomation/onedrive/csv/common.csv", Constant.RESOURCE_PATH);
    public static String CSV_TARGET_TEST_DATA = String.format("%spluginautomation/onedrive/csv/target.csv", Constant.RESOURCE_PATH);

    public static String YML = Constant.RESOURCE_PATH + "pluginautomation/onedrive/yml/load.yml";

    public static String QUERY_TARGET_TABLE = String.format("select %s, %s, %s, %s, %s, %s from %s",
            QUERIES, CLICKS, IMPRESSIONS, CTR, POSITION, TIME, TD_TARGET_TABLE);

    @Parameters({"csvFile", "yml"})
    @Test
    public void initialize(@Optional("csv file") String csvFile, @Optional("yml file") String ymlFile){
        LOGGER = LogManager.getLogger(OneDriveInputBackup.class.getName());
        TD_DB = TD_USER + "_onedrive_db";
        CSV_SOURCE_TEST_DATA = (!csvFile.contains(".csv")) ? CSV_SOURCE_TEST_DATA : csvFile;
        YML = (!ymlFile.contains(".yml")) ? YML : ymlFile;
    }

    @Test(dependsOnMethods = {"initialize"}, groups = {"sanityTest"})
    public void importDataFromOneDrive() {
        CommonSteps.createTable(TD_CONF, TD_DB, TD_TARGET_TABLE); // create a new table, delete the table if it is existed before
        String result = CommonSteps.transferDataToTd(TD_CONF, YML, TD_DB, TD_TARGET_TABLE);
        assertTrue(result.contains(": success"));
    }

    @Test(dependsOnMethods = {"importDataFromOneDrive"}, groups = {"sanityTest"})
    public void exportTargetTableToCSVFile() {
        String result = CommonSteps.exportTdDataToFile(TD_CONF, TD_DB, "presto", "csv", CSV_TARGET_TEST_DATA, QUERY_TARGET_TABLE);
        assertTrue(result.contains(": success"));
    }

    @Test(dependsOnMethods = {"exportTargetTableToCSVFile"}, groups = {"sanityTest"})
    public void compareExportedDataVWithSourceData() {
        boolean result = CsvUtil.compareCsv(CSV_SOURCE_TEST_DATA, CSV_TARGET_TEST_DATA, null, null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0 );
        assertTrue(result);
    }
}