package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;
/**3.12  Import  files containing different types of data **/
@Listeners(TestCaseBase.class)

public class ImportDifferentTypesData extends GoogleDriveInputRefactor2 {

    /**Defining variables **/
    public static String ID = "id";
    public static String NAME = "name";
    public static String AGE = "age";
    public static String EMAIL = "email";
    public static String DATE = "date";
    public static String TIME = "mytime";
    public static String BOOLEAN = "mybool";
    public static String DECIMAL = "mydecimal";

    public String tdQuery = String.format("select id, name, email, age, TD_TIME_FORMAT(TD_TIME_PARSE(date), 'yyyy-MM-dd') as date, TD_TIME_FORMAT(TD_TIME_PARSE(mytime), 'HH:mm:ss') as mytime , mybool, mydecimal  from "+
            tdTargetTable);
    public static String GOOGLEDRIVE_TO_TD = "td -c %s connector:issue %s --database %s --table %s --auto-create-table -w";


    public static String YML = String.format("%spluginautomation/googledrive/yml/load_diff_dataTypes.yml", Constant.RESOURCE_PATH);
    public static String targetTestData  = String.format("%spluginautomation/googledrive/csv/target.csv", Constant.RESOURCE_PATH);
    public static String sourceTestData = String.format("%spluginautomation/googledrive/csv/output.csv", Constant.RESOURCE_PATH);

    public String python_input = String.format(PYTHON_INPUT, 5, ",", 8,
            String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", ID, NAME, EMAIL, AGE, DATE, TIME,BOOLEAN,DECIMAL/*,SPECIAL*/),
            String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", "id", "name", "email", "number,1,100", "date,2015-01-01,2018-01-01", "time","bool","float,2,5"),"output.csv");

    /**
     * Defining Constructors for intializing variables
     */
    public ImportDifferentTypesData(){
        super(null,YML,sourceTestData,targetTestData,"output.csv");
    }

    public void createPythonInputFile() {
        String[] lines = {python_input};
        FileUtil.writeFile(PYTHON_INPUT_FILE, lines);
    }

    /** Overriding createTestData method from GoogleDriveInputRefactor2
     * Generating a csv test file in different schema
     * Asserting that there is no error
     */
    @Override
    public void firstCreateTestData() {
        createPythonInputFile();
        String result = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/", Constant.RESOURCE_PATH));
        assertTrue(!result.contains("error"));
    }

    /** Overriding importDataFromGoogleDriveToTd method from GoogleDriveInputRefactor2
     * Replacing the file Id generated in previous step in yml file
     * Importing Data to Treasure Data table using Google Drive connector (requires yml file)
     * Asserting that job is successful
     */

    @Override
    public void importDataFromGoogleDriveToTd() {
        GOOGLEDRIVE_TO_TD = String.format(GOOGLEDRIVE_TO_TD, TD_CONF,YML, TD_DB, tdTargetTable);
        new YmlFileUtil().setFileIdInYml(YML, "  id:", "  id: " + fileId);
        String result = CmdUtil.executeUsingCmd(GOOGLEDRIVE_TO_TD);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /** Overriding exportDataFromSourceTd method from GoogleDriveInputRefactor2
     * Querying the TD table to select all column values except time column
     * Store the result in a target file
     */
    @Override
    public void exportDataFromTdToCsv() {
        result = CommonSteps.exportTdDataToFile(TD_DB, "presto", "csv", targetTestData, tdQuery);
        assertTrue(!(result.contains("error") || result.contains("killed")));
    }

    /** Overriding compareData method from GoogleDriveInputRefactor2
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void compareData() {
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }
}


