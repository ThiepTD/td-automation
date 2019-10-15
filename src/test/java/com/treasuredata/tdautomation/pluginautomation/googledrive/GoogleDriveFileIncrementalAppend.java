package com.treasuredata.tdautomation.pluginautomation.googledrive;




import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import org.testng.annotations.*;

/**3.21 Verify Incremental Loading for a File
 (“without “Modified After”)
 **/
@Listeners(TestCaseBase.class)

public class GoogleDriveFileIncrementalAppend extends GoogleDriveFileIncremental {

    public static String ymlFile = String.format("%spluginautomation/googledrive/yml/load_append.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables in super class
     */
    public GoogleDriveFileIncrementalAppend(){
        super(null,ymlFile,null,null);
    }
}
