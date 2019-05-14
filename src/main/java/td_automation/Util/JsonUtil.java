package td_automation.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public static ArrayList<String> jsonToCsv(String jsonString, String csvHeader, String delimiter){
        ArrayList<String> result = new ArrayList<String>();
        JSONArray jsonArray = new JSONArray(jsonString);
        String [] headers = csvHeader.split(delimiter);
        String tmp;
        result.add(0, "fileName:fromJson");
        result.add(csvHeader);
        for (int i = 0; i < jsonArray.length(); i ++){
            tmp = "";
            for (int j = 0; j < headers.length; j ++){
                tmp += ((JSONObject)jsonArray.get(i)).get(headers[j]) + delimiter;
            }
            result.add(tmp.substring(tmp.length() - 1));
        }
        return result;
    }
}
