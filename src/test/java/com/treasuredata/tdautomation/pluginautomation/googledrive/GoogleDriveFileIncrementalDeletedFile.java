package com.treasuredata.tdautomation.pluginautomation.googledrive;


import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.testng.annotations.*;


import static org.testng.Assert.assertTrue;

/**3.24 Verify Incremental Loading for File
//(file deleted after first run)**/
@Listeners(TestCaseBase.class)

public class GoogleDriveFileIncrementalDeletedFile extends GoogleDriveFileIncremental {

    public static String ymlFile = String.format("%spluginautomation/googledrive/yml/load_replace.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables in super class
     */
    public GoogleDriveFileIncrementalDeletedFile(){
        super(null,ymlFile,null,null);
    }

    /**
     * Overriding secondCreateTestData in GoogleDriveFileIncremental
     */
    @Override
    public void secondCreateTestData(){}

    /**
     * Overriding updateFileContentInGd in GoogleDriveFileIncremental
     */
    @Override
    public void updateFileContentInGd(){}

    /** Overriding secondCallDataTransferToTdV2 in GoogleDriveFileIncremental
     * Delete file before importing data to td
     * Run the Connector session
     * Asserting that the result shows error message
     */
   @Override
    public void secondCallDataTransferToTdV2() {
        deleteFileInGd();
       String result = CommonSteps.runConnectionSession(TD_CONF, CONNECTOR_SESSION);
       assertTrue(result.contains(": error"));
    }

    /**
     * Overriding secondCallDataTransferToTdV2 in GoogleDriveFileIncremental
     * Compare the Source file and Target file from first connection session run
     * Assert that the comparision returns True
     */
    @Override
    public void secondCompareData() {
        boolean result = CsvUtil.compareCsv(file1, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }
}

