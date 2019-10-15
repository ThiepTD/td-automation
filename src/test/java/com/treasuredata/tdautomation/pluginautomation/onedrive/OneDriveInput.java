package com.treasuredata.tdautomation.pluginautomation.onedrive;

import com.treasuredata.tdautomation.pluginautomation.commontestscenarios.BasicInputPlugin;
import com.treasuredata.tdautomation.util.Constant;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.Listeners;

/*
 * Extended from BasicInputPlugin since it has the same scenario
 * Just need to define parameters: csvExpected file, yml file, db and target table names, query
 */

@Listeners(BasicInputPlugin.class)

public class OneDriveInput extends BasicInputPlugin {

    public OneDriveInput(){
        this.setDb("_onedrive_db")
                .setSourceData(String.format("%spluginautomation/onedrive/csv/common.csv", Constant.RESOURCE_PATH))
                .setTargetData(String.format("%spluginautomation/onedrive/csv/target.csv", Constant.RESOURCE_PATH))
                .setTdTable("from_3rd_party")
                .setYml(String.format("%spluginautomation/onedrive/yml/load.yml", Constant.RESOURCE_PATH))
                .setTdQuery("select Queries, Clicks, Impressions, Position, Time, CTR from from_3rd_party");
        LOGGER = LogManager.getLogger(OneDriveInput.class.getName());
    }

    @Override
    public String [] getEnvInfo(){
        String [] ymlValues = {Constant.ENV.get(OneDriveUtil.ONEDRIVE_REFRESH_TOKEN), Constant.ENV.get(OneDriveUtil.ONEDRIVE_CLIENT_ID), Constant.ENV.get(OneDriveUtil.ONEDRIVE_CLIENT_SECRET)};
        return ymlValues;
    }
}


