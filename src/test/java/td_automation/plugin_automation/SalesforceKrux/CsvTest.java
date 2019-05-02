package td_automation.plugin_automation.SalesforceKrux;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import td_automation.Util.Constant;
import td_automation.Util.CsvUtil;

import static org.junit.Assert.assertTrue;

public class CsvTest{

    private String srcCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(CsvTest.class.getName());

    @Test
    public void csvTest(){
        LOGGER.info("------------- Start running csvTest -------------");
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null);
        assertTrue(result);
    }

    @Test
    public void csvQuyenTest(){
        LOGGER.info("------------- Start running csvQuyenTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/1.csv";
        desCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/2.csv";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null);
        assertTrue(result);
    }

    @Test
    public void csvDelimiterTest(){
        LOGGER.info("------------- Start running csvDelimiterTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/Result.csv";
        desCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/Result1.csv";
        String myDelimiter = "\\^";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, myDelimiter);
        assertTrue(result);
    }

    @Test
    public void csvListTest(){
        LOGGER.info("------------- Start running csvListTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "SalesforceKrux/csv/media_user_data.csv";
        desCsv = "/Users/thiep/Documents/2019-04-17";
        String myDelimiter = "\\^";

        boolean result = CsvUtil.compareCsvList(srcCsv, desCsv, "part-",  myDelimiter);
        assertTrue(result);
    }
}