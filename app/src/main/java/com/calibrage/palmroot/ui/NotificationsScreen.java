package com.calibrage.palmroot.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.DatabaseKeys;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.dbmodels.Alerts;
import com.calibrage.palmroot.ui.Adapter.NotificationDisplayAdapter;
import com.calibrage.palmroot.uihelper.ProgressBar;
import com.calibrage.palmroot.utils.UiUtils;

import java.util.Collections;
import java.util.List;

import static com.calibrage.palmroot.datasync.helpers.DataSyncHelper.refreshtableNamesList;
import static com.calibrage.palmroot.datasync.helpers.DataSyncHelper.refreshtransactionsDataMap;

public class NotificationsScreen extends AppCompatActivity {
    public static final String LOG_TAG = NotificationsScreen.class.getName();
    private Button refreshBtn;
    private RecyclerView notiRecyclerView;
    private NotificationDisplayAdapter notificationDisplayAdapter;
    private LinearLayoutManager layoutManager;
    private DataAccessHandler dataAccessHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_screen);

        notiRecyclerView = (RecyclerView) findViewById(R.id.notificationRecyclerView);
        refreshBtn = (Button) findViewById(R.id.refreshBtn);
        layoutManager = new LinearLayoutManager(this);
        notiRecyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_);
        toolbar.setTitle("Notifications");
        setSupportActionBar(toolbar);


        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSyncHelper.getAlertsData(NotificationsScreen.this, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            renderNotifications();
                        } else {
                            UiUtils.showCustomToastMessage("Error while getting alerts Data", NotificationsScreen.this, 1);
                        }
                    }
                });
            }
        });

        dataAccessHandler = new DataAccessHandler(this);

        renderNotifications();

        updateNotificationStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNotificationStatus() {
        dataAccessHandler.upNotificationStatus();

        List<Alerts> dataToSendCloud = (List<Alerts>) dataAccessHandler.getAlertsDetails(Queries.getInstance()
                .getAlertsDetailsQueryToSendCloud(), 1, true);

        if (dataToSendCloud == null || dataToSendCloud.isEmpty()) {
            return;
        }

        DataSyncHelper.reverseSyncTransCount = 0;
        DataSyncHelper.transactionsCheck = 0;
        refreshtableNamesList.clear();
        refreshtransactionsDataMap.clear();
        refreshtableNamesList.add(DatabaseKeys.TABLE_ALERTS);
        refreshtransactionsDataMap.put(DatabaseKeys.TABLE_ALERTS, dataToSendCloud);

//        DataSyncHelper.postTransactionsDataToCloud(this, DatabaseKeys.TABLE_ALERTS, dataAccessHandler, new ApplicationThread.OnComplete() {
//            @Override
//            public void execute(boolean success, Object result, String msg) {
//                super.execute(success, result, msg);
//                refreshtableNamesList.clear();
//                refreshtransactionsDataMap.clear();
//            }
//        });
// TODO No need update status
        refreshtableNamesList.clear();
        refreshtransactionsDataMap.clear();
    }

    private void renderNotifications()    {

        ProgressBar.showProgressBar(this, "Please wait...");
        ApplicationThread.bgndPost(LOG_TAG, "", new Runnable() {
            @Override
            public void run() {
                ProgressBar.hideProgressBar();
                final List<Alerts> alertsList = (List<Alerts>) dataAccessHandler.getAlertsDetails(Queries.getInstance().getAlertsDetailsQueryToRender(), 1, false);

                Collections.reverse(alertsList);
                ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                    @Override
                    public void run() {
                        if (null != alertsList && !alertsList.isEmpty()) {
                            notificationDisplayAdapter = new NotificationDisplayAdapter(alertsList);
                            notiRecyclerView.setAdapter(notificationDisplayAdapter);
                        }
                    }
                });
            }
        });
    }

}


