package com.treasuredata.tdautomation.pluginautomation.zendesk;

import com.treasuredata.tdautomation.pluginautomation.TestCaseBase;
import com.treasuredata.tdautomation.util.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CsvZendesk extends TestCaseBase {

    public static Logger LOGGER = LogManager.getLogger(CsvZendesk.class.getName());

    @Test
    public void detailCompareWithJson(){
        LOGGER.info("------------- Start running detailCompareWithJson -------------");
        String srcCsv = Constant.RESOURCE_PATH + "zendesk/json/file_json_test.json";
        String desCsv = Constant.RESOURCE_PATH + "zendesk/csv/file_csv_test.csv";
        ZendeskCsvUtil zendeskCsvUtil = new ZendeskCsvUtil(srcCsv, desCsv, null);
        String [] timeFields = {"created_at", "updated_at"};
        boolean result = zendeskCsvUtil.compareWithJSON(timeFields,null, null, "url");
        assertTrue(result == true);
    }

    @Test
    public void detailCompareWithJsonOldData(){
        LOGGER.info("------------- Start running detailCompareWithJsonOldData -------------");
        String srcCsv = Constant.RESOURCE_PATH + "zendesk/json/ticketevents_json";
        String desCsv = Constant.RESOURCE_PATH + "zendesk/csv/2890913_new.csv";
        ZendeskCsvUtil zendeskCsvUtil = new ZendeskCsvUtil(srcCsv, desCsv, null);
        zendeskCsvUtil.getJsonArrayData("ticket_events");
        String [] timeFields = {"created_at"};
        boolean result = zendeskCsvUtil.compareWithJSON(timeFields,"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd HH:mm:ss.000","timestamp");
        assertTrue(result == true);
    }
}
