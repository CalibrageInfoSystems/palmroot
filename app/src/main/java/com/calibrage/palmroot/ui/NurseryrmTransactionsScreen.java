package com.calibrage.palmroot.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.NurseryRMTransctions;
import com.calibrage.palmroot.ui.Adapter.RMTransactionRecyclerViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NurseryrmTransactionsScreen extends AppCompatActivity {
    RecyclerView Transactionrcv;
    private DataAccessHandler dataAccessHandler;
    Button addBtn;
    String activityTypeId, activityName,  RmActivityId;

TextView activity_name;
    private List<NurseryRMTransctions> RmTransactionlist = new ArrayList<>();
    String   Fromdate,Todate;
    String last_30day;
    int Feild_id;
  RMTransactionRecyclerViewAdapter rmtransactionRecyclerViewAdapter;

    private EditText fromDateEdt, toDateEdt;
    private String fromDateStr = "";
    private String toDateStr = "";
    private Calendar myCalendar = Calendar.getInstance();
    private Button searchBtn;
    private String searchQuery = "";
    public static String SearchCollectionwithoutPlotQuery = "";
    DatePickerDialog picker;
    String currentDate, sendcurrentDate ,sendlastmonth;
    TextView nodata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurseryrm_transactions_screen_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Nursery R&M");
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();  // SETUP title For Activity
        if (extras != null) {
            try {
                activityName = extras.getString("RmActivityname");
                RmActivityId = extras.getString("RmActivityId");
                Log.d("activity_Name========>", activityName + "==="+ RmActivityId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initviews();
        setviews();
    }


    private void initviews() {
        dataAccessHandler = new DataAccessHandler(this);
        addBtn = findViewById(R.id.addBtn);
        activity_name = findViewById(R.id.activityname);
        activity_name.setText(activityName);
        Transactionrcv = findViewById(R.id.Transactionrcv);
        Transactionrcv.setHasFixedSize(true);
        Transactionrcv.setLayoutManager(new LinearLayoutManager(this));
        searchBtn = findViewById(R.id.searchBtn);
        fromDateEdt = (EditText) findViewById(R.id.fromDate);
        toDateEdt = (EditText) findViewById(R.id.toDate);
        nodata = findViewById( R.id.nodata);
    }

    private void setviews() {


       // multiplelist = dataAccessHandler.getMultipleDataDetails(Queries.getInstance().getMultiplerecordsDetailsQuery(consignmentcode, activityTypeId));

        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
   sendcurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
  Log.i("LOG_RESPONSE date ", currentDate);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);

        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        last_30day = format.format(date);
        sendlastmonth =  formate.format(date);
        Log.i("LOG_RESPONSE ===week", last_30day);
        fromDateEdt.setText(last_30day);
        toDateEdt.setText(currentDate);

        fromDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(NurseryrmTransactionsScreen.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromDateEdt.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });

        toDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(NurseryrmTransactionsScreen.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                toDateEdt.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });




        nurseryrmTransactions();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectionscreen = new Intent(NurseryrmTransactionsScreen.this, RMActivityFields.class);
                selectionscreen.putExtra("Name", activityName);
                selectionscreen.putExtra("camefrom",  1);
                selectionscreen.putExtra("transactionId",   RmActivityId);
                selectionscreen.putExtra("ActivityId",   RmActivityId);

                startActivity(selectionscreen);
            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromString = fromDateEdt.getText().toString().trim();
                String  toString = toDateEdt.getText().toString().trim();
                SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

                try {

                    Fromdate = myFormat.format(fromUser.parse(fromString));
                    Todate = myFormat.format(fromUser.parse(toString));
                    Log.d("collection", "RESPONSE reformattedStr======" + Fromdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                RmTransactionlist = dataAccessHandler.getNurseryrmTransactionsg(Queries.getInstance().getrmActivitttransaction(Fromdate,Todate,RmActivityId,CommonConstants.NurseryCode));

              //  irrigationloglist = dataAccessHandler.getirigationlogs(Queries.getInstance().getIrrigationStatus(Fromdate,Todate));

                if(RmTransactionlist.size() != 0){
                    Transactionrcv.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.GONE);
                    Transactionrcv.setLayoutManager(new LinearLayoutManager(NurseryrmTransactionsScreen.this));
                    rmtransactionRecyclerViewAdapter =    new RMTransactionRecyclerViewAdapter(NurseryrmTransactionsScreen.this, RmTransactionlist,activityName,RmActivityId);
                    Transactionrcv.setAdapter(rmtransactionRecyclerViewAdapter);
                }else{
                    Transactionrcv.setVisibility(View.GONE);
                    nodata.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void updateLabel(int type) {
        String myFormat = "dd-MM-yyyy";
        String dateFormatter = "yyyy-MM-dd";

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormatter, Locale.US);

        if (type == 0) {
            fromDateStr = sdf2.format(myCalendar.getTime());
            fromDateEdt.setText(sdf.format(myCalendar.getTime()));
        } else {
            toDateStr = sdf2.format(myCalendar.getTime());
            toDateEdt.setText(sdf.format(myCalendar.getTime()));
        }

    }

    private void nurseryrmTransactions() {
        Log.d("NurseryCodeeeee", CommonConstants.NurseryCode + "");
        RmTransactionlist = dataAccessHandler.getNurseryrmTransactionsg(Queries.getInstance().getrmActivitttransaction(sendlastmonth,sendcurrentDate,RmActivityId,CommonConstants.NurseryCode));
        if(RmTransactionlist.size() != 0){
            Transactionrcv.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.GONE);
            Transactionrcv.setLayoutManager(new LinearLayoutManager(NurseryrmTransactionsScreen.this));
            rmtransactionRecyclerViewAdapter =    new RMTransactionRecyclerViewAdapter(NurseryrmTransactionsScreen.this, RmTransactionlist,activityName,RmActivityId);
            Transactionrcv.setAdapter(rmtransactionRecyclerViewAdapter);
        }else{
            Transactionrcv.setVisibility(View.GONE);
            nodata.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        nurseryrmTransactions();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}