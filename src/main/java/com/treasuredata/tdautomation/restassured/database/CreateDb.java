package com.treasuredata.tdautomation.restassured.database;

import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.treasuredata.tdautomation.restassured.ServiceApiBase;

import static io.restassured.RestAssured.given;

public class CreateDb extends ServiceApiBase {

    static Logger LOGGER = LogManager.getLogger(CreateDb.class.getName());

    public CreateDb(){
        super();
    }

    public CreateDb(String envName){
        super(envName);
    }

    public void makeApiCall(String dbName){
        String url = baseUrl + apiList.get("createDb").toString() + dbName;
        response = given()
                .header("Authorization", apiKey)
                .accept(ContentType.JSON)
                .when()
                .post(url);

        jsonPath = response.jsonPath();
    }

    public String getDbName(){ return jsonPath.getString("database");}

}
