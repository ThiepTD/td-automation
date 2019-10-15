package com.treasuredata.tdautomation.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonUtil {

    static Logger LOGGER = LogManager.getLogger(JsonUtil.class.getName());

    public JsonUtil() {
    }

    public static HashMap<String, Object> getJson(ArrayList<HashMap<String, Object>> jsonArray, String name, String value) {
        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.get(i).get(name).toString().equalsIgnoreCase(value)) {
                return jsonArray.get(i);
            }
        }
        return null;
    }

    public static ArrayList<HashMap<String, Object>> getJSONArray(JSONObject jsonObject, String keyName, String[] headers) {
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        JSONArray jsonArray = (JSONArray) jsonObject.get(keyName);
        HashMap<String, Object> tmp = new HashMap<String, Object>();
        tmp.put("fileName", "fromJSON");
        tmp.put("headers", headers);
        result.add(tmp);

        for (int i = 0; i < jsonArray.size(); i++) {
            HashMap<String, Object> originalMap = (HashMap<String, Object>) jsonArray.get(i);
            HashMap<String, Object> tmpMap = new HashMap<String, Object>();
            for (int j = 0; j < headers.length; j++) {
                tmpMap.put(headers[j], originalMap.get(headers[j]));
            }
            result.add(tmpMap);
        }
        return result;
    }

    public static JSONArray toJsonArray(ArrayList<HashMap<String, Object>> arrayList) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++)
            jsonArray.add(toJsonObject(arrayList.get(i)));
        return jsonArray;
    }

    public static JSONObject toJsonObject(HashMap<String, Object> hashMap) {
        JSONObject jsonObject = new JSONObject();
        for (String keyName : hashMap.keySet())
            jsonObject.put(keyName, hashMap.get(keyName));
        return jsonObject;
    }

    public static boolean compareJsonObject(JSONObject src, JSONObject tar) {
        int srcKey = src.keySet().size();
        int tarKey = tar.keySet().size();
        boolean result = true;

        if (srcKey != tarKey) {
            //LOGGER.error(String.format("Source has %d keyName while target has %d", srcKey, tarKey));
            result = false;
        } else {

            for (Object keyName : src.keySet()) {

                if (!tar.containsKey(keyName)) {
                    //LOGGER.error(String.format("Source has keyName %s while target doesn't", keyName.toString()));
                    result = false;
                    break;
                } else {
                    Object srcObject = src.get(keyName);
                    Object tarObject = tar.get(keyName);

                    if (srcObject instanceof JSONObject && tarObject instanceof JSONObject) {
                        result = compareJsonObject((JSONObject) srcObject, (JSONObject) tarObject);
                        if (result == false)
                            break;
                    } else {

                        try {

                            if (srcObject != null && srcObject.toString().length() > 2 && (srcObject.toString().startsWith("{") || srcObject.toString().startsWith("["))) {
                                JSONParser jsonParser = new JSONParser();
                                Object jsonObject = jsonParser.parse(srcObject.toString());
                                if (jsonObject instanceof JSONObject) {
                                    result = compareJsonObject((JSONObject) jsonObject, (JSONObject) tarObject);
                                    if (result == false)
                                        break;
                                    continue;
                                } else {
                                    srcObject = jsonObject;
                                }
                            }

                        } catch (ParseException e) {
                            // Do nothing since comparing will be taken cared by the following code
                        }

                        if (srcObject instanceof JSONArray && tarObject instanceof JSONArray) {
                            int matchNum = compareJsonArray((JSONArray) srcObject, (JSONArray) tarObject, null);
                            if (matchNum != ((JSONArray) srcObject).size()) {
                                result = false;
                                break;
                            }
                        } else {

                            if (srcObject == null && tarObject == null)
                                continue;
                            else {

                                if (srcObject != null && tarObject != null) {
                                    if (srcObject.toString().equals(tarObject.toString()))
                                        continue;
                                    else {
                                        try {

                                            double dbleSrc = Double.parseDouble(srcObject.toString());
                                            double dbleTar = Double.parseDouble(tarObject.toString());
                                            if (dbleSrc - dbleTar == 0) {
                                                // For debugging purpose
                                                //LOGGER.warn(String.format("Source value %s when target value %s at keyName %s", srcObject.toString(), tarObject.toString(), keyName));
                                                continue;
                                            }
                                        } catch (Exception e) {
                                            // For debugging purpose
                                            //LOGGER.warn(e.getStackTrace().toString());
                                        }

                                        if (srcObject instanceof Boolean) {
                                            String srcValue = (srcObject.toString().equals("true")) ? "1" : "0";
                                            if (srcValue.equals(tarObject.toString()))
                                                continue;
                                        }
                                    }
                                } else {
                                    if (srcObject == null && tarObject.toString().isEmpty())
                                        continue;
                                }

                                // Debug code
                                //LOGGER.info("Source --------------------->" + srcObject.toString());
                                //LOGGER.info("Target --------------------->" + tarObject.toString());
                                //LOGGER.info("Keyname --------------------->" + keyName);
                                result = false;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public static int compareJsonArray(JSONArray src, JSONArray tar, String rootKey) {
        int srcSize = src.size();
        int tarSize = tar.size();
        int matchNum = 0;

        // For debug purpose
        //HashMap<String, Integer> countFound = new HashMap<String, Integer>();

        for (int i = 0; i < srcSize; i++) {
            Object srcObject = src.get(i);
            boolean found = false;
            Object tarObject = new Object();

//  Debug code
//            try {
//                if (((JSONObject)srcObject).containsKey("url"))
//                    LOGGER.info(String.format("Source object: %s", ((JSONObject) srcObject).get("id").toString()));
//            }catch (Exception e){}

            for (int j = 0; j < tarSize; j++) {
                tarObject = tar.get(j);
                int subMatch = 0;

//  Debug code
//                try {
//                    if (((JSONObject)tarObject).containsKey("url"))
//                        LOGGER.info(String.format("Target object: %s", ((JSONObject) tarObject).get("id").toString()));
//                }catch (Exception e){}

                if (srcObject instanceof JSONObject && tarObject instanceof JSONObject)
                    found = compareJsonObject((JSONObject) srcObject, (JSONObject) tarObject);
                else {
                    if (srcObject instanceof JSONArray && tarObject instanceof JSONArray) {
                        int size = ((JSONArray) srcObject).size();
                        subMatch = compareJsonArray((JSONArray) srcObject, (JSONArray) tarObject, null);
                        if (subMatch == size) found = true;
                    } else {
                        if (srcObject.equals(tarObject))
                            found = true;
                    }
                }

                if (found == true) {
                    break;
                }
            }

            if (found == true) {
                matchNum++;

                // Debug code
/*
                if (srcObject instanceof JSONObject && ((JSONObject) srcObject).containsKey("timestamp")){
                    int value = 0;
                    String id = ((JSONObject) srcObject).get("id").toString();
                    if (countFound.containsKey(id))
                        value = countFound.get(id);
                    countFound.put(id, ++ value);
                }
*/
            } else {
                if (srcObject instanceof JSONObject && rootKey != null && ((JSONObject) srcObject).containsKey(rootKey))
                    LOGGER.info(String.format("Not found: %s", (new org.json.JSONObject(srcObject.toString())).toString(4)));
            }
        }

        return matchNum;
    }

    public static void writeJsonData(String fileName, ArrayList<HashMap<String, Object>> csvData) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(fw);
            for (int i = 0; i < csvData.size(); i++) {
                String line = "{";
                HashMap<String, Object> currentMap = csvData.get(i);
                for (String key: currentMap.keySet()){
                    line = String.format("%s\"%s\":\"%s\",", line, key, currentMap.get(key).toString());
                }
                line = line.substring(0, line.length() - 1) + "}\n";
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

}
