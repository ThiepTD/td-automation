package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import org.testng.annotations.*;


/** 3.21 Verify Incremental Loading for a File
 //(“without “Modified After”)(replace option)**/
@Listeners(TestCaseBase.class)

public class GoogleDriveFolderIncrementalReplace extends GoogleDriveIncreamentalFolder{

    public static String yml = String.format("%spluginautomation/googledrive/yml/load_folder_replace.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveFolderIncrementalReplace(){
        super(null,yml,null,null);
    }

    /** Overriding secondCompareData from GoogleDriveIncreamentalFolder
     * Target file should only contain the contents of file3
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void secondCompareData() {
        boolean result = CsvUtil.compareCsv(file3, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }
}

