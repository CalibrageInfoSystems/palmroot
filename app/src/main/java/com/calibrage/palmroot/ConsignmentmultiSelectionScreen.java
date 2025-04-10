package com.calibrage.palmroot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.ConsignmentData;

import com.calibrage.palmroot.ui.Adapter.MultiConsignmentRecyclerviewAdapter;

import com.calibrage.palmroot.ui.irrigation.IrrigationActivity;


import java.util.ArrayList;
import java.util.List;

public class ConsignmentmultiSelectionScreen extends AppCompatActivity {

    RecyclerView consignmentRecyclerview;
    private DataAccessHandler dataAccessHandler;
    private List<ConsignmentData> consignmentList = new ArrayList<>();
    private MultiConsignmentRecyclerviewAdapter consignmentRecyclerviewAdapter;
    String nurserycode;
    private ArrayList<ConsignmentData> selectedconsignmentList = new ArrayList<>();
    // private AppCompatButton btnGetSelected;

    Button select_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consignment_multi_selection_screen);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nurserycode = extras.getString("NurseryCode");
        }

        Log.d("nurserycode", nurserycode + "");
        dataAccessHandler = new DataAccessHandler(this);
        init();
        setViews();
        String UserId = CommonConstants.USER_ID;
        Log.d("UserId Is : ", UserId);


        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (consignmentRecyclerviewAdapter.getSelected().size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < consignmentRecyclerviewAdapter.getSelected().size(); i++) {
                        stringBuilder.append(consignmentRecyclerviewAdapter.getSelected().get(i).getConsignmentCode());
                        stringBuilder.append("\n");

                        Intent intent = new Intent(getBaseContext(), IrrigationActivity.class);
//                         CommonConstants.ConsignmentID = consignmentList
//                        CommonConstants.ConsignmentCode = consignmentList.get(position).getConsignmentCode();
                        startActivity(intent);
//                        intent.putExtra("NurseryCode",nurserysList.get(position).getCode());


                    }
                    showToast(stringBuilder.toString().trim());
                } else {
                    showToast("No Selection");
                }


            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void init() {

        consignmentRecyclerview = findViewById(R.id.consignmentRecyclerview);
        select_btn = findViewById(R.id.select_btn);
    }

    private void setViews() {

        //mActivitiesList= dataAccessHandler.getNurseryActivities(Queries.getInstance().getNurseryActivities(selectedFarmer.getCode(), 193));

        consignmentList = dataAccessHandler.getConsignmentData(Queries.getInstance().getConsignmentDataQuery(CommonConstants.USER_ID, nurserycode));
        consignmentRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        consignmentRecyclerviewAdapter = new MultiConsignmentRecyclerviewAdapter(ConsignmentmultiSelectionScreen.this, consignmentList, nurserycode);
        consignmentRecyclerview.setAdapter(consignmentRecyclerviewAdapter);

    }
}