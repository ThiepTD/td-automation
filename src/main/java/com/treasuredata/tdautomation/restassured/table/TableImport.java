package com.treasuredata.tdautomation.restassured.table;

import com.treasuredata.tdautomation.restassured.ServiceApiBase;
import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

public class TableImport extends ServiceApiBase {
    static Logger LOGGER = LogManager.getLogger(TableImport.class.getName());

    public TableImport(){
        super();
    }

    public TableImport(String env){
        super(env);
    }

    public void makeApiCall(String dbName, String tableName, String fileName){
        String url = String.format("%s%s%s/%s",baseUrl, apiList.get("tableImport").toString(), dbName, tableName);
        response = given()
                .header("Authorization", apiKey)
                .accept(ContentType.JSON)
                .when()
                .put(url);

        jsonPath = response.jsonPath();
    }

}
