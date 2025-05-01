package com.calibrage.palmroot.ui;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Palm3FoilDatabase;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataManager;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.dbmodels.UserDetails;
import com.calibrage.palmroot.helper.PrefUtil;
import com.calibrage.palmroot.uihelper.ProgressBar;
import com.calibrage.palmroot.utils.UiUtils;

import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.calibrage.palmroot.datasync.helpers.DataManager.USER_DETAILS;
import static com.calibrage.palmroot.datasync.helpers.DataManager.USER_VILLAGES;

public class  MainLoginScreen extends AppCompatActivity {

    public static final String LOG_TAG = MainLoginScreen.class.getName();

    private TextView imeiNumberTxt;
    private TextView versionnumbertxt, dbVersionTxt;
    private EditText userID;
    private EditText passwordEdit;
    private Button signInBtn;
    private String userId;
    private String password;
    DataAccessHandler dataAccessHandler;
    FloatingActionButton sync;
    LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.sync=(FloatingActionButton) findViewById(R.id.sync);
       /// toolbar.setTitle(R.string.login_screen);
       // setSupportActionBar(toolbar);

        dataAccessHandler = new DataAccessHandler(MainLoginScreen.this);


        initView();

        imeiNumberTxt.setText(CommonUtils.getIMEInumber(this));
        versionnumbertxt.setText(CommonUtils.getAppVersion(this));
        dbVersionTxt.setText(""+ Palm3FoilDatabase.DATA_VERSION);

        String query = Queries.getInstance().getUserDetailsNewQuery(CommonUtils.getIMEInumber(this)); // Get Tab IMEI number


        final UserDetails userDetails = (UserDetails) dataAccessHandler.getUserDetails(query, 0);

        if (null != userDetails ) {

            userID.setText(userDetails.getUserName());
            passwordEdit.setText(userDetails.getPassword());

            List userVillages = dataAccessHandler.getSingleListData(Queries.getInstance().getUserVillages(userDetails.getId()));
            DataManager.getInstance().addData(USER_DETAILS, userDetails);
            if (!userVillages.isEmpty()) {
                DataManager.getInstance().addData(USER_VILLAGES, userVillages);
            }
            CommonConstants.USER_ID = userDetails.getId();
            CommonConstants.TAB_ID = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTabId(CommonUtils.getIMEInumber(MainLoginScreen.this)));
            CommonConstants.TAB_ID = CommonConstants.TAB_ID.replace("Tab", "T");
            CommonConstants.USER_CODE = userDetails.getUserCode();
            imeiNumberTxt.setText(CommonUtils.getIMEInumber(this)+" ("+CommonConstants.TAB_ID+")");
            List<String> userActivityRights = dataAccessHandler.getSingleListData(Queries.getInstance().activityRightQuery(userDetails.getRoleId()));
//            List<String> userActivityRights = dataAccessHandler.getSingleListData(Queries.getInstance().activityRightQuery(1));
            DataManager.getInstance().addData(DataManager.USER_ACTIVITY_RIGHTS, userActivityRights);
            Log.v(LOG_TAG, "@@@@ activity rights ");
        } else {
            UiUtils.showCustomToastMessage("User not existed", MainLoginScreen.this, 1);
        }

        /*
         * If backgrount type 0(Zero) = Green
         *                    1        = Red*/


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MainLoginScreen.this, CropMaintenanceHomeScreen.class));
                userId = userID.getText().toString();
                password = passwordEdit.getText().toString();
                if (validateField()) {
                    CommonUtils.hideKeyPad(MainLoginScreen.this, passwordEdit); //hide keypad
                    startActivity(new Intent(MainLoginScreen.this, HomeActivity.class));
                    finish();
                }
            }
        });
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMasterSync();

            }
        });



    }



    private void initView() {
        imeiNumberTxt = (TextView) findViewById(R.id.imeiNumberTxt);
        versionnumbertxt = (TextView) findViewById(R.id.versionnumbertxt);
        dbVersionTxt = (TextView) findViewById(R.id.dbVersiontxt);
        userID = (EditText) findViewById(R.id.userID);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        signInBtn = (Button) findViewById(R.id.signInBtn);
    }

    private boolean validateField() {
        if (TextUtils.isEmpty(userId)) {
            Toasty.error(this, "Please enter user id", Toast.LENGTH_SHORT).show();
            userID.requestFocus();  // not Editble
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toasty.error(this, "Please enter password", Toast.LENGTH_SHORT).show();
            passwordEdit.requestFocus(); // not Editble
            return false;
        }
        return true;
    }


    public void startMasterSync() {


        DataSyncHelper.performMasterSync(this, PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS), new ApplicationThread.OnComplete() {
            @Override
            public void execute(boolean success, Object result, String msg) {

                if (success) {

                    ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                        @Override
                        public void run() {
                            UiUtils.showCustomToastMessage("Data syncing success", MainLoginScreen.this, 0);
                            ProgressBar.hideProgressBar();


                        }
                    });

                } else {
                    Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                    ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                        @Override
                        public void run() {
                            UiUtils.showCustomToastMessage("Data syncing failed", MainLoginScreen.this, 1);
                            ProgressBar.hideProgressBar();
                        }
                    });
                }
            }
        });

    }
}