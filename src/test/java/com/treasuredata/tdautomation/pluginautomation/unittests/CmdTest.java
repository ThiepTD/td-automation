package com.treasuredata.tdautomation.pluginautomation.unittests;

import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;


import static org.junit.Assert.assertTrue;

public class CmdTest {

    public static Logger LOGGER = LogManager.getLogger(CmdTest.class.getName());
    public static String TD_QUERY = String.format("td -c ~/.td/td.conf query --column-header -d %s -w -T presto -f csv -o %sresult.csv", "sfdc_sample", Constant.RESOURCE_PATH);
    public static String TD_IMPORT = String.format("td -c ~/.td/td.conf table:import %s %s people --json -t time - < %slf_json.json", "sfdc_sample", "people", Constant.RESOURCE_PATH + "pluginautomation/sample/json/");

    @Test
    public void testTdExport(){
        LOGGER.info("------------- Start running testTdExport -------------");
        String command = TD_QUERY;
        String query = "select * from people";
        String result = CmdUtil.tdExport(command, query);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void testPythonCmd(){
        LOGGER.info("------------- Start running testPythonCmd -------------");
        String result = CmdUtil.generateData(String.format("%sSample/csv/", Constant.RESOURCE_PATH));
        assertTrue(!result.isEmpty());
    }

    @Test
    public void testIdImport(){
        LOGGER.info("------------- Start running testIdImport -------------");
        String result = CmdUtil.executeUsingCmd(TD_IMPORT);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void test11ExecuteCommads(){
        LOGGER.info("------------- Start running test11ExecuteCommads -------------");
        String [] commands = {"td account", TD_IMPORT};
        String result = CmdUtil.oneByOneExecute(commands);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void test11ExecuteCommandWithExport(){
        LOGGER.info("------------- Start running test11ExecuteCommandWithExport -------------");
        String [] commands = {"td account", String.format("%s \"select * from krux limit 11000000\"", TD_QUERY)};
        String result = CmdUtil.oneByOneExecute(commands);
        assertTrue(!result.isEmpty());
    }


}
