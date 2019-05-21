package td_automation.Util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class FileUtil {

    static Logger LOGGER = LogManager.getLogger(FileUtil.class.getName());

    /*
     - After reading, file data will be stored in a java array list of string and the first element will be file name
     */
    public ArrayList<String> readLine(String filePath) {
        ArrayList<String> lines = new ArrayList<String>();

        try {
            LOGGER.info("Read " + filePath);
            File file = new File(filePath);
            lines = readLine(file);
            lines.add(0, file.getName());
        } catch (FileNotFoundException e) {
            LOGGER.error(e.toString());
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
        return lines;
    }

    public ArrayList<String> readLine(File file) throws FileNotFoundException, IOException {
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;

        while ((st = br.readLine()) != null) {

            if (!st.isEmpty()) {
                lines.add(st);
            }
        }
        return lines;
    }

    /*
     - After reading, each file data will be stored in a java array list of string and the first element will be file name
     */
    public ArrayList<ArrayList<String>> readFolder(String folder, String fileName) {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        ArrayList<String> fileData;

        try {
            File[] files = new File(folder).listFiles();

            for (File file : files) {

                if (file.getName().contains(fileName)) {
                    String currentFile = file.getName();
                    LOGGER.info("Read " + currentFile);
                    fileData = readLine(file);
                    fileData.add(0, currentFile);
                    result.add(fileData);
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e.toString());
        } catch (IOException e) {
            LOGGER.error(e.toString());
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return result;
    }

    public static List<String> getFiles(String folder, String fileName){
        List<String> fileList = new ArrayList<String>();
        try{
            File[] files = new File(folder).listFiles();

            for (File file : files) {

                if (file.getAbsolutePath().contains(fileName)) {
                    fileList.add(file.getAbsolutePath());
                }
            }
        } catch (Exception e){
            LOGGER.error(e.getStackTrace().toString());
        }
        return fileList;
    }

    public static int searchRecord(String line, ArrayList<String> fileData) {

        try {

            // Skip the first element since it is file name
            for (int i = 1; i < fileData.size(); i++) {

                if (line.equals(fileData.get(i))) {
                    return i;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return -1;
    }

    public static boolean searchRecords(ArrayList<String> src, ArrayList<String> des) {
        int totalLine = src.size();
        int found = 0;
        int foundIndex = -1;
        boolean result = true;

        try {

            // Skip the first element since it is file name
            for (int i = 1; i < totalLine; i++) {

                foundIndex = searchRecord(src.get(i), des);
                if (foundIndex == -1) {
                    result = false;
                } else {
                    found++;

                    // Remove found element out of des so that the next search won't touch it
                    des.remove(i);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        LOGGER.info(String.format("Total record %d", totalLine - 1));
        LOGGER.info(String.format("Found record %d", found));
        return result;
    }

    public static boolean searchRecordsInFolder(ArrayList<ArrayList<String>> fileDataList, ArrayList<String> lines) {
        boolean result = true;
        int match = 0;
        int numOfLine = 0;
        int totalFile = fileDataList.size();

        for (int j = 0; j < fileDataList.size(); j++)
            numOfLine += fileDataList.get(j).size() - 1;

        try {

            // Skip the first element since it is file name
            for (int i = 0; i < totalFile; i++) {

                if (!searchRecords(fileDataList.get(i), lines)) {
                    result = false;
                    LOGGER.info(String.format("------------------>Record %d not found !", i));
                } else
                    match++;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        LOGGER.info(String.format("Total destination record %d ...", lines.size() - 1));
        LOGGER.info(String.format("Total source record %d ...", numOfLine));
        LOGGER.info(String.format("Number of match(es) found %d ...", match));
        LOGGER.info(String.format("Final result %s ...", String.valueOf(result)));
        return result;
    }

    public static String fileToMd5(String fileName) {
        String result = "";

        try {
            result = DigestUtils.md5Hex(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            LOGGER.error(e.toString());
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
        return result;
    }

    public JSONObject readJson(String filename) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = new JSONObject();

        try (FileReader reader = new FileReader(Constant.RESOURCE_PATH + "Configuration/" + filename)) {
            Object obj = jsonParser.parse(reader);
            jsonObject = (JSONObject) obj;
        } catch (FileNotFoundException e){
            LOGGER.error(e.toString());
        } catch (IOException e){
            LOGGER.error(e.toString());
        } catch (ParseException e){
            LOGGER.error(e.toString());
        }
        return jsonObject;
    }
}
