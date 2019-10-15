package com.treasuredata.tdautomation.pluginautomation.unittests;

import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import com.treasuredata.tdautomation.util.SearchUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CsvTest{

    private String srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(CsvTest.class.getName());

    @Test
        public void detailCompareDefaultDelimiter(){
            LOGGER.info("------------- Start running detailCompareDefaultDelimiter -------------");
            srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/1.csv";
            desCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/2.csv";
            boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null,null, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
            assertTrue(result);
    }

    // Compare csv value with default compare option
    @Test
    public void detailCompareDefaultDelimiterWithCompareOption(){
        LOGGER.info("------------- Start running detailCompareDefaultDelimiterWithCompareOption -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/output.csv";
        desCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/target.csv";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null,null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        assertTrue(result);
    }

    // Compare csv value with no (null) compare option
    @Test
    public void detailCompareDefaultDelimiterNoCompareOption(){
        LOGGER.info("------------- Start running detailCompareDefaultDelimiterNoCompareOption -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/output.csv";
        desCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/target.csv";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null,null);
        assertTrue(result);
    }

    // Compare csv value with Boolean compare option
    @Test
    public void detailCompareDefaultDelimiterBooleanOption(){
        LOGGER.info("------------- Start running detailCompareDefaultDelimiterNoCompareOption -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/output.csv";
        desCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/target.csv";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null,null, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0);
        assertTrue(result);
    }

    @Test
    public void detailCompareWithNonDefaultDelimiter(){
        LOGGER.info("------------- Start running detailCompareWithNonDefaultDelimiter -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/Result.csv";
        desCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/Result1.csv";
        Character myDelimiter = '^';
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null, myDelimiter, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        assertTrue(result);
    }

    @Test
    public void readCsv(){
        LOGGER.info("------------- Start running readCsv -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/json_bk.csv";
        ArrayList<HashMap<String, Object>> result = CsvUtil.csvToArrayListOfMap(srcCsv, null, null);
        assertTrue(result != null);
    }

    @Test
    public void detailCompareWithJson(){
        LOGGER.info("------------- Start running detailCompareWithJson -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/json/json_old.txt";
        desCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/json_old.csv";
        boolean result = CsvUtil.compareWithJSON(desCsv,srcCsv,"ticket_events", null, "timestamp");
        assertTrue(result == true);
    }

    @Test
    public void quickCompareUsingSearchMap() {
        LOGGER.info("------------- Start running quickCompareUsingSearchMap -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/Result.csv";
        desCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/Result1.csv";
        ArrayList<String> srcData = FileUtil.readLine(srcCsv);
        srcData.add(0, srcCsv);
        ArrayList<String> desData = FileUtil.readLine(desCsv);
        desData.add(0, desCsv);
        boolean result = (new SearchUtil()).searchMap(srcData, desData);
        assertTrue(result);
    }

    @Test
    public void quickCompareUsingSearchMaps(){
        LOGGER.info("------------- Start running quickCompareUsingSearchMaps -------------");
        desCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/14399983.csv";
        srcCsv = "/Users/thiep/Documents/dc";
        ArrayList<String> srcData = FileUtil.readLine(desCsv);
        srcData.add(0, desCsv);
        ArrayList<ArrayList<String>> dataList = FileUtil.readFolder(srcCsv, "part-");
        boolean result = (new SearchUtil()).searchMaps(dataList, srcData,"\\^");
        assertTrue(result);
    }

    @Test
    public void detailCompareWithFolderOfCsvFile(){
        LOGGER.info("------------- Start running detailCompareWithFolderOfCsvFile -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/14399983.csv";
        desCsv = "/Users/thiep/Documents/dc";
        Character targetDelimiter = '^';
        boolean result = CsvUtil.compareCsvList(srcCsv, desCsv, "part-",  null, null, targetDelimiter, CsvUtil.CompareOptions.TRUE_FALSE_IS_1_0, CsvUtil.CompareOptions.SKIP_NUMBER_TYPE);
        assertTrue(result);
    }

    @Test
    public void readTsvFile(){
        LOGGER.info("------------- Start running readTsvFile -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/csv/basic.csv";
        List<String[]> result = CsvUtil.getCsvStrings(srcCsv, '\t');
        assertTrue(result.size() > 0);
    }
}
