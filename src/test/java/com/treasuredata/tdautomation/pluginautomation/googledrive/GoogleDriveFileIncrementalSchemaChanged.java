package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

/**3.23 Verify Incremental Loading for File
//( with changed schema)
 **/
@Listeners(TestCaseBase.class)

public class GoogleDriveFileIncrementalSchemaChanged extends GoogleDriveFileIncremental {

    public static String ymlFile = String.format("%spluginautomation/googledrive/yml/load_append_modified.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveFileIncrementalSchemaChanged() {
        super(null, ymlFile, null, null);
    }

    /** Overriding createConnectorSessionV2 from GoogleDriveFileIncremental
     * Creating another csv file with different schema
     * Asserting that there is no error
     */
    @Override
    public void secondCreateTestData() {
        String PYTHON_INPUT = "input = {\n" +
                "  \"num_of_row\": %d,\n" +
                "  \"delimiter\": \"%s\",\n" +
                "  \"num_of_column\": %d,\n" +
                "  \"headers\": %s,\n" +
                "  \"column_types\": %s,\n" +
                "  \"fileName\": \"%s\"\n" +
                "}";
        PYTHON_INPUT = String.format(PYTHON_INPUT, 5, ",", 9,
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", ID, NAME, EMAIL, AGE, DATE, TIME,BOOLEAN,DECIMAL,SPECIAL),
                String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\"]", "id", "name", "email", "number,1,100", "date,2015-01-01,2018-01-01", "time","bool","float,2,5","special_char,5"),"output.csv");
        String[] lines = {PYTHON_INPUT};
        FileUtil.writeFile(PYTHON_INPUT_FILE, lines);


        String result = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/2_", Constant.RESOURCE_PATH));
        assertTrue(!result.contains("error"));
    }

    /** Overriding secondCompareData from GoogleDriveFileIncremental
     * Target file should only contain the contents from file1
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void secondCompareData() {
        sourceTestData = file1;
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }
}

