package td_automation.Util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CsvUtil {

    public static Logger LOGGER = LogManager.getLogger(Util.class.getName());

    /*
     - Compare values b/w 2 csv files. each line in csv file is a java ash map where key names are column .
     - We search src data in des data
     - The first element of array list is always file name
    */
    public static boolean compareCsv(ArrayList<HashMap<String, Object>> srcData, ArrayList<HashMap<String, Object>> desData) {

        boolean result = true;
        int match = 0;
        if (compareQuantity(srcData, desData)) {

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

    /*
     - Search values of each csv file from source in destination csv file. each line in csv file is a java Hash map where key names are column .
     - We search src data in des data
    */
    public static HashMap<String, Object> searchMap(HashMap<String, Object> srcData, ArrayList<HashMap<String, Object>> desData) {
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
                String desValue = desRow.get(key).toString();
                String srcValue = srcData.get(key).toString();

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

    public static boolean compareCsvList(ArrayList<HashMap<String, Object>> desDataSource, ArrayList<ArrayList<HashMap<String, Object>>> srcDataList) {
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
                currentDiff = searchMap(srcRow, srcData);

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
        LOGGER.info(String.format("%d match(es) found !", match));
        LOGGER.info(String.format("Total source record %d !", desDataSource.size() - 1));
        LOGGER.info(String.format("Total destination record %d !", totalSrcRecord));
        LOGGER.info(String.format("Compare result %s !", String.valueOf(result)));

        return result;
    }

    public static boolean compareCsv(String srcCsv, String desCsv, String [] headers) {
        ArrayList<HashMap<String, Object>> srcData = csvToArrayListOfMap(srcCsv, headers, null);
        ArrayList<HashMap<String, Object>> desData = csvToArrayListOfMap(desCsv, headers, null);
        return compareCsv(srcData, desData);
    }

    public static boolean compareCsv(String srcCsv, String desCsv, String [] headers, Character myDelimiter) {
        ArrayList<HashMap<String, Object>> srcData = csvToArrayListOfMap(srcCsv, headers, myDelimiter);
        ArrayList<HashMap<String, Object>> desData = csvToArrayListOfMap(desCsv, headers, myDelimiter);
        return compareCsv(srcData, desData);
    }

    public static boolean compareCsvList(String srcCsv, String folder, String fileName, String [] headers, Character srcDelimiter, Character targetDelimiter) {
        ArrayList<HashMap<String, Object>> srcData = csvToArrayListOfMap(srcCsv, headers, srcDelimiter);
        String [] myHeaders = (String []) srcData.get(0).get("headers");
        ArrayList<ArrayList<HashMap<String, Object>>> desDataList = csvFolderToArrayListOfMap(folder, fileName, myHeaders, targetDelimiter);
        return compareCsvList(srcData, desDataList);
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

    public static ArrayList<HashMap<String,Object>> csvToArrayListOfMap(String fileName, String [] headers, Character delimiter){
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        if (delimiter == null) delimiter = ',';
        try{
            Reader reader = Files.newBufferedReader(Paths.get(fileName));
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(delimiter)
                    .withIgnoreQuotations(false)
                    .build();
            CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();
            List<String[]> list = new ArrayList<>();
            list = csvReader.readAll();
            String [] myHeader = list.get(0);
            HashMap<String, Object> headerMap = new HashMap<String, Object>();
            headerMap.put("fileName", fileName);

            int startPoint = 1;
            String [] keyName = list.get(0);

            // header != null means csv file does NOT contains headers so we need to pass headers separately
            if (headers != null){
                startPoint = 0;
                keyName = headers;
                headerMap.put("headers", headers);
            } else {
                headerMap.put("headers", myHeader);
            }
            result.add(headerMap);

            for (int i = startPoint; i < list.size(); i ++){
                HashMap<String, Object> tmpMap = new HashMap<String, Object>();
                for (int j = 0; j < keyName.length; j ++ )
                    tmpMap.put(keyName[j], list.get(i)[j]);
                result.add(tmpMap);
            }

        }catch (IOException e){
            LOGGER.error(e.getStackTrace().toString());
        }
        return result;
    }

    public static ArrayList<ArrayList<HashMap<String, Object>>> csvFolderToArrayListOfMap(String folder, String fileName, String [] headers, Character delimiter){
        ArrayList<ArrayList<HashMap<String, Object>>> result = new ArrayList<ArrayList<HashMap<String, Object>>>();
        List<String> files = FileUtil.getFiles(folder, fileName);

        for(String file: files){
            result.add(csvToArrayListOfMap(file, headers, delimiter));
        }
        return result;
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
}
