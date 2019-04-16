package td_automation.tdautomation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import td_automation.Util.Constant;
import td_automation.Util.CsvUtil;
import td_automation.Util.FileUtil;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class FileUtilTest {

    private String srcCsv = Constant.RESOURCE_PATH + "csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(FileUtilTest.class.getName());

    @Test
    public void fileListTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "csv/fromTD.csv";
        desCsv = "/Users/thiep/Downloads/2019-04-08";
        ArrayList<String> srcData = FileUtil.readLine(srcCsv);
        ArrayList<ArrayList<String>> dataList = FileUtil.readFolder(desCsv, "part-");
        boolean result = FileUtil.seachRecordsInFolder(srcData, dataList);
        assertTrue(result);
    }
}
