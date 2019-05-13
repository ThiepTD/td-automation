package td_automation.rest_assured;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import td_automation.Util.FileUtil;

public class ServiceApiBase {
    protected String baseUrl;
    protected String apiKey;
    protected JSONObject apiList;
    protected Response response;
    protected JsonPath jsonPath;
    public static final String DEV_ENV = "developmentAws";
    public static final String STG_ENV = "stagingAws";
    public static final String PRO_ENV = "productionAws";

    static Logger LOGGER = LogManager.getLogger(ServiceApiBase.class.getName());

    public ServiceApiBase(){
        JSONObject tdEnv = (new FileUtil()).readJson("td_environment.json");
        baseUrl = ((JSONObject)tdEnv.get(DEV_ENV)).get("baseUrl").toString();
        apiKey = ((JSONObject)tdEnv.get(DEV_ENV)).get("apiKey").toString();
        apiList = (JSONObject)tdEnv.get("apiList");
    }

    public ServiceApiBase(String envName){
        JSONObject tdEnv = (new FileUtil()).readJson("td_environment.json");
        baseUrl = ((JSONObject)tdEnv.get(envName)).get("baseURL").toString();
        apiKey = ((JSONObject)tdEnv.get(envName)).get("apiKey").toString();
        apiList = (JSONObject)tdEnv.get("apiList");
    }

    public String getBaseUrl() { return baseUrl; }
    public String getApiKey() { return apiKey; }
    public JSONObject getApiList() { return apiList;}
    public Response getResponse() { return response;}
    public JsonPath getJsonPath() { return jsonPath;};

    public void setResponse(Response myResponse) {response = myResponse;}
    public void setJsonPath(JsonPath myJsonPath) {jsonPath = myJsonPath;}

    public void print() {
        LOGGER.info("");
        response.prettyPrint();
    }
}
