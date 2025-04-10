package com.calibrage.palmroot.ui;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.ConsignmentDetails;
import com.calibrage.palmroot.dbmodels.NurseryDetails;

import java.util.LinkedHashMap;
import java.util.List;

public class SelectionScreen extends AppCompatActivity {

    Spinner nurserySpinner, consignmentSpinner;
    private LinkedHashMap<String, Pair> nurserydatamap = null;
    private LinkedHashMap<String, Pair> consignmentdatamap = null;

    private DataAccessHandler dataAccessHandler;
    private List<NurseryDetails> nurseryDetails;
    private List<ConsignmentDetails> consignmentDetails;
    LinearLayout detailslyt, consignmentdetailslyt;
    TextView nurseryname, nurserycode, nurserypin;
    TextView consignmentname, consignmentcode, consignmentpin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Selection Screen");
        setSupportActionBar(toolbar);
        dataAccessHandler = new DataAccessHandler(SelectionScreen.this);

        init();
        setviews();
    }

    private void init() {

        nurserySpinner = findViewById(R.id.nurseryspin);
        consignmentSpinner = findViewById(R.id.consignmentSpin);
        nurseryname = findViewById(R.id.nurseryname);
        nurserycode = findViewById(R.id.nurserycode);
        nurserypin = findViewById(R.id.nurserypin);
        detailslyt =  findViewById(R.id.detailslyt);
        consignmentdetailslyt = findViewById(R.id.consignmentdetailslyt);
        consignmentname = findViewById(R.id.consignmentname);
        consignmentcode = findViewById(R.id.consignmentcode);
        consignmentpin = findViewById(R.id.consignmentpin);
    }

    private void setviews() {

        nurserydatamap = dataAccessHandler.getPairData(Queries.getInstance().getNurseryMasterQuery());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(SelectionScreen.this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(nurserydatamap, "Nursery"));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nurserySpinner.setAdapter(spinnerArrayAdapter);

        nurserySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (nurserySpinner.getSelectedItemPosition() != 0) {
                    detailslyt.setVisibility(View.VISIBLE);
                    Log.d("Selected1", nurserySpinner.getSelectedItem().toString());
                    nurseryDetails = dataAccessHandler.getNurseryDetails(Queries.getInstance().getNurseryDetailsQuery(nurserySpinner.getSelectedItem().toString()));
                    nurseryname.setText(nurseryDetails.get(0).getName());
                    nurserycode.setText(nurseryDetails.get(0).getCode());
                    nurserypin.setText(nurseryDetails.get(0).getPinCode() + "");

                    consignmentdatamap = dataAccessHandler.getPairData(Queries.getInstance().getConsignmentByNurceryMastQuery(""+nurseryDetails.get(0).getCode()));
                    ArrayAdapter<String> consspinnerArrayAdapter = new ArrayAdapter<>(SelectionScreen.this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(consignmentdatamap, "Cons"));
                    consspinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    consignmentSpinner.setAdapter(consspinnerArrayAdapter);

                }else {

                    detailslyt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        consignmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (consignmentSpinner.getSelectedItemPosition() != 0) {
                    consignmentdetailslyt.setVisibility(View.VISIBLE);
                    Log.d("Selected1", consignmentSpinner.getSelectedItem().toString());
                    consignmentDetails = dataAccessHandler.getConsignmentDetails(Queries.getInstance().getConsignmentDetailsQuery(consignmentSpinner.getSelectedItem().toString()));
                    consignmentname.setText(consignmentDetails.get(0).getNurseryCode());
                    consignmentcode.setText(consignmentDetails.get(0).getConsignmentCode());
                   // consignmentpin.setText(consignmentDetails.get(0).getPinCode() + "");

                }else {

                    consignmentdetailslyt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

}
}