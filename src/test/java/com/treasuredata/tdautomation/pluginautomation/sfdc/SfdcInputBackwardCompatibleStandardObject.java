package com.treasuredata.tdautomation.pluginautomation.sfdc;

import com.treasuredata.tdautomation.pluginautomation.commontestscenarios.InputPluginBackwardCompatible;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.Listeners;

/*
 * Extended from InputPluginBackwardCompatible since it has the same scenario
 * Just need to define parameters: 2 csv output files, 2 yml file2, db and 2 table names, query
 */

@Listeners(InputPluginBackwardCompatible.class)

public class SfdcInputBackwardCompatibleStandardObject extends InputPluginBackwardCompatible {
    public static String NAME = "StandardObject";

    public static String V1_YML_FILE = String.format("pluginautomation/sfdc/yml/v1%s.yml", NAME);
    public static String V2_YML_FILE = String.format("pluginautomation/sfdc/yml/v2%s.yml", NAME);
    public static String V1_EXPORTED_FILE = String.format("pluginautomation/sfdc/csv/v1%s.csv", NAME);
    public static String V2_EXPORTED_FILE = String.format("pluginautomation/sfdc/csv/v2%s.csv", NAME);
    public static String V1_TABLE = "v1_table";
    public static String V2_TABLE = "v2_table";

    public static String QUERY = "SELECT name, accountnumber, ownerid, active__c, site, accountSource, AnnualRevenue, " +
            "BillingAddress, CleanStatus, CreatedById, CustomerPriority__c, DandbCompanyId, DunsNumber, Jigsaw, Description, " +
            "NumberOfEmployees, Fax, Industry, LastModifiedById, NaicsCode, NaicsDesc, NumberofLocations__c, Ownership, ParentId, " +
            "Phone, Rating, ShippingAddress, Sic, SicDesc, SLA__c, SLAExpirationDate__c, SLASerialNumber__c, TickerSymbol, Tradestyle, " +
            "Type, UpsellOpportunity__c, Website, YearStarted from %s";

    public static String DB_NAME = "_sfdc";
    public SfdcInputBackwardCompatibleStandardObject(){
        this.setYml(V1_YML_FILE)
                .setSourceData(V1_EXPORTED_FILE)
                .setTargetData(V2_EXPORTED_FILE)
                .setTdTable(V1_TABLE)
                .setTdQuery(QUERY)
                .setDb(DB_NAME);
        this.setV2Table(V2_TABLE).setV2Yml(V2_YML_FILE);
        LOGGER = LogManager.getLogger(SfdcInputBackwardCompatibleStandardObject.class.getName());
    }
}


