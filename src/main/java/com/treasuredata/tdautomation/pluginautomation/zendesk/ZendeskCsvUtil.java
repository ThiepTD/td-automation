package com.treasuredata.tdautomation.pluginautomation.zendesk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.treasuredata.tdautomation.util.CsvUtil;
import com.treasuredata.tdautomation.util.FileUtil;
import com.treasuredata.tdautomation.util.JsonUtil;
import com.treasuredata.tdautomation.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ZendeskCsvUtil extends CsvUtil {

    public static Logger LOGGER = LogManager.getLogger(ZendeskCsvUtil.class.getName());
    private JSONArray csvJsonArrayData = new JSONArray();
    private JSONArray jsonJsonArrayData = new JSONArray();
    private Object jsonObject = new Object();

    public ZendeskCsvUtil() {
        super();
    }

    public ZendeskCsvUtil(String jsonFile, String csvFile, Character delimiter) {
        super();
        ArrayList<HashMap<String, Object>> csvMapData = CsvUtil.csvToArrayListOfMap(csvFile, null, delimiter);
        csvMapData.remove(0);
        csvJsonArrayData = (new JsonUtil()).toJsonArray(csvMapData);

        jsonObject = FileUtil.readJson(jsonFile);

        if (jsonObject instanceof JSONArray) {
            jsonJsonArrayData = (JSONArray) jsonObject;
        }
    }

    public void modifyData() {
        for (int i = 0; i < jsonJsonArrayData.size(); i++) {
            JSONObject tmpJsonObject = (JSONObject) jsonJsonArrayData.get(i);
            if (!tmpJsonObject.containsKey("description"))
                break;
            String description = tmpJsonObject.get("description").toString();
            tmpJsonObject.put("description", description.replaceAll("\r\n", "\n"));
        }

    }

    public void modifyTimeFormat(String[] fields, String srcFormat, String tarFormat) {

        if (srcFormat != null && tarFormat != null) {
            SimpleDateFormat srcDateFormat = new SimpleDateFormat(srcFormat);
            SimpleDateFormat tarDateFormat = new SimpleDateFormat(tarFormat);
            for (int i = 0; i < jsonJsonArrayData.size(); i++) {
                Date date;
                JSONObject tmpJsonObject = (JSONObject) jsonJsonArrayData.get(i);

                for (int j = 0; j < fields.length; j++) {
                    String tmpString = "";
                    try {
                        tmpString = tmpJsonObject.get(fields[j]).toString();
                        date = srcDateFormat.parse(tmpString);
                        tmpJsonObject.put(fields[j], tarDateFormat.format(date));
                    } catch (ParseException ex) {
                        LOGGER.error(String.format("Original string %s: %s", tmpString, ex.getMessage()));
                    }
                }
            }
        }
    }

    public void getJsonArrayData(String keyName) {
        if (((JSONObject) jsonObject).containsKey(keyName))
            jsonJsonArrayData = (JSONArray) ((JSONObject) jsonObject).get(keyName);
    }

    public boolean compareWithJSON(String[] fields, String srcTimeFormat, String tarTimeFormat, String elementKey) {
        if (jsonJsonArrayData != null) {
            modifyData();
            modifyTimeFormat(fields, srcTimeFormat, tarTimeFormat);
            int matchNum = (new JsonUtil()).compareJsonArray(jsonJsonArrayData, csvJsonArrayData, elementKey);

            String[] printedResult = {String.format("Total record in json %d", jsonJsonArrayData.size()),
                    String.format("Total record in csv %d", csvJsonArrayData.size()),
                    String.format("Number of match %d", matchNum)};
            Util.printResult(printedResult, ZendeskCsvUtil.LOGGER);
            if (matchNum == jsonJsonArrayData.size()) return true;
        }
        return false;
    }
}
