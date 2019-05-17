package td_automation.Util;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.*;

public class Util {

    static Logger LOGGER = LogManager.getLogger(Util.class.getName());

    public static ArrayList<String> replaceAll(ArrayList<String> data, String oldString, String newString) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < data.size(); i++) {
            result.add(data.get(i).replaceAll(oldString, newString));
        }
        return result;
    }

    /*
     - If your csv file contains headers, please pass true for the second parameter
     - Each line in csv file will become key names. In case the hash map already contains the key then the line become value
     */
    public static HashMap<Object, Object> arrayStringToMap(ArrayList<String> strings, boolean containHeader) {
        HashMap<Object, Object> result = new HashMap<Object, Object>();
        int startingPoint = (containHeader == true) ? 2 : 1;
        for (int i = startingPoint; i < strings.size(); i++) {
            String value = strings.get(i);

            if (!result.containsKey(value)) {
                result.put(value, strings.get(0) + ":" + String.valueOf(i));
            } else {
                result.put(strings.get(0) + ":" + String.valueOf(i), value);
            }
        }
        return result;
    }

    /*
     - Remove " out  of csv lines
     - Each line in csv file will become key names. In case the hash map already contains the key then the line become value
     */
    public static HashMap<Object, Object> arrayFolderToMap(ArrayList<ArrayList<String>> stringFolder, String myDelimiter) {
        HashMap<Object, Object> result = new HashMap<Object, Object>();
        for (int i = 0; i < stringFolder.size(); i++) {
            ArrayList<String> currentString = new ArrayList<String>();
            if (myDelimiter != null)
                currentString = Util.replaceAll(Util.replaceAll(stringFolder.get(i), myDelimiter, ","), "\"", "");

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
