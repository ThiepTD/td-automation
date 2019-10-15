package com.treasuredata.tdautomation.restassured.database;

import com.treasuredata.tdautomation.restassured.ServiceApiBase;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class CreateDbTest extends ServiceApiBase {

    public CreateDb createDb;
    public GetDbList dbList;
    public DeleteDb deleteDb;
    public static boolean madeCall = false;
    public static String dbName = "thiep_test";

    @BeforeTest
    public void setUp(){
       createDb = new CreateDb();
       dbList = new GetDbList();
       dbList.makeApiCall();

       if (madeCall == false) {
           if(dbList.attrContains("name", dbName) != null){
               deleteDb = new DeleteDb();
               deleteDb.makeApiCall(dbName);
           }

           createDb.makeApiCall(dbName);
           createDb.print();
           madeCall = true;
       }
    }

    @Test
    public void verifyDbName(){
        assertTrue(createDb.getDbName().equals(dbName));
    }

    @Test
    public void dbContains(){
        GetDbList getDbList = new GetDbList();
        getDbList.makeApiCall();
        Database myDb = getDbList.attrContains("name", dbName);
        myDb.print();
        assertTrue(myDb != null);
    }

    @Test
    public void validateStatusCode(){
        assertTrue(createDb.getResponse().getStatusCode() == 200);
    }

}
