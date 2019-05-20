package td_automation.plugin_automation.Sfdc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import td_automation.Util.Constant;
import td_automation.Util.CsvUtil;

import static org.junit.Assert.assertTrue;

public class CsvTest {

    private String srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/destination.csv";
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
        srcCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/1.csv";
        desCsv = Constant.RESOURCE_PATH + "Sfdmp/csv/2.csv";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null);
        assertTrue(result);
    }

}
