package td_automation.plugin_automation.Sfdmp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import td_automation.Util.*;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class FileUtilTest {

    private String srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(FileUtilTest.class.getName());

    @Test
    public void checksumTest(){
        LOGGER.info("------------- Start running fileListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/krux.csv";
        desCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/s3.csv";
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
