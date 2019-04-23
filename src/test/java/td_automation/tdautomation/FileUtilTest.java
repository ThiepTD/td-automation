package td_automation.tdautomation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import td_automation.Util.Constant;
import td_automation.Util.FileUtil;
import td_automation.Util.Util;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class FileUtilTest {

    private String srcCsv = Constant.RESOURCE_PATH + "csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(FileUtilTest.class.getName());

    @Test
    public void fileListTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "csv/media_user_data.csv";
        desCsv = "/Users/thiep/Documents/2019-04-17";
        ArrayList<String> srcData = FileUtil.readLine(srcCsv);
        ArrayList<ArrayList<String>> dataList = FileUtil.readFolder(desCsv, "part-");
        for (int i = 0; i < dataList.size(); i ++)
            dataList.set(i, Util.replaceAll(Util.replaceAll(dataList.get(i), "\"", ""), "\\^", ","));
        boolean result = FileUtil.seachRecordsInFolder(srcData, dataList);
        assertTrue(result);
    }

    @Test
    public void mapTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "csv/media_user_data.csv";
        desCsv = "/Users/thiep/Documents/2019-04-17";
        ArrayList<String> srcData = FileUtil.readLine(srcCsv);
        ArrayList<ArrayList<String>> dataList = FileUtil.readFolder(desCsv, "part-");
        boolean result = FileUtil.seachMaps(srcData, dataList, "\\^");
        assertTrue(result);
    }
}
