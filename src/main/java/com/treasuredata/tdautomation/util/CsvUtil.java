package com.treasuredata.tdautomation.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class CsvUtil {

    public static Logger LOGGER = LogManager.getLogger(CsvUtil.class.getName());
    public static String FILENAME = "fileName";
    public static String HEADERS = "headers";

    // when this plag is set, true/false, 1/0 and yes/no are considered the same
    public static String TRUE_FALSE_IS_1_0 = "true_false_is_1_0";
    public static String SKIP_NUMBER_TYPE = "skip_number_type";

    public CsvUtil() {
    }

    ;

    /*
     - Compare values b/w 2 csv files. each line in csv file is a java ash map where key names are column .
     - We search src data in des data
     - The first element of array list is always file name
    */
    public static boolean compareCsv(ArrayList<HashMap<String, Object>> srcData, ArrayList<HashMap<String, Object>> desData, CompareOptions... compareOptions) {
        HashMap<String, Boolean> compareOptionHashMap = new HashMap();
        Stream.of(compareOptions).forEach(compareOption -> compareOptionHashMap.put(compareOption.getCompareOption(), true));
        boolean result = true;
        int match = 0;
        if (!compareQuantity(srcData, desData)) result = false;

        int numOfColumn = srcData.get(1).size();
        HashMap<String, Object> diffMap = new HashMap<String, Object>();

        // Skip the first record since it contains file name
        for (int i = 1; i < srcData.size(); i++) {
            HashMap<String, Object> srcRow = srcData.get(i);
            int finalDiff = numOfColumn;
            int foundIndex = 0;

            // Skip the first record since it contains file name
            for (int j = 1; j < desData.size(); j++) {
                HashMap<String, Object> desRow = desData.get(j);
                int currentOfDiff = 0;

                for (String key : srcRow.keySet()) {

                    Object desObject = desRow.get(key);
                    String desValue = (desObject == null || desObject.toString().contains("null")) ? "" : desObject.toString();
                    Object srcObject = srcRow.get(key);
                    String srcValue = (srcObject == null || srcObject.toString().contains("null")) ? "" : srcObject.toString();

                    if (!desValue.equals(srcValue)) {
                        if (compareOptionHashMap != null && compareOptionHashMap.containsKey(SKIP_NUMBER_TYPE) && compareOptionHashMap.get(SKIP_NUMBER_TYPE) == true) {
                            try {
                                double src = Double.parseDouble(srcValue);
                                double des = Double.parseDouble(desValue);
                                if (src - des == 0)
                                    continue;

                            } catch (Exception e) {
                                // Do nothing
                            }
                        }

                        if (compareOptionHashMap != null && compareOptionHashMap.containsKey(TRUE_FALSE_IS_1_0) && compareOptionHashMap.get(TRUE_FALSE_IS_1_0) == true) {
                            desValue = updateBooleanValue(desValue);
                            srcValue = updateBooleanValue(srcValue);
                            if (srcValue.equals(desValue))
                                continue;
                        }

                        currentOfDiff++;
                    }
                }

                if (currentOfDiff == 0) {
                    foundIndex = j;
                    finalDiff = currentOfDiff;
                    break;
                }

                if (currentOfDiff < finalDiff) {
                    finalDiff = currentOfDiff;
                    diffMap = desRow;
                }
            }

            if (finalDiff == 0) {

                //Found 1 match so remove that item out of desData so the next search won't touch it
                desData.remove(foundIndex);
                match++;
            } else {
                result = false;
                LOGGER.info("No match !");
                LOGGER.info(String.format("The current record %s", srcRow.toString()));
                LOGGER.info(String.format("Most likely record %s", diffMap.toString()));
            }
        }
        String[] printedResult = {String.format("%d match(es) found !", match),
                String.format("Total record %d !", srcData.size() - 1),
                String.format("Compare result %s !", String.valueOf(result))};
        Util.printResult(printedResult, CsvUtil.LOGGER);
        return result;
    }

    public static String updateBooleanValue(String str){
        if (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes"))
            return "1";
        if (str.equalsIgnoreCase("false") || str.equalsIgnoreCase("no"))
            return "0";
        return str;
    }

    /*
     - Search values of each csv file from source in destination csv file. each line in csv file is a java Hash map where key names are column .
     - We search src data in des data
    */
    public static HashMap<String, Object> searchMap(HashMap<String, Object> srcData, ArrayList<HashMap<String, Object>> desData, CompareOptions... compareOptions) {
        HashMap<String, Boolean> compareOptionHashMap = new HashMap<>();
        int numOfColumn = srcData.size();
        HashMap<String, Object> diffMap = new HashMap<String, Object>();

        int finalDiff = numOfColumn;
        int foundIndex = 0;
        int mostLikelyIndex = 0;

        for (int j = 1; j < desData.size(); j++) {
            HashMap<String, Object> desRow = desData.get(j);
            if (desRow == null) continue;
            int currentOfDiff = 0;

            for (String key : srcData.keySet()) {
                Object desObject = desRow.get(key);
                String desValue = (desObject == null || desObject.toString().contains("null")) ? "" : desObject.toString();
                Object srcObject = srcData.get(key);
                String srcValue = (srcObject == null || srcObject.toString().contains("null")) ? "" : srcObject.toString();

                if (!desValue.equals(srcValue)) {
                    if (compareOptionHashMap != null && compareOptionHashMap.containsKey(SKIP_NUMBER_TYPE) && compareOptionHashMap.get(SKIP_NUMBER_TYPE) == true) {
                        try {
                            double src = Double.parseDouble(srcValue);
                            double des = Double.parseDouble(desValue);
                            if (src - des == 0)
                                continue;

                        } catch (Exception e) {
                            // Do nothing
                        }
                    }

                    if (compareOptionHashMap != null && compareOptionHashMap.containsKey(TRUE_FALSE_IS_1_0) && compareOptionHashMap.get(TRUE_FALSE_IS_1_0) == true) {
                        desValue = updateBooleanValue(desValue);
                        srcValue = updateBooleanValue(srcValue);
                        if (srcValue.equals(desValue))
                            continue;
                    }

                    currentOfDiff++;
                    // Un comment if you want to have better performance but cannot figure out the most likely record
                    //break;
                }
            }

            if (currentOfDiff == 0) {
                foundIndex = j;
                finalDiff = 0;
                break;
            }

            if (currentOfDiff < finalDiff) {
                finalDiff = currentOfDiff;
                diffMap = desRow;
                mostLikelyIndex = j;
            }
        }

        if (finalDiff == 0) {

            //Found 1 match so assign null value to that item so the next search won't touch it. Don't remove since it causes wrong found or most likely index
            //desData.remove(foundIndex);
            desData.set(foundIndex, null);
            diffMap.put("file:index", desData.get(0).get(FILENAME) + ":" + String.valueOf(foundIndex));
        } else {
            //LOGGER.info("No match !");
            //LOGGER.info(String.format("The current record %s", srcData.toString()));
            //LOGGER.info(String.format("Most likely record %s", diffMap.toString()));
            diffMap.put("file:index", desData.get(0).get(FILENAME) + ":" + String.valueOf(mostLikelyIndex));
        }

        diffMap.put("numOfDiff", String.valueOf(finalDiff));
        return diffMap;
    }

    /*
     - Search values of each csv file from source in destination csv file. each line in csv file is a java Hash map where key names are column .
     - We search src data in des data
    */

    public static boolean compareCsvList(ArrayList<HashMap<String, Object>> desDataSource, ArrayList<ArrayList<HashMap<String, Object>>> srcDataList, CompareOptions... compareOptions) {
        boolean result = true;
        int match = 0;
        int totalSrcRecord = 0;

        for (int m = 0; m < srcDataList.size(); m++)
            totalSrcRecord += srcDataList.get(m).size() - 1;

        for (int i = 1; i < desDataSource.size(); i++) {
            HashMap<String, Object> finalDiff = new HashMap<String, Object>();
            //LOGGER.info(String.format("----------> Compare with data block %d !", i));
            //LOGGER.info(String.format("Search record %d ...", i));
            HashMap<String, Object> currentDiff;
            HashMap<String, Object> srcRow = desDataSource.get(i);

            for (int j = 0; j < srcDataList.size(); j++) {
                ArrayList<HashMap<String, Object>> srcData = srcDataList.get(j);
                currentDiff = searchMap(srcRow, srcData, compareOptions);

                if (Integer.parseInt(currentDiff.get("numOfDiff").toString()) == 0) {
                    //desDataSource.remove(i);
                    finalDiff.put("numOfDiff", "0");
                    break;
                } else {
                    if (finalDiff.size() == 0 || Integer.parseInt(currentDiff.get("numOfDiff").toString()) < Integer.parseInt(finalDiff.get("numOfDiff").toString()))
                        finalDiff = currentDiff;
                }
            }

            if (Integer.parseInt(finalDiff.get("numOfDiff").toString()) == 0) {
                match++;
                //LOGGER.info(String.format("Found record %d !", i));
            } else {
                result = false;
                finalDiff.remove("numOfDiff");
                String[] fileIndex = finalDiff.get("file:index").toString().split(":");
                String mostLikelyIndex = fileIndex[1];
                String fileName = fileIndex[0];
                finalDiff.remove("file:index");

                LOGGER.info(String.format("The current record %s", srcRow.toString()));
                LOGGER.info(String.format("Most likely record %s", finalDiff.toString()));
                LOGGER.info(String.format("Most likely record in file %s row %s", fileName, mostLikelyIndex));
                LOGGER.info(String.format("Record %d not found !", i));
            }
        }
        String[] printedResult = {String.format("%d match(es) found !", match),
                String.format("Total source record %d !", desDataSource.size() - 1),
                String.format("Total destination record %d !", totalSrcRecord),
                String.format("Compare result %s !", String.valueOf(result))};
        Util.printResult(printedResult, CsvUtil.LOGGER);
        return result;
    }

    public static boolean compareCsv(String srcCsv, String desCsv, String[] headers, Character myDelimiter, CompareOptions... compareOptions) {
        LOGGER.info(String.format("Compare data b/w %s and %s", srcCsv, desCsv));
        ArrayList<HashMap<String, Object>> srcData = csvToArrayListOfMap(srcCsv, headers, myDelimiter);
        ArrayList<HashMap<String, Object>> desData = csvToArrayListOfMap(desCsv, headers, myDelimiter);
        return compareCsv(srcData, desData, compareOptions);
    }

    public static boolean compareCsv(String tarCsv, String srcCsv, String desCsv, String[] headers, Character myDelimiter, CompareOptions... compareOptions) {
        LOGGER.info(String.format("Compare data b/w %s and %s", srcCsv, desCsv));
        ArrayList<HashMap<String, Object>> srcData = mergeCsv(tarCsv, srcCsv, myDelimiter);
        ArrayList<HashMap<String, Object>> desData = csvToArrayListOfMap(desCsv, headers, myDelimiter);
        return compareCsv(srcData, desData, compareOptions);
    }

    public static boolean compareCsvList(String srcCsv, String folder, String fileName, String[] headers, Character srcDelimiter, Character targetDelimiter, CompareOptions... compareOptions) {
        ArrayList<HashMap<String, Object>> srcData = csvToArrayListOfMap(srcCsv, headers, srcDelimiter);
        String[] myHeaders = (String[]) srcData.get(0).get(HEADERS);
        ArrayList<ArrayList<HashMap<String, Object>>> desDataList = csvFolderToArrayListOfMap(folder, fileName, myHeaders, targetDelimiter);
        return compareCsvList(srcData, desDataList, compareOptions);
    }

    // Keep value of target if target and source have the same column name
    public static ArrayList<HashMap<String, Object>> mergeCsv(String targetFile, String srcFile, Character myDelimiter) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> src = csvToArrayListOfMap(srcFile, null, myDelimiter);
        ArrayList<HashMap<String, Object>> tar = csvToArrayListOfMap(targetFile, null, myDelimiter);

        Object [] srcHeaders = (Object []) src.get(0).get(HEADERS);
        Object [] targetHeaders = (Object []) tar.get(0).get(HEADERS);
        ArrayList<String> headerList = new ArrayList<String>(Arrays.asList((String [])tar.get(0).get(HEADERS)));

        // Merge headers
        for (int i = 0; i < srcHeaders.length; i ++){
            boolean found = false;
            for (int j = 0; j < targetHeaders.length; j ++) {
                if (srcHeaders[i].equals(targetHeaders[j])){
                    found = true;
                    break;
                }
            }
            if (found == true){
                headerList.add(srcHeaders[i].toString());
            }
        }

        // Add first element into array list of hash map data
        HashMap<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put(HEADERS, (Object)headerList);
        headerMap.put(FILENAME, tar.get(0).get(FILENAME));
        data.add(headerMap);

        for (int i = 1; i < tar.size(); i ++){
            HashMap<String, Object> tmpMap = tar.get(i);
            for (int j = 0; j < srcHeaders.length; j ++) {
                if (!tmpMap.containsKey(srcHeaders[j]))
                    tmpMap.put(srcHeaders[j].toString(), null);
            }
            data.add(tmpMap);
        }

        for (int i = 1; i < src.size(); i ++){
            HashMap<String, Object> tmpMap = src.get(i);
            for (int j = 0; j < targetHeaders.length; j ++) {
                if (!tmpMap.containsKey(targetHeaders[j]))
                    tmpMap.put(targetHeaders[j].toString(), null);
            }
            data.add(tmpMap);
        }

        return data;
    }


    public static boolean compareQuantity(ArrayList<HashMap<String, Object>> srcData, ArrayList<HashMap<String, Object>> desData) {
        int srcNumOfRow = srcData.size();
        int desNumOfRow = desData.size();
        boolean result = theSameColumns(srcData, desData);

        if (srcNumOfRow != desNumOfRow) {
            LOGGER.info(String.format("Source #row = %d while Destination #row = %d", srcNumOfRow, desNumOfRow));
            result = false;
        }
        return result;
    }

    /*
     *   Use Open CSV to read csv content of fileName
     **/
    public static List<String[]> getCsvStrings(String fileName, Character delimiter) {
        List<String[]> result = new ArrayList<String[]>();
        if (delimiter == null) delimiter = ',';
        try {
            Reader reader = Files.newBufferedReader(Paths.get(fileName));
            CSVParser parser;
            if (!delimiter.equals('\t'))
                parser = new CSVParserBuilder()
                    .withSeparator(delimiter)
                    .withIgnoreQuotations(false).withEscapeChar('\n').withEscapeChar('\t').withEscapeChar('\b')
                    .build();
            else
                parser = new CSVParserBuilder()
                        .withSeparator(delimiter)
                        .withIgnoreQuotations(false).withEscapeChar('\n').withEscapeChar('\b')
                        .build();
            CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();
            result = csvReader.readAll();
        } catch (IOException e) {
            LOGGER.error(e.getStackTrace().toString());
        }
        return result;
    }

    public static String [] getHeaders(String fileName, Character delimiter){
        return getCsvStrings(fileName, delimiter).get(0);
    }

    public static void mergeHeaders(List<String> firstHeaders, String [] secondHeaders){
        for (int i = 0; i < secondHeaders.length; i ++) {
            boolean found = false;
            for (int j = 0; j < firstHeaders.size(); j ++){
                if (secondHeaders[i].equals(firstHeaders.get(j))){
                    found = true;
                    break;
                }
            }
            if (found == false){
                firstHeaders.add(secondHeaders[i]);
            }
        }
    }

    public static String createQueryFromHeaders(List<String> headers){
        String query = "select ";
        for (int i = 0; i < headers.size(); i ++){
            query += headers.get(i) + ", ";
        }
        query = query.substring(0, query.length() - 2) + " from %s";
        return query;
    }

    public static ArrayList<HashMap<String, Object>> csvToArrayListOfMap(String fileName, String[] headers, Character delimiter) {
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        List<String[]> list = getCsvStrings(fileName, delimiter);
        String[] myHeader = list.get(0);
        HashMap<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put(FILENAME, fileName);

        int startPoint = 1;
        String[] keyName = list.get(0);

        // header != null means csv file does NOT contains headers so we need to pass headers separately
        if (headers != null) {
            startPoint = 0;
            keyName = headers;
            headerMap.put(HEADERS, headers);
        } else {
            headerMap.put(HEADERS, myHeader);
        }
        result.add(headerMap);
        JSONParser jsonParser = new JSONParser();

        for (int i = startPoint; i < list.size(); i++) {
            HashMap<String, Object> tmpMap = new HashMap<String, Object>();

            for (int j = 0; j < keyName.length; j++) {
                String value;
                try {
                    value = list.get(i)[j];
                } catch (Exception e) {
                    LOGGER.warn(e.toString());
                    break;
                }

                //value = value.replaceAll("\\/", "/");

                if (value.startsWith("{") || value.startsWith("[")) {
                    try {
                        tmpMap.put(keyName[j], jsonParser.parse(value));
                    } catch (ParseException e) {
                        tmpMap.put(keyName[j], value);
                    }
                } else
                    tmpMap.put(keyName[j], value);
            }
            result.add(tmpMap);
        }

        return result;
    }

    public static ArrayList<ArrayList<HashMap<String, Object>>> csvFolderToArrayListOfMap(String folder, String fileName, String[] headers, Character delimiter) {
        ArrayList<ArrayList<HashMap<String, Object>>> result = new ArrayList<ArrayList<HashMap<String, Object>>>();
        List<String> files = FileUtil.getFiles(folder, fileName);

        for (String file : files) {
            result.add(csvToArrayListOfMap(file, headers, delimiter));
        }
        return result;
    }

    public static boolean compareWithJSON(String csvFile, String jsonFile, String keyName, Character delimiter, String elementKey) {
        ArrayList<HashMap<String, Object>> csvData = csvToArrayListOfMap(csvFile, null, delimiter);
        csvData.remove(0);
        JSONArray csvJsonArray = (new JsonUtil()).toJsonArray(csvData);

        Object object = FileUtil.readJson(jsonFile);
        JSONArray jsonArray = new JSONArray();
        if (object instanceof JSONArray)
            jsonArray = (JSONArray) object;
        else {
            if (keyName != null)
                jsonArray = (JSONArray) ((JSONObject) object).get(keyName);
        }

        int matchNum = (new JsonUtil()).compareJsonArray(jsonArray, csvJsonArray, elementKey);
        String[] printedResult = {String.format("Total record in json %d", jsonArray.size()),
                String.format("Total record in csv %d", csvData.size()),
                String.format("Number of match %d", matchNum)};
        Util.printResult(printedResult, CsvUtil.LOGGER);
        if (matchNum == jsonArray.size()) return true;
        return false;
    }


    public static boolean theSameColumns(ArrayList<HashMap<String, Object>> srcData, ArrayList<HashMap<String, Object>> desData) {

        int srcNumOfColumn = srcData.get(0).size();
        int desNumOfColumn = desData.get(0).size();
        boolean result = true;

        if (srcNumOfColumn != desNumOfColumn) {
            LOGGER.info(String.format("Source #column = %d while Destination #column = %d", srcNumOfColumn, desNumOfColumn));
            result = false;
        } else {
            Set<String> srcKeys = srcData.get(0).keySet();
            Set<String> desKeys = desData.get(0).keySet();

            for (String srcKey : srcKeys) {

                boolean found = false;
                for (String desKey : desKeys) {

                    if (srcKey.equals(desKey)) {
                        //desKeys.remove(desKey);
                        found = true;
                        break;
                    }
                }

                if (found == false) {
                    LOGGER.error(String.format("Cannot find key %s in keySet %s", srcKey, desKeys.toString()));
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public enum CompareOptions {
        TRUE_FALSE_IS_1_0("true_false_is_1_0"),
        SKIP_NUMBER_TYPE("skip_number_type");

        String compareOption;

        CompareOptions(String compareOption) {
            this.compareOption = compareOption;
        }

        public String getCompareOption() {
            return compareOption;
        }
    }
}
