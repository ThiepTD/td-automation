package com.treasuredata.tdautomation.pluginautomation.unittests;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * The BasicInputPlugin extends TestCaseBase
 * It is a basic scenario for input plugin
 * It basically has 1 steps to initialize necessary parameters and three other steps to
 *      - Transfer data from third party data platform to TD based on provided yml file
 *      - Export from TD to csv file
 *      - Compare exported csv file with our expected csv data file
 * @author  Thiep Truong
 * @version 1.0
 * @since   2019-07-23
 */

@Listeners(SampleTemplateTestCase.class)

public class SampleTemplateTestCase extends TestCaseBase {

    // WARNING: Removing the default constructor makes TestNg fail to run our test suite/script
    public static int count = 0;
    public SampleTemplateTestCase(){
        LOGGER = LogManager.getLogger(SampleTemplateTestCase.class.getName());
        super.addSkippedTest("thirdMethod");
    }
    @Test(retryAnalyzer = TestCaseBase.class)
    public void firstMethod(){
        LOGGER.info("First method {}", String.valueOf(count));
        assertTrue(count ++ == 1);
    }

    @Test(dependsOnMethods = {"firstMethod"},retryAnalyzer = TestCaseBase.class)
    public void secondMethod() {
        LOGGER.info("Second method");
    }

    @Test(dependsOnMethods = {"secondMethod"},retryAnalyzer = TestCaseBase.class)
    public void thirdMethod() {
        LOGGER.info("Third method");
    }

    @Test(dependsOnMethods = {"thirdMethod"},retryAnalyzer = TestCaseBase.class)
    public void fourthMethod() {
        LOGGER.info("Fourth method");
    }

}


