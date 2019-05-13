package td_automation.rest_assured.database;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import td_automation.rest_assured.ServiceApiBase;
import static org.testng.Assert.assertTrue;

public class DatabaseTest extends ServiceApiBase {

    public CreateDb createDb;
    public GetDbList dbList;
    public DeleteDb deleteDb;
    public static String dbName = "thiep_test";

    @BeforeSuite
    public void setUp(){
        dbList = new GetDbList();
        createDb = new CreateDb();
        deleteDb = new DeleteDb();
        dbList.makeApiCall();

        if(dbList.attrContains("name", dbName) != null){
            deleteDb.makeApiCall(dbName);
        }
    }

    @Test
    public void createDb(){
        createDb.makeApiCall(dbName);
        assertTrue(createDb.getDbName() != null);
    }

    @Test(dependsOnMethods = {"createDb"})
    public void verifyDbIsCreated(){
        dbList.makeApiCall();
        assertTrue(dbList.attrContains("name", dbName) != null);
    }

    @Test(dependsOnMethods = {"verifyDbIsCreated"})
    public void deleteDb(){
        deleteDb.makeApiCall(dbName);
        assertTrue(createDb.getDbName().equals(dbName));
    }

    @Test(dependsOnMethods = {"deleteDb"})
    public void verifyDbIsDeleted(){
        dbList.makeApiCall();
        assertTrue(dbList.attrContains("name", dbName) == null);
    }
}
