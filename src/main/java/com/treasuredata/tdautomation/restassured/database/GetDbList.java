package com.treasuredata.tdautomation.restassured.database;

import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.treasuredata.tdautomation.restassured.ServiceApiBase;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

public class GetDbList extends ServiceApiBase {

    private List<Database> databaseList;
    static Logger LOGGER = LogManager.getLogger(GetDbList.class.getName());

    public GetDbList() {
        super();
    }

    public GetDbList(String envName) {
        super(envName);
    }

    public void makeApiCall() {
        String url = baseUrl + apiList.get("dbList").toString();
        response = given()
                .header("Authorization", apiKey)
                .accept(ContentType.JSON)
                .when()
                .get(url);

        jsonPath = response.jsonPath();
        initializeDb();
    }

    public List<Database> getDatabaseList() {
        return databaseList;
    }

    public Response getResponse() {
        return response;
    }

    public JsonPath getJsonPath() {
        return jsonPath;
    }

    public void initializeDb() {
        List<Object> databases = jsonPath.getList("databases");
        databaseList = new ArrayList<Database>();
        try {
            for (int i = 0; i < databases.size(); i++) {
                databaseList.add(new Database((HashMap<String, Object>) databases.get(i)));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }

    /*
     * Verify if database list returned by server contains a completed specific db or not
     * If yes then return the match found
     */
    public Database contains(Database db) {
        for (int i = 0; i < databaseList.size(); i++) {
            if (databaseList.get(i).isEqual(db))
                return databaseList.get(i);
        }
        return null;
    }

    /*
     * Verify if database list returned by server contains a not completed specific db or not
     * If yes then return the match found
     */
    public Database partiallyContains(Database db) {
        for (int i = 0; i < databaseList.size(); i++) {
            if (databaseList.get(i).contains(db))
                return databaseList.get(i);
        }
        return null;
    }

    /*
     * Verify if database list returned by server contains a specific db which has the info as parameters or not
     * If yes then return the match found
     */
    public Database attrContains(String keyName, Object value) {
        for (int i = 0; i < databaseList.size(); i++) {
            if (databaseList.get(i).attrContains(keyName, value))
                return databaseList.get(i);
        }
        return null;
    }

}
