package td_automation.plugin_automation.SalesforceKrux;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import td_automation.Util.*;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class FileUtilTest {

    private String srcCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(FileUtilTest.class.getName());

//    @Test
    public void fileListTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/media_user_data.csv";
        desCsv = "/Users/thiep/Documents/2019-04-17";
        ArrayList<String> srcData = (new FileUtil()).readLine(srcCsv);
        ArrayList<ArrayList<String>> dataList = (new FileUtil()).readFolder(desCsv, "part-");
        for (int i = 0; i < dataList.size(); i ++)
            dataList.set(i, Util.replaceAll(Util.replaceAll(dataList.get(i), "\"", ""), "\\^", ","));
        boolean result = FileUtil.searchRecordsInFolder(dataList, srcData);
        assertTrue(result);
    }

//    @Test
    public void mapTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/krux.csv";
        desCsv = "/Users/thiep/Documents/2019-04-20";
        ArrayList<String> srcData = (new FileUtil()).readLine(srcCsv);
        ArrayList<ArrayList<String>> dataList = (new FileUtil()).readFolder(desCsv, "part-");
        boolean result = SearchUtil.searchMaps(dataList, srcData,"\\^");
        assertTrue(result);
    }

    @Test
    public void customMapTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/media_user_data.csv";
        desCsv = "/Users/thiep/Documents/2019-04-17";
        ArrayList<String> srcData = (new FileUtil()).readLine(srcCsv);
        ArrayList<ArrayList<String>> dataList = (new FileUtil()).readFolder(desCsv, "part-");
        boolean result = SearchUtil.searchMaps(dataList, srcData, "\\^");
        assertTrue(result);
    }

    @Test
    public void checksumTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/krux.csv";
        desCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/s3.csv";
        String md5Value = FileUtil.fileToMd5(srcCsv);
        System.out.println(md5Value);

        md5Value = FileUtil.fileToMd5(desCsv);
        System.out.println(md5Value);
        //LOGGER.info(String.format("MD5 value %s", md5Value));
        assertTrue(true);
    }

    @Test
    public void md5SimpleTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = "/Users/thiep/Documents/workspace/text1";
        desCsv = "/Users/thiep/Documents/workspace/text2";
        String md5Value = FileUtil.fileToMd5(srcCsv);
        System.out.println(md5Value);

        md5Value = FileUtil.fileToMd5(desCsv);
        System.out.println(md5Value);
        //LOGGER.info(String.format("MD5 value %s", md5Value));
        assertTrue(true);
    }
}
