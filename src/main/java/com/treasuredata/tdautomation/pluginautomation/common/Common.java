package com.treasuredata.tdautomation.pluginautomation.common;

import com.treasuredata.tdautomation.util.Constant;

import java.util.HashMap;

public class Common {
    public static HashMap<String, String> getTdEnvInfo(String env) {
        HashMap<String, String> result = new HashMap<String, String>();
        if (env == null) {
            result.put(Constant.TD_USERNAME, Constant.ENV.get("DEV_USER"));
            result.put(Constant.TD_APIKEY, Constant.ENV.get("DEV_API_KEY"));
            result.put(Constant.TD_ENDPOINT, Constant.ENV.get("DEV_END_POINT"));
        } else
            switch (env.trim().toLowerCase()) {
                case Constant.DEV_EU01:
                    result.put(Constant.TD_USERNAME, Constant.ENV.get("DEV_EU01_USER"));
                    result.put(Constant.TD_APIKEY, Constant.ENV.get("DEV_EU01_API_KEY"));
                    result.put(Constant.TD_ENDPOINT, Constant.ENV.get("DEV_EU01_END_POINT"));
                    break;
                case Constant.STAG:
                    result.put(Constant.TD_USERNAME, Constant.ENV.get("STAG_USER"));
                    result.put(Constant.TD_APIKEY, Constant.ENV.get("STAG_API_KEY"));
                    result.put(Constant.TD_ENDPOINT, Constant.ENV.get("STAG_END_POINT"));
                    break;
                case Constant.PROD:
                    result.put(Constant.TD_USERNAME, Constant.ENV.get("PROD_USER"));
                    result.put(Constant.TD_APIKEY, Constant.ENV.get("PROD_API_KEY"));
                    result.put(Constant.TD_ENDPOINT, Constant.ENV.get("PROD_END_POINT"));
                    break;
                default:
                    result.put(Constant.TD_USERNAME, Constant.ENV.get("DEV_USER"));
                    result.put(Constant.TD_APIKEY, Constant.ENV.get("DEV_API_KEY"));
                    result.put(Constant.TD_ENDPOINT, Constant.ENV.get("DEV_END_POINT"));
                    break;
            }
        return result;
    }
}
