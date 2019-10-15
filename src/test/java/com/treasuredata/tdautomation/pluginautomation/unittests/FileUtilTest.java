package com.treasuredata.tdautomation.pluginautomation.unittests;

import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import static org.junit.Assert.assertTrue;

public class FileUtilTest {

    private String srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sfdmp/csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "pluginautomation/sfdmp/csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(FileUtilTest.class.getName());

    @Test
    public void checksumTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sfdmp/csv/krux.csv";
        desCsv = Constant.RESOURCE_PATH + "pluginautomation/sfdmp/csv/s3.csv";
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

    @Test
    public void testReplaceLine(){
        LOGGER.info("------------- Start running testReplaceLine -------------");
        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/yml/loadv1.yml";
        FileUtil.replaceLine(srcCsv, "  id:", "  id: already replaced by Parul");
        assertTrue(true);
    }

    @Test
    public void replaceLineByMultipleLines(){
        LOGGER.info("------------- Start running replaceLineByMultipleLines -------------");
        String mylines = "         - name: Queries\n" +
                "           type: string\n" +
                "         - name: Clicks\n" +
                "           type: long\n" +
                "         - name: Impressions\n" +
                "           type: long\n" +
                "         - name: CTR\n" +
                "           type: string\n" +
                "         - name: Position\n" +
                "           type: double\n" +
                "         - name: time\n" +
                "           type: long";

        srcCsv = Constant.RESOURCE_PATH + "pluginautomation/sample/yml/load.yml";
        FileUtil.replaceLine(srcCsv, "Parul will add columns names & types here at run time", mylines);
        assertTrue(true);
    }
}
