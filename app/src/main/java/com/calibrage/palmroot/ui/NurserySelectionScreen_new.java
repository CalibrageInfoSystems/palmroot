package com.calibrage.palmroot.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.NurseryData;
import com.calibrage.palmroot.ui.Adapter.NurseryRecyclerviewAdapternew;

import java.util.ArrayList;
import java.util.List;

public class NurserySelectionScreen_new extends AppCompatActivity {

    RecyclerView nurseryRecyclerview;
    private DataAccessHandler dataAccessHandler;
    private List<NurseryData> nurserysList = new ArrayList<>();
    private NurseryRecyclerviewAdapternew nurseryRecyclerviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery_selection_screen);

        dataAccessHandler = new DataAccessHandler(this);
        init();
        setViews();
        String UserId = CommonConstants.USER_ID;
        Log.d("UserId Is : ", UserId);


    }

    private void init() {

        nurseryRecyclerview = findViewById(R.id.nurseryRecyclerview);

    }

    private void setViews() {

        //mActivitiesList= dataAccessHandler.getNurseryActivities(Queries.getInstance().getNurseryActivities(selectedFarmer.getCode(), 193));

        nurserysList = dataAccessHandler.getNurseryData(Queries.getInstance().getNurseryDataQuery(CommonConstants.USER_ID));
        nurseryRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        nurseryRecyclerviewAdapter = new NurseryRecyclerviewAdapternew(NurserySelectionScreen_new.this, nurserysList);
        nurseryRecyclerview.setAdapter(nurseryRecyclerviewAdapter);
    }


}