package com.treasuredata.tdautomation.restassured.database;

import com.treasuredata.tdautomation.restassured.ServiceApiBase;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

public class DeleteDbTest extends ServiceApiBase {

    public CreateDb createDb;
    public GetDbList dbList;
    public DeleteDb deleteDb;
    public static boolean makeCall = false;
    public static String dbName = "thiep_test";

    @BeforeTest
    public void setUp() {
        deleteDb = new DeleteDb();
        dbList = new GetDbList();
        dbList.makeApiCall();
        if (makeCall == false) {
            if (null == dbList.attrContains("name", dbName)) {
                createDb = new CreateDb();
                createDb.makeApiCall(dbName);
            }
            deleteDb.makeApiCall(dbName);
            deleteDb.print();
        }
    }

    @Test
    public void verifyDbName() {
        assertTrue(deleteDb.getDbName().equals(dbName));
    }

    @Test
    public void dbContains() {
        GetDbList getDbList = new GetDbList();
        getDbList.makeApiCall();
        Database myDb = getDbList.attrContains("name", dbName);
        assertFalse(myDb != null);
    }

    @Test
    public void validateStatusCode() {
        assertTrue(deleteDb.getResponse().getStatusCode() == 200);
    }
}
