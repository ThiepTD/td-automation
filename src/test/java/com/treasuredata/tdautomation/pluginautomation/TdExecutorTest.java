package com.treasuredata.tdautomation.pluginautomation;

import com.treasuredata.tdautomation.TdExecutor;
import com.treasuredata.tdautomation.util.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import static org.junit.Assert.assertTrue;

public class TdExecutorTest {

    public static Logger LOGGER = LogManager.getLogger(TdExecutorTest.class.getName());
    public static String TD_EXECUTION_XML = String.format("%spluginautomation/td-automation.xml", Constant.RESOURCE_PATH);

    @Test
    public void dev(){
        TdExecutor.execute(TD_EXECUTION_XML, null);
        assertTrue(true);
    }

    @Test
    public void devEu01(){
        TdExecutor.execute(TD_EXECUTION_XML, Constant.DEV_EU01);
        assertTrue(true);
    }

    @Test
    public void staging(){
        TdExecutor.execute(TD_EXECUTION_XML, Constant.STAG);
        assertTrue(true);
    }
}
