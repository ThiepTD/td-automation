package td_automation.plugin_automation.Sfdmp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import td_automation.Util.Constant;
import td_automation.Util.CsvUtil;
import td_automation.Util.FileUtil;
import td_automation.Util.SearchUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class CsvTest{

    private String srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(CsvTest.class.getName());

    @Test
    public void detailCompareDefaultDelimiter(){
        LOGGER.info("------------- Start running detailCompareDefaultDelimiter -------------");
        srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/1.csv";
        desCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/2.csv";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null);
        assertTrue(result);
    }

    @Test
    public void detailCompareWithNonDefaultDelimiter(){
        LOGGER.info("------------- Start running detailCompareWithNonDefaultDelimiter -------------");
        srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/Result.csv";
        desCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/Result1.csv";
        Character myDelimiter = '^';
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null, myDelimiter);
        assertTrue(result);
    }

    @Test
    public void readCsv() throws IOException {
        LOGGER.info("------------- Start running readCsv -------------");
        srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/json.csv";
        ArrayList<HashMap<String, Object>> result = CsvUtil.csvToArrayListOfMap(srcCsv, null, null);
        assertTrue(result != null);
    }

    @Test
    public void quickCompareUsingSearchMap() {
        LOGGER.info("------------- Start running quickCompareUsingSearchMap -------------");
        srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/Result.csv";
        desCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/Result1.csv";
        ArrayList<String> srcData = (new FileUtil()).readLine(srcCsv);
        ArrayList<String> desData = (new FileUtil()).readLine(desCsv);
        boolean result = SearchUtil.searchMap(srcData, desData);
        assertTrue(result);
    }
    @Test
    public void quickCompareUsingSearchMaps(){
        LOGGER.info("------------- Start running quickCompareUsingSearchMaps -------------");
        desCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/14399983.csv";
        srcCsv = "/Users/thiep/Documents/dc";
        ArrayList<String> srcData = (new FileUtil()).readLine(desCsv);
        ArrayList<ArrayList<String>> dataList = (new FileUtil()).readFolder(srcCsv, "part-");
        boolean result = SearchUtil.searchMaps(dataList, srcData,"\\^");
        assertTrue(result);
    }

    @Test
    public void detailCompareWithFolderOfCsvFile(){
        LOGGER.info("------------- Start running detailCompareWithFolderOfCsvFile -------------");
        srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/14399983.csv";
        desCsv = "/Users/thiep/Documents/dc";
        Character targetDelimiter = '^';
        boolean result = CsvUtil.compareCsvList(srcCsv, desCsv, "part-",  null, null, targetDelimiter);
        assertTrue(result);
    }
}
