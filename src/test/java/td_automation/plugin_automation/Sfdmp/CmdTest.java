package td_automation.plugin_automation.Sfdmp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import td_automation.Util.CmdUtil;
import td_automation.Util.Constant;
import td_automation.Util.CsvUtil;

import static org.junit.Assert.assertTrue;

public class CmdTest {

    private String srcCsv = Constant.RESOURCE_PATH + "csv/source.csv";
    private String desCsv = Constant.RESOURCE_PATH + "csv/destination.csv";
    public static Logger LOGGER = LogManager.getLogger(CmdTest.class.getName());

//    @Test
    public void cmdTest(){
        LOGGER.info("------------- Start running cmdTest -------------");
        String dbName = "sfdc_sample";
        String query = "\"select * from people\"";
        String result = CmdUtil.tdExport(dbName, query);
        assertTrue(result != null);
    }

//    @Test
    public void cmdQuyenTest(){
        LOGGER.info("------------- Start running cmdQuyenTest -------------");
        srcCsv = Constant.RESOURCE_PATH + "csv/1.csv";
        desCsv = Constant.RESOURCE_PATH + "csv/2.csv";
        boolean result = CsvUtil.compareCsv(srcCsv, desCsv, null);
        assertTrue(result);
    }
}
