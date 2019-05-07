package td_automation.rest_assured;

import org.json.simple.JSONObject;
import td_automation.Util.FileUtil;

public class ServiceApiBase {
    private String baseUrl;
    private String apiKey;
    private JSONObject apiList;
    public static final String DEV_ENV = "developmentAws";
    public static final String STG_ENV = "stagingAws";
    public static final String PRO_ENV = "productionAws";

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
}
