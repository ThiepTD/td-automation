package td_automation.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class FileUtil {

    static Logger LOGGER = LogManager.getLogger(FileUtil.class.getName());

    public static ArrayList<String> readLine(String filePath) {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            LOGGER.info("Read " + filePath);
            File file = new File(filePath);
            lines = readLine(file);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.toString());
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
        return lines;
    }

    public static ArrayList<String> readLine(File file) throws FileNotFoundException, IOException {
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null) {
            if (!st.isEmpty()) {
                //st = st.replaceAll("\"", "");
                //lines.add(st);
                String beginString = "";
                if (!st.startsWith("\"")) {
                    beginString = st.substring(0, 4);
                } else {
                    beginString = st.substring(1, 5);
                }

                try {
                    int value = Integer.parseInt(beginString);
                    lines.add(st);
                } catch (Exception e) {
                    if (lines.size() == 0 || beginString.isEmpty())
                        lines.add(st);
                    else
                        lines.set(lines.size() - 1, lines.get(lines.size() - 1) + st);
                }
            }
        }
        return lines;
    }

    public static ArrayList<ArrayList<String>> readFolder(String folder, String fileName) {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        ArrayList<String> fileData;

        try {
            File[] files = new File(folder).listFiles();
            for (File file : files) {
                if (file.getName().contains(fileName)) {
                    String currentFile = file.getName();
                    LOGGER.info("Read " + currentFile);
                    fileData = Util.replaceAll(readLine(file), "\"", "");
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

    public static boolean seachRecord(String line, ArrayList<String> fileData) {
        try {
            for (int i = 0; i < fileData.size(); i++) {
                if (line.equals(fileData.get(i))) {
                    fileData.remove(i);
                    LOGGER.info("FOUND !");
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return false;
    }

    public static boolean seachRecords(ArrayList<String> lines, ArrayList<String> fileData) {
        int totalLine = lines.size();
        int found = 0;
        boolean result = true;
        try {
            for (int i = 0; i < lines.size(); i++) {
                if (!seachRecord(lines.get(i), fileData))
                    result = false;
                else
                    found++;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        LOGGER.info(String.format("Total record %s", totalLine));
        LOGGER.info(String.format("Found record %d", found));
        return true;
    }

    public static boolean seachRecordInFolder(String line, ArrayList<ArrayList<String>> fileDataList) {
        boolean found = false;
        try {
            for (int i = 0; i < fileDataList.size(); i++) {
                ArrayList<String> tmp = fileDataList.get(i);

                for (int j = 0; j < tmp.size(); j++) {
                    if (line.equals(tmp.get(j))) {
                        tmp.remove(j);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return found;
    }

    public static boolean seachRecordsInFolder(ArrayList<String> lines, ArrayList<ArrayList<String>> fileDataList) {
        boolean result = true;
        int match = 0;
        int numOfLine = 0;
        for (int j = 0; j < fileDataList.size(); j++)
            numOfLine += fileDataList.get(j).size() - 1;

        try {
            for (int i = 1; i < lines.size(); i++) {
                LOGGER.info(String.format("Search line %d ...", i));
                if (!seachRecordInFolder(lines.get(i), fileDataList)) {
                    result = false;
                    LOGGER.info(String.format("------------------>Line %d not found !", i));
                } else
                    match++;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        LOGGER.info(String.format("Total td lines %d ...", lines.size() - 1));
        LOGGER.info(String.format("Total Krux lines %d ...", numOfLine));
        LOGGER.info(String.format("Number of match(es) found %d ...", match));
        LOGGER.info(String.format("Final result %s ...", String.valueOf(result)));
        return result;
    }

    public static boolean seachMaps(ArrayList<String> lines, ArrayList<ArrayList<String>> fileDataList, String myDelimiter) {
        boolean result = true;
        int match = 0;
        int s3Lines, tdLines = 0;
        HashMap<Object, Object> tdMap = arrayStringToMap(lines);
        HashMap<Object, Object> s3Maps = arrayFolderToMap(fileDataList, myDelimiter);
        s3Lines = s3Maps.keySet().size();
        tdLines = tdMap.keySet().size();

        // Search by key
        LOGGER.info("##########################################");
        LOGGER.info("------------------------------------ Search by key ----------------------------------");
        LOGGER.info("##########################################");
        Object[] keys = s3Maps.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            //LOGGER.info(String.format("Search line %s ...",s3Maps.get(keys[i])));

            if (!tdMap.containsKey(keys[i])) {
                LOGGER.info(String.format("--------------------------> Line %s not found !", s3Maps.get(keys[i])));
            } else {
                match++;
                tdMap.remove(keys[i]);
                s3Maps.remove(keys[i]);
            }
        }

        // Search by value
        LOGGER.info("##########################################");
        LOGGER.info("------------------------------------ Search by value ----------------------------------");
        LOGGER.info("##########################################");
        Object[] values = s3Maps.values().toArray();
        Object[] newKeys = s3Maps.keySet().toArray();
        for (int i = 0; i < values.length; i++) {
            //LOGGER.info(String.format("Search line %s ...", s3Maps.get(newKeys[i])));

            if (!tdMap.containsValue(values[i])) {
                result = false;
                LOGGER.info(String.format("--------------------------> Line %s not found !", s3Maps.get(newKeys[i])));
            } else {
                match++;
            }
        }
        LOGGER.info(String.format("Total td lines %d ...", tdLines));
        LOGGER.info(String.format("Total Krux lines %d ...", s3Lines));
        LOGGER.info(String.format("Number of match(es) found %d ...", match));
        LOGGER.info(String.format("Final result %s ...", String.valueOf(result)));
        return result;
    }

    public static HashMap<Object, Object> arrayStringToMap(ArrayList<String> strings) {
        HashMap<Object, Object> result = new HashMap<Object, Object>();
        for (int i = 1; i < strings.size(); i++) {
            String value = strings.get(i);

            if (!result.containsKey(value)) {
                result.put(value, i);
            } else {
                result.put(i, value);
            }
        }
        return result;
    }

    public static HashMap<Object, Object> arrayFolderToMap(ArrayList<ArrayList<String>> stringFolder, String myDelimeter) {
        HashMap<Object, Object> result = new HashMap<Object, Object>();
        for (int i = 0; i < stringFolder.size(); i++) {
            ArrayList<String> currentString = new ArrayList<String>();
            if (myDelimeter != null)
                currentString = Util.replaceAll(Util.replaceAll(stringFolder.get(i), "\"", ""), myDelimeter, ",");

            for (int j = 1; j < currentString.size(); j++) {
                String value = currentString.get(j);
                if (!result.containsKey(value)) {
                    result.put(value, currentString.get(0) + ":" + String.valueOf(j));
                } else {
                    result.put(currentString.get(0) + ":" + String.valueOf(j), value);
                }
            }
        }
        return result;
    }
}
