package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import org.testng.annotations.*;

/**3.15 Verify Incremental Loading for Folder
//( without “Modified After”)**/

@Listeners(TestCaseBase.class)

public class GoogleDriveFolderIncrementalAppend extends GoogleDriveIncreamentalFolder{

    public static String ymlFile = String.format("%spluginautomation/googledrive/yml/load_folder_append.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveFolderIncrementalAppend(){
        super(null,ymlFile,null,null);
    }
}
