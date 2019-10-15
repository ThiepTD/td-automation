package com.treasuredata.tdautomation.pluginautomation;

import com.treasuredata.tdautomation.pluginautomation.common.Common;
import com.treasuredata.tdautomation.pluginautomation.sfdc.SfdcUtil;
import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.FileUtil;
import com.treasuredata.tdautomation.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;

import static org.testng.Assert.assertTrue;

/**
 * The TestCaseBase implements an iTestListener and IRetryAnalyzer
 * to support automatically rerun failure TestNg tests
 * It also contains other methods so that children classes can overwrite
 *
 * @author Thiep Truong
 * @version 1.0
 * @since 2019-07-23
 */
public class TestCaseBase implements ITestListener, IRetryAnalyzer {

    public static Logger LOGGER = LogManager.getLogger(TestCaseBase.class.getName());
    public static int DELAY_TIME = 15;
    public static int RETRY_LIMIT = 2;
    private static int counter = 1;

    public String tdTargetTable;
    public String yml;
    public String ymlContent;
    public String sourceTestData;
    public String targetTestData;
    public String tdQuery;
    public String dbName;
    public static String RUNNING_TEST_METHOD;
    public static String RUNNING_TEST_NAME;
    public static String TD_CONF_CONTENT = "[account]\n" +
            "\tuser = %s\n" +
            "\tapikey = %s\n" +
            "\tendpoint = %s\n";
    public ArrayList<String> skippedTests = new ArrayList<String>();

    public static long CURRENT_UNIX_TIMESTAMP = Util.getCurrentUnixTimeStamp();

    public static String TD_CONF = Constant.RESOURCE_PATH + "pluginautomation/configuration/td.conf";;
    public static String TD_DB = "";
    public static String TD_USER = "";

    public static String PYTHON_INPUT_FILE = String.format("%s/src/main/python/data_generator/input.py", Constant.PATH);

    // Python input file to generate testing data in csv format
    public static String PYTHON_INPUT = "input = {\n" +
            "  \"num_of_row\": %d,\n" +
            "  \"delimiter\": \"%s\",\n" +
            "  \"num_of_column\": %d,\n" +
            "  \"headers\": %s,\n" +
            "  \"column_types\": %s\n" +
            "}";

    public TestCaseBase() {
    }

    /**
     * This is the constructor for the class.
     *
     * @param ymlFile      yml file - full path.
     * @param targetFile   target csv file - full path, normally we use to store exported data from TD.
     * @param expectedFile our expected csv - full path, our expected data which will be used to compare with the target csv file
     * @param database     a part of database name. Ex: in td configuration file, user name is thiep.truong@treasure-data.com
     *                     and database name is sfdc then full database name will be thiep_truong_sfdc
     * @param targetTable  TD table name which will store data we got from third party data platform.
     * @param tdQuery      our sql query to query above target TD table.
     */
    public TestCaseBase(String ymlFile, String targetFile, String expectedFile, String database, String targetTable, String tdQuery) {
        yml = ymlFile;
        targetTestData = targetFile;
        sourceTestData = expectedFile;
        tdTargetTable = targetTable;
        this.tdQuery = tdQuery;
        dbName = database;
    }

    public void initializeSkippedTest() {
        if (skippedTests == null)
            skippedTests = new ArrayList<String>();
    }

    public TestCaseBase setTdTable(String table) {
        tdTargetTable = table;
        return this;
    }

    public TestCaseBase setYml(String ymlFile) {
        yml = Constant.RESOURCE_PATH + ymlFile;
        return this;
    }

    public TestCaseBase setDb(String database) {
        dbName = database;
        return this;
    }

    public TestCaseBase setTdQuery(String query) {
        tdQuery = query;
        return this;
    }

    public TestCaseBase setSourceData(String sourceData) {
        sourceTestData = Constant.RESOURCE_PATH + sourceData;
        return this;
    }

    public TestCaseBase setTargetData(String targetData) {
        targetTestData = Constant.RESOURCE_PATH + targetData;
        return this;
    }

    public TestCaseBase addSkippedTest(String testName) {
        initializeSkippedTest();
        skippedTests.add(testName);
        return this;
    }

    @Override
    public void onStart(ITestContext testContext) {
        RUNNING_TEST_NAME = testContext.getName();
        LOGGER.info("---------------- ON START {} -------------", RUNNING_TEST_NAME);
    }

    @Override
    public void onTestStart(ITestResult Result) {
        LOGGER.info(String.format("------------- Start running %s -------------", Result.getName()));
        RUNNING_TEST_METHOD = Result.getName();
        if (skippedTests != null) {
            for (String skippedTest : skippedTests) {
                if (RUNNING_TEST_METHOD.equalsIgnoreCase(skippedTest)) {
                    throw new SkipException(String.format("Test %s will be skipped", RUNNING_TEST_METHOD));
                }
            }
        }
    }

    @Override
    public void onTestFailure(ITestResult Result) {
        LOGGER.info(Result.getThrowable().toString());
        LOGGER.info(String.format("------------- Step %s has failed -------------", Result.getName()));
    }

    @Override
    public void onTestSuccess(ITestResult Result) {
        LOGGER.info(String.format("------------- Step %s has passed -------------", Result.getName()));
    }

    @Override
    public void onTestSkipped(ITestResult Result) {
        LOGGER.info(String.format("------------- Step %s has been skipped -------------", Result.getName()));
        //clearResultStatus(Result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult Result) {
    }

    @Override
    public void onFinish(ITestContext Result) {
        LOGGER.info("---------------- ON FINISH -------------");
    }

    /**
     * This is TestNg before suite method. It will get value of a variable env from TestNg test suite xml file
     * In case we run our test script from the code (not from xml file) then its value will be td.conf
     * It reads td configuration file to get td user. Ex: we have thiep.truong@treasure-data.com in, then user will be thiep_truong
     * It replaces '.' by '_' since TD doesn't allow '.' in database and table name
     * It sets value TRUE for BEFORE_SUITE_IS_EXECUTED since we just want to read td configuration file one time only
     *
     * @param env yml file - full path.
     */
    @Parameters("env")
    @BeforeSuite
    public void beforeSuite(@Optional("null") String env) {
        LOGGER.info("---------------- Before Suite -------------");
        HashMap<String, String> envInfo = Common.getTdEnvInfo(env);
        String [] tdInfo = {envInfo.get(Constant.TD_USERNAME), envInfo.get(Constant.TD_APIKEY), envInfo.get(Constant.TD_ENDPOINT)};
        LOGGER.info("---------------> Username {}", tdInfo[0]);
        LOGGER.info("---------------> Endpoint {}", tdInfo[2]);
        FileUtil.writeFile(TD_CONF, String.format(TD_CONF_CONTENT, tdInfo[0], tdInfo[1], tdInfo[2]));
        //TD_CONF_CONTENT = FileUtil.updateFileContent(TD_CONF, tdInfo);
        getTdUser();
    }

    /**
     * This is TestNg after suite method. It just sets FALSE for BEFORE_SUITE_IS_EXECUTED
     */
    @AfterSuite
    public void afterSuite() {
        LOGGER.info("---------------- After Suite -------------");
        FileUtil.writeFile(TD_CONF, TD_CONF_CONTENT);
    }

    // This function may need to be override for other plugin than sfdc
    public String [] getEnvInfo(){
        String [] envInfo = {Constant.ENV.get(SfdcUtil.SFDC_USER), Constant.ENV.get(SfdcUtil.SFDC_PASS), Constant.ENV.get(SfdcUtil.SFDC_SECURITY_TOKEN), Constant.ENV.get(SfdcUtil.SFDC_CLIENT_ID), Constant.ENV.get(SfdcUtil.SFDC_CLIENT_SECRET)};
        return envInfo;
    }

    @AfterTest
    public void resetFileContent(){
        if (yml != null)
            FileUtil.writeFile(yml, ymlContent);
    }

    /**
     * This is a method to read td configuration file TD_CONF to get the user
     * Ex: thiep.truong@treauser-data.com in td configuration file the method will get user name is thiep_truong and
     * assign value to a static variable TD_USER. Note that '.' or '+' will be replaced by '_'
     *
     * @return nothing
     */
    public void getTdUser() {
        if (!TD_USER.equalsIgnoreCase("")) return;
        ArrayList<String> lines = FileUtil.readLine(TD_CONF);
        for (int i = 0; i < lines.size(); i++) {
            String tmpString = lines.get(i).trim();
            if (tmpString.contains("user =")) {
                TD_USER = tmpString.split("=")[1].trim().split("@")[0].replace(".", "_").replace("+", "_");
                break;
            }
        }
    }

    /**
     * This is a method to create our csv or tsv test data
     * Please make sure PYTHON_INPUT is well defined
     *
     * @param csvOutputFile the name of the connector session we want to create
     * @return output of td cli command
     */
    public String createTestData(String csvOutputFile) {
        String[] lines = {PYTHON_INPUT};
        FileUtil.writeFile(PYTHON_INPUT_FILE, lines);
        return CmdUtil.generateData(csvOutputFile);
    }

    /**
     * This is a method to create our csv or tsv test data
     * Please make sure PYTHON_INPUT is well defined
     *
     * @param result execution result of the TestNg test which was failed
     * @return true if it can be rerun and false if retry limit is exceed
     */
    @Override
    public boolean retry(ITestResult result) {
        if (counter < RETRY_LIMIT) {
            counter++;
            Util.sleep(DELAY_TIME);
            return true;
        }
        return false;
    }

    /**
     * This is a method to clear execution result status of a TestNg test so that the depended method can be executed if
     * it is passed after some retry
     * If the TestNg test doesn't define IRetryAnalyzer then it prints out the message "Retry object is NULL"
     *
     * @param result execution result of the TestNg test which was failed
     * @return nothing
     */
    public void clearResultStatus(ITestResult result) {
        IRetryAnalyzer retry = result.getMethod().getRetryAnalyzer();
        if (retry == null) {
            LOGGER.info("Retry object is NULL");
            return;
        } else {
            if (counter <= RETRY_LIMIT) {
                LOGGER.info(String.format(" ---------------   Clear test result for retrying   -------------"));
                if (isSkipTest()) {
                    return;
                }
                result.setStatus(ITestResult.SKIP);
                result.getTestContext().getFailedTests().removeResult(result.getMethod());
                result.getTestContext().getSkippedTests().removeResult(result.getMethod());
            }
        }
    }

    public boolean isSkipTest(){
        for (int i = 0; i < skippedTests.size(); i ++){
            if (RUNNING_TEST_METHOD.equalsIgnoreCase(skippedTests.get(i)))
                return true;
        }
        return false;
    }

    /**
     * This is a TestNg after method to set an appropriate action based on its execution result
     *
     * @param result execution result of the TestNg test which was failed
     * @return nothing
     */
    @AfterMethod
    public void afterMethod(ITestResult result) {
        LOGGER.info(" ---------------   After Method    -------------");
        if (result.getStatus() == ITestResult.SUCCESS)
            counter = 1;
        else
            clearResultStatus(result);
    }

    // The following methods will be used for child class to overwrite
    public void compareDataAssertion(Object result) throws AssertionError {
        assertTrue((boolean) result);
    }

    public void transferDataToTdAssertion(Object result) throws AssertionError {
        assertTrue(result.toString().contains(": success"));
    }

    public void exportDataFromTdToCsvAssertion(Object result) throws AssertionError {
        assertTrue(result.toString().contains(": success"));
    }

    public void importTestDataToTdAssertion(Object result) throws AssertionError {
        assertTrue(result.toString().contains(": SUCCESS"));
    }

    public void createTestDataAssertion(Object result) throws AssertionError {
        assertTrue(result.toString().contains("output.csv was created"));
    }

    public void exportDataFromTdAssertion(Object result) throws AssertionError {
        assertTrue(result.toString().contains(": success"));
    }

    public void createConnectorSessionAssertion(Object result) throws AssertionError {
        assertTrue(result.toString().contains("Config Diff"));
    }

    public void getRecordCountAssertion(Object result) throws AssertionError {
        assertTrue(Integer.parseInt(result.toString()) > 0);
    }
}
