package td_automation.plugin_automation.Sfdmp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import td_automation.Util.CmdUtil;
import td_automation.Util.Constant;
import org.testng.annotations.Test;


import static org.junit.Assert.assertTrue;

public class CmdTest {

    public static Logger LOGGER = LogManager.getLogger(CmdTest.class.getName());

    @Test
    public void testTdExport(){
        LOGGER.info("------------- Start running testTdExport -------------");
        String dbName = "thiep_db";
        String output = String.format("%sresult.csv", Constant.RESOURCE_PATH);
        String query = "select * from people";
        String result = CmdUtil.tdExport(dbName, output, query);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void testPythonCmd(){
        LOGGER.info("------------- Start running testPythonCmd -------------");
        String result = CmdUtil.generateData();
        assertTrue(!result.isEmpty());
    }
}
