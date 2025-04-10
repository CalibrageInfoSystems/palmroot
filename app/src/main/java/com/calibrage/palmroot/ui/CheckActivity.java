package com.calibrage.palmroot.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.calibrage.palmroot.R;
import com.calibrage.palmroot.common.DateTimeUtil;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.CheckNurseryAcitivity;
import com.calibrage.palmroot.dbmodels.SaplingActivity;
import com.calibrage.palmroot.ui.Adapter.RecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.calibrage.palmroot.common.DateTimeUtil.stringTodate;

public class CheckActivity extends AppCompatActivity implements RecyclerAdapter.ClickListner {
    public static String TAG = CheckActivity.class.getSimpleName();
    private CompactCalendarView compactCalendarView;
    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    TextView emptyView;
    private TextView textView;
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private ActionBar toolbar;
    ImageButton showPreviousMonthBut,showNextMonthBut;
    final List<String> mutableBookings = new ArrayList<>();
    List<CheckNurseryAcitivity> saplingActivitiesList = new ArrayList<>();
    List<CheckNurseryAcitivity> saplingActivitiesdatesList = new ArrayList<>();
    DataAccessHandler dataAccessHandler;
    String updateddate;
    TextView date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        intviews();
        setview();
    }
    private void intviews() {

        dataAccessHandler = new DataAccessHandler(this);
        emptyView = findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.event_listview);
        date = findViewById(R.id.date);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        showPreviousMonthBut = findViewById(R.id.prev_button);
        showNextMonthBut = findViewById(R.id.next_button);
        textView = findViewById(R.id.month_title);

        compactCalendarView = findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(false);
//        loadEvents();
//        loadEventsForYear(2017);
        compactCalendarView.invalidate();


        logEventsByMonth(compactCalendarView, DateTimeUtil.onGetCurrentDate(this).substring(0, 7));  //  load data in  calendar

        toolbar = ((AppCompatActivity) this).getSupportActionBar();
        textView.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

    }



    private void setview() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        saplingActivitiesList = dataAccessHandler.getNurseryCheckActivityDetails(Queries.getInstance().getdata(currentDate));

        date.setText("Selected Date :  "+dateFormatForDisplaying.format(new Date()));
        Log.e("==>listsize",   saplingActivitiesList.size()+"" );
//
        if (saplingActivitiesList.size() > 0) {

            recyclerAdapter = new RecyclerAdapter(CheckActivity.this, saplingActivitiesList, CheckActivity.this);
            recyclerView.setAdapter(recyclerAdapter);
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() { //Click on date in Calendar view
            @Override
            public void onDayClick(Date dateClicked) {
                textView.setText(dateFormatForMonth.format(dateClicked));
                date.setText("Selected Date :  "+ dateFormatForDisplaying.format(dateClicked));
                Log.d(TAG, "OnDate Selected :" + DateTimeUtil.DateToString(dateClicked));
//

                saplingActivitiesList = dataAccessHandler.getNurseryCheckActivityDetails(Queries.getInstance().getdata( DateTimeUtil.DateToString(dateClicked)));
                Log.e("==>listsize",   saplingActivitiesList.size()+"" );
//
                if (saplingActivitiesList.size() > 0) {

                    recyclerAdapter = new RecyclerAdapter(CheckActivity.this, saplingActivitiesList, CheckActivity.this);
                    recyclerView.setAdapter(recyclerAdapter);
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }


            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                textView.setText(dateFormatForMonth.format(firstDayOfNewMonth));
                Log.d(TAG, "On Month Change :" + DateTimeUtil.DateToString(firstDayOfNewMonth));
                logEventsByMonth(compactCalendarView, DateTimeUtil.DateToString(firstDayOfNewMonth).substring(0, 7));
            }
        });


        showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollLeft();
            }
        });

        showNextMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollRight();
            }
        });

    }

    private void logEventsByMonth(CompactCalendarView compactCalendarView, String Date) {  // show dot in Calendar view dates


        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);

        String date = Date;
        Log.d(TAG, "final Month :" + date);

        saplingActivitiesdatesList = dataAccessHandler.getNurseryCheckActivityDetails(Queries.getInstance().getTargetdatesActivities());
        Log.e("==>listsize======202",   saplingActivitiesdatesList.size()+"" );
        for(int i = 0; i < saplingActivitiesdatesList.size(); i ++) {
//            //Event ev1 = new Event(Color.GREEN, stringTodate(data.getReqCreatedDate()).getTime(),"mallem");

            //      saplingActivitiesdatesList = dataAccessHandler.getNurseryCheckActivityDetails(Queries.getInstance().getTargetdatesActivities());

            updateddate = saplingActivitiesdatesList.get(i).getTargetDate();

            Log.d(TAG, "Each Event :" + updateddate);
            Event ev1 = new Event(Color.RED, stringTodate(updateddate).getTime(), "Test data"); // red color dots on date
            try {
                compactCalendarView.removeEvent(ev1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            compactCalendarView.addEvent(ev1);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        textView.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        // Set to current day on resume to set calendar to latest day
        textView.setText(dateFormatForMonth.format(new Date()));
    }

    @Override
    public void onNotificationClick(int po, SaplingActivity saplings) {

    }
}