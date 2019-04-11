package td_automation.tdautomation;

import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import td_automation.Util.Constant;
import td_automation.Util.CsvUtil;

import static org.junit.Assert.assertTrue;

public class CsvTest{

    private String srcCsv = Constant.RESOURCE_PATH + "csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(CsvTest.class.getName());

    @Test
    public void csvTest(){
        LOGGER.info("------------- Start running csvTest -------------");
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv);
        assertTrue(result);
    }

    @Test
    public void csvQuyenTest(){
        LOGGER.info("------------- Start running csvQuyenTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "csv/1.csv";
        desCsv = Constant.RESOURCE_PATH + "csv/2.csv";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv);
        assertTrue(result);
    }

    @Test
    public void csvDelimiterTest(){
        LOGGER.info("------------- Start running csvDelimiterTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "csv/Result.csv";
        desCsv = Constant.RESOURCE_PATH + "csv/Result1.csv";
        String myDelimiter = "\\^";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, myDelimiter);
        assertTrue(result);
    }
}
