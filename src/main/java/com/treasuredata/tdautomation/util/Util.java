package com.treasuredata.tdautomation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void printResult(String [] args, Logger myLogger){
        for (int i = 0; i < args.length; i ++)
            myLogger.info(args[i]);
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

    public static void sleep(int second){
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e){
            LOGGER.error(e.getMessage());
        }
    }


    public static boolean contains(String origString, String pattern, boolean regularExpression){
        boolean result = false;
        if (regularExpression){
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(origString);   // get a matcher object
            int count = 0;

            while(m.find()) {
                count++;
                //LOGGER.info("Match number {}", count);
                //LOGGER.info("start(): {}", m.start());
                //LOGGER.info("end(): {}", m.end());
            }
            LOGGER.info("Match number {}", count);
            if (count > 0)
                result = true;
        } else {
            result = origString.contains(pattern);
        }
        return result;
    }


    public static long getCurrentUnixTimeStamp(){
        return System.currentTimeMillis() / 1000L;
    }

    public static String getOsName(){return System.getProperty("os.name");}

    public static String dateTimeFormat (String dateTimeString, String template){
        String result = "";
        SimpleDateFormat srcDateFormat = (template == null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") : new SimpleDateFormat(template);
        try{
            result = srcDateFormat.parse(dateTimeString).toString();
        } catch (ParseException parseException){
            LOGGER.error(parseException.getMessage());
        }
        return result;
    }

    public static String getDateStringFromUnixTimeStamp(long unixTimeStamp){
        Date date = new java.util.Date(unixTimeStamp*1000L);
        return date.toString();
    }
}
