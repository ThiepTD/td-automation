package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.treasuredata.tdautomation.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.io.*;


public class YmlFileUtil extends FileUtil {

    static Logger LOGGER = LogManager.getLogger(YmlFileUtil.class.getName());


    /** writing result to a  file **/
    //@Override
    public static void writeFile(String fileName, String[] lines) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(fw);
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }


    /** set file or folder Id in yml file **/
    public void setFileIdInYml(String fileName, String condition, String newLine){
        replaceLine(fileName,condition,newLine);
    }

    /** change the modified time in yml file **/
    public void setModifiedTimeInYml(String fileName, String condition, String newLine){
        replaceLine(fileName,condition,newLine);
    }

    /** Replacing the modified time in YML file with current file **/
    public void replaceModifiedTimeInYml(String yml) {
        Date date = new Date();
        TimeZone toUtcTimezone = TimeZone.getTimeZone("UTC");

        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        formatter.setTimeZone(toUtcTimezone);
        new YmlFileUtil().setModifiedTimeInYml(yml, "  last_modified_time:", "  last_modified_time: '" + formatter.format(date)+"'");
    }

    /** Setting refresh token in yml file **/
   public void setRefreshToken(String fileName, String condition, String newLine){
       replaceLine(fileName,condition,newLine);
   }

}
