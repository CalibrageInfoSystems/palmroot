package com.calibrage.palmroot.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.NurseryRMActivity;
import com.calibrage.palmroot.ui.Adapter.NotificationDisplayAdapter;
import com.calibrage.palmroot.ui.Adapter.NurseryrmActivitiesAdapter;

import java.util.ArrayList;
import java.util.List;

public class NurseryrmActivities extends AppCompatActivity {

    public static final String LOG_TAG = NurseryrmActivities.class.getSimpleName();
     Button ok_btn;
    private RecyclerView ActivityRecyclerView;
    private NotificationDisplayAdapter notificationDisplayAdapter;
    private LinearLayoutManager layoutManager;
    private DataAccessHandler dataAccessHandler;
    TextView othertext;
    EditText Activitynameedit;
    LinearLayout otherlinear;
    private List<NurseryRMActivity>rmActivity_List = new ArrayList<>();
    NurseryrmActivitiesAdapter nurseryrmListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurseryrm_activities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Nursery R&M");
        setSupportActionBar(toolbar);
        initviews();
        setviews();
    }

    private void initviews() {
        dataAccessHandler = new DataAccessHandler(this);
        othertext= findViewById(R.id.othertext);
        ActivityRecyclerView = findViewById(R.id.ActivityRecyclerView);

        ActivityRecyclerView.setHasFixedSize(true);
        ActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void setviews() {
        rmActivity_List = dataAccessHandler.getNurseryRMActivities(Queries.getInstance().getrmActivities());

        nurseryrmActivities();





        }


    private void nurseryrmActivities() {



        nurseryrmListAdapter =    new NurseryrmActivitiesAdapter(this, rmActivity_List);
        ActivityRecyclerView.setAdapter(nurseryrmListAdapter);
    }
}