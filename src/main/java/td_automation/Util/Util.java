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

}
