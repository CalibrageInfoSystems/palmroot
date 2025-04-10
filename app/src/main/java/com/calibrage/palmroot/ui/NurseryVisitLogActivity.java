package com.calibrage.palmroot.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.dbmodels.ConsignmentData;
import com.calibrage.palmroot.dbmodels.NurseryDetails;
import com.calibrage.palmroot.uihelper.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static com.calibrage.palmroot.common.CommonUtils.REQUEST_CAM_PERMISSIONS;

public class  NurseryVisitLogActivity extends AppCompatActivity {
    public static final String LOG_TAG = NurseryVisitLogActivity.class.getSimpleName();
    LinkedHashMap<String, Pair> nurserydatamap = null;
  LinkedHashMap<String, Pair> typeofLogdatamap = null;
    private Spinner nurserySpinner,Consingmentspinner,logtypespin;
    private DataAccessHandler dataAccessHandler;
    Button submitBtn;
    String nursery_code, Consignment_code;
    List<NurseryDetails> nurseryDetails;
    List<ConsignmentData> consignmentList = new ArrayList<>();
    ArrayList<String> listdata ;
    Integer LogTypeId;
    String currentDate,sendcurrentDate;
    DatePickerDialog picker;
    EditText Clientname,LogDate, Comments;

    private int GALLERY = 1, CAMERA = 2;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    ImageView imageview;

    private Bitmap currentBitmap = null;
    private LinearLayout consignment_linear;
    public static  String  local_ImagePath  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery_visit_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Nursery Visit Log");
        setSupportActionBar(toolbar);
        initviews();
        setviews();
    }
    private void initviews() {
        dataAccessHandler = new DataAccessHandler(this);
        nurserySpinner = findViewById(R.id.nurserySpinner);
        submitBtn =  findViewById(R.id.submitBtn);
        logtypespin= findViewById(R.id.logtypespin);
        Consingmentspinner =findViewById(R.id.consignmentcode);
        Clientname= findViewById(R.id.clientname);
        LogDate= findViewById(R.id.LogDate);

        Comments = findViewById(R.id.comments);
        imageview = findViewById(R.id.imageview);
        consignment_linear = findViewById(R.id.consignment_linear);


    }

    private void setviews() {

        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        sendcurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.i("LOG_RESPONSE date ", currentDate+"========"+sendcurrentDate);
        LogDate.setText(currentDate);
        LogDate.setInputType(InputType.TYPE_NULL);
        LogDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(NurseryVisitLogActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                int month = monthOfYear + 1;
                                String formattedMonth = "" + month;
                                String formattedDayOfMonth = "" + dayOfMonth;
                                String formattedYear=String.valueOf(year);

                                if(month < 10){

                                    formattedMonth = "0" + month;
                                }
                                if(dayOfMonth < 10){

                                    formattedDayOfMonth = "0" + dayOfMonth;
                                }
                                LogDate.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);
                                sendcurrentDate = year + "-" + (formattedMonth ) + "-" + formattedDayOfMonth ;

                                Log.i("LOG_RESPONSE date ", "========"+sendcurrentDate);
                            }
                        }, year, month, day);
                picker.show();
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });
        nurserydatamap = dataAccessHandler.getPairData(Queries.getInstance().getNurseryMasterQuery());


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(nurserydatamap, "Nursery"));
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
                    consignmentdata();

            }}


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
       typeofLogdatamap = dataAccessHandler.getPairData(Queries.getInstance().getTypeofvisitLogQuery());
     //   typeofLogdatamap = dataAccessHandler.getGenericData(Queries.getInstance().getTypeofvisitLogQuery());
        ArrayAdapter<String> LogArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(typeofLogdatamap, "Log Type"));
        LogArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        logtypespin.setAdapter(LogArrayAdapter);

        logtypespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (logtypespin.getSelectedItemPosition() != 0) {
                    consignmentdata();
                    Log.d("Selected1", logtypespin.getSelectedItem().toString());
                   LogTypeId = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getlogtypeid(logtypespin.getSelectedItem().toString()));

                    Log.d("LogTypeId====", LogTypeId+"");
//                    nursery_code = nurseryDetails.get(0).getCode();
//                    Log.d("Selected1===nurserycode", nursery_code);
                    if(LogTypeId == 359){
                        consignment_linear.setVisibility(View.GONE);
                    }else{
                        consignment_linear.setVisibility(View.VISIBLE);
                    }


                } else {


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        imageview.setOnClickListener(v1 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(this, Manifest.permission.CAMERA))) {
                android.util.Log.v(LOG_TAG, "Camera Permissions Not Granted");
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_CAM_PERMISSIONS
                );
            } else {
                takePhotoFromCamera();
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "File Saved::--->302" + local_ImagePath);
                if (goValidate()){

                        LinkedHashMap mapStatus = new LinkedHashMap();
                        mapStatus.put("NurseryCode", nursery_code);
                    mapStatus.put("LogTypeId", LogTypeId);
                    if (Consignment_code != null) {
                        mapStatus.put("CosignmentCode", Consignment_code);
                    }else{
                        mapStatus.put("CosignmentCode", "");
                    }

                    mapStatus.put("ClientName", Clientname.getText().toString());
                    mapStatus.put("LogDate", sendcurrentDate);

                    mapStatus.put("Comments", Comments.getText().toString());
                    mapStatus.put("CreatedByUserId", CommonConstants.USER_ID);
                        mapStatus.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("ServerUpdatedStatus", 0);
                    if(local_ImagePath != null  ){
                        mapStatus.put("FileName ", "");
                    mapStatus.put("FileLocation", local_ImagePath);
                        mapStatus.put("FileExtension", ".jpg");}

                    else{
                        mapStatus.put("FileName ", "");
                        mapStatus.put("FileLocation", "");
                        mapStatus.put("FileExtension", "");
                    }



                        final List<LinkedHashMap> nurseryvisit_log = new ArrayList<>();
                        nurseryvisit_log.add(mapStatus);

                        Log.e("==============>",nurseryvisit_log+"");
                        dataAccessHandler.insertMyDataa("NurseryVisitLog", nurseryvisit_log, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                if (success) {
                                    if (CommonUtils.isNetworkAvailable(NurseryVisitLogActivity.this)) {


                                        DataSyncHelper.performRefreshTransactionsSync(NurseryVisitLogActivity.this, new ApplicationThread.OnComplete() {
                                            @Override
                                            public void execute(boolean success, Object result, String msg) {
                                                if (success) {
                                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(NurseryVisitLogActivity.this, "Successfully data sent to server", Toast.LENGTH_SHORT).show();
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
                                    local_ImagePath = null;
                                    finish();
                                    Log.d(ActivityTask.class.getSimpleName(), "==> NurseryVisitLog    INSERT COMPLETED");
                                }
                            }
                        });


                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please enter at least one value", Toast.LENGTH_SHORT).show();
                    }}
            });}

    private void consignmentdata() {
        listdata = new ArrayList<String>();
        listdata.clear();
        consignmentList = dataAccessHandler.getConsignmentcode(Queries.getInstance().getAllConsignment(CommonConstants.USER_ID, nursery_code));
        Log.d("consignmentList===>", consignmentList.size()+"");

        if (consignmentList.size() != 0) {

            listdata.add("-- Select Consignment --");

            for (int ii = 0; ii < consignmentList.size(); ii++) {

                listdata.add(consignmentList.get(ii).getConsignmentCode());
                Log.d("consignmentList==>size", listdata.size() + "");
                //  period_id.add(data.getTypeCdId());
            }

            ArrayAdapter consignmentAdapter = new ArrayAdapter(NurseryVisitLogActivity.this,android.R.layout.simple_spinner_item, listdata);
            consignmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Consingmentspinner.setAdapter(consignmentAdapter);

            Consingmentspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (Consingmentspinner.getSelectedItemPosition() != 0) {

                        Log.d("Consingmentspinner===>", Consingmentspinner.getSelectedItem().toString());
                        Consignment_code = Consingmentspinner.getSelectedItem().toString();
//                    nurseryDetails = dataAccessHandler.getNurseryDetails(Queries.getInstance().getNurseryDetailsQuery(logtypespin.getSelectedItem().toString()));
//                    nursery_code = nurseryDetails.get(0).getCode();
//                    Log.d("Selected1===nurserycode", nursery_code);


                    } else {

                        Consignment_code = null;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {

        }
    }


    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }




    //Handling on Activity Result
    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }



    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/3F_Pictures/" + "NurseryPhotos_visit");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->1" + f.getAbsolutePath());
            local_ImagePath = f.getAbsolutePath();
            Log.d("TAG", "File Saved::--->2" + local_ImagePath);
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }






    private boolean goValidate() {
      //  Log.e("=========>localmag499",local_ImagePath);
        if (nurserySpinner.getSelectedItemPosition() == 0){

            Toast.makeText(this, "Please Select Nursery", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (logtypespin.getSelectedItemPosition() == 0){

            Toast.makeText(this, "Please Select Log Type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(consignment_linear.getVisibility() == View.VISIBLE){
            if (Consingmentspinner.getSelectedItemPosition() == 0){

                Toast.makeText(this, "Please Select Consingment", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if ( Comments.getText().toString().equalsIgnoreCase("") || TextUtils.isEmpty(Comments.getText().toString())){

            Toast.makeText(this, "Please Enter Comments", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}