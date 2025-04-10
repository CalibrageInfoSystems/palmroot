package com.calibrage.palmroot.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.dbmodels.NurseryDetails;
import com.calibrage.palmroot.uihelper.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class NurseryLabourLogActivity extends AppCompatActivity {
    public static final String LOG_TAG = NurseryLabourLogActivity.class.getSimpleName();
    LinkedHashMap<String, Pair> nurserydatamap = null;
    private Spinner nurserySpinner;
    private DataAccessHandler dataAccessHandler;
    Button submitBtn;
    EditText edit_date;
    String currentDate,sendcurrentDate,nursery_code;
    DatePickerDialog picker;
    private EditText manregular_edt, femalereg_edt, manout_edt, femaleout_edt;
    List<NurseryDetails> nurseryDetails;
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery_labour_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Nursery Labour Log");
        setSupportActionBar(toolbar);
        initviews();
        setviews();
    }

    private void initviews() {
        dataAccessHandler = new DataAccessHandler(this);
        nurserySpinner = findViewById(R.id.nurseryspin);
        submitBtn =  findViewById(R.id.submitBtn);
        edit_date =  findViewById(R.id.date);
        manregular_edt = findViewById(R.id.manreg_edt);
        femalereg_edt = findViewById(R.id.Femalereg_edt);
        manout_edt = findViewById(R.id.manout_edt);
        femaleout_edt = findViewById(R.id.femaleout_edt);
    }

    private void setviews() {


        nurserydatamap = dataAccessHandler.getPairData(Queries.getInstance().getNurseryMasterQuery());


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(NurseryLabourLogActivity.this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(nurserydatamap, "Nursery"));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nurserySpinner.setAdapter(spinnerArrayAdapter);

        nurserySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (nurserySpinner.getSelectedItemPosition() != 0) {

                    Log.d("Selected1", nurserySpinner.getSelectedItem().toString());
                    nurseryDetails = dataAccessHandler.getNurseryDetails(Queries.getInstance().getNurseryDetailsQuery(nurserySpinner.getSelectedItem().toString()));
                    nursery_code = nurseryDetails.get(0).getCode();
                    Log.d("Selected1===nurserycode", nursery_code);


                }else {


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        sendcurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
     Log.i("LOG_RESPONSE date ", currentDate+"========"+sendcurrentDate);
        edit_date.setText(currentDate);
        edit_date.setInputType(InputType.TYPE_NULL);
        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(NurseryLabourLogActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                edit_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                sendcurrentDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth ;
                                Log.i("LOG_RESPONSE date ", "========"+sendcurrentDate);
                            }
                        }, year, month, day);
                picker.show();
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (goValidate()){
                if (manregular_edt.length() != 0 || femalereg_edt.length() != 0 ||
                        manout_edt.length() != 0 || femaleout_edt.length() != 0) {

                    LinkedHashMap mapStatus = new LinkedHashMap();

                    mapStatus.put("LogDate",sendcurrentDate);

                    if (manregular_edt.length() != 0) {
                        mapStatus.put("RegularMale", manregular_edt.getText().toString());
                    } else
                        mapStatus.put("RegularMale", "");

                    if (femalereg_edt.length() != 0) {
                        mapStatus.put("RegularFemale", femalereg_edt.getText().toString());
                    } else
                        mapStatus.put("RegularFemale", "");

                    if (manout_edt.length() != 0) {
                        mapStatus.put("ContractMale", manout_edt.getText().toString());
                    } else
                        mapStatus.put("ContractMale", "");

                    if (femaleout_edt.length() != 0) {
                        mapStatus.put("ContractFemale", femaleout_edt.getText().toString());
                    } else
                        mapStatus.put("ContractFemale", "");

                    mapStatus.put("IsActive", 1);
                    mapStatus.put("CreatedByUserId", CommonConstants.USER_ID);
                    mapStatus.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    mapStatus.put("UpdatedByUserId", CommonConstants.USER_ID);
                    mapStatus.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    mapStatus.put("ServerUpdatedStatus", 0);
                    mapStatus.put("NurseryCode", nursery_code);

                    final List<LinkedHashMap> nurserylabour_log = new ArrayList<>();
                    nurserylabour_log.add(mapStatus) ;

                    Log.e("==============>",nurserylabour_log+"");
                    dataAccessHandler.insertMyDataa("NurseryLabourLog", nurserylabour_log, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                if (CommonUtils.isNetworkAvailable(NurseryLabourLogActivity.this)) {


                                    DataSyncHelper.performRefreshTransactionsSync(NurseryLabourLogActivity.this, new ApplicationThread.OnComplete() {
                                        @Override
                                        public void execute(boolean success, Object result, String msg) {
                                            if (success) {
                                                ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(NurseryLabourLogActivity.this, "Successfully data sent to server", Toast.LENGTH_SHORT).show();
//
                                                        finish();
                                                    }
                                                });
                                            } else {
                                                ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        ProgressBar.hideProgressBar();

                                                        finish();

                                                    }
                                                });
                                            }
                                        }
                                    });
                                }


                               Log.d(ActivityTask.class.getSimpleName(), "==> NurseryLabourLog    INSERT COMPLETED");
                            }
                        }
                    });


                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter at least one value", Toast.LENGTH_SHORT).show();
                }}
            }});}

    private boolean goValidate() {
        if (nurserySpinner.getSelectedItemPosition() == 0){

            Toast.makeText(this, "Please Select Nursery", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.i("LOG_RESPONSE date ", "========"+sendcurrentDate);
        String count = dataAccessHandler.getCountValue(Queries.getInstance().Nurserylabourlogcount(sendcurrentDate,nursery_code));
        Log.i("count== ", "========"+Integer.parseInt(count) );
        if ( Integer.parseInt(count) != 0) {
            Toast.makeText(this, "Already Record Added Today", Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }
}