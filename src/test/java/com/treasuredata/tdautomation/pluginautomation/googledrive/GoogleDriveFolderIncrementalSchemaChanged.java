package com.treasuredata.tdautomation.pluginautomation.googledrive;


import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import org.testng.annotations.*;
import static org.testng.Assert.assertTrue;

/**  3.23 Verify Incremental Loading for File
 //( with changed schema)**/
@Listeners(TestCaseBase.class)

public class GoogleDriveFolderIncrementalSchemaChanged extends GoogleDriveIncreamentalFolder{

    public static String ymlFile = String.format("%spluginautomation/googledrive/yml/load_folder_append.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveFolderIncrementalSchemaChanged(){
        super(null,ymlFile,null,null);
    }

    /** Overriding secondCreateTestData from GoogleDriveIncreamentalFolder
     * Generating another test data having different schema and storing in csv file
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

        String result = CmdUtil.generateData(String.format("%spluginautomation/googledrive/csv/3_", Constant.RESOURCE_PATH));
        assertTrue(!result.contains("error"));
    }

    /** Overriding secondCompareData from GoogleDriveIncreamentalFolder
     * Merge file1 and file2 in source file for comparision
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void secondCompareData() {
        new GoogleDriveUtil().mergeFiles(file1,file2,sourceTestData);
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }
}
