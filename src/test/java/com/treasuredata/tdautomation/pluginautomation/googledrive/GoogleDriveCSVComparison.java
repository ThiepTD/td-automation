package com.treasuredata.tdautomation.pluginautomation.googledrive;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.treasuredata.tdautomation.util.Constant;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;


//Comparing test data for smaller and larger data sets
public class GoogleDriveCSVComparison{

    private String srcCsv;
    private String desCsv;
    
    public static Logger LOGGER = LogManager.getLogger(GoogleDriveCSVComparison.class.getName());

      @Test
    public void detailCompareDefaultDelimiter(){
        LOGGER.info("------------- Start running detailCompareDefaultDelimiter -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/googledrive/csv/drive_source.csv";
        desCsv = Constant.RESOURCE_PATH + "pluginautomation/googledrive/csv/drive_dest.csv";
        String[] headers = {"Queries","Clicks","Impressions","CTR","Position"};
        boolean result = (new GoogleDriveUtil()).compareCsv(srcCsv, desCsv, headers,',');
        assertTrue(result);
    }
 
     @Test
    public void detailMillionCompareDefaultDelimiter(){
    	 srcCsv = Constant.RESOURCE_PATH + "pluginautomation/googledrive/csv/source_million.csv";
         desCsv = Constant.RESOURCE_PATH + "pluginautomation/googledrive/csv/drive_dest_million.csv";
        LOGGER.info("------------- Start running detailMillionCompareDefaultDelimiter -------------");
        String[] headers = {"Queries","Clicks","Impressions","CTR","Position"};
        boolean result = (new GoogleDriveUtil()).compareCsv(srcCsv, desCsv, headers,',');
        assertTrue(result);
    }
    
    
}
