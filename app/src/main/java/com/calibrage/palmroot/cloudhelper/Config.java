package com.calibrage.palmroot.cloudhelper;

import com.calibrage.palmroot.BuildConfig;

public class Config {

    public static final boolean DEVELOPER_MODE = false;

public static String live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api"; //localtest
   // public static String live_url = "http://192.168.1.118/Smartpalm/API/api";
//public static String live_url = "http://182.18.157.215/3FOilPalmNursery_TestLive/API/api"; //Nursery_TestLive

    public static void initialize() {
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
        //    live_url = "http://192.168.1.118/Smartpalm/API/api";

live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api";//localtest
//live_url = "http://182.18.157.215/3FOilPalmNursery_TestLive/API/api"; //Nursery_TestLive

         } else {
     //       live_url = "http://192.168.1.118/Smartpalm/API/api";
live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api";//localtest
//live_url = "http://182.18.157.215/3FOilPalmNursery_TestLive/API/api"; //Nursery_TestLive

        }
    }

    public static final String masterSyncUrl = "/SyncMasters/SyncNurseryMasters";

    public static final String transactionSyncURL = "/SyncTransactions/SyncNurseryTransactions";
    public static final String locationTrackingURL = "/LocationTracker/SaveOfflineLocations";
    public static final String imageUploadURL = "/SyncTransactions/UploadImage";

    public static final String findcollectioncode = "/SyncTransactions/FindCollectionCode/%s";
    public static final String findconsignmentcode = "/SyncTransactions/FindConsignmentCode/%s";
    public static final String findcollectionplotcode = "/SyncTransactions/FindCollectionPlotXref/%s/%s";

    public static final String updatedbFile = "/TabDatabase/UploadDatabase";

    public static final String getTransCount = "/SyncTransactions/GetNurseryCount";//{Date}/{UserId}
    public static final String getTransData = "/SyncTransactions/%s";//api/TranSync/SyncFarmers/{Date}/{UserId}/{Index}
    public static final String validateTranSync = "/TranSync/ValidateTranSync/%s";
  //  public static final String image_url = "http://192.168.1.118/Smartpalm/FileRepository/";
public static final String image_url = "http://182.18.157.215/3FSmartPalm_Nursery/3FSmartPalm_Nursery_Repo/FileRepository/";
//local

    // Commented on 1-06-2022 to change the URL to local
// public static final String image_url = "http://182.18.157.215/3FOilPalmNursery_TestLive/3FOilPalmNursery_TestLiveRepo/FileRepository/";//testlive


   // http://183.82.111.111/3FOilPalm_Nursery/3FNurseryRepository/FileRepository//2022\03\04\RMTransaction/20220304035712813.jpg
    public static final String GETMONTHLYTARGETSBYUSERIDANDFINANCIALYEAR = "/KRA/GetMonthlyTargetsByUserIdandFinancialYear";
    public static final String GETTARGETSBYUSERIDANDFINANCIALYEAR = "/KRA/GetTargetsByUserIdandFinancialYear";
    public static final String GET_ALERTS = "/SyncTransactions/SyncNurseryAlertDetails";//{UserId}
}
