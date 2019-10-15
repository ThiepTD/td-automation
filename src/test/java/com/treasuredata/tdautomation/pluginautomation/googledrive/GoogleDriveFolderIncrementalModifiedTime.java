package com.treasuredata.tdautomation.pluginautomation.googledrive;


import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.*;
import org.testng.annotations.*;
import java.io.IOException;
import static org.testng.Assert.assertTrue;

/**3.16 Verify Incremental Loading for Folder
 //( with “Modified After”)**/
@Listeners(TestCaseBase.class)

public class GoogleDriveFolderIncrementalModifiedTime extends GoogleDriveIncreamentalFolder{

    public static String ymlFile = String.format("%spluginautomation/googledrive/yml/load_append_folder_modified.yml", Constant.RESOURCE_PATH);

    /**
     * Defining Constructors for intializing variables
     */
    public GoogleDriveFolderIncrementalModifiedTime(){
        super(null,ymlFile,null,null);
    }

    /** Overriding firstExportDataToGoogleDrive method from GoogleDriveIncreamentalFolder
     * Set the modified time in yml file
     * Wait for few seconds and  upload another file to google drive using api
     * Exporting csv files to Google Drive folder using google drive Api
     * Storing the file Id returned by api call
     * Asserting that folder id is not null
     */
    @Override
    public void firstExportDataToGoogleDrive() throws IOException{
        folderId = googleDriveApi.createFolder("FolderIncrementModifiedTime");
        fileId1 = googleDriveApi.uploadFileInFolder(folderId,"file1.csv","text/csv",file1);

        try {
            Thread.sleep(60000);
        }catch(InterruptedException ex){
            System.out.println(ex);
        }
        new YmlFileUtil().replaceModifiedTimeInYml(yml);
        fileId2 = googleDriveApi.uploadFileInFolder(folderId,"file2.csv","text/csv",file2);
        assertTrue(folderId != null);
    }

    /** Overriding compareData method from GoogleDriveIncreamentalFolder
     * Merge file1 and file3 in source file for comparision
     * Compare the Source file and Target file
     * Assert that the comparision returns True
     */
    @Override
    public void secondCompareData() {
        new GoogleDriveUtil().mergeFiles(file2,file3,sourceTestData);
        boolean result = CsvUtil.compareCsv(sourceTestData, targetTestData, null, null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        compareDataAssertion(result);
    }
}



