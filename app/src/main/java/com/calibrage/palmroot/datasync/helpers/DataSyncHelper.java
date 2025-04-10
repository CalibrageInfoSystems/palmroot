package com.calibrage.palmroot.datasync.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.cloudhelper.CloudDataHandler;
import com.calibrage.palmroot.cloudhelper.Config;
import com.calibrage.palmroot.cloudhelper.HttpClient;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.DatabaseKeys;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.Alerts;
import com.calibrage.palmroot.dbmodels.CullinglossFileRepository;
import com.calibrage.palmroot.dbmodels.DataCountModel;
import com.calibrage.palmroot.dbmodels.Irrigationhistorymodel;
import com.calibrage.palmroot.dbmodels.LocationTracker;
import com.calibrage.palmroot.dbmodels.NurseryIrrigationLogForDb;
import com.calibrage.palmroot.dbmodels.NurseryIrrigationLogXref;
import com.calibrage.palmroot.dbmodels.NurseryLabourLog;
import com.calibrage.palmroot.dbmodels.NurseryVisitLog;
import com.calibrage.palmroot.dbmodels.RMTransactions;
import com.calibrage.palmroot.dbmodels.RMTransactionsStatusHistory;
import com.calibrage.palmroot.dbmodels.SaplingActivity;
import com.calibrage.palmroot.dbmodels.SaplingActivityHistoryModel;
import com.calibrage.palmroot.dbmodels.SaplingActivityStatusModel;
import com.calibrage.palmroot.dbmodels.SaplingActivityXrefModel;
import com.calibrage.palmroot.dbmodels.SaplingActivityXrefModelget;
import com.calibrage.palmroot.dbmodels.Saplings;
import com.calibrage.palmroot.ui.RefreshSyncActivity;
import com.calibrage.palmroot.uihelper.ProgressBar;
import com.calibrage.palmroot.uihelper.ProgressDialogFragment;
import com.calibrage.palmroot.utils.UiUtils;

import org.apache.commons.io.FileUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.calibrage.palmroot.cloudhelper.HttpClient.getOkHttpClient;

public class DataSyncHelper {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String LOG_TAG = DataSyncHelper.class.getName();
    //public static String PREVIOUS_SYNC_DATE = "previous_sync_date";
    public static String PREVIOUS_SYNC_DATE = "null";
    public static LinkedHashMap<String, List> dataToUpdate = new LinkedHashMap<>();
    public static int countCheck, transactionsCheck = 0, imagesCount = 0, reverseSyncTransCount = 0, innerCountCheck = 0;
    public static List<String> refreshtableNamesList = new ArrayList<>();
    public static LinkedHashMap<String, List> refreshtransactionsDataMap = new LinkedHashMap<>();

    private static String IMEINUMBER;
    public static int resetCount;
    public static int FarmerDataCount = 0;
    public static int PlotDataCount = 0;
    public static int AdvanceTourPlan = 0;
    public static int FarmerResetCount;
    public static int PlotResetCount;
public static String str;
    private static String[] PERMISSIONS_REQUIRED = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };

    public static synchronized void performMasterSync(final Context context, final boolean firstTimeInsertFinished, final ApplicationThread.OnComplete onComplete) {
        IMEINUMBER = CommonUtils.getIMEInumber(context);
        LinkedHashMap<String, String> syncDataMap = new LinkedHashMap<>();
        syncDataMap.put("LastUpdatedDate", "");
        syncDataMap.put("IMEINumber", IMEINUMBER);
        countCheck = 0;
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        ProgressBar.showProgressBar(context, "Making data ready for you...");
        CloudDataHandler.getMasterData(Config.live_url + Config.masterSyncUrl, syncDataMap, new ApplicationThread.OnComplete<HashMap<String, List>>() {
            @Override
            public void execute(boolean success, final HashMap<String, List> masterData, String msg) {
                if (success) {
                    if (masterData != null && masterData.size() > 0) {
                        Log.v(LOG_TAG, "@@@ Master sync is success and data size is " + masterData.size());

                        final Set<String> tableNames = masterData.keySet();
                        masterData.remove("CcRate");
                        for (final String tableName : tableNames) {
                            Log.v(LOG_TAG, "@@@ Delete Query " + String.format(Queries.getInstance().deleteTableData(), tableName));
                            ApplicationThread.dbPost("Master Data Sync..", "master data", new Runnable() {
                                @Override
                                public void run() {
                                    countCheck++;
                                    if (!firstTimeInsertFinished) {
                                        dataAccessHandler.deleteRow(tableName, null, null, false, new ApplicationThread.OnComplete<String>() {
                                            @Override
                                            public void execute(boolean success, String result, String msg) {
                                                if (success) {
                                                    dataAccessHandler.insertData(true, tableName, masterData.get(tableName), new ApplicationThread.OnComplete<String>() {
                                                        @Override
                                                        public void execute(boolean success, String result, String msg) {
                                                            if (success) {
                                                                Log.v(LOG_TAG, "@@@ sync success for " + tableName);
                                                            } else {
                                                                Log.v(LOG_TAG, "@@@ check 1 " + masterData.size() + "...pos " + countCheck);
                                                              Log.v(LOG_TAG, "@@@ sync failed for " + tableName + " message " + msg);
                                                            }
                                                            if (countCheck == masterData.size()) {
                                                                Log.v(LOG_TAG, "@@@ Done with master sync " + countCheck);
                                                                ProgressBar.hideProgressBar();
                                                                onComplete.execute(true, null, "Sync is success");
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Log.v(LOG_TAG, "@@@ Master table deletion failed for " + tableName);
                                                }
                                            }
                                        });
                                    } else {
                                        dataAccessHandler.insertData(tableName, masterData.get(tableName), new ApplicationThread.OnComplete<String>() {
                                            @Override
                                            public void execute(boolean success, String result, String msg) {
                                                if (success) {
                                                    Log.v(LOG_TAG, "@@@ sync success for " + tableName);
                                                } else {
                                                    Log.v(LOG_TAG, "@@@ check 2 " + masterData.size() + "...pos " + countCheck);
                                                    Log.v(LOG_TAG, "@@@ sync failed for " + tableName + " message " + msg);
                                                }
                                                if (countCheck == masterData.size()) {
                                                    Log.v(LOG_TAG, "@@@ Done with master sync " + countCheck);
                                                    ProgressBar.hideProgressBar();
                                                    onComplete.execute(true, null, "Sync is success");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {
                        ProgressBar.hideProgressBar();
                        Log.v(LOG_TAG, "@@@ Sync is up-to-date");
                        onComplete.execute(true, null, "Sync is up-to-date");
                    }
                } else {
                    ProgressBar.hideProgressBar();
                    onComplete.execute(false, null, "Master sync failed. Please try again");
                }
            }
        });
    }

    public static synchronized void performRefreshTransactionsSync(final Context context, final ApplicationThread.OnComplete onComplete) {
        countCheck = 0;
        transactionsCheck = 0;
        reverseSyncTransCount = 0;
        imagesCount = 0;
        refreshtableNamesList.clear();
        refreshtransactionsDataMap.clear();
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        ProgressBar.showProgressBar(context, "Sending data to server...");
        ApplicationThread.bgndPost(LOG_TAG, "getting transactions data", new Runnable() {
            @Override
            public void run() {
                getRefreshSyncTransDataMap(context, new ApplicationThread.OnComplete<LinkedHashMap<String, List>>() {
                    @Override
                    public void execute(boolean success, final LinkedHashMap<String, List> transDataMap, String msg) {
                        if (success) {
                            if (transDataMap != null && transDataMap.size() > 0) {
                                Log.v(LOG_TAG, "transactions data size " + transDataMap.size());
                                Set<String> transDataTableNames = transDataMap.keySet();
                                refreshtableNamesList.addAll(transDataTableNames);
                                refreshtransactionsDataMap = transDataMap;
                                //sendTrackingData(context, onComplete);
                                postTransactionsDataToCloud(context, refreshtableNamesList.get(transactionsCheck), dataAccessHandler, onComplete);
                            }
                        } else {
                            ProgressBar.hideProgressBar();
                            Log.v(LOG_TAG, "@@@ Transactions sync failed due to data retrieval error");
                            onComplete.execute(false, null, "Transactions sync failed due to data retrieval error");
                        }
                    }
                });
            }
        });

    }

    public static void postTransactionsDataToCloud(final Context context, final String tableName, final DataAccessHandler dataAccessHandler, final ApplicationThread.OnComplete onComplete) {

        List cctransDataList = refreshtransactionsDataMap.get(tableName);

        if (null != cctransDataList && cctransDataList.size() > 0) {
            Type listType = new TypeToken<List>() {
            }.getType();
            Gson gson = new GsonBuilder().serializeNulls().create();

            String dat = gson.toJson(cctransDataList, listType);
            JSONObject transObj = new JSONObject();
            try {
                transObj.put(tableName, new JSONArray(dat));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v(LOG_TAG, "@@@@ check.." + transObj.toString());
            CommonConstants.SyncTableName = tableName;
            CloudDataHandler.placeDataInCloud(context, transObj, Config.live_url + Config.transactionSyncURL, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {if (success) {
                        dataAccessHandler.executeRawQuery(String.format(Queries.getInstance().updateServerUpdatedStatus(), tableName));
                        Log.v(LOG_TAG, "@@@ Transactions sync success for " + tableName);transactionsCheck++;
                        if (transactionsCheck == refreshtransactionsDataMap.size()) {
                            Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);
                            onComplete.execute(true, null, "Sync is success");

                        } else {
                            postTransactionsDataToCloud(context, refreshtableNamesList.get(transactionsCheck), dataAccessHandler, onComplete);
                        }
                    } else {
                        ApplicationThread.uiPost(LOG_TAG, "Sync is failed", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Sync failed for " + tableName, context, 1);
                            }
                        });
                        transactionsCheck++;
                        if (transactionsCheck == refreshtransactionsDataMap.size()) {
//                            Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);
//                            final List<ImageDetails> imagesData = dataAccessHandler.getImageDetails();
//                            if (null != imagesData && !imagesData.isEmpty()) {
//                                sendImageDetails(context, imagesData, dataAccessHandler, onComplete);
//                            } else {
                            ProgressBar.hideProgressBar();
                            onComplete.execute(true, null, "Sync is success");
                            //}
                        } else {
                            postTransactionsDataToCloud(context, refreshtableNamesList.get(transactionsCheck), dataAccessHandler, onComplete);
                        }
                        Log.v(LOG_TAG, "@@@ Transactions sync failed for " + tableName);
                        Log.v(LOG_TAG, "@@@ Transactions sync due to " + result);

                    }
                }
            });
        } else {
            transactionsCheck++;
            if (transactionsCheck == refreshtransactionsDataMap.size()) {
//                Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);
//                final List<ImageDetails> imagesData = dataAccessHandler.getImageDetails();
//                if (null != imagesData && !imagesData.isEmpty()) {
//                    sendImageDetails(context, imagesData, dataAccessHandler, onComplete);
//                } else {
                ProgressBar.hideProgressBar();
                onComplete.execute(true, null, "Sync is success");
                Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);

//                }
            } else {
                postTransactionsDataToCloud(context, refreshtableNamesList.get(transactionsCheck), dataAccessHandler, onComplete);
            }
        }
    }



    private static void getRefreshSyncTransDataMap(final Context context, final ApplicationThread.OnComplete onComplete) {

        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

        List<SaplingActivityStatusModel> saplingActivityStatuslist = (List<SaplingActivityStatusModel>) dataAccessHandler.getSaplingActivityStatusDetails(Queries.getInstance().getSaplingActivityStatusRefresh(), 1);
        List<Saplings> saplingsList = (List<Saplings>) dataAccessHandler.getSaplingDetails(Queries.getInstance().getSaplingRefresh(), 1);
        List<SaplingActivity> saplingacityList = (List<SaplingActivity>) dataAccessHandler.getSaplingActivityDetails(Queries.getInstance().getSaplingActivityRefresh(), 1);
        List<SaplingActivityXrefModel> saplingActivityXreflist = (List<SaplingActivityXrefModel>) dataAccessHandler.getSaplingActivityXrefDetails(Queries.getInstance().getSaplingActivityXrefRefresh(), 1);
        List<SaplingActivityHistoryModel> saplingActivityHistorylist = (List<SaplingActivityHistoryModel>) dataAccessHandler.getSaplingActivityHistoryDetails(Queries.getInstance().getSaplingActivityHistoryRefresh(), 1);
        List<NurseryIrrigationLogForDb> nurseryIrrigationLog = (List<NurseryIrrigationLogForDb>) dataAccessHandler.getIrrigationDetails(Queries.getInstance().getNurceryIrrigationHistoryRefresh(), 1);
        List<NurseryIrrigationLogXref> nurseryIrrigationLogXref = (List<NurseryIrrigationLogXref>) dataAccessHandler.getIrrigationDetailsXref(Queries.getInstance().getNurceryIrrigationXrefHistoryRefresh(), 1);
        List<Irrigationhistorymodel> nurseryIrrigationHistory = (List<Irrigationhistorymodel>) dataAccessHandler.getIrrigationHistoryDetails(Queries.getInstance().getNurceryIrrigation_HistoryRefresh(), 1);
        List<CullinglossFileRepository> cullinglossrepoList = (List<CullinglossFileRepository>) dataAccessHandler.getCullinglossRepoDetails(Queries.getInstance().getFileRepositoryRefresh());
        List<NurseryLabourLog>nurseryLabourLogslist  = (List<NurseryLabourLog>) dataAccessHandler.getnurserylabourlogs(Queries.getInstance().getNurserylabourlogs());
        List<NurseryVisitLog>nurseryvisitLogslist  = (List<NurseryVisitLog>) dataAccessHandler.getNurseryVisitLog(Queries.getInstance().getNurseryvisitlogs());
        List<RMTransactions> rmTransactionsList = (List<RMTransactions>) dataAccessHandler.getRMTransactionsData(Queries.getInstance().getRMTransactionRecrods());
        List<RMTransactionsStatusHistory> rmTransactionshistoryList = (List<RMTransactionsStatusHistory>) dataAccessHandler.getRMTransactionsStatusHistoryData(Queries.getInstance().getRMTransactionhistoryRecrods());
        LinkedHashMap<String, List> allRefreshDataMap = new LinkedHashMap<>();
        allRefreshDataMap.put(DatabaseKeys.TABLE_SaplingActivityStatus, saplingActivityStatuslist);
        allRefreshDataMap.put(DatabaseKeys.TABLE_SAPLING, saplingsList);
        allRefreshDataMap.put(DatabaseKeys.TABLE_SaplingActivity, saplingacityList);
        allRefreshDataMap.put(DatabaseKeys.TABLE_SaplingActivityXref, saplingActivityXreflist);
        allRefreshDataMap.put(DatabaseKeys.TABLE_SaplingActivityHistory, saplingActivityHistorylist);
        allRefreshDataMap.put(DatabaseKeys.TABLE_NurseryIrrigationLog, nurseryIrrigationLog);
        allRefreshDataMap.put(DatabaseKeys.TABLE_NurseryIrrigationLogXREF, nurseryIrrigationLogXref);
        allRefreshDataMap.put(DatabaseKeys.TABLE_NurseryIrrigationhistory, nurseryIrrigationHistory);
        allRefreshDataMap.put(DatabaseKeys.TABLE_FILEREPOSITORY, cullinglossrepoList);
        allRefreshDataMap.put(DatabaseKeys.TABLE_NurseryLabourLog, nurseryLabourLogslist);
        allRefreshDataMap.put(DatabaseKeys.TABLE_NURSERYVISITLOGS, nurseryvisitLogslist);
        allRefreshDataMap.put(DatabaseKeys.TABLE_RMTransactions, rmTransactionsList);
        allRefreshDataMap.put(DatabaseKeys.TABLE_RMTransactionStatusHistory, rmTransactionshistoryList);



//        allRefreshDataMap.put(DatabaseKeys.TABLE_Location_TRACKING_DETAILS, gpsTrackingList);


        onComplete.execute(true, allRefreshDataMap, "here is collection of table transactions data");

    }

    public static void startTransactionSync(final Context context, final ProgressDialogFragment progressDialogFragment) {
//        updateSyncDate(context,"2021-08-05 15:21:00");// TODO MAHESH ADDED for Static testing
        SharedPreferences sharedPreferences = context.getSharedPreferences("appprefs", MODE_PRIVATE);
        String date = sharedPreferences.getString(PREVIOUS_SYNC_DATE, null);

        final String finalDate = date ;//"2021-10-16 10:21:40";  // date
        Log.v(LOG_TAG, "@@@ Date " + date);
        progressDialogFragment.updateText("Getting total records count");
        final ProgressDialogFragment finalProgressDialogFragment = progressDialogFragment;
        getCountOfHits(finalDate, new ApplicationThread.OnComplete() {
            @Override
            public void execute(boolean success, Object result, String msg) {
                if (success) {
                    Log.v(LOG_TAG, "@@@@ count here " + result.toString());
                    List<DataCountModel> dataCountModelList = (List<DataCountModel>) result;
                    prepareIndexes(finalDate, dataCountModelList, context, finalProgressDialogFragment);
                } else {
                    if (null != finalProgressDialogFragment) {
                        finalProgressDialogFragment.dismiss();
                    }
                    Log.v(LOG_TAG, "Transaction sync failed due to data issue-->" + msg);
                    UiUtils.showCustomToastMessage("Transaction sync failed due to data issue", context, 1);
                }
            }
        });
    }


    public static void prepareIndexes(final String date, List<DataCountModel> countData, final Context context, ProgressDialogFragment progressDialogFragment) {
        if (!countData.isEmpty()) {
            reverseSyncTransCount = 0;
            transactionsCheck = 0;
            dataToUpdate.clear();
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            new DownLoadData(context, date, countData, 0, 0, dataAccessHandler, progressDialogFragment).execute();
        } else {
            ProgressBar.hideProgressBar();
            if (null != progressDialogFragment) {
                progressDialogFragment.dismiss();
            }

            CommonUtils.showMyToast("There is no transactions data to sync", context);
        }
    }

    public static void getCountOfHits(String date, final ApplicationThread.OnComplete onComplete) {
        String countUrl = "";
        LinkedHashMap<String, String> syncDataMap = new LinkedHashMap<>();
        syncDataMap.put("Date", TextUtils.isEmpty(date) ? "null" : date);
        syncDataMap.put("UserId", CommonConstants.USER_ID);
        syncDataMap.put("IsUserDataAccess", CommonConstants.migrationSync);
        countUrl = Config.live_url + Config.getTransCount;
        CloudDataHandler.getGenericData(countUrl, syncDataMap, new ApplicationThread.OnComplete<List<DataCountModel>>() {
            @Override
            public void execute(boolean success, List<DataCountModel> result, String msg) {
                onComplete.execute(success, result, msg);
            }
        });
    }

    public static void updateSyncDate(Context context, String date) {
        Log.v(LOG_TAG, "@@@ saving date into");
        SharedPreferences sharedPreferences = context.getSharedPreferences("appprefs", MODE_PRIVATE);
        sharedPreferences.edit().putString(PREVIOUS_SYNC_DATE, date).apply();
    }

    public static void ableToProceedToTransactionSync(final String password, final ApplicationThread.OnComplete onComplete) {
        CloudDataHandler.getGenericData(Config.live_url + String.format(Config.validateTranSync, password), new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                onComplete.execute(success, result, msg);
            }
        });
    }

    private static void updateOrInsertData(final String tableName, List dataToInsert, String whereCondition, boolean recordExisted, DataAccessHandler dataAccessHandler, final ApplicationThread.OnComplete onComplete) {
        if (recordExisted) {
            dataAccessHandler.updateData(tableName, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    onComplete.execute(success, null, "Sync is " + success + " for " + tableName);
                }
            });
        } else {
            dataAccessHandler.insertData(tableName, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    onComplete.execute(true, null, "Sync is " + success + " for " + tableName);
                }
            });
        }
    }

    private static synchronized void updateDataIntoDataBase(final LinkedHashMap<String, List> transactionsData, final DataAccessHandler dataAccessHandler, final String tableName, final ApplicationThread.OnComplete onComplete) {
        final List dataList = transactionsData.get(tableName);
        List dataToInsert = new ArrayList();
        JSONObject ccData = null;
        Gson gson = new GsonBuilder().serializeNulls().create();

        boolean recordExisted = false;
        String whereCondition = null;

        if (dataList.size() > 0) {
            if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SAPLING)) {
                Saplings saplingslist = (Saplings) dataList.get(innerCountCheck);
                saplingslist.setServerUpdatedStatus(1);
                whereCondition = " where  NurseryCode = '" + saplingslist.getNurseryCode() + "'  AND ConsignmentCode =  '"+ saplingslist.getConsignmentCode() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(saplingslist));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "ConsignmentCode", saplingslist.getConsignmentCode()));
            } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SaplingActivity)) {
                SaplingActivity saplingActivity = (SaplingActivity) dataList.get(innerCountCheck);
                saplingActivity.setServerUpdatedStatus(1);
                whereCondition = " where  TransactionId = '" + saplingActivity.getTransactionId() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(saplingActivity));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                    recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "TransactionId", saplingActivity.getTransactionId()));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
            }
            else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SaplingActivityXref)) {
        //    } else if (tableName.equalsIgnoreCase("SaplingActivityXref")) {
                SaplingActivityXrefModelget saplingActivityXredata = null;
                try {
                    saplingActivityXredata = (SaplingActivityXrefModelget) dataList.get(innerCountCheck);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                    e.printStackTrace();
                }
                saplingActivityXredata.setServerUpdatedStatus(1);
                whereCondition = " where  TransactionId= '" + saplingActivityXredata.getTransactionId() + "'   AND FieldId = "+saplingActivityXredata.getFieldId();
                try {
                    ccData = new JSONObject(gson.toJson(saplingActivityXredata));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable2(tableName, "FieldId",  saplingActivityXredata.getFieldId()+"", "TransactionId", saplingActivityXredata.getTransactionId() + ""));
            } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SaplingActivityHistory)) {
                SaplingActivityHistoryModel saplingActivityHistorydata = (SaplingActivityHistoryModel) dataList.get(innerCountCheck);
                saplingActivityHistorydata.setServerUpdatedStatus(1);
                whereCondition = " where  TransactionId= '" + saplingActivityHistorydata.getTransactionId() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(saplingActivityHistorydata));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "TransactionId", saplingActivityHistorydata.getTransactionId()));
            } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SaplingActivityStatus)) {
                SaplingActivityStatusModel saplingActivityStatusModel = (SaplingActivityStatusModel) dataList.get(innerCountCheck);
                saplingActivityStatusModel.setServerUpdatedStatus(1);
                whereCondition = " where  ConsignmentCode= '" + saplingActivityStatusModel.getConsignmentCode() + "'  AND ActivityId = '" + saplingActivityStatusModel.getActivityId() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(saplingActivityStatusModel));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
                Log.d(DataSyncHelper.LOG_TAG, "===> analysis ==> CHECK SAPLINGACTIVITYSTATUS TABLE EXIST :" + Queries.getInstance().checkRecordStatusInTable2(tableName, "ConsignmentCode", saplingActivityStatusModel.getConsignmentCode(), "ActivityId", saplingActivityStatusModel.getActivityId() + ""));
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable2(tableName, "ConsignmentCode", saplingActivityStatusModel.getConsignmentCode(), "ActivityId", saplingActivityStatusModel.getActivityId() + ""));
            }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SaplingActivityXref)) {
                SaplingActivityXrefModelget saplingActivityXrefModel = (SaplingActivityXrefModelget) dataList.get(innerCountCheck);
                saplingActivityXrefModel.setServerUpdatedStatus(1);
                whereCondition = " where  TransactionId = '" + saplingActivityXrefModel.getTransactionId() + "'  AND FieldId = '" + saplingActivityXrefModel.getFieldId() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(saplingActivityXrefModel));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
//                Log.d(DataSyncHelper.LOG_TAG, "===> analysis ==> CHECK SAPLINGACTIVITYSTATUS TABLE EXIST :" + Queries.getInstance().checkRecordStatusInTable2(tableName, "ConsignmentCode", saplingActivityStatusModel.getConsignmentCode(), "ActivityId", saplingActivityStatusModel.getActivityId() + ""));
              //  recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "FieldId", saplingActivityXrefModel.getFieldId()+""));

                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable2(tableName, "FieldId",  saplingActivityXrefModel.getFieldId()+"", "TransactionId", saplingActivityXrefModel.getTransactionId() + ""));
            }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_NurseryIrrigationLog)) {
                NurseryIrrigationLogForDb nurseryIrrigationLog = (NurseryIrrigationLogForDb) dataList.get(innerCountCheck);
                nurseryIrrigationLog.setServerUpdatedStatus(1);
                whereCondition = " where  IrrigationCode = '" + nurseryIrrigationLog.getIrrigationCode()+"'" ;
                try {
                    ccData = new JSONObject(gson.toJson(nurseryIrrigationLog));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
//                Log.d(DataSyncHelper.LOG_TAG, "===> analysis ==> CHECK SAPLINGACTIVITYSTATUS TABLE EXIST :" + Queries.getInstance().checkRecordStatusInTable2(tableName, "ConsignmentCode", saplingActivityStatusModel.getConsignmentCode(), "ActivityId", saplingActivityStatusModel.getActivityId() + ""));
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "IrrigationCode", nurseryIrrigationLog.getIrrigationCode()));
            }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_NurseryIrrigationLogXREF)) {
                NurseryIrrigationLogXref nurseryIrrigationLogXref = (NurseryIrrigationLogXref) dataList.get(innerCountCheck);
                nurseryIrrigationLogXref.setServerUpdatedStatus(1);
                whereCondition = " where  IrrigationCode = '" + nurseryIrrigationLogXref.getIrrigationCode() + "'  AND ConsignmentCode = '" + nurseryIrrigationLogXref.getConsignmentCode() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(nurseryIrrigationLogXref));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
//                Log.d(DataSyncHelper.LOG_TAG, "===> analysis ==> CHECK SAPLINGACTIVITYSTATUS TABLE EXIST :" + Queries.getInstance().checkRecordStatusInTable2(tableName, "ConsignmentCode", saplingActivityStatusModel.getConsignmentCode(), "ActivityId", saplingActivityStatusModel.getActivityId() + ""));
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable2(tableName, "ConsignmentCode", nurseryIrrigationLogXref.getConsignmentCode(), "IrrigationCode", nurseryIrrigationLogXref.getIrrigationCode() + ""));
            }

            else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_ALERTS)) {

               // List<Alerts> alertsList;
                Alerts alertsList = (Alerts) dataList.get(innerCountCheck);
                alertsList.setServerUpdatedStatus(1);
                whereCondition = " where  Id= '" + alertsList.getId() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(alertsList));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "Id", alertsList.getId()+""));
            }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_RMTransactions)) {
                RMTransactions rmTransactions = (RMTransactions) dataList.get(innerCountCheck);
                rmTransactions.setServerUpdatedStatus(1);
                whereCondition = " where  TransactionId = '" + rmTransactions.getTransactionId() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(rmTransactions));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "TransactionId", rmTransactions.getTransactionId()));

            }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_RMTransactionStatusHistory)) {
                RMTransactionsStatusHistory rmTransactionsStatusHistory = (RMTransactionsStatusHistory) dataList.get(innerCountCheck);
                rmTransactionsStatusHistory.setServerUpdatedStatus(1);
                whereCondition = " where  TransactionId = '" + rmTransactionsStatusHistory.getTransactionId() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(rmTransactionsStatusHistory));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "TransactionId", rmTransactionsStatusHistory.getTransactionId()));

            }
                if (dataList.size() != innerCountCheck) {
                updateOrInsertData(tableName, dataToInsert, whereCondition, recordExisted, dataAccessHandler, new ApplicationThread.OnComplete() {
                    @Override
                    public void execute(boolean success, Object result, String msg) {
                        innerCountCheck++;
                        if (innerCountCheck == dataList.size()) {
                            innerCountCheck = 0;
                            onComplete.execute(true, "", "");

                        } else {
                            updateDataIntoDataBase(transactionsData, dataAccessHandler, tableName, onComplete);
                        }
                    }
                });
            } else {
                onComplete.execute(true, "", "");
            }
        } else {
            innerCountCheck++;
            if (innerCountCheck == dataList.size()) {
                innerCountCheck = 0;
                onComplete.execute(true, "", "");
            } else {
                updateDataIntoDataBase(transactionsData, dataAccessHandler, tableName, onComplete);
            }
        }

    }

    public static synchronized void updateTransactionData(final LinkedHashMap<String, List> transactionsData, final DataAccessHandler dataAccessHandler, final List<String> tableNames, final ProgressDialogFragment progressDialogFragment, final ApplicationThread.OnComplete onComplete) {
        progressDialogFragment.updateText("Updating data...");

        if (transactionsData != null && transactionsData.size() > 0) {
            Log.v(LOG_TAG, "@@@ Transactions sync is success and data size is " + transactionsData.size());
            final String tableName = tableNames.get(reverseSyncTransCount);
            innerCountCheck = 0;
            updateDataIntoDataBase(transactionsData, dataAccessHandler, tableName, new ApplicationThread.OnComplete() {
                @Override
                public void execute(boolean success, Object result, String msg) {
                    if (success) {
                        //Todo check
                        reverseSyncTransCount++;
                        if (reverseSyncTransCount == transactionsData.size()) {
                            onComplete.execute(success, "data updated successfully", "");
                        } else {
                            updateTransactionData(transactionsData, dataAccessHandler, tableNames, progressDialogFragment, onComplete);
                        }
                    } else {
                        reverseSyncTransCount++;
                        if (reverseSyncTransCount == transactionsData.size()) {
                            onComplete.execute(success, "data updated successfully", "");
                        } else {
                            updateTransactionData(transactionsData, dataAccessHandler, tableNames, progressDialogFragment, onComplete);
                        }
                    }
                }
            });
        } else {
            onComplete.execute(false, "data not found to save", "");
        }
    }

    public static void sendTrackingData(final Context context, final ApplicationThread.OnComplete onComplete) {
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        List<LocationTracker> gpsTrackingList = (List<LocationTracker>) dataAccessHandler.getGpsTrackingData(Queries.getInstance().getGpsTrackingRefresh(), 1);
        if (null != gpsTrackingList && !gpsTrackingList.isEmpty()) {
            Type listType = new TypeToken<List>() {
            }.getType();
            Gson gson = null;
            gson = new GsonBuilder().serializeNulls().create();
            String dat = gson.toJson(gpsTrackingList, listType);
            JSONObject transObj = new JSONObject();
            try {
                transObj.put(DatabaseKeys.TABLE_Location_TRACKING_DETAILS, new JSONArray(dat));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v(LOG_TAG, "@@@@ check.." + transObj.toString());
            CloudDataHandler.placeDataInCloud(context, transObj, Config.live_url + Config.locationTrackingURL, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                dataAccessHandler.executeRawQuery(String.format(Queries.getInstance().updateServerUpdatedStatus(), DatabaseKeys.TABLE_Location_TRACKING_DETAILS));
                                Log.v(LOG_TAG, "@@@ Transactions sync success for " + DatabaseKeys.TABLE_Location_TRACKING_DETAILS);
                                onComplete.execute(true, null, "Sync is success");
                            } else {
                                onComplete.execute(false, null, "Sync is failed");
                            }
                        }
                    }
            );

        }
    }

    public static void getAlertsData(final Context context, final ApplicationThread.OnComplete<String> onComplete) {
        CloudDataHandler.getGenericData(Config.live_url + Config.GET_ALERTS, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
                    dataAccessHandler.executeRawQuery("delete from Alerts");
                    LinkedHashMap<String, List> dataMap = new LinkedHashMap<>();
                    JSONArray resultArray = null;
                    try {
                        resultArray = new JSONArray(result);
//                        dataMap.put("Alerts", CommonUtils.toList(resultArray));
                        List dataList = new ArrayList();
                        dataList.add(CommonUtils.toList(resultArray));
                        dataAccessHandler.insertData(DatabaseKeys.TABLE_ALERTS, CommonUtils.toList(resultArray), new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                if (success) {
                                    onComplete.execute(true, "", "");
                                } else {
                                    onComplete.execute(false, "", "");
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onComplete.execute(false, "", "");
                    }
                } else {
                    onComplete.execute(false, "", "");
                }
            }
        });
    }



    public static class DownLoadData extends AsyncTask<String, String, String> {

        private static final MediaType TEXT_PLAIN = MediaType.parse("application/x-www-form-urlencoded");
        private Context context;
        private String date;
        private List<DataCountModel> totalData;
        private int totalDataCount;
        private int currentIndex;
        private DataAccessHandler dataAccessHandler;
        private ProgressDialogFragment progressDialogFragment;


        public DownLoadData(final Context context, final String date, final List<DataCountModel> totalData, int totalDataCount, int currentIndex, DataAccessHandler dataAccessHandler, ProgressDialogFragment progressDialogFragment) {
            this.context = context;
            this.totalData = totalData;
            this.date = date;
            this.totalDataCount = totalDataCount;
            this.currentIndex = currentIndex;
            this.dataAccessHandler = dataAccessHandler;
            this.progressDialogFragment = progressDialogFragment;
        }

        @Override
        protected String doInBackground(String... params) {
            String resultMessage = null;
            Response response = null;
            String countUrl = Config.live_url + String.format(Config.getTransData, totalData.get(totalDataCount).getMethodName());
            Log.v(LOG_TAG, "@@@ data sync url " + countUrl);
            final String tableName = totalData.get(totalDataCount).getTableName();

            progressDialogFragment.updateText("Downloading " + tableName + " (" + currentIndex + "/" + totalData.get(totalDataCount).getCount() + ")" + " data");
            if (currentIndex == 0) {
                if (tableName.equalsIgnoreCase("Farmer")) {
                    FarmerDataCount = totalData.get(totalDataCount).getCount();
                } else if (tableName.equalsIgnoreCase("Plot")) {
                    PlotDataCount = totalData.get(totalDataCount).getCount();
                }
            }
            try {
                URL obj = new URL(countUrl);
                Map<String, String> syncDataMap = new LinkedHashMap<>();
                syncDataMap.put("Date", TextUtils.isEmpty(date) ? "null" : date);
                syncDataMap.put("UserId", CommonConstants.USER_ID);
                syncDataMap.put("IsUserDataAccess", CommonConstants.migrationSync);
                syncDataMap.put("Index", String.valueOf(currentIndex));
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("User-Agent", USER_AGENT);

                final StringBuilder sb = new StringBuilder();
                boolean first = true;
                RequestBody requestBody = null;
                for (Map.Entry<String, String> entry : syncDataMap.entrySet()) {
                    if (first) first = false;
                    else sb.append("&");

                    sb.append(URLEncoder.encode(entry.getKey(), HTTP.UTF_8)).append("=")
                            .append(URLEncoder.encode(entry.getValue().toString(), HTTP.UTF_8));

                    Log.d(LOG_TAG, "\nposting key: " + entry.getKey() + " -- value: " + entry.getValue());
                }
                requestBody = RequestBody.create(TEXT_PLAIN, sb.toString());

                Request request = HttpClient.buildRequest(countUrl, "POST", (requestBody != null) ? requestBody : RequestBody.create(TEXT_PLAIN, "")).build();
                OkHttpClient client = getOkHttpClient();
                response = client.newCall(request).execute();
                int statusCode = response.code();

                final String strResponse = response.body().string();


                Log.d(LOG_TAG, " ############# POST RESPONSE ################ (" + statusCode + ")\n\n" + strResponse + "\n\n");
                JSONArray dataArray = new JSONArray(strResponse);

                if (statusCode == HttpURLConnection.HTTP_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_REQUIRED, 0);
                        }

                    }

                    if (TextUtils.isEmpty(date)) {

                        List dataToInsert = new ArrayList();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject eachDataObject = dataArray.getJSONObject(i);
                            dataToInsert.add(CommonUtils.toMap(eachDataObject));
                        }
                        dataAccessHandler.insertData(tableName, dataToInsert, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                if (success) {
                                    Log.v(LOG_TAG, "@@@@ Data insertion status " + result);
                                    Log.v(LOG_TAG, "@@@@ Data 817 tableName  " + tableName);


                                    if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_RMTransactions)) {// TODO need to check

                                        File dir = new File(Environment.getExternalStorageDirectory()
                                                .getAbsolutePath() + File.separator + "3F_Pictures/" + "RM_Transactions");
                                        try {
                                            FileUtils.deleteDirectory(dir);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<RMTransactions>>() {
                                        }.getType();
                                        List<RMTransactions> rmTransactions = gson.fromJson(dataArray.toString(), type);
                                        Log.v(LOG_TAG, "@@@@ Data  823  " + rmTransactions.size());
                                        ArrayList<String> img = new ArrayList<>();
                                        for(int i =0 ; i<rmTransactions.size() ;i++){
                                            //str = Config.image_url_1+"//"+str;
                                            str = Config.image_url+"/"+rmTransactions.get(i).getFileLocation()+"/"+rmTransactions.get(i).getFileName()+rmTransactions.get(i).getFileExtension();
                                            str.replace('\\', '/');
                                            Log.e("=======>image save", str);
                                            img.add(str);
                                            downloadFile(str,rmTransactions.get(i).getTransactionId());
                                            //       new DownloadImage().execute("http://developer.android.com/images/activity_lifecycle.png");
                                            Log.e("image",str+"");
                                        }}
//
//                                        if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_FARMER)) {
//                                        if (currentIndex == 0) {
//                                            FarmerResetCount = 1;
//                                        } else {
//                                            FarmerResetCount = FarmerResetCount + 1;
//                                        }
//                                    } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_PLOT)) {
//                                        if (currentIndex == 0) {
//                                            PlotResetCount = 1;
//                                        } else {
//                                            PlotResetCount = PlotResetCount + 1;
//                                        }
//                                    }
                                } else {
                                    Log.v(LOG_TAG, "@@@@ Data insertion Failed In Table-" + tableName + "Due to" + result);
                                }

                            }
                        });
                    }
                    else {
                        if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SAPLING)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Saplings>>() {
                            }.getType();
                            List<Saplings> saplingsDataList = gson.fromJson(dataArray.toString(), type);
                            if (null != saplingsDataList && saplingsDataList.size() > 0)
                                dataToUpdate.put(tableName, saplingsDataList);
                        } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SaplingActivity)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SaplingActivity>>() {
                            }.getType();
                            List<SaplingActivity> saplingActivityDataList = gson.fromJson(dataArray.toString(), type);
                            if (null != saplingActivityDataList && saplingActivityDataList.size() > 0)
                                dataToUpdate.put(tableName, saplingActivityDataList);
                        } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SaplingActivityXref)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SaplingActivityXrefModelget>>() {
                            }.getType();
                            List<SaplingActivityXrefModelget> saplingActivityXrefList = gson.fromJson(dataArray.toString(), type);
                            if (null != saplingActivityXrefList && saplingActivityXrefList.size() > 0)
                                dataToUpdate.put(tableName, saplingActivityXrefList);
                        } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SaplingActivityHistory)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SaplingActivityHistoryModel>>() {
                            }.getType();
                            List<SaplingActivityHistoryModel> SaplingActivityHistoryDataList = gson.fromJson(dataArray.toString(), type);
                            if (null != SaplingActivityHistoryDataList && SaplingActivityHistoryDataList.size() > 0)
                                dataToUpdate.put(tableName, SaplingActivityHistoryDataList);
                        } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_SaplingActivityStatus)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SaplingActivityStatusModel>>() {
                            }.getType();
                            List<SaplingActivityStatusModel> saplingActivityStatusModel = gson.fromJson(dataArray.toString(), type);
                            if (null != saplingActivityStatusModel && saplingActivityStatusModel.size() > 0)
                                dataToUpdate.put(tableName, saplingActivityStatusModel);
                        } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_NurseryIrrigationLog)) {  // TODO need to check
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<NurseryIrrigationLogForDb>>() {
                            }.getType();
                            List<NurseryIrrigationLogForDb> irrigationLogs = gson.fromJson(dataArray.toString(), type);
                            if (null != irrigationLogs && irrigationLogs.size() > 0)
                                dataToUpdate.put(tableName, irrigationLogs);
                        }
                        else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_ALERTS)) {  // TODO need to check
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Alerts>>() {
                            }.getType();
                            List<Alerts> alters = gson.fromJson(dataArray.toString(), type);
                            if (null != alters && alters.size() > 0)
                                dataToUpdate.put(tableName, alters);
                        }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_RMTransactions)) {  // TODO need to check
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<RMTransactions>>() {
                            }.getType();
                            List<RMTransactions> rmTransactions = gson.fromJson(dataArray.toString(), type);
                            ArrayList<String> img = new ArrayList<>();
                            for(int i =0 ; i<rmTransactions.size() ;i++){
                                //str = Config.image_url_1+"//"+str;
                                str = Config.image_url+"/"+rmTransactions.get(i).getFileLocation()+"/"+rmTransactions.get(i).getFileName()+rmTransactions.get(i).getFileExtension();
                                str.replace('\\', '/');
                                img.add(str);
                                downloadFile(str,rmTransactions.get(i).getTransactionId());
                         //       new DownloadImage().execute("http://developer.android.com/images/activity_lifecycle.png");
                                Log.e("image",str+"");
                            }


                            if (null != rmTransactions && rmTransactions.size() > 0)
                                dataToUpdate.put(tableName, rmTransactions);
                        }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_RMTransactionStatusHistory)) {  // TODO need to check
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<RMTransactionsStatusHistory>>() {
                            }.getType();
                            List<RMTransactionsStatusHistory> rmTransactionsStatusHistories = gson.fromJson(dataArray.toString(), type);
                            if (null != rmTransactionsStatusHistories && rmTransactionsStatusHistories.size() > 0)
                                dataToUpdate.put(tableName, rmTransactionsStatusHistories);
                        }
                    }
                    resultMessage = "success";
                } else {
                    resultMessage = "failed";
                }
            } catch (Exception e) {
                resultMessage = e.getMessage();
                Log.e(LOG_TAG, "@@@ data sync failed for " + tableName);
            }
            return resultMessage;
        }

        public void downloadFile(String uRl, String TransID) {

            File direct = new File(
                    Environment.getExternalStorageDirectory() + "/3F_Pictures/" + "RM_Transactions");
            // have the object build the directory structure, if needed.

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Demo")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/3F_Pictures/" + "RM_Transactions/", TransID+".jpg");

            mgr.enqueue(request);

        }



        @Override
        protected void onPostExecute(String result) {
            currentIndex++;
            if (currentIndex == totalData.get(totalDataCount).getCount()) {
                currentIndex = 0;
                totalDataCount++;
                if (totalDataCount == totalData.size()) {
                    Log.v(LOG_TAG, "@@@ done with data syncing");

                    UiUtils.showCustomToastMessage("Data synced successfully", context, 0);
                    if (TextUtils.isEmpty(date)) {
                        ProgressBar.hideProgressBar();
                        if (null != progressDialogFragment && !CommonUtils.currentActivity.isFinishing()) {
                            if (null != progressDialogFragment && !CommonUtils.currentActivity.isFinishing()) {
                                progressDialogFragment.dismiss();
                            }

                        }
                        if (CommonUtils.isNetworkAvailable(context)) {
                            updateSyncDate(context, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            RefreshSyncActivity.allRefreshDataMap = new ArrayList<String>();
                            for (String s : RefreshSyncActivity.allRefreshDataMap) {
                                dataAccessHandler.executeRawQuery("DELETE FROM " + s);
                                Log.v(LOG_TAG, "delete table" + s);
                            }
                            //  progressDialogFragment = new ProgressDialogFragment();
                            // startTransactionSync(context, progressDialogFragment);
                        } else {
                            UiUtils.showCustomToastMessage("Please check network connection", context, 1);
                        }

                    } else {
                        reverseSyncTransCount = 0;
                        Set tableNames = dataToUpdate.keySet();
                        List<String> tableNamesList = new ArrayList();
                        tableNamesList.addAll(tableNames);
                        updateSyncDate(context, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        UiUtils.showCustomToastMessage("Data synced successfully", context, 0);
                        if (null != progressDialogFragment && !CommonUtils.currentActivity.isFinishing()) {
                            progressDialogFragment.dismiss();
                        }
                        //TODO checj sync pending
                        updateTransactionData(dataToUpdate, dataAccessHandler, tableNamesList, progressDialogFragment, new ApplicationThread.OnComplete() {
                            @Override
                            public void execute(boolean success, Object result, String msg) {
                                if (success) {
                                    updateSyncDate(context, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                    UiUtils.showCustomToastMessage("Data synced successfully", context, 0);
                                } else {
                                    UiUtils.showCustomToastMessage(msg, context, 1);
                                }
                                if (null != progressDialogFragment && !CommonUtils.currentActivity.isFinishing()) {
                                    progressDialogFragment.dismiss();
                                }
                            }
                        });
                    }
                } else {
                    Log.v(LOG_TAG, "@@@ data downloading next count " + currentIndex + " out of " + totalData.size());
                    new DownLoadData(context, date, totalData, totalDataCount, currentIndex, dataAccessHandler, progressDialogFragment).execute();
                }
            } else {
                Log.v(LOG_TAG, "@@@ data downloading next count " + currentIndex + " out of " + totalData.size());
                new DownLoadData(context, date, totalData, totalDataCount, currentIndex, dataAccessHandler, progressDialogFragment).execute();
            }
        }
//        public void downloadFile(String uRl) {
//            File direct = new File(Environment.getExternalStorageDirectory()
//                    + "/AnhsirkDasarp");
//
//            if (!direct.exists()) {
//                direct.mkdirs();
//            }
//
//            DownloadManager mgr = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
//
//            Uri downloadUri = Uri.parse(uRl);
//            DownloadManager.Request request = new DownloadManager.Request(
//                    downloadUri);
//
//            request.setAllowedNetworkTypes(
//                    DownloadManager.Request.NETWORK_WIFI
//                            | DownloadManager.Request.NETWORK_MOBILE)
//                    .setAllowedOverRoaming(false).setTitle("Demo")
//                    .setDescription("Something useful. No, really.")
//                    .setDestinationInExternalPublicDir("/AnhsirkDasarp", "fileName.jpg");
//
//            mgr.enqueue(request);
//
//        }

//
    }
}
