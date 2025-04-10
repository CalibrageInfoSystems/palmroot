package com.calibrage.palmroot.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.ViewVisitLog;
import com.calibrage.palmroot.ui.Adapter.VisitLogRecyclerviewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewVisitlogs extends AppCompatActivity {

    private static final String LOG_TAG =  ViewVisitlogs.class.getSimpleName();
    private DataAccessHandler dataAccessHandler;

    private RecyclerView visitlogsRecyclerview;
    private List<ViewVisitLog> visitloglist = new ArrayList<>();

    private String NURCERYCODE,CONSINEMENTCODE;
    private VisitLogRecyclerviewAdapter visitlogRecyclerviewAdapter;
    String   Fromdate,Todate;
    EditText fromText, toText;
    DatePickerDialog picker;
    String currentDate,last_weekday,sendcurrentDate,sendweekdate;
    Button buttonSubmit;
    TextView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visitlogs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("View Visit Logs");
        setSupportActionBar(toolbar);
        initViews();
        setViews();
    }

    private void initViews() {
        dataAccessHandler = new DataAccessHandler(this);

        visitlogsRecyclerview = findViewById(R.id.visitlogsRecyclerview);

        fromText = (EditText) findViewById(R.id.from_date);
        toText = (EditText) findViewById(R.id.to_date);
        buttonSubmit =(Button)findViewById(R.id.buttonSubmit);
        nodata = findViewById( R.id.nodata);
    }
    private void setViews() {

        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        sendcurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        android.util.Log.i("LOG_RESPONSE date ", currentDate);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);

        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        last_weekday = format.format(date);
        sendweekdate =  formate.format(date);
        Log.i("LOG_RESPONSE ===week", last_weekday);
        fromText.setText(last_weekday);
        toText.setText(currentDate);

        fromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ViewVisitlogs.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });
        toText.setInputType(InputType.TYPE_NULL);
        toText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ViewVisitlogs.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                toText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });



        displayActivityData(); // Visits log data display

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromString = fromText.getText().toString().trim();
                String  toString = toText.getText().toString().trim();
                SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

                try {

                    Fromdate = myFormat.format(fromUser.parse(fromString));
                    Todate = myFormat.format(fromUser.parse(toString));
                    Log.d("collection", "RESPONSE reformattedStr======" + Fromdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                visitloglist = dataAccessHandler.getviewNurseryVisitLog(Queries.getInstance().getVisitLogdata(Fromdate,Todate));

                if(visitloglist.size() != 0){
                    visitlogsRecyclerview.setVisibility(View.VISIBLE);
                    nodata.setVisibility(View.GONE);
                    visitlogsRecyclerview.setLayoutManager(new LinearLayoutManager(ViewVisitlogs.this));
                    visitlogRecyclerviewAdapter = new VisitLogRecyclerviewAdapter(ViewVisitlogs.this, visitloglist);
                    visitlogsRecyclerview.setAdapter(visitlogRecyclerviewAdapter);
                }else{
                    visitlogsRecyclerview.setVisibility(View.GONE);
                    nodata.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void displayActivityData() {
        visitloglist = dataAccessHandler.getviewNurseryVisitLog(Queries.getInstance().getVisitLogdata(sendweekdate,sendcurrentDate));

        if(visitloglist.size() != 0){
            visitlogsRecyclerview.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.GONE);
            visitlogsRecyclerview.setLayoutManager(new LinearLayoutManager(ViewVisitlogs.this));
            visitlogRecyclerviewAdapter = new VisitLogRecyclerviewAdapter(ViewVisitlogs.this, visitloglist);
            visitlogsRecyclerview.setAdapter(visitlogRecyclerviewAdapter);
        }else{
            visitlogsRecyclerview.setVisibility(View.GONE);
            nodata.setVisibility(View.VISIBLE);
        }
    }
}
