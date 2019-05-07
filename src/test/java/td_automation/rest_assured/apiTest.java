package td_automation.rest_assured;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeSuite;
import td_automation.rest_assured.database.Database;
import td_automation.rest_assured.database.GetDbList;

public class apiTest extends ServiceApiBase{

    public GetDbList dbList;

    @BeforeSuite
    public void setUp(){
       dbList = new GetDbList();
       dbList.makeApiCall();
       dbList.print();
    }

    @Test
    public void attrContains(){
        Database myDb = dbList.attrContains("name", "sfdc_sample");
        myDb.print();
        assertTrue(myDb != null);
    }

    @Test
    public void dbContains(){
        Database myDb = new Database();
        myDb.put(Database.UPDATED_AT, "2019-01-14 14:25:59 UTC");
        Database tdDb = dbList.partiallyContains(myDb);
        tdDb.print();
        assertTrue(myDb != null);
    }

    @Test
    public void validateStatusCode(){
        assertTrue(dbList.getResponse().getStatusCode() == 200);
    }
}
