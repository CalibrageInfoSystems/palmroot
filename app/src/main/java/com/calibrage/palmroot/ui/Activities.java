package com.calibrage.palmroot.ui;

import android.os.Bundle;
import android.util.Pair;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.ConsignmentStatuData;
import com.calibrage.palmroot.dbmodels.MutipleData;
import com.calibrage.palmroot.dbmodels.NurseryAcitivity;
import com.calibrage.palmroot.ui.Adapter.ActivitiesRecyclerviewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class Activities extends AppCompatActivity {
    private static final String LOG_TAG =  Activities.class.getSimpleName();
    private TextView txtSatus,txtType,txtAge,txtDateOfsowing,txtSlectedConsiment;
    private RecyclerView activitiesRecyclerview;
    private ActivitiesRecyclerviewAdapter activitiesRecyclerviewAdapter;
    private List<NurseryAcitivity> mActivitiesList = new ArrayList<>();
    private DataAccessHandler dataAccessHandler;
    private String NURCERYCODE,CONSINEMENTCODE, EstimatedQty;
    private List<ConsignmentStatuData> consignmentstatusList = new ArrayList<>();
    LinearLayout consignmentdatalyt, recyclerviewlayout;
    private List<MutipleData> multiplelist = new ArrayList<>();



    LinkedHashMap<String, Pair> consignmentdatamap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Activity");
        setSupportActionBar(toolbar);
if( getIntent()!= null) {
    NURCERYCODE = getIntent().getStringExtra("nurceryId");
    CONSINEMENTCODE = getIntent().getStringExtra("ConsignmentCode");
    EstimatedQty = getIntent().getStringExtra("EstimatedQty");
}

        init();
        setViews();
    }

    private void init() {
        dataAccessHandler = new DataAccessHandler(this);
        Log.d(LOG_TAG, " ==> Analysis NURCERYCODE :"+NURCERYCODE+"  ====> CONSINEMENTCODE :"+CONSINEMENTCODE);

        txtSatus = findViewById(R.id.txtSatus);
        txtSlectedConsiment = findViewById(R.id.txtSlectedConsiment);
        txtType = findViewById(R.id.txtType);
        txtAge = findViewById(R.id.txtAge);
        txtDateOfsowing = findViewById(R.id.txtDateOfsowing);

        activitiesRecyclerview = findViewById(R.id.activitiesRecyclerview);

      //  consignmentdatalyt = findViewById(R.id.consignmentdatalyt);
        recyclerviewlayout = findViewById(R.id.recyclerviewlayout);


    }

    private void setViews() {
         txtSlectedConsiment.setText(":  " +CONSINEMENTCODE);
         txtAge.setText(":  " + "");
        txtDateOfsowing.setText(":  " + "");
         consignmentstatusList = dataAccessHandler.getConsignmentStatus(Queries.getInstance().getConsignmentStatusQuery(CONSINEMENTCODE));
        if(consignmentstatusList != null & consignmentstatusList.size() > 0){
             txtSatus.setText(":  " +consignmentstatusList.get(0).getStatusType() + "");
             txtType.setText(":  " +consignmentstatusList.get(0).getVarietyname() + "");
             String dateofsowing = consignmentstatusList.get(0).getSowingDate();
            if ((dateofsowing != null && !dateofsowing.isEmpty() && !dateofsowing.equals("null"))) {
                txtDateOfsowing.setText(":  " + CommonUtils.getProperComplaintsDate2(dateofsowing));
                Log.e("====>date '====", dateofsowing+"");

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date d = null;
                try {
                    d = formatter.parse(dateofsowing);//catch exception
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                Calendar thatDay = Calendar.getInstance();
                thatDay.setTime(d);

                Calendar today = Calendar.getInstance();

                long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
                long days = diff / (24 * 60 * 60 * 1000);

                txtAge.setText(":  " + days +" Day(s)");

            }
        }



//        long diff = date1.getTime() - date2.getTime();
//        long seconds = diff / 1000;
//        long minutes = seconds / 60;
//        long hours = minutes / 60;
//        long days = hours / 24;
        displayActivityData();




    }

    @Override
    protected void onResume() {
        super.onResume();

        if(CONSINEMENTCODE != null && !CONSINEMENTCODE.isEmpty())
            displayActivityData();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void displayActivityData() {

        mActivitiesList = dataAccessHandler.getNurseryActivityDetails(Queries.getInstance().getNurseryActivities(CONSINEMENTCODE));
        activitiesRecyclerview.setLayoutManager(new LinearLayoutManager(Activities.this));
        activitiesRecyclerviewAdapter = new ActivitiesRecyclerviewAdapter(Activities.this, mActivitiesList, CONSINEMENTCODE);
        activitiesRecyclerview.setAdapter(activitiesRecyclerviewAdapter);
    }

}