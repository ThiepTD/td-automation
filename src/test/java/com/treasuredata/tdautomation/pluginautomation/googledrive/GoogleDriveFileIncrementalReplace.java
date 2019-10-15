package com.treasuredata.tdautomation.pluginautomation.googledrive;



import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import org.testng.annotations.*;

/** 3.21 Verify Incremental Loading for a File(replace option) **/
@Listeners(TestCaseBase.class)

public class GoogleDriveFileIncrementalReplace extends GoogleDriveFileIncremental {

    public static String ymlFile = String.format("%spluginautomation/googledrive/yml/load_replace.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveFileIncrementalReplace(){
        super(null,ymlFile,null,null);
    }

    /** Overriding secondCompareData from GoogleDriveFileIncremental
     * Target file should only contain contents from file2
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void secondCompareData() {
        boolean result = CsvUtil.compareCsv(file2, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }
}

