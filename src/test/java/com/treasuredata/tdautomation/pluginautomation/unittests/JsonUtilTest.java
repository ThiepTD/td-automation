package com.treasuredata.tdautomation.pluginautomation.unittests;

import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.FileUtil;
import com.treasuredata.tdautomation.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class JsonUtilTest {

    public static Logger LOGGER = LogManager.getLogger(JsonUtilTest.class.getName());

    @Test
    public void readJSON(){
        LOGGER.info("------------- Start running fileListTest -------------");
        String jsonFile = Constant.RESOURCE_PATH + "pluginautomation/sfdmp/json/json.txt";
        String [] headers = new String[] {"child_events", "id", "ticket_id", "timestamp", "created_at", "updater_id", "via", "system", "event_type"};
        JSONObject jsonObject = (JSONObject) FileUtil.readJson(jsonFile);
        ArrayList<HashMap<String, Object>> result = (new JsonUtil()).getJSONArray(jsonObject, "ticket_events", headers);
        assertTrue(result != null);
    }
}
