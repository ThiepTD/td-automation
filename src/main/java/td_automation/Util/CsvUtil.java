package td_automation.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CsvUtil {

    public static Logger LOGGER = LogManager.getLogger(Util.class.getName());
    public static String delimiter = ",";

    //public static String S3_HEADERS = "timestamp^day^krux_user_id^ip_address^browser^device^operating_system^url^site_data^geo_data_display";
    public static String S3_HEADERS = "timestamp^day^configuration_id^krux_user_id^ip_address^browser^device_type^operating_system^kxbrand^advertiser_id^campaign_id^placement_id^site_id^creative_id^ad_id^is_click^geo_data_display";
    //public static String SITE_USER_DATA = "timestamp^day^krux_user_id^ip_address^browser^device^operating_system^url^site_data^geo_data_display";
    public static String SITE_USER_DATA = "timestamp^day^configuration_id^krux_user_id^ip_address^browser^device_type^operating_system^kxbrand^advertiser_id^campaign_id^placement_id^site_id^creative_id^ad_id^is_click^geo_data_display";

    /*
     - Compare values b/w 2 csv files. each line in csv file is a java ash map where key names are column .
     - We search src data in des data
     - The first element of array list is always file name
    */
    public static boolean compareCsv(ArrayList<HashMap<String, String>> srcData, ArrayList<HashMap<String, String>> desData) {

        boolean result = true;
        int match = 0;
        if (compareQuantity(srcData, desData)) {

            int numOfColumn = srcData.get(1).size();
            HashMap<String, String> diffMap = new HashMap<String, String>();

            // Skip the first record since it contains file name
            for (int i = 1; i < srcData.size(); i++) {
                HashMap<String, String> srcRow = srcData.get(i);
                int finalDiff = numOfColumn;
                int foundIndex = 0;

                // Skip the first record since it contains file name
                for (int j = 1; j < desData.size(); j++) {
                    HashMap<String, String> desRow = desData.get(j);
                    int currentOfDiff = 0;

                    for (String key : srcRow.keySet()) {

                        if (!desRow.get(key).equals(srcRow.get(key))) {
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
            LOGGER.info(String.format("%d match(es) found !", match));
            LOGGER.info(String.format("Total record %d !", srcData.size() - 1));
        } else {
            result = false;
        }
        LOGGER.info(String.format("Compare result %s !", String.valueOf(result)));
        return result;
    }

    public static HashMap<String, String> compareAndRemoveMatches(ArrayList<HashMap<String, String>> srcData, ArrayList<HashMap<String, String>> desData) {
        HashMap<String, String> mostLikelyRecord = new HashMap<String, String>();
        if (theSameColumns(srcData, desData)) {
            int match = 0;
            int numOfColumn = srcData.get(0).size();
            HashMap<String, String> diffMap = new HashMap<String, String>();

            for (int i = 0; i < srcData.size(); i++) {
                HashMap<String, String> srcRow = srcData.get(i);
                int finalDiff = numOfColumn;
                int foundIndex = 0;

                for (int j = 0; j < desData.size(); j++) {
                    HashMap<String, String> desRow = desData.get(j);
                    int currentOfDiff = 0;

                    for (String key : srcRow.keySet()) {

                        if (!desRow.get(key).equalsIgnoreCase(srcRow.get(key))) {
                            currentOfDiff++;
                        }
                    }

                    if (currentOfDiff < finalDiff) {
                        finalDiff = currentOfDiff;
                        diffMap = desRow;
                    }

                    if (currentOfDiff == 0) foundIndex = j;
                }

                if (finalDiff == 0) {

                    //Found 1 match so remove that item out of desData so the next search won't touch it
                    desData.remove(foundIndex);
                    srcData.remove(i);
                    match++;
                } else {
                    LOGGER.info("No match !");
                    LOGGER.info(String.format("The current record %s", srcRow.toString()));
                    LOGGER.info(String.format("Most likely record %s", diffMap.toString()));
                }
            }
            mostLikelyRecord.put("numOfMatch", String.valueOf(match));
        }
        return mostLikelyRecord;
    }

    /*
     - Search values of each csv file from source in destination csv file. each line in csv file is a java Hash map where key names are column .
     - We search src data in des data
    */
    public static HashMap<String, String> searchMap(HashMap<String, String> srcData, ArrayList<HashMap<String, String>> desData) {
        int numOfColumn = srcData.size();
        HashMap<String, String> diffMap = new HashMap<String, String>();

        int finalDiff = numOfColumn;
        int foundIndex = 0;
        int mostLikelyIndex = 0;

        for (int j = 1; j < desData.size(); j++) {
            HashMap<String, String> desRow = desData.get(j);
            if (desRow == null) continue;
            int currentOfDiff = 0;

            for (String key : srcData.keySet()) {
                String desValue = desRow.get(key);
                String srcValue = srcData.get(key);

                if (!desValue.equals(srcValue)) {
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
            diffMap.put("file:index", desData.get(0).get("fileName") + ":" + String.valueOf(foundIndex));
        } else {
            //LOGGER.info("No match !");
            //LOGGER.info(String.format("The current record %s", srcData.toString()));
            //LOGGER.info(String.format("Most likely record %s", diffMap.toString()));
            diffMap.put("file:index", desData.get(0).get("fileName") + ":" + String.valueOf(mostLikelyIndex));
        }

        diffMap.put("numOfDiff", String.valueOf(finalDiff));
        return diffMap;
    }

    /*
     - Search values of each csv file from source in destination csv file. each line in csv file is a java Hash map where key names are column .
     - We search src data in des data
    */

    public static boolean compareCsvList(ArrayList<HashMap<String, String>> srcData, ArrayList<ArrayList<HashMap<String, String>>> desDataList) {
        boolean result = true;
        int match = 0;
        int totalDesRecord = 0;

        for (int m = 0; m < desDataList.size(); m++)
            totalDesRecord += desDataList.get(m).size();

        for (int i = 0; i < srcData.size(); i++) {
            HashMap<String, String> finalDiff = new HashMap<String, String>();
            //LOGGER.info(String.format("----------> Compare with data block %d !", i));
            //LOGGER.info(String.format("Search record %d ...", i));
            HashMap<String, String> currentDiff;
            HashMap<String, String> srcRow = srcData.get(i);
            for (int j = 0; j < desDataList.size(); j++) {
                ArrayList<HashMap<String, String>> desData = desDataList.get(j);
                currentDiff = searchMap(srcRow, desData);

                if (Integer.parseInt(currentDiff.get("numOfDiff")) == 0) {
                    //srcData.remove(i);
                    finalDiff.put("numOfDiff", "0");
                    break;
                } else {
                    if (finalDiff.size() == 0 || Integer.parseInt(currentDiff.get("numOfDiff")) < Integer.parseInt(finalDiff.get("numOfDiff")))
                        finalDiff = currentDiff;
                }
            }

            if (Integer.parseInt(finalDiff.get("numOfDiff")) == 0) {
                match++;
                //LOGGER.info(String.format("Found record %d !", i));
            } else {
                result = false;
                finalDiff.remove("numOfDiff");
                String[] fileIndex = finalDiff.get("file:index").split(":");
                String mostLikelyIndex = fileIndex[1];
                String fileName = fileIndex[0];
                finalDiff.remove("file:index");

                LOGGER.info(String.format("The current record %s", srcRow.toString()));
                LOGGER.info(String.format("Most likely record %s", finalDiff.toString()));
                LOGGER.info(String.format("Most likely record in file %s row %s", fileName, mostLikelyIndex));
                LOGGER.info(String.format("Record %d not found !", i));
            }
        }
        LOGGER.info(String.format("%d match(es) found !", match));
        LOGGER.info(String.format("Total source record %d !", srcData.size()));
        LOGGER.info(String.format("Total destination record %d !", totalDesRecord));
        LOGGER.info(String.format("Compare result %s !", String.valueOf(result)));

        return result;
    }

    public static boolean compareCsv(String srcCsv, String desCsv, String headers) {
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv, headers);
        ArrayList<HashMap<String, String>> desData = fileToMaps(desCsv, headers);
        return compareCsv(srcData, desData);
    }

    public static boolean compareCsv(String srcCsv, String desCsv, String headers, String myDelimiter) {
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv, headers, myDelimiter);
        ArrayList<HashMap<String, String>> desData = fileToMaps(desCsv, headers, myDelimiter);
        return compareCsv(srcData, desData);
    }

    public static boolean compareCsvList(String srcCsv, String folder, String fileName, String headers) {
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv, headers);
        ArrayList<ArrayList<HashMap<String, String>>> desDataList = folderToMaps(folder, fileName, headers);
        return compareCsvList(srcData, desDataList);
    }

    public static boolean compareCsvList(String srcCsv, String folder, String fileName, String headers, String myDelimiter) {
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv, headers);
        ArrayList<ArrayList<HashMap<String, String>>> desDataList = folderToMaps(folder, fileName, headers, myDelimiter);
        return compareCsvList(srcData, desDataList);
    }

    /*
     - file data will be stored in an array list of java hash map where the first hash map contains file name
     */
    public static ArrayList<HashMap<String, String>> arrayDataToMaps(ArrayList<String> fileData, String headers) {
        ArrayList<HashMap<String, String>> csvData = new ArrayList<HashMap<String, String>>();
        String[] keys;
        int startPoint;
        String fileName = fileData.get(0);
        HashMap<String, String> tmpMap = new HashMap<String, String>();
        tmpMap.put("fileName", fileName);
        csvData.add(0, tmpMap);

        if (headers == null) {
            keys = fileData.get(1).split(delimiter);
            startPoint = 2;
        } else {
            startPoint = 1;
            keys = headers.split(delimiter);
        }

        for (int i = startPoint; i < fileData.size(); i++) {
            String[] values = fileData.get(i).split(delimiter);
            String[] usableValues = new String[values.length];
            int index = 0;
            for (int m = 0; m < values.length; m++) {
                if (values[m].startsWith("\"")) {
                    usableValues[index] = values[m];
                    if (values[m].endsWith("\"")) {
                        index++;
                        continue;
                    }

                    int l;
                    for (l = m + 1; l < values.length; l++) {
                        usableValues[index] += delimiter + values[l];
                        if (values[l].endsWith("\"")) {
                            //usableValues[m] = usableValues[m].replaceAll("\"", "");
                            break;
                        }
                    }
                    m = l;
                } else {
                    usableValues[index] = values[m];
                }
                index++;
            }

            tmpMap = new HashMap<String, String>();

            for (int j = 0; j < keys.length; j++)
                try {
                    tmpMap.put(keys[j], usableValues[j].replaceAll("\"", ""));
                } catch (Exception e) {
                    tmpMap.put(keys[j], "");
                }
            csvData.add(tmpMap);
        }
        return csvData;
    }

    public static ArrayList<HashMap<String, String>> arrayDataToMaps(ArrayList<String> fileData, String headers, String myDelimiter) {
        String oldDelimiter = delimiter;
        delimiter = myDelimiter;
        ArrayList<HashMap<String, String>> csvData = arrayDataToMaps(fileData, headers);
        delimiter = oldDelimiter;
        return csvData;
    }

    public static boolean compareQuantity(ArrayList<HashMap<String, String>> srcData, ArrayList<HashMap<String, String>> desData) {
        int srcNumOfRow = srcData.size();
        int desNumOfRow = desData.size();
        boolean result = theSameColumns(srcData, desData);

        if (srcNumOfRow != desNumOfRow) {
            LOGGER.info(String.format("Source #row = %d while Destination #row = %d", srcNumOfRow, desNumOfRow));
            result = false;
        }
        return result;
    }

    public static ArrayList<HashMap<String, String>> fileToMaps(String filePath, String headers) {
        ArrayList<String> fileData = (new FileUtil()).readLine(filePath);
        return arrayDataToMaps(fileData, headers);
    }

    public static ArrayList<HashMap<String, String>> fileToMaps(String filePath, String headers, String myDelimiter) {
        ArrayList<String> fileData = (new FileUtil()).readLine(filePath);
        return arrayDataToMaps(fileData, headers, myDelimiter);
    }

    /*
     - Read all file under the folder which have name contains filename
     */
    public static ArrayList<ArrayList<HashMap<String, String>>> folderToMaps(String folder, String fileName, String headers) {
        ArrayList<ArrayList<String>> csvDataList = (new FileUtil()).readFolder(folder, fileName);
        ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
        for(int i = 0; i < csvDataList.size(); i ++){
            ArrayList<HashMap<String, String>> arrayElement =  arrayDataToMaps(csvDataList.get(i), headers);
            result.add(arrayElement);
        }
        return result;
    }

    public static ArrayList<ArrayList<HashMap<String, String>>> folderToMaps(String folder, String fileName, String headers, String myDelimiter) {
        ArrayList<ArrayList<String>> csvDataList = (new FileUtil()).readFolder(folder, fileName);
        ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
        for(int i = 0; i < csvDataList.size(); i ++){
            ArrayList<HashMap<String, String>> arrayElement =  arrayDataToMaps(csvDataList.get(i), headers, myDelimiter);
            result.add(arrayElement);
        }
        return result;
    }

    public static boolean theSameColumns(ArrayList<HashMap<String, String>> srcData, ArrayList<HashMap<String, String>> desData) {

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

    public static boolean compareQuantity(String srcCsv, String desCsv, String headers) {
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv, headers);
        ArrayList<HashMap<String, String>> desData = fileToMaps(desCsv, headers);
        return compareQuantity(srcData, desData);
    }

    public static boolean compareQuantity(String srcCsv, String desCsv, String headers, String myDelimiter) {
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv, headers, myDelimiter);
        ArrayList<HashMap<String, String>> desData = fileToMaps(desCsv, headers, myDelimiter);
        return compareQuantity(srcData, desData);
    }

}
