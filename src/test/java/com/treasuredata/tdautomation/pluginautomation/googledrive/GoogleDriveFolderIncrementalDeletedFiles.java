package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import org.testng.annotations.*;


import static org.testng.Assert.assertTrue;

/**3.15 Verify Incremental Loading for Folder
//( without “Modified After”)**/
@Listeners(TestCaseBase.class)

public class GoogleDriveFolderIncrementalDeletedFiles extends GoogleDriveIncreamentalFolder{

    public static String ymlFile = String.format("%spluginautomation/googledrive/yml/load_folder_append.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveFolderIncrementalDeletedFiles(){
        super(null,ymlFile,null,null);
    }

    /** Overriding createConnectorSessionV2 from GoogleDriveIncreamentalFolder
     * Replacing the folder Id generated in previous step in yml file
     * Delete file2 using google drive api
     * Create a new Connector session
     * Asserting that there is no error
     */
    @Override
    public void createConnectorSessionV2() {
        new YmlFileUtil().setFileIdInYml(yml, "  id:", "  id: " + folderId);
        googleDriveApi.deleteFile(fileId2);
        String result = CommonSteps.createConnectionSession(CONNECTOR_SESSION,null,TD_DB, tdTargetTable, yml);
        assertTrue(result.contains("Config Diff"));
    }

    /** Override secondCompareData from GoogleDriveIncreamentalFolder
     * Merge file1 and file3 in source file for comparision
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void secondCompareData() {
        new GoogleDriveUtil().mergeFiles(file1,file3,sourceTestData);
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }
}


