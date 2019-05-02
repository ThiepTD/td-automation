package td_automation.plugin_automation.SalesforceKrux;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import td_automation.Util.FileUtil;

import java.io.*;
import java.util.ArrayList;

public class SfdmpFileUtil extends FileUtil {

    static Logger LOGGER = LogManager.getLogger(SfdmpFileUtil.class.getName());

    public ArrayList<String> readLine(File file) throws FileNotFoundException, IOException {
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int i = 0;

        while ((st = br.readLine()) != null) {

            if (!st.isEmpty()) {
                //LOGGER.info(String.format("Read line %d", ++i));
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


}
