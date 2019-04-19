package td_automation.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public static boolean compareCsv(ArrayList<HashMap<String, String>> srcData,ArrayList<HashMap<String, String>> desData){

        boolean result = true;
        int match = 0;
        if(compareQuantity(srcData, desData)){

            int numOfColumn = srcData.get(0).size();
            HashMap<String, String> diffMap = new HashMap<String, String>();

            for (int i = 0; i < srcData.size(); i ++){
                HashMap<String, String> srcRow = srcData.get(i);
                int finalDiff = numOfColumn;
                int foundIndex = 0;

                for (int j = 0; j < desData.size(); j++){
                    HashMap<String, String> desRow = desData.get(j);
                    int currentOfDiff = 0;

                    for (String key: srcRow.keySet()){

                        if(!desRow.get(key).equals(srcRow.get(key))){
                            currentOfDiff ++;
                        }
                    }

                    if (currentOfDiff < finalDiff){
                        finalDiff = currentOfDiff;
                        diffMap = desRow;
                    }

                    if (currentOfDiff == 0) foundIndex = j;
                }

                if (finalDiff == 0){

                    //Found 1 match so remove that item out of desData so the next search won't touch it
                    desData.remove(foundIndex);
                    match ++;
                } else {
                    result = false;
                    LOGGER.info("No match !");
                    LOGGER.info(String.format("The current record %s", srcRow.toString()));
                    LOGGER.info(String.format("Most likely record %s", diffMap.toString()));
                }
            }
            LOGGER.info(String.format("%d match(es) found !", match));
            LOGGER.info(String.format("Total record %d !", srcData.size()));
        } else {
            result = false;
        }
        LOGGER.info(String.format("Compare result %s !", String.valueOf(result)));
        return result;
    }

    public static boolean compareCsv(ArrayList<HashMap<String, String>> srcData,ArrayList<HashMap<String, String>> desData, boolean skipNumOfRecord){
        if (skipNumOfRecord){
            if(compareQuantity(srcData, desData)){

            }
        } else{

        }
        return true;
    }

    public static HashMap<String, String> compareAndRemoveMatches(ArrayList<HashMap<String, String>> srcData,ArrayList<HashMap<String, String>> desData){
        HashMap<String, String> mostLikelyRecord = new HashMap<String, String>();
        if (theSameColumns(srcData,desData)){
            int match = 0;
            int numOfColumn = srcData.get(0).size();
            HashMap<String, String> diffMap = new HashMap<String, String>();

            for (int i = 0; i < srcData.size(); i ++) {
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

    public static HashMap<String, String> searchMap(HashMap<String, String> srcData,ArrayList<HashMap<String, String>> desData) {
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
                String tmpDes = desRow.get(key);
                String tmpSrc = srcData.get(key);

                if (!tmpDes.equalsIgnoreCase(tmpSrc)) {
                    currentOfDiff++;
                    // Un comment if you want to have better performance but cannot figure out the most likely record
                    break;
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

            //Found 1 match so remove that item out of desData so the next search won't touch it
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

    public static boolean compareCsvList(ArrayList<HashMap<String, String>> srcData,ArrayList<ArrayList<HashMap<String, String>>> desDataList){
        boolean result = true;
        int match = 0;
        int totalDesRecord = 0;

        for(int m = 0; m < desDataList.size(); m ++)
            totalDesRecord += desDataList.get(m).size();

        for(int i = 0; i < srcData.size(); i ++){
            HashMap<String, String> finalDiff = new HashMap<String, String>();
            //LOGGER.info(String.format("----------> Compare with data block %d !", i));
            //LOGGER.info(String.format("Search record %d ...", i));
            HashMap<String, String> currentDiff;
            HashMap<String, String> srcRow = srcData.get(i);
            for(int j = 0; j < desDataList.size(); j ++) {
                ArrayList<HashMap<String, String>> desData = desDataList.get(j);
                currentDiff = searchMap(srcRow, desData);

                if(Integer.parseInt(currentDiff.get("numOfDiff")) == 0) {
                    //srcData.remove(i);
                    finalDiff.put("numOfDiff", "0");
                    break;
                } else {
                    if (finalDiff.size() == 0 || Integer.parseInt(currentDiff.get("numOfDiff")) < Integer.parseInt(finalDiff.get("numOfDiff")))
                        finalDiff = currentDiff;
                }
            }

            if(Integer.parseInt(finalDiff.get("numOfDiff")) == 0){
                match ++;
                //LOGGER.info(String.format("Found record %d !", i));
            }else{
                result = false;
                finalDiff.remove("numOfDiff");
                String [] fileIndex = finalDiff.get("file:index").split(":");
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

    public static boolean compareCsv(String srcCsv,String desCsv){
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv);
        ArrayList<HashMap<String, String>> desData = fileToMaps(desCsv);
        return compareCsv(srcData, desData);
    }

    public static boolean compareCsv(String srcCsv,String desCsv, String myDelimiter){
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv, myDelimiter);
        ArrayList<HashMap<String, String>> desData = fileToMaps(desCsv, myDelimiter);
        return compareCsv(srcData, desData);
    }

    public static boolean compareCsvList(String srcCsv,String folder,String fileName){
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv);
        ArrayList<ArrayList<HashMap<String, String>>> desDataList = new ArrayList<ArrayList<HashMap<String, String>>>();
        ArrayList<ArrayList<String>> csvDataList = folderToMaps(folder, fileName, SITE_USER_DATA);
        for (int i = 0; i < csvDataList.size(); i++){
            ArrayList<HashMap<String, String>> tmpDesData = arrayDataToMaps(csvDataList.get(i));
            desDataList.add(tmpDesData);
        }
        return compareCsvList(srcData, desDataList);
    }

    public static boolean compareCsvList(String srcCsv,String folder,String fileName, String myDelimiter){
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv);
        ArrayList<ArrayList<HashMap<String, String>>> desDataList = new ArrayList<ArrayList<HashMap<String, String>>>();
        ArrayList<ArrayList<String>> csvDataList = folderToMaps(folder, fileName, SITE_USER_DATA);
        for (int i = 0; i < csvDataList.size(); i++){
            ArrayList<HashMap<String, String>> tmpDesData = arrayDataToMaps(Util.replaceAll(csvDataList.get(i), "\"", ""), myDelimiter);
            desDataList.add(tmpDesData);
        }
        return compareCsvList(srcData, desDataList);
    }

    public static ArrayList<HashMap<String, String>> arrayDataToMaps(ArrayList<String> fileData){
        ArrayList<HashMap<String, String>> csvData = new ArrayList<HashMap<String, String>>();
        String [] keys = fileData.get(0).split(delimiter);
        int startPoint;
        String fileName = fileData.get(1);

        if (fileName.contains(delimiter.replace("\\",""))){
            startPoint = 1;
        } else {
            startPoint = 2;
            HashMap<String, String> tmpMap = new HashMap<String, String>();
            tmpMap.put("fileName", fileName);
            csvData.add(tmpMap);
        }

        for (int i = startPoint; i < fileData.size(); i ++){
            String [] values = fileData.get(i).split(delimiter);
            String [] usableValues = new String[values.length];
            int index = 0;
            for (int m = 0; m < values.length; m++){
                if (values[m].startsWith("\"")){
                    usableValues[index] = values[m];
                    if (values[m].endsWith("\"")) {
                        index ++;
                        continue;
                    }

                    int l;
                    for (l = m + 1; l < values.length; l ++){
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
                index ++;
            }

            HashMap<String, String> tmpMap = new HashMap<String, String>();

            for (int j = 0; j < keys.length; j ++)
                try{
                    tmpMap.put(keys[j], usableValues[j].replaceAll("\"", ""));
                } catch (Exception e){
                    tmpMap.put(keys[j], "");
                }
            csvData.add(tmpMap);
        }
        return csvData;
    }

    public static ArrayList<HashMap<String, String>> arrayDataToMaps(ArrayList<String> fileData, String myDelimiter){
        String oldDelimiter = delimiter;
        delimiter = myDelimiter;
        ArrayList<HashMap<String, String>> csvData = arrayDataToMaps(fileData);
        delimiter = oldDelimiter;
        return csvData;
    }

    public static boolean compareQuantity(ArrayList<HashMap<String, String>> srcData, ArrayList<HashMap<String, String>> desData){
        int srcNumOfRow = srcData.size();
        int desNumOfRow = desData.size();
        boolean result = theSameColumns(srcData,desData);

        if (srcNumOfRow != desNumOfRow){
            LOGGER.info(String.format("Source #row = %d while Destination #row = %d", srcNumOfRow, desNumOfRow));
            result = false;
        }
        return result;
    }

    public static ArrayList<HashMap<String, String>> fileToMaps(String filePath){
        ArrayList<String> fileData = FileUtil.readLine(filePath);
        return arrayDataToMaps(fileData);
    }

    public static ArrayList<HashMap<String, String>> fileToMaps(String filePath, String myDelimiter){
        ArrayList<String> fileData = FileUtil.readLine(filePath);
        return arrayDataToMaps(fileData, myDelimiter);
    }

    public static ArrayList<ArrayList<String>> folderToMaps(String folder, String fileName){
        ArrayList<ArrayList<String>> csvDataList = FileUtil.readFolder(folder, fileName);
        return csvDataList;
    }

    public static ArrayList<ArrayList<String>> folderToMaps(String folder, String fileName, String myHeaders){
        ArrayList<ArrayList<String>> csvDataList = FileUtil.readFolder(folder, fileName);
        for(int i = 0; i < csvDataList.size(); i ++)
            csvDataList.get(i).add(0, myHeaders);
        return csvDataList;
    }

    public static boolean theSameColumns(ArrayList<HashMap<String, String>> srcData, ArrayList<HashMap<String, String>> desData){

        int srcNumOfColumn = srcData.get(0).size();
        int desNumOfColumn = desData.get(0).size();
        boolean result = true;

        if (srcNumOfColumn != desNumOfColumn){
            LOGGER.info(String.format("Source #column = %d while Destination #column = %d", srcNumOfColumn, desNumOfColumn));
            result = false;
        } else {
            Set <String> srcKeys = srcData.get(0).keySet();
            Set <String> desKeys = desData.get(0).keySet();

            for(String srcKey: srcKeys){

                boolean found = false;
                for(String desKey: desKeys){

                    if(srcKey.equals(desKey)){
                        //desKeys.remove(desKey);
                        found = true;
                        break;
                    }
                }

                if (found == false){
                    LOGGER.error(String.format("Cannot find key %s in keySet %s", srcKey, desKeys.toString()));
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public static boolean compareQuantity(String srcCsv, String desCsv){
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv);
        ArrayList<HashMap<String, String>> desData = fileToMaps(desCsv);
        return compareQuantity(srcData, desData);
    }

    public static boolean compareQuantity(String srcCsv, String desCsv, String myDelimiter){
        ArrayList<HashMap<String, String>> srcData = fileToMaps(srcCsv, myDelimiter);
        ArrayList<HashMap<String, String>> desData = fileToMaps(desCsv, myDelimiter);
        return compareQuantity(srcData, desData);
    }

}
