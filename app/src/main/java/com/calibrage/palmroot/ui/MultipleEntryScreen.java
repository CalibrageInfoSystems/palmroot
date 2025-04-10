package com.calibrage.palmroot.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.LandlevellingFields;
import com.calibrage.palmroot.dbmodels.MutipleData;
import com.calibrage.palmroot.ui.Adapter.MultipleEntriesRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MultipleEntryScreen extends AppCompatActivity {

    RecyclerView multipleentryrcv;
    private DataAccessHandler dataAccessHandler;
    Button addBtn;
    String activityTypeId, activityName, ismultipleentry, consignmentcode, status;
    int statusId;
    MultipleEntriesRecyclerViewAdapter multipleEntriesRecyclerViewAdapter;
    private List<MutipleData> multiplelist = new ArrayList<>();
    private List<LandlevellingFields> fieldslist = new ArrayList<>();
String btn_visibility;
int Feild_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_entry_screen);


        dataAccessHandler = new DataAccessHandler(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            activityTypeId = extras.getString("ActivityTypeId1");
            activityName = extras.getString("ActivityName1");
            ismultipleentry = extras.getString("Ismultipleentry1");
            consignmentcode = extras.getString("consignmentcode");
            status = extras.getString("status");
            statusId = extras.getInt("statusId");
//            consignmentcode = extras.getString("addActivity");
            Log.d("ActivityTypeIdHere", activityTypeId + "");
            Log.d("status===55", status + "");
        }

        init();
        setViews();

    }

    private void init() {

        multipleentryrcv = findViewById(R.id.multipleentries);
        addBtn = findViewById(R.id.addBtn);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ismultipleentry != null && !ismultipleentry.isEmpty())
            setViews();
    }

    private void setViews() {


        multiplelist = dataAccessHandler.getMultipleDataDetails(Queries.getInstance().getMultiplerecordsDetailsQuery(consignmentcode, activityTypeId));


        fieldslist = dataAccessHandler.getlandlevelligfeildDetails(Queries.getInstance().getFieldsData());

        Log.d("multiplelist", multiplelist.size() + "");
        multipleentryrcv.setLayoutManager(new LinearLayoutManager(this));
        Log.d("status===88", status + "");
        multipleEntriesRecyclerViewAdapter = new MultipleEntriesRecyclerViewAdapter(MultipleEntryScreen.this, multiplelist, fieldslist, activityName, activityTypeId, ismultipleentry, consignmentcode, status);
        multipleentryrcv.setAdapter(multipleEntriesRecyclerViewAdapter);

        Feild_id = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getFeildID( activityTypeId)) ;
        btn_visibility = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().addbutton(Feild_id,consignmentcode, activityTypeId)) ;


        if (btn_visibility != null && btn_visibility.equalsIgnoreCase("true")) {
            addBtn.setVisibility(View.GONE);
        }
      else  if (statusId == 346) {
            addBtn.setVisibility(View.GONE);

        } else
            {
            addBtn.setVisibility(View.VISIBLE);
        }
//        addBtn.setVisibility(View.GONE);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean showbutton = false;

                // TOdo  Check Job Done or Not
//                int value = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().CheckJobDoneOrnot(consignmentcode, activityTypeId));
                if (statusId == 349) {
                    showbutton = true;
                }
                if (statusId != 346) {
                    Intent at = new Intent(MultipleEntryScreen.this, ActivityTask.class);

                    at.putExtra("ActivityTypeId", activityTypeId);
                    at.putExtra("consignmentcode", consignmentcode);
                    at.putExtra("multipleEntry", true);
                    at.putExtra("ActivityName", activityName);                                 
                    at.putExtra("enableEditing", showbutton);
                    at.putExtra(CommonConstants.SCREEN_CAME_FROM, CommonConstants.FROM_MULTIPLE_ADD_NEW_TASK);
                    startActivity(at);
                } else {

                    Toast.makeText(MultipleEntryScreen.this, "Already Job done", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}