package com.treasuredata.tdautomation.pluginautomation;

import com.treasuredata.tdautomation.util.CmdUtil;
import com.treasuredata.tdautomation.util.Constant;
import com.treasuredata.tdautomation.util.FileUtil;
import com.treasuredata.tdautomation.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The CommonSteps implements an iTestListener and IRetryAnalyzer
 * to support automatically rerun failure TestNg tests
 * It also contains other methods so that children classes can overwrite
 * @author  Thiep Truong
 * @version 1.0
 * @since   2019-08-06
 */

public class CommonSteps {

    public static Logger LOGGER = LogManager.getLogger(CommonSteps.class.getName());
    public static String TD_CONF;

    public CommonSteps(){};

    public static String transferDataToTd(String ymlFile, String tdDb, String tdTable){
        return transferDataToTd(TD_CONF, ymlFile, tdDb, tdTable);
    }

    public static String transferDataToTd(String env, String ymlFile, String tdDb, String tdTable){
        String tdConnectorIssue = "td -c %s connector:issue %s --database %s --table %s --auto-create-table -w";
        String tdCmd = String.format(tdConnectorIssue, env, ymlFile, tdDb, tdTable);
        return CmdUtil.executeUsingCmd(tdCmd);
    }

    public static String exportTdDataToFile(String tdDb, String queryType, String fileFormat, String outputFile, String query){
        return exportTdDataToFile(TD_CONF, tdDb, queryType, fileFormat, outputFile, query);
    }

    public static String exportTdDataToFile(String env, String tdDb, String queryType, String fileFormat, String outputFile, String query){
        String exportTdDataToFile = "td -c %s query --column-header -d %s -w -T %s -f %s -o %s \"%s\"";
        String defaultQueryType = (queryType.equalsIgnoreCase("hive") || queryType.equalsIgnoreCase("presto")) ? queryType : "presto";
        String defaultFileFormat = (fileFormat.equalsIgnoreCase("csv") | fileFormat.equalsIgnoreCase("tsv")) ? fileFormat : "csv";
        String tdCmd = String.format(exportTdDataToFile, env, tdDb, defaultQueryType, defaultFileFormat, "%s", query);
        return exportTdDataToFile(tdCmd, outputFile);
    }

    public static String exportTdDataTo3rdParty(String tdDb, String engine, String url, String query){
        return exportTdDataTo3rdParty(TD_CONF, tdDb, engine, url, query);
    }

    public static String exportTdDataTo3rdParty(String env, String tdDb, String engine, String url, String query){
        String exportTdDataTo3rdParty = "td -c %s query -d %s -w -T %s -r %s \"%s\"";
        String queryType = (engine.equalsIgnoreCase("hive") || engine.equalsIgnoreCase("presto")) ? engine : "presto";
        String tdCmd = String.format(exportTdDataTo3rdParty, env, tdDb, queryType, url, query);
        return CmdUtil.executeUsingCmd(tdCmd);
    }

    /**
     * This is a method to create a connector session which will be use for incremental data loading
     * It firstly delete the connector if it is already exists
     * @param connectionName the name of the connector session we want to create
     * @param schedule the schedule for running, default value will be hourly if we pass null value for it
     * @param tableName TD table name we want to store data transferred from third party data platform
     * @param ymlFile yml file - full path.
     * @return output of td cli command
     */
    public static String createConnectionSession(String connectionName, String schedule,String database, String tableName, String ymlFile) {
        return createConnectionSession(connectionName, TD_CONF, schedule, database,tableName, ymlFile);
    }

    public static String createConnectionSession(String connectionName, String env, String schedule,String database, String tableName, String ymlFile) {
        if (schedule == null)
            schedule = "@hourly";
        deleteConnectionSession(env, connectionName);
        String createConnectionSession = "td -c %s connector:create %s %s %s %s %s";
        String tdCmd = String.format(createConnectionSession, env, connectionName, schedule,database, tableName, ymlFile);
        return CmdUtil.executeUsingCmd(tdCmd);
    }

    /**
     * This is a method to run a connector session which will be use for incremental data loading
     * @param connectionName the name of the connector session we want to create
     * @return output of td cli command
     */
    public static String runConnectionSession(String connectionName) {
        return runConnectionSession(TD_CONF, connectionName);
    }

    public static String runConnectionSession(String env, String connectionName) {
        String tdCmd = String.format("td -c %s connector:run %s %s -w", env, connectionName, Util.dateTimeFormat(Util.getDateStringFromUnixTimeStamp(Util.getCurrentUnixTimeStamp() + 2), null));
        return CmdUtil.executeUsingCmd(tdCmd);
    }

    public static String deleteConnectionSession(String connectionName) {
        return deleteConnectionSession(TD_CONF, connectionName);
    }

    /**
     * This is a method to delete a connector session which will be use for incremental data loading
     * @param connectionName the name of the connector session we want to create
     * @param env td configuration file
     * @return output of td cli command
     */
    public static String deleteConnectionSession(String env, String connectionName) {
        String tdCmd = String.format("td -c %s connector:delete %s", env, connectionName);
        return CmdUtil.executeUsingCmd(tdCmd);
    }

    public static String importTestDataToTd(String fileName, String tdDb, String tdTable, String fileFormat, long unixTimeStamp, long range) {
        return importTestDataToTd(TD_CONF, fileName, tdDb, tdTable, fileFormat, unixTimeStamp, range);
    }

    /**
     * This is a method to import our csv test data into an existing TD table
     * @param fileName source test data to be imported to Td
     * @param fileFormat format of source test data file
     * @param unixTimeStamp provided unix timestamp
     * @param range time range = unix timestamp + range
     * @param env td configuration file
     * @param tdTable the name of the connector session we want to create
     * @param tdDb td database name
     * @return output of td cli command
     */
    public static String importTestDataToTd(String env, String fileName, String tdDb, String tdTable, String fileFormat, long unixTimeStamp, long range) {
        // We use this td import:auto to import csv test data to TD
        //td -c <td config> import:auto <csv file> --auto-create <db>.<table> --format csv --column-header --time-column <unix timestamp>,<duration>
        String importToTd = "td -c %s import:auto %s --auto-create %s.%s --format %s --column-header --time-value %d,%d";

        long currentUnixTimeStamp = (unixTimeStamp == 0) ? Util.getCurrentUnixTimeStamp() : unixTimeStamp;
        long defaultRange = (range == 0) ? 10000 : range;
        String defaultFormat = (fileFormat == null) ? "csv" : fileFormat;
        String tdCmd = String.format(importToTd, env, fileName, tdDb, tdTable, defaultFormat, currentUnixTimeStamp, defaultRange);
        return CmdUtil.executeUsingCmd(tdCmd);
    }

    public static String freshImportTestDataToTd(String tdDb, String tdTable, String fileFormat, String fileName, long unixTimeStamp, long range) {
        return freshImportTestDataToTd(TD_CONF, tdDb, tdTable, fileFormat, fileName, unixTimeStamp, range);
    }

    /**
     * This is a method to import our csv test data into a new TD table
     * @param fileName source test data to be imported to Td
     * @param fileFormat format of source test data file
     * @param unixTimeStamp provided unix timestamp
     * @param range time range = unix timestamp + range
     * @param env td configuration file
     * @param tdTable the name of the connector session we want to create
     * @param tdDb td database name
     * @return output of td cli command
     */
    public static String freshImportTestDataToTd(String env, String tdDb, String tdTable, String fileFormat, String fileName, long unixTimeStamp, long range) {
        createTable(env, tdDb, tdTable);
        return importTestDataToTd(env, fileName, tdDb, tdTable, fileFormat, unixTimeStamp, range);
    }

    /**
     * This is a method to export data from a TD table to a our csv by using temp files
     * It will export data into a temp file (contain unix timestamp) then write content of the temp file into target csv file
     * @param command td command line to export test data
     * @param tarFile target csv file - full path
     * @return output of td cli command
     */
    public static String exportTdDataToFile(String command, String tarFile) {
        String tempFile = tarFile + String.valueOf(Util.getCurrentUnixTimeStamp());
        command = String.format(command, tempFile);
        String result = CmdUtil.executeUsingCmd(command);
        try {
            File srcFile = new File(tempFile);
            File desFile = new File(tarFile);
            //desFile.deleteOnExit();
            FileUtil.copyFileUsingChannel(srcFile, desFile);
            srcFile.delete();
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
        return result;
    }

    public static String deleteDb(String dbName) {
        return deleteDb(TD_CONF, dbName);
    }

    /**
     * This is a method to delete a certain TD database by using td cli
     * @param dbName TD database name.
     * @param env td configuration file
     * @return a string as an output of the td cli command
     */
    public static String deleteDb(String env, String dbName) {
        String commands = String.format("td -c %s db:delete -f %s", env, dbName);
        return CmdUtil.executeUsingCmd(commands);
    }

    public static String deleteTable(String dbName, String tableName) {
        return deleteTable(TD_CONF, dbName, tableName);
    }

    /**
     * This is a method to delete a certain TD table by using td cli
     * @param env td configuration file
     * @param dbName TD database name.
     * @param tableName TD table name.
     * @return a string as an output of the td cli command
     */
    public static String deleteTable(String env, String dbName, String tableName) {
        String commands = String.format("td -c %s table:delete -f %s %s", env, dbName, tableName);
        return CmdUtil.executeUsingCmd(commands);
    }

    public static String createDb(String dbName) {
        return createDb(TD_CONF, dbName);
    }

    /**
     * This is a method to create a certain TD database by using td cli
     * @param env td configuration file
     * @param dbName TD database name.
     * @return a string as an output of the td cli command
     */
    public static String createDb(String env, String dbName) {
        String commands = String.format("td -c %s db:create %s", env, dbName);
        return CmdUtil.executeUsingCmd(commands);
    }

    public static String createTable(String dbName, String tableName) {
        return createTable(TD_CONF, dbName, tableName);
    }

    public static String createTableIfNotExist(String env, String dbName, String tableName) {
        String commands = String.format("td -c %s table:create %s %s", env, dbName, tableName);
        if (!isTableExist(env, dbName, tableName)){
            createDb(env, dbName);
            return CmdUtil.executeUsingCmd(commands);
        }
        return "Table is already exist";
    }

    public static String createTableIfNotExist(String dbName, String tableName) {
        return createTableIfNotExist(TD_CONF, dbName, tableName);
    }
    /**
     * This is a method to create a certain TD database by using td cli
     * It firstly creates database first then create table for that database
     * @param env td configuration file
     * @param dbName TD database name.
     * @param tableName TD table name.
     * @return a string as an output of the td cli command
     */
    public static String createTable(String env, String dbName, String tableName) {
        String commands = String.format("td -c %s table:create %s %s", env, dbName, tableName);
        createDb(env, dbName);
        deleteTable(env, dbName, tableName);
        return CmdUtil.executeUsingCmd(commands);
    }

    public static int getRecordCount(String dbName, String outputFile, String query) {
        return getRecordCount(TD_CONF, dbName, outputFile, query);
    }

    public static boolean isTableExist(String env, String dbName, String tableName){
        String command = String.format("td -c %s table:show %s %s", env, dbName, tableName);
        String result = CmdUtil.executeUsingCmd(command);
        if (result.contains("does not exist"))
            return false;
        return true;
    }
    public static boolean isTableExist(String dbName, String tableName){
        return isTableExist(TD_CONF, dbName, tableName);
    }

    /**
     * This is a method to export data from a TD table to a our csv
     * It expects the csv file just has 2 lines and the second line contains the info we want to see, number of record
     * @param env td configuration file
     * @param dbName TD database name
     * @param outputFile csv output file to store exporting result
     * @param query the sql query - Ex: select count(id) from my_table
     * @return number of records
     */
    public static int getRecordCount(String env, String dbName, String outputFile, String query) {
        outputFile = (outputFile == null) ? String.format("%soutput.csv", Constant.RESOURCE_PATH) : outputFile;
        String tdCmd = String.format("td -c %s query -d %s -w -T presto -f csv -o %s \"%s\"", env, dbName, outputFile, query);
        String result = CmdUtil.executeUsingCmd(tdCmd);
        if (result.contains(": success")) {
            String recordCount = FileUtil.readLine(outputFile).get(0);
            try {
                (new File(outputFile)).delete();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            return Integer.parseInt(recordCount);
        }
        return -1;
    }

    public static String exportTdDataToFileWithoutHeaders(String tdDb, String queryType, String fileFormat, String outputFile, String query){
        return exportTdDataToFileWithoutHeaders(TD_CONF, tdDb, queryType, fileFormat, outputFile, query);
    }

    public static String exportTdDataToFileWithoutHeaders(String tdConfig, String tdDb, String queryType, String fileFormat, String outputFile, String query){
        String exportTdDataToFile = "td -c %s query  -d %s -w -T %s -f %s -o %s \"%s\"";
        String defaultQueryType = (queryType.equalsIgnoreCase("hive") || queryType.equalsIgnoreCase("presto")) ? queryType : "presto";
        String defaultFileFormat = (fileFormat.equalsIgnoreCase("csv") | fileFormat.equalsIgnoreCase("tsv")) ? fileFormat : "csv";
        String tdCmd = String.format(exportTdDataToFile, tdConfig, tdDb, defaultQueryType, defaultFileFormat, "%s", query);
        return exportTdDataToFile(tdCmd, outputFile);
    }
}
