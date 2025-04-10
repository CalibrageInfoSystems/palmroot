package com.calibrage.palmroot.database;

import java.util.ArrayList;
import java.util.List;

public class DatabaseKeys {


    public static List<String> transactionTables = new ArrayList<>();
    public static String TABLE_SAPLING = "Sapling";
    public static String TABLE_SaplingActivity = "SaplingActivity";
    public static String TABLE_SaplingActivityXref = "SaplingActivityXref";
    public static String TABLE_SaplingActivityHistory = "SaplingActivityHistory";
    public static String TABLE_SaplingActivityStatus = "SaplingActivityStatus";
    public static String TABLE_NurseryIrrigationLog = "NurseryIrrigationLog";
    public static String TABLE_NurseryIrrigationLogXREF = "NurseryIrrigationLogXref";
    public static String TABLE_NurseryIrrigationhistory = "IrrigationLogStatusHistory";
    public static String TABLE_FILEREPOSITORY = "CullingLossFileRepository";
    public static String TABLE_NURSERYVISITLOGS = "NurseryVisitLog";
    public static String TABLE_NurseryLabourLog = "NurseryLabourLog";
    public static String TABLE_PLOT = "Plot";
    public static String TABLE_RMTransactions = "RMTransactions";
    public static String TABLE_RMTransactionStatusHistory = "RMTransactionStatusHistory";

    public static String TABLE_FARMER= "Farmer";



    // Falog  Tracking
    public static final String TABLE_Location_TRACKING_DETAILS = "LocationTracker";
    public static final String LATITUDE="Latitude";
    public static final String LONGITUDE="Longitude";
    public static  final String IsActive = "IsActive";

    public final static String TABLE_ALERTS = "Alerts";


    public static final String TABLE_VisitLog = "VisitLog";

    public static final String CreatedByUserId = "CreatedByUserId";
    public static final String CreatedDate="CreatedDate";
    public static final String ServerUpdatedStatus="ServerUpdatedStatus";

}
