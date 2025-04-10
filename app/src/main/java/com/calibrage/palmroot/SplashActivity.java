package com.calibrage.palmroot;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Palm3FoilDatabase;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.helper.PrefUtil;
import com.calibrage.palmroot.ui.MainLoginScreen;
import com.calibrage.palmroot.uihelper.ProgressBar;
import com.calibrage.palmroot.utils.UiUtils;

public class SplashActivity extends AppCompatActivity {

    public static final String LOG_TAG = SplashActivity.class.getName();

    private Palm3FoilDatabase palm3FoilDatabase;
    private String[] PERMISSIONS_REQUIRED = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.FOREGROUND_SERVICE
    };
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getSharedPreferences("appprefs", MODE_PRIVATE);
        if (!CommonUtils.isNetworkAvailable(this)) {
            UiUtils.showCustomToastMessage("Please check your network connection", SplashActivity.this, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !CommonUtils.areAllPermissionsAllowedNew(this, PERMISSIONS_REQUIRED)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, CommonUtils.PERMISSION_CODE);
        } else {
            try {
                palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
                palm3FoilDatabase.createDataBase();
                dbUpgradeCall();
            } catch (Exception e) {
                e.getMessage();
            }
            startMasterSync();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CommonUtils.PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOG_TAG, "permission granted");
                    try {
                        palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
                        palm3FoilDatabase.createDataBase();
                        dbUpgradeCall();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "@@@ Error while getting master data " + e.getMessage());
                    }
                    startMasterSync(); // Master Sync
                }
                break;
        }
    }
    /**
     * Master in Splash Screen
     *

     * @return true if available else false
     */
    public void startMasterSync() {

        if (CommonUtils.isNetworkAvailable(this) && !sharedPreferences.getBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS,false)) {
            DataSyncHelper.performMasterSync(this, PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS), new ApplicationThread.OnComplete() {
                @Override
                public void execute(boolean success, Object result, String msg) {
                    ProgressBar.hideProgressBar();
                    if (success) {
                        sharedPreferences.edit().putBoolean(CommonConstants.IS_MASTER_SYNC_SUCCESS, true).apply();
                        UiUtils.showCustomToastMessage("Master Sync Success", SplashActivity.this, 0);
                        startActivity(new Intent(SplashActivity.this, MainLoginScreen.class));
//                        startActivity(new Intent(SplashActivity.this, Activities.class));
                        finish();
                    } else {
                        Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                        ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Data syncing failed", SplashActivity.this, 1);
                                startActivity(new Intent(SplashActivity.this, MainLoginScreen.class));
                                finish();
                            }
                        });
                    }
                }
            });
        } else {
            startActivity(new Intent(SplashActivity.this, MainLoginScreen.class));
            finish();
        }
    }

    public void dbUpgradeCall() {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(SplashActivity.this, false);
        String count = dataAccessHandler.getCountValue(Queries.getInstance().UpgradeCount());
        if (TextUtils.isEmpty(count) || Integer.parseInt(count) == 0) {
            SharedPreferences sharedPreferences = getSharedPreferences("appprefs", MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(CommonConstants.IS_FRESH_INSTALL, true).apply();
        }
    }
}