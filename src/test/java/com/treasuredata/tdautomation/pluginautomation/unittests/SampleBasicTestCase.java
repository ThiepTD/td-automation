package com.treasuredata.tdautomation.pluginautomation.unittests;

import org.testng.annotations.Test;

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

//@Listeners(SampleTemplateTestCase.class)

public class SampleBasicTestCase extends SampleTemplateTestCase {

    // WARNING: Removing the default constructor makes TestNg fail to run our test suite/script
    @Test(dependsOnMethods = {"secondMethod"})
    public void customMethod() {
        LOGGER.info("Custom method");
    }

//    @Override//(dependsOnMethods = {"firstMethod"},retryAnalyzer = TestCaseBase.class)
//    public void secondMethod() {
//        LOGGER.info("Custom method");
//    }

/*
    @Override
    //@Test(dependsOnMethods = {"firstMethod"},retryAnalyzer = TestCaseBase.class)
    public void thirdMethod() {
        LOGGER.info("Third method");
    }

    @Override
    //@Test(dependsOnMethods = {"thirdMethod"},retryAnalyzer = TestCaseBase.class)
    public void fourthMethod() {
        LOGGER.info("Fourth method");
    }
*/

}


