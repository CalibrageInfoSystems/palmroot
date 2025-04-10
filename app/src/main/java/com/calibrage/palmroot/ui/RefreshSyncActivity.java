package com.calibrage.palmroot.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.cloudhelper.CloudDataHandler;
import com.calibrage.palmroot.cloudhelper.Config;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.DatabaseKeys;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.dbmodels.UserSync;
import com.calibrage.palmroot.uihelper.ProgressBar;
import com.calibrage.palmroot.uihelper.ProgressDialogFragment;
import com.calibrage.palmroot.utils.UiUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class RefreshSyncActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = RefreshSyncActivity.class.getName();
    private static int consignmentCount = 0, collectionsCount = 0, collectionPlotsCountInt = 0;
    private TextView tvsapling, tvsaplingActivity, tvsaplinghistory, tvsaplingxref,irrigationLog,irrigationLogXref,cullingLossFileRepository,irrigationLoghistory,Irrigationlabourlog,nurseryvisitlog,statuscount, rmactivities,rmstatushistory;
    private Button btnsend, btnmastersync, btnDBcopy, transSyncBtn, btresetdatabase;
    private DataAccessHandler dataAccessHandler;
    private List<String> collectionCodes, consignmentCodes, farmerCodes, farmerBankCodes, idproofCodes, addressCodes, plotCodes, plotCurrentCropCodes, neighbourPlotCodes, waterResourceCodes,
            soilResourceCodes, plotIrrigationCodes, followupCodes, referralCodes, marketsurveyCodes;
    private List<Pair> collectionPlots = null;
    private List<Pair> farmerhistoryCodes = null;
    private ArrayList<String> tableNames = new ArrayList<>();
    public static ArrayList<String> allRefreshDataMap;
    public static int resetFarmerCount = 0;
    public static int resetPlotCount = 0;
    public static int afterResetFarmerCount = 0;
    public static int afterResetPlotCount = 0;
    private boolean isDataUpdated = false;
    UserSync userSync;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.refresh_sync);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle(R.string.refresh);
//        setSupportActionBar(toolbar);

        allRefreshDataMap = new ArrayList<>();
        allRefreshDataMap.add(DatabaseKeys.TABLE_SAPLING);
        allRefreshDataMap.add(DatabaseKeys.TABLE_SaplingActivity);
        allRefreshDataMap.add(DatabaseKeys.TABLE_SaplingActivityStatus);
        allRefreshDataMap.add(DatabaseKeys.TABLE_SaplingActivityXref);
        allRefreshDataMap.add(DatabaseKeys.TABLE_SaplingActivityHistory);
        allRefreshDataMap.add(DatabaseKeys.TABLE_NurseryIrrigationLog);
        allRefreshDataMap.add(DatabaseKeys.TABLE_NurseryIrrigationLogXREF);
        allRefreshDataMap.add(DatabaseKeys.TABLE_NurseryIrrigationhistory);
        allRefreshDataMap.add(DatabaseKeys.TABLE_FILEREPOSITORY);
        allRefreshDataMap.add(DatabaseKeys.TABLE_ALERTS);
        allRefreshDataMap.add(DatabaseKeys.TABLE_NURSERYVISITLOGS);
        allRefreshDataMap.add(DatabaseKeys.TABLE_RMTransactions);
        allRefreshDataMap.add(DatabaseKeys.TABLE_RMTransactionStatusHistory);

        dataAccessHandler = new DataAccessHandler(this);

        CommonUtils.currentActivity = this;

        initUI();
        consignmentCount = 0;
        collectionsCount = 0;
        collectionPlotsCountInt = 0;

    }

    /**
     * Intializing UI elements
     */
    private void initUI() {



        tvsapling = findViewById(R.id.saplingcount);
        tvsaplingActivity = findViewById(R.id.saplingactivitycount);
        tvsaplingxref = findViewById(R.id.xrefcount);
        tvsaplinghistory = findViewById(R.id.historycount);
        irrigationLog = findViewById(R.id.irrigationLog);
        irrigationLogXref = findViewById(R.id.irrigationLogXref);
        cullingLossFileRepository =findViewById(R.id.cullingLossFileRepository);
        irrigationLoghistory = findViewById(R.id.irrigationLoghistory);
        Irrigationlabourlog =findViewById(R.id.Irrigationlabourlog);
        statuscount = findViewById(R.id.statuscount);
        btnsend = findViewById(R.id.btsynctoserver);
        btnmastersync = findViewById(R.id.btnmastersync);
        btnDBcopy = findViewById(R.id.btcopydatabase);
        transSyncBtn = findViewById(R.id.transSyncBtn);
        btresetdatabase = findViewById(R.id.btresetdatabase);
        nurseryvisitlog = findViewById(R.id.nurseryvisitlog);
        rmstatushistory = findViewById(R.id.rmstatushistory);
        btnmastersync.setEnabled(true);
        btresetdatabase.setEnabled(true);
        btnDBcopy.setEnabled(true);

        rmactivities = findViewById(R.id.randmactivities);

        btnsend.setOnLongClickListener(view -> {
            Log.v(LOG_TAG, "long pressed");
            CommonUtils.copyFile(RefreshSyncActivity.this);
            return true;
        });

        /** 199 to 207 ** line **/

//        transSyncBtn.setOnClickListener(view -> showTransactionsAlertDialog(false));

        transSyncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionsAlertDialog(false);
//                List<UserSync> resetList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfTraSync());
//                List<UserSync> resetList = (List<UserSync>) dataAccessHandler.getUserSyncData(Queries.getInstance().countOfSync());
//
//                if (resetList.size() == 0) {
//                    Log.v("@@@MM", "mas");
//                    addUserTraSyncDetails();
//                } else {
//                    dataAccessHandler.updateTransactionSync();
//                }


            }
        });


        btresetdatabase.setOnClickListener(view -> {

//            fetchCount();
            showTransactionsAlertDialog(true);
            // List<UserSync> resetList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfResetdata());
//            List<UserSync> resetList = (List<UserSync>) dataAccessHandler.getUserSyncData(Queries.getInstance().countOfSync());
//
//            if (resetList.size() == 0) {
//                Log.v("@@@MM", "mas");
//                addUserResetSyncDetails();
//            } else {
//                dataAccessHandler.updateResetDataSync();
//            }
          //  dataAccessHandler.updateResetDataSync();
        });

        btnsend.setOnClickListener(this);
        btnmastersync.setOnClickListener(this);
        btnDBcopy.setOnClickListener(this);


        bindData();

        if (tvsapling.getText().toString().equalsIgnoreCase("0")
                && tvsaplingActivity.getText().toString().equalsIgnoreCase("0")
                && tvsaplingxref.getText().toString().equalsIgnoreCase("0")
                && tvsaplinghistory.getText().toString().equalsIgnoreCase("0")
        ) {

//            btresetdatabase.setEnabled(true);
//            btresetdatabase.setVisibility(View.VISIBLE);

        } else {
//            btresetdatabase.setEnabled(false);
//            btresetdatabase.setVisibility(View.GONE);
        }

    }

    public void fetchCount() {
        resetFarmerCount = Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getFarmerCount()));
        resetPlotCount = Integer.parseInt(dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getPlotCount()));
    }

    private void bindData() {
        try {

            tvsapling.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("Sapling")));
            tvsaplingActivity.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("SaplingActivity")));
            tvsaplingxref.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("SaplingActivityXref")));
            tvsaplinghistory.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("SaplingActivityHistory")));
            irrigationLog.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("NurseryIrrigationLog")));
            irrigationLogXref.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("NurseryIrrigationLogXref")));
            irrigationLoghistory.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("IrrigationLogStatusHistory")));
            cullingLossFileRepository.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("CullingLossFileRepository")));
            Irrigationlabourlog.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("NurseryLabourLog")));
            nurseryvisitlog.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("NurseryVisitLog")));
            statuscount .setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("SaplingActivityStatus")));
            rmactivities.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("RMTransactions")));
            rmstatushistory.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("RMTransactionStatusHistory")));
            Log.e("=============>",Queries.getInstance().getRefreshCountQuery("CullingLossFileRepository"));

//            //getVistLogRecords
//            String getVistLogRecords = dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("VisitLog"));
//            Log.v(LOG_TAG, "getVistLogRecords " + getVistLogRecords);
//            String recomFertilizer = dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("FertilizerRecommendations"));
//            Log.v(LOG_TAG, "recomFertilizer data count" + recomFertilizer);

            isDataUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btsynctoserver:

//                List<UserSync> traList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfTraSync());
//                Log.v("@@@MM","trasize"+traList.size());
//                if(traList.size()==0){
//                    Log.v("@@@MM","mas");
//                    addUserTraSyncDetails();
//                }

                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {

                    btnsend.setVisibility(View.GONE);
                    isDataUpdated = false;
                    DataSyncHelper.performRefreshTransactionsSync(RefreshSyncActivity.this, new ApplicationThread.OnComplete() {
                        @Override
                        public void execute(boolean success, Object result, String msg) {
                            if (success) {
                                ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                    @Override
                                    public void run() {
                                        bindData();
//                                        Toasty.success(RefreshSyncActivity.this,"Successfully data sent to server",10).show();
                                        if (isDataUpdated) {
                                            UiUtils.showCustomToastMessage("Successfully data sent to server", RefreshSyncActivity.this, 0);
                                            ProgressBar.hideProgressBar();
                                            btnsend.setVisibility(View.VISIBLE);
                                            //  dataAccessHandler.updateUserSync();
                                        }

                                    }
                                });
                            } else {
                                ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                    @Override
                                    public void run() {
                                        bindData();
                                        Toasty.error(RefreshSyncActivity.this, "Data sending failed", 10).show();
//                                        Toast.makeText(RefreshSyncActivity.this, "Data sending failed", Toast.LENGTH_SHORT).show();
                                        ProgressBar.hideProgressBar();
                                        btnsend.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                    btnsend.setVisibility(View.VISIBLE);
                }


                break;

            case R.id.btnmastersync:

                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {
                    DataSyncHelper.performMasterSync(this, false, new ApplicationThread.OnComplete() {
                        @Override
                        public void execute(boolean success, Object result, String msg) {
                            ProgressBar.hideProgressBar();
                            if (success) {
                                if (!msg.equalsIgnoreCase("Sync is up-to-date")) {
                                    Toast.makeText(RefreshSyncActivity.this, "Data synced successfully", Toast.LENGTH_SHORT).show();
                                    // List<UserSync> userSyncList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfMasterSync());
//                                    List<UserSync> userSyncList = (List<UserSync>) dataAccessHandler.getUserSyncData(Queries.getInstance().countOfSync());
//
//                                    if (userSyncList.size() == 0) {
//                                        Log.v("@@@MM", "mas");
//                                        addUserMasSyncDetails();
//                                    } else {
//                                        dataAccessHandler.updateMasterSync();
//                                    }
//                                    dataAccessHandler.updateMasterSync();
                                    // DataAccessHandler dataAccessHandler = new DataAccessHandler(RefreshSyncActivity.this);
                                    // dataAccessHandler.updateMasterSyncDate(false, CommonConstants.USER_ID);
                                } else {
                                    ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RefreshSyncActivity.this, "You have updated data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                                ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RefreshSyncActivity.this, "Master sync failed. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }
                break;
            case R.id.btcopydatabase:
                showAlertDialog();
                break;
            default:
                break;
        }

    }

    public void showAlertDialog() {
        final Dialog dialog = new Dialog(RefreshSyncActivity.this);
        dialog.setContentView(R.layout.custom_alert_dailog);

        Button yesDialogButton = dialog.findViewById(R.id.Yes);
        Button noDialogButton = dialog.findViewById(R.id.No);
        TextView msg = dialog.findViewById(R.id.test);
        yesDialogButton.setTextColor(getResources().getColor(R.color.green));
        noDialogButton.setTextColor(getResources().getColor(R.color.btnPressedColor));
        msg.setText("Do you want to upload data base to server ?");
        // if button is clicked, close the custom dialog
        yesDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {
                    dialog.dismiss();
                    ProgressBar.showProgressBar(RefreshSyncActivity.this, "uploading database...");
                    CommonUtils.copyFile(RefreshSyncActivity.this);
                    uploadDatabaseFile();
                } else {
                    dialog.dismiss();
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }
            }
        });
        dialog.show();
        noDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void addUserTraSyncDetails() {

        SimpleDateFormat simpledatefrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = simpledatefrmt.format(new Date());

        userSync = new UserSync();
        userSync.setUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setApp("3fMainApp");
        userSync.setDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setMasterSync(0);
        userSync.setTransactionSync(1);
        userSync.setResetData(0);
        userSync.setIsActive(1);
        userSync.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setServerUpdatedStatus(0);
        userSync.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        dataAccessHandler.addUserSync(userSync);

    }

    public void addUserMasSyncDetails() {

        SimpleDateFormat simpledatefrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = simpledatefrmt.format(new Date());

        userSync = new UserSync();
        userSync.setUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setApp("" + "3fMainApp");
        userSync.setDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setMasterSync(1);
        userSync.setTransactionSync(0);
        userSync.setResetData(0);
        userSync.setIsActive(1);
        userSync.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setServerUpdatedStatus(0);

        long resul = dataAccessHandler.addUserSync(userSync);
        if (resul > -1) {
            Log.v("@@@MM", "Success");
        }

    }


    public void uploadDataBase(final File uploadDbFile, final ApplicationThread.OnComplete<String> onComplete) {
        if (null != uploadDbFile) {
            final long nanoTime = System.nanoTime();
            final String filePathToSave = "/sdcard/3f_" + CommonConstants.TAB_ID + "_" + nanoTime + "_v_" + CommonUtils.getAppVersion(RefreshSyncActivity.this) + ".gzip";
            final File toZipFile = getDbFileToUpload();
            CommonUtils.gzipFile(toZipFile, filePathToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        File dir = Environment.getExternalStorageDirectory();
                        File uploadFile = new File(dir, "3f_" + CommonConstants.TAB_ID + "_" + nanoTime + "_v_" + CommonUtils.getAppVersion(RefreshSyncActivity.this) + ".gzip");
                        Log.v(LOG_TAG, "@@@ file size " + uploadFile.length());
                        if (uploadFile != null) {
                            CloudDataHandler.uploadFileToServer(uploadFile, Config.live_url + Config.updatedbFile, new ApplicationThread.OnComplete<String>() {
                                @Override
                                public void execute(boolean success, String result, String msg) {
                                    onComplete.execute(success, result, msg);
                                }
                            });
                        } else {
                            onComplete.execute(false, "failed", "data base is empty");
                        }

                    } else {
                        onComplete.execute(success, result, msg);
                    }
                }
            });
        } else {
            onComplete.execute(false, "file upload failed", "null database");
        }

    }

    public boolean copy(File src, File dst) {

        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return true;
        } catch (Exception e) {
            android.util.Log.w("Settings Backup", e);
            return false;
        }
    }

    public File getDbFileToUpload() {
        try {
//            File dir = Environment.getExternalStorageDirectory();
//            File dbFileToUpload = new File("/sdcard/3F_Files/3F_Database/3foilpalm.sqlite");
            File dbFileToUpload = new File("/sdcard/PalmRoot_Files/3F_Database/3foilpalm.sqlite");
            return dbFileToUpload;
        } catch (Exception e) {
            android.util.Log.w("Settings Backup", e);
        }
        return null;
    }


    public void uploadDatabaseFile() {
        ApplicationThread.bgndPost(LOG_TAG, "upload database..", new Runnable() {
            @Override
            public void run() {
                uploadDataBase(getDbFileToUpload(), new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        ProgressBar.hideProgressBar();
                        if (success) {
                            Log.v(LOG_TAG, "@@@ 3f db file upload success");
                            CommonUtils.showToast("3f db file uploaded successfully", RefreshSyncActivity.this);
                        } else {
                            Log.v(LOG_TAG, "@@@ 3f db file upload failed due to " + msg);
                            CommonUtils.showToast("3f db file upload failed due to" + msg, RefreshSyncActivity.this);
                        }
                    }
                });
            }
        });
    }
    public void showTransactionsAlertDialog(final boolean fromReset) {
        final Dialog dialog = new Dialog(RefreshSyncActivity.this);
        dialog.setContentView(R.layout.custom_alert_dailog);

        Button yesDialogButton = dialog.findViewById(R.id.Yes);
        Button noDialogButton = dialog.findViewById(R.id.No);
        TextView msg = dialog.findViewById(R.id.test);
        yesDialogButton.setTextColor(getResources().getColor(R.color.green));
        noDialogButton.setTextColor(getResources().getColor(R.color.btnPressedColor));
        msg.setText(R.string.you_want_to_perform_transactions_sync);
        // if button is clicked, close the custom dialog
        yesDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<UserSync> resetList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfResetdata());
//                if(resetList.size()==0){
//                    Log.v("@@@MM","mas");
//                    addUserResetSyncDetails();
//                }
//                addUserResetSyncDetails();
                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {
                    dialog.dismiss();
                    if (fromReset) {
                        DataSyncHelper.updateSyncDate(RefreshSyncActivity.this, null);
                        for (String s : allRefreshDataMap) {
                            dataAccessHandler.executeRawQuery("DELETE FROM " + s);
                            Log.v(LOG_TAG, "delete table" + s);
                        }
                    }
//                    DataSyncHelper.updateSyncDate(RefreshSyncActivity.this, null);
                    FragmentManager fm = getSupportFragmentManager();
                    ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
                    progressDialogFragment.show(fm, "progress dialog fragment");
                    DataSyncHelper.startTransactionSync(RefreshSyncActivity.this, progressDialogFragment);
                } else {
                    dialog.dismiss();
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }
            }
        });
        dialog.show();
        noDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void updateCollectionsData() {
        if (null != collectionCodes && !collectionCodes.isEmpty()) {
            CloudDataHandler.getRecordStatus(String.format(Config.live_url + Config.findcollectioncode, collectionCodes.get(collectionsCount)), new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success && !TextUtils.isEmpty(result) && result.equalsIgnoreCase("true")) {
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedCollectionDetailsStatus("'" + collectionCodes.get(collectionsCount)) + "'");
                    }
                    collectionsCount++;
                    if (collectionsCount == collectionCodes.size()) {
                        Log.v(LOG_TAG, "update finished");
                        ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                            @Override
                            public void run() {
                                updateConsignmentData();
                            }
                        });
                    } else {
                        updateCollectionsData();
                    }
                }
            });
        } else {
            updateConsignmentData();
        }
    }

    public void updateConsignmentData() {
        if (null != consignmentCodes && !consignmentCodes.isEmpty()) {
            CloudDataHandler.getRecordStatus(String.format(Config.live_url + Config.findconsignmentcode, consignmentCodes.get(consignmentCount)), new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success && !TextUtils.isEmpty(result) && result.equalsIgnoreCase("true")) {
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedConsignmentDetailsStatus("'" + consignmentCodes.get(consignmentCount)) + "'");
                    }
                    consignmentCount++;
                    if (consignmentCount == consignmentCodes.size()) {
                        Log.v(LOG_TAG, "update finished");
                        ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                            @Override
                            public void run() {
                                updateCollectionPlotsData();
                            }
                        });
                    } else {
                        updateConsignmentData();
                    }
                }
            });
        } else {
            updateCollectionPlotsData();
        }
    }

    public void updateCollectionPlotsData() {
        if (null != collectionPlots && !collectionPlots.isEmpty()) {
            CloudDataHandler.getRecordStatus(String.format(Config.live_url + Config.findcollectionplotcode, collectionPlots.get(collectionPlotsCountInt).first, collectionPlots.get(collectionPlotsCountInt).second), new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    Pair collectionPlotPair = collectionPlots.get(collectionPlotsCountInt);
                    if (success && !TextUtils.isEmpty(result) && result.equalsIgnoreCase("true")) {
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedCollectionPlotXRefDetailsStatus(collectionPlotPair.first.toString(), collectionPlotPair.second.toString()));
                    }
                    collectionPlotsCountInt++;
                    if (collectionPlotsCountInt == collectionPlots.size()) {
                        Log.v(LOG_TAG, "update finished");
                        ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                            @Override
                            public void run() {
                                ProgressBar.hideProgressBar();
                                bindData();
                            }
                        });
                    } else {
                        updateCollectionPlotsData();
                    }
                }
            });
        } else {
            ProgressBar.hideProgressBar();
        }
    }

    public void updateFarmerCodesData() {
        if (null != farmerCodes && !farmerCodes.isEmpty()) {
            CloudDataHandler.getRecordStatus(String.format(Config.live_url + Config.findcollectioncode, collectionCodes.get(collectionsCount)), new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success && !TextUtils.isEmpty(result) && result.equalsIgnoreCase("true")) {
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedCollectionDetailsStatus("'" + collectionCodes.get(collectionsCount)) + "'");
                    }
                    collectionsCount++;
                    if (collectionsCount == collectionCodes.size()) {
                        Log.v(LOG_TAG, "update finished");
                        ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                            @Override
                            public void run() {
                                updateConsignmentData();
                            }
                        });
                    } else {
                        updateCollectionsData();
                    }
                }
            });
        } else {
            updateConsignmentData();
        }
    }

}