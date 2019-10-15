package com.treasuredata.tdautomation.pluginautomation.common;

import com.treasuredata.tdautomation.pluginautomation.CommonSteps;
import com.treasuredata.tdautomation.util.CmdUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class TdTable {
    public static Logger LOGGER = LogManager.getLogger(TdTable.class.getName());
    private String dbName;
    private String name;
    private long count;
    private int columns;
    private HashMap<String, String> schema;

    private TdTable(String dbName, String tableName) {
        this.dbName = dbName;
        this.name = tableName;
        schema = new HashMap<String, String>();
    }

    public TdTable setDbName(String dbName){
        this.dbName = dbName;
        return this;
    }

    public String getDbName(){
        return dbName;
    }

    public TdTable setName(String name){
        this.name = name;
        return this;
    }

    public String getName(){
        return name;
    }

    public TdTable setCount(long count){
        this.count = count;
        return this;
    }

    public long getCount(){
        return count;
    }

    public TdTable setColumns(int numOfColumns){
        this.columns = numOfColumns;
        return this;
    }

    public int getColumns(){
        return columns;
    }

    public TdTable updateSchema(String name, String value){
        this.schema.put(name, value);
        return this;
    }

    public TdTable updateSchema(String [] nameValue){
        this.schema.put(nameValue[0].trim(), nameValue[1].trim());
        return this;
    }
    public HashMap<String, String> getSchema(){
        return schema;
    }

    public TdTable updateSchema(String [] name, String [] value){
        int len = (name.length > value.length) ? value.length: name.length;
        for (int i = 0; i < len; i ++){
            this.schema.put(name[i], value[i]);
        }
        return this;
    }

    public static TdTable getInstance(String dbName, String tableName){
        return new TdTable(dbName, tableName);
    }

    /**
     * This function will use td cli command "td -c %s table:show %s %s"
     * @param tdConf td configuration file.
     * @return this
     */
    public TdTable getTableInfo(String tdConf){
        String tdCli = String.format("td -c %s table:show %s %s", tdConf, dbName, name);
        String result = CmdUtil.executeUsingCmd(tdCli);
        if (tdCli.contains("doesn't exist")) return this;

        String [] lines = result.split("\n");
        boolean isSchema = false;
        for (int i = 0; i < lines.length; i ++){
            if (lines[i].contains("Count       :")){
                long count = Long.parseLong(lines[i].split(":")[1].trim());
                setCount(count);
            }

            if (lines[i].contains("Schema      :")){
                isSchema = true;
                continue;
            }

            if (isSchema == true){
                if (lines[i].contains(")")) {
                    isSchema = false;
                    break;
                }
                updateSchema(lines[i].split(":"));
            }
        }
        setColumns(schema.size());
        return this;
    }

    public HashMap<String, String> getSubSchema(ArrayList<String> headers){
        HashMap<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < headers.size(); i ++){
            if (schema.containsKey(headers.get(i)))
                result.put(headers.get(i), schema.get(headers.get(i)));
        }
        return result;
    }

    public  String createQuery(String [] excludeCols){
        String result = "select ";
        HashMap<String, String> tmpMap = (HashMap<String, String>) schema.clone();
        for (int i = 0; i < excludeCols.length; i ++){
            String column = excludeCols[i].trim().toLowerCase();
            if (tmpMap.containsKey(column)){
                tmpMap.remove(column);
            }
        }
        for (String key: tmpMap.keySet()){
            result += key + ", ";
        }
        result = result.substring(0, result.length() - 2) + " from " + name;
        return result;
    }

    public boolean doesSchemaContain(HashMap<String, String> expectedSchema){
        boolean result = true;
        for (String key: expectedSchema.keySet()){
            if (!schema.containsKey(key)){
                LOGGER.error("Schema doesn't contain column {}", key);
                result = false;
            } else {
                if (!schema.get(key).equals(expectedSchema.get(key))) {
                    LOGGER.error("Column {}, expected type is {} but actual type is {}", key, schema.get(key), expectedSchema.get(key));
                    result = false;
                }
            }
        }
        if (result == true){
            LOGGER.info("Already contains the expected schema");
        }
        return result;
    }

    public String exportToFile(String queryType, String fileFormat, String outputFile, String query){
        return CommonSteps.exportTdDataToFile(dbName, queryType, fileFormat, outputFile, query);
    }

    public String exportToFile(String tdConf, String queryType, String fileFormat, String outputFile, String query){
        return CommonSteps.exportTdDataToFile(tdConf, dbName, queryType, fileFormat, outputFile, query);
    }

    public String exportTo3rdParty(String queryType, String url, String query){
        return CommonSteps.exportTdDataTo3rdParty(dbName, queryType, url, query);
    }

    public String exportTo3rdParty(String tdConf, String queryType, String url, String query){
        return CommonSteps.exportTdDataTo3rdParty(tdConf, dbName, queryType, url, query);
    }
}
