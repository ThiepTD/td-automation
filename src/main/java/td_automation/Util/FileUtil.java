package td_automation.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class FileUtil {

    static Logger LOGGER = LogManager.getLogger(FileUtil.class.getName());

    public static ArrayList<String> readLine(String filePath){
        ArrayList<String> lines = new ArrayList<String>();
        try {
            LOGGER.info("Read " + filePath);
            File file = new File(filePath);
            lines = readLine(file);
        }catch (FileNotFoundException e){
            LOGGER.error(e.toString());
        }catch (IOException e){
            LOGGER.error(e.toString());
        }
        return lines;
    }

    public static ArrayList<String> readLine(File file) throws FileNotFoundException, IOException{
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null){
            if (!st.isEmpty()){
                //st = st.replaceAll("\"", "");
                //lines.add(st);
                String beginString = "";
                if (!st.startsWith("\"")) {
                    beginString = st.substring(0, 4);
                } else {
                    beginString = st.substring(1, 5);
                }

                try{
                    int value = Integer.parseInt(beginString);
                    lines.add(st);
                } catch (Exception e){
                    if (lines.size() == 0 || beginString.isEmpty())
                        lines.add(st);
                    else
                        lines.set(lines.size() - 1, lines.get(lines.size() - 1) + st);
                }
            }
        }
        return lines;
    }

    public static ArrayList<ArrayList<String>> readFolder(String folder, String fileName){
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        ArrayList<String> fileData;

        try{
            File [] files = new File(folder).listFiles();
            for(File file: files){
                if (file.getName().contains(fileName)){
                    String currentFile = file.getName();
                    LOGGER.info("Read " + currentFile);
                    fileData = Util.replaceAll(readLine(file), "\"", "");
                    fileData = readLine(file);

                    fileData.add(0, currentFile);
                    result.add(fileData);
                }
            }
        } catch (FileNotFoundException e){
            LOGGER.error(e.toString());
        } catch (IOException e){
            LOGGER.error(e.toString());
        } catch (Exception e){
            LOGGER.error(e.toString());
        }
        return result;
    }

    public static boolean seachRecord(String line, ArrayList<String> fileData){
        try {
            for (int i = 0; i < fileData.size(); i ++) {
                if (line.equals(fileData.get(i))) {
                    fileData.remove(i);
                    LOGGER.info("FOUND !");
                    return true;
                }
            }
        } catch (Exception e){
            LOGGER.error(e.toString());
        }
        return false;
    }

    public static boolean seachRecords(ArrayList<String> lines, ArrayList<String> fileData){
        int totalLine = lines.size();
        int found = 0;
        boolean result = true;
        try {
            for (int i = 0; i < lines.size(); i ++) {
                if (!seachRecord(lines.get(i), fileData))
                    result = false;
                else
                    found ++;
            }
        } catch (Exception e){
            LOGGER.error(e.toString());
        }
        LOGGER.info(String.format("Total record %s", totalLine));
        LOGGER.info(String.format("Found record %d", found));
        return true;
    }

    public static boolean seachRecordInFolder(String line, ArrayList<ArrayList<String>> fileDataList){
        boolean found = false;
        try {
            for (int i = 0; i < fileDataList.size(); i ++) {
                ArrayList<String> tmp = fileDataList.get(i);

                for (int j = 0; j < tmp.size(); j++){
                    if (line.equals(tmp.get(j))) {
                        tmp.remove(j);
                        return true;
                    }
                }
            }
        } catch (Exception e){
            LOGGER.error(e.toString());
        }
        return found;
    }

    public static boolean seachRecordsInFolder(ArrayList<String> lines, ArrayList<ArrayList<String>> fileDataList){
        try {
            for (int i = 0; i < lines.size(); i ++) {
                if (!seachRecordInFolder(lines.get(i), fileDataList))
                    return false;
            }
        } catch (Exception e){
            LOGGER.error(e.toString());
        }
        return true;
    }

}
