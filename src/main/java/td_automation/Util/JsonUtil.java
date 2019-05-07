package td_automation.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonUtil {

    static Logger LOGGER = LogManager.getLogger(JsonUtil.class.getName());

    public static HashMap<String, Object> getJson(ArrayList<HashMap<String, Object>> jsonArray, String name, String value) {
        for (int i = 0; i < jsonArray.size(); i++){
            if (jsonArray.get(i).get(name).toString().equalsIgnoreCase(value)){
                return jsonArray.get(i);
            }
        }
        return null;
    }
}
