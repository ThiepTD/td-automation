package com.treasuredata.tdautomation;

import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.XmlUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TdExecutor {

    static Logger LOGGER = LogManager.getLogger(TdExecutor.class.getName());

    public static void execute(String xmlFile, String env) {
        try {

            TestNG testng = new TestNG();
            Document doc = XmlUtil.readXml(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList suiteFiles = doc.getElementsByTagName("suite-file");
            Element parameter = (Element) doc.getElementsByTagName("parameter").item(0);

            String tdEnv = (env == null) ? parameter.getAttribute("value") : env;

            List<XmlSuite> xmlSuites = new ArrayList<XmlSuite>();
            for (int i = 0; i < suiteFiles.getLength(); i ++) {
                Element currentSuite = (Element) suiteFiles.item(i);
                XmlSuite suite = getSuite(currentSuite.getAttribute("path"));
                HashMap<String, String> suiteParamMap = (HashMap<String, String>) suite.getAllParameters();
                suiteParamMap.put("env", tdEnv);

                suite.setParameters(suiteParamMap);
                xmlSuites.add(suite);
            }

            testng.setXmlSuites(xmlSuites);
            testng.run();

        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
    }

    public static XmlSuite getSuite(String xmlSuiteFile) {
        try {
            XmlSuite xmlSuite = new XmlSuite();
            xmlSuiteFile = Constant.RESOURCE_PATH + "pluginautomation/" + xmlSuiteFile;
            xmlSuite.setFileName(xmlSuiteFile);
            Document doc = XmlUtil.readXml(xmlSuiteFile);
            doc.getDocumentElement().normalize();
            xmlSuite.setName(doc.getDocumentElement().getAttribute("name"));
            NodeList parameters = doc.getElementsByTagName("parameter");
            HashMap<String, String> paramMap = new HashMap<String, String>();
            for (int i = 0; i < parameters.getLength(); i ++){
                Element currentParam = (Element) parameters.item(i);
                paramMap.put(currentParam.getAttribute("name"), currentParam.getAttribute("value"));
            }
            xmlSuite.setParameters(paramMap);

            // Loop test tags
            NodeList tests = doc.getElementsByTagName("test");
            for (int i = 0; i < tests.getLength(); i ++) {
                Element currentTest = (Element) tests.item(i);
                XmlTest test = new XmlTest(xmlSuite);
                test.setName(currentTest.getAttribute("name"));

                // Loop class
                NodeList classNodes = currentTest.getElementsByTagName("class");
                List<XmlClass> classes = new ArrayList<XmlClass>();
                for (int j = 0; j < classNodes.getLength(); j++){
                    XmlClass currentClass = new XmlClass(((Element)classNodes.item(j)).getAttribute("name"));
                    classes.add(currentClass);
                }
                test.setXmlClasses(classes);

                // Loop test parameter
                NodeList testParam = currentTest.getElementsByTagName("parameter");
                HashMap<String, String> testParamMap = new HashMap<String, String>();
                for (int j = 0; j < testParam.getLength(); j++){
                    Element currentTestParam = (Element) testParam.item(j);
                    testParamMap.put(currentTestParam.getAttribute("name"), currentTestParam.getAttribute("value"));
                }
                test.setParameters(testParamMap);

            }
            return xmlSuite;

        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
        }
        return null;
    }
}
