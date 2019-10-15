package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

/**3.22 Verify Incremental Loading for a File
//(“with “Modified After”)**/
@Listeners(TestCaseBase.class)

public class GoogleDriveFileIncrementalModifiedTime extends GoogleDriveFileIncremental {

    public static String ymlFile = String.format("%spluginautomation/googledrive/yml/load_append_modified.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveFileIncrementalModifiedTime() {
        super(null, ymlFile, null, null);
    }

    /** Overriding createConnectorSessionV2 from GoogleDriveFileIncremental
     * Replacing the file Id generated in previous step in yml file
     * Change the Modified time in yml file
     * Create a new Connector session
     * Asserting that there is no error
     */
    @Override
    public void createConnectorSessionV2()  {
        try {
            Thread.sleep(6000);
        }catch(InterruptedException ex){
            System.out.println(ex);
        }
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + fileId);
        new YmlFileUtil().replaceModifiedTimeInYml(yml);

        String result =  CommonSteps.createConnectionSession(CONNECTOR_SESSION,null,TD_DB, TD_TABLE, yml);
        assertTrue(result.contains("Config Diff"));
    }

    /** Overriding secondCompareData from GoogleDriveFileIncremental
     * Target file should only contain the contents from file2
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void secondCompareData() {
        sourceTestData = file2;
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }
}
