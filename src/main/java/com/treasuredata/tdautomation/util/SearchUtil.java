package com.treasuredata.tdautomation.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchUtil {

    static Logger LOGGER = LogManager.getLogger(SearchUtil.class.getName());

    public SearchUtil (){}

    public static boolean searchMaps(ArrayList<ArrayList<String>> fileDataList, ArrayList<String> lines, String myDelimiter) {
        HashMap<Object, Object> desMap = Util.arrayStringToMap(lines, true);
        HashMap<Object, Object> srcMap = Util.arrayFolderToMap(fileDataList, myDelimiter);
        return searchMaps(srcMap, desMap);
    }

    public static boolean searchMap(ArrayList<String> src, ArrayList<String> des) {
        HashMap<Object, Object> desMap = Util.arrayStringToMap(des, true);
        HashMap<Object, Object> srcMap = Util.arrayStringToMap(src, true);
        return searchMaps(srcMap, desMap);
    }

    public static boolean searchMaps(HashMap<Object, Object> srcMap, HashMap<Object, Object> desMap) {
        int srcLines = srcMap.keySet().size();
        int tarLines = desMap.keySet().size();
        boolean result = true;
        int match = 0;

        // Search by key
        LOGGER.info("##########################################");
        LOGGER.info("------------------------------------ Search by key ----------------------------------");
        LOGGER.info("##########################################");
        Object[] keys = srcMap.keySet().toArray();

        for (int i = 0; i < keys.length; i++) {

            if (!desMap.containsKey(keys[i])) {
                LOGGER.info(String.format("--------------------------> Line %s not found %s", keys[i], srcMap.get(keys[i])));
            } else {
                match++;
                desMap.remove(keys[i]);
                srcMap.remove(keys[i]);
            }
        }

        if (srcMap.size() > 0) {

            // Search by value
            LOGGER.info("##########################################");
            LOGGER.info("------------------------------------ Search by value ----------------------------------");
            LOGGER.info("##########################################");
            Object[] values = srcMap.values().toArray();
            Object[] newKeys = srcMap.keySet().toArray();

            for (int i = 0; i < values.length; i++) {

                if (!desMap.containsValue(values[i])) {
                    result = false;
                    LOGGER.info(String.format("--------------------------> Line %s not found %s", srcMap.get(newKeys[i]), newKeys[i]));
                } else {
                    match++;
                }
            }
        }

        String [] printedResult = {String.format("Total target lines %d ...", tarLines),
                String.format("Total source lines %d ...", srcLines),
                String.format("Number of match(es) found %d ...", match),
                String.format("Final result %s ...", String.valueOf(result))};
        Util.printResult(printedResult, SearchUtil.LOGGER);
        return result;
    }

}
