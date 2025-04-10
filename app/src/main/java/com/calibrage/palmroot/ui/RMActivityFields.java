package com.calibrage.palmroot.ui;

import static com.calibrage.palmroot.common.CommonUtils.REQUEST_CAM_PERMISSIONS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import com.calibrage.palmroot.R;

import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.dbmodels.RMTransactions;

import java.text.DecimalFormat;
import com.calibrage.palmroot.common.CommonUtils;

import com.calibrage.palmroot.uihelper.ProgressBar;
import com.calibrage.palmroot.utils.UiUtils;

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

public class RMActivityFields extends AppCompatActivity {
    public static final String LOG_TAG = RMActivityFields.class.getSimpleName();
    Spinner typespinner, uomSpinner;
    LinearLayout labourlyt, otherlyt, nameactivity;

    EditText mandaysmale, mandaysfemale, mandaysmaleoutside, mandaysfemaleoutside,cost;


    EditText expensetype, quantity, date,comment,nameofactivity,othercomments;


    ImageView imageView;
    Button submitBtn, cancelBtn;
    int Flag;
    String transactionId, Activity_Name, Nurseryname;
    TextView activity_name, nurseryname;
    DatePickerDialog picker;
    int labourcost = 10;
    String Remarks;
    String currentDate,sendcurrentDate,Userid,Date_history;

 //   TextView cost;

    private int GALLERY = 1, CAMERA = 2;

    String activityTypeId, uomId;
    String activityId ;
    String transactionid,Transactionid_New;
    String Sapcode;

    private DataAccessHandler dataAccessHandler;
    LinkedHashMap<String, Pair> activityTypeDataMap = null;
    LinkedHashMap<String, Pair> uomTypeDataMap = null;

    DecimalFormat precision = new DecimalFormat("0.00");

    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static String local_ImagePath = null;


    private List<RMTransactions>RMTransactionData = new ArrayList<>();
    double male_cost,Female_cost,male_cost_contract,Female_cost_contract;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rmfields);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Nursery R&M");
        setSupportActionBar(toolbar);
        init();
        setviews();
    }

    private void init() {

        dataAccessHandler = new DataAccessHandler(this);

        nameactivity = findViewById(R.id.nameactivity);
        nurseryname = findViewById(R.id.nurseryname);
        date = findViewById(R.id.rmdate);
        date.setEnabled(false);
        typespinner = findViewById(R.id.typeSpinner);
        uomSpinner = findViewById(R.id.uomspinner);

        labourlyt = findViewById(R.id.labourslyt);
        otherlyt = findViewById(R.id.otherslyt);

        mandaysmale = findViewById(R.id.mandaysmale);
        mandaysfemale = findViewById(R.id.mandaysfemale);
        mandaysmaleoutside = findViewById(R.id.mandaysmaleoutside);
        mandaysfemaleoutside = findViewById(R.id.mandaysfemaleoutside);
        nameofactivity = findViewById(R.id.nameofactivity);
        expensetype = findViewById(R.id.expensetype);
        quantity = findViewById(R.id.quantity);
        comment = findViewById(R.id.comments);
        imageView = findViewById(R.id.rmimageview);
        othercomments = findViewById(R.id.othercomments);

        activity_name = findViewById(R.id.activityname);
        cost = findViewById(R.id.cost);

        submitBtn = findViewById(R.id.rmsubmitBtn);
        cancelBtn = findViewById(R.id.rmcancelBtn);

    }

    private void setviews() {

        labourlyt.setVisibility(View.GONE);
        otherlyt.setVisibility(View.GONE);

        Log.d("Nurserynameeeee", CommonConstants.NurseryName + "");
        Log.d("NurseryCodeeeee", CommonConstants.NurseryCode + "");

        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        sendcurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.i("LOG_RESPONSE date ", currentDate + "========" + sendcurrentDate);
        date.setText(currentDate);
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RMActivityFields.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                sendcurrentDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                Log.i("LOG_RESPONSE date ", "========" + sendcurrentDate);
                            }
                        }, year, month, day);
                picker.show();
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });

        activityTypeDataMap = dataAccessHandler.getPairData(Queries.getInstance().getActivityTypeofRMQuery());
        ArrayAdapter<String> typeArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(activityTypeDataMap, "Type"));
        typeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typespinner.setAdapter(typeArrayAdapter);

        uomTypeDataMap = dataAccessHandler.getPairData(Queries.getInstance().getUOMTypeofRMQuery());
        ArrayAdapter<String> uomArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(uomTypeDataMap, "UOM"));
        uomArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uomSpinner.setAdapter(uomArrayAdapter);

        if (getIntent() != null) {

            Activity_Name = getIntent().getStringExtra("Name");
            Flag = getIntent().getIntExtra("camefrom", 1); // if Flag 2 , edit on rejection
            transactionId = getIntent().getStringExtra("transactionId");
            activityId = getIntent().getStringExtra("ActivityId");
            Log.d(LOG_TAG, "Name==========> :" + Activity_Name +"==="+activityId);
            Log.d(LOG_TAG, "Flag=====" + Flag);
            Log.d(LOG_TAG, "transactionId=====prev" + transactionId);

            RMTransactionData = dataAccessHandler.getRMTransactions(Queries.getInstance().getRmTransactiondata(transactionId));
            Log.v(LOG_TAG, "===202"+RMTransactionData.size()+"");
            if (Flag == 2) {
                nurseryname.setText(CommonConstants.NurseryName + "");
                activity_name.setText(Activity_Name + "");
                mandaysmale.setText(precision.format(RMTransactionData.get(0).getMaleRegular()));
                mandaysfemale.setText(precision.format(RMTransactionData.get(0).getFemaleRegular()));
                mandaysmaleoutside.setText(precision.format(RMTransactionData.get(0).getMaleOutside()));
                mandaysfemaleoutside.setText(precision.format(RMTransactionData.get(0).getFemaleOutside()));
                expensetype.setText(RMTransactionData.get(0).getExpenseType()+"");
                quantity.setText(precision.format(RMTransactionData.get(0).getQuantity()));
                if (RMTransactionData.get(0).getActivityTypeId() == 379) {
                    typespinner.setSelection(2);

                } else if (RMTransactionData.get(0).getActivityTypeId() == 378) {

                    typespinner.setSelection(1);

                }


                cost.setText(precision.format(RMTransactionData.get(0).getTotalCost()));

              int  Fstposition = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getlookupid(0));
                int  sstposition = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getlookupid(1));
                int  Tstposition = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getlookupid(2));
                if (RMTransactionData.get(0).getUOMId() == Fstposition) {
                    uomSpinner.setSelection(1);


                } else if (RMTransactionData.get(0).getUOMId() == sstposition) {

                    uomSpinner.setSelection(2);

                }
                else if (RMTransactionData.get(0).getUOMId() == Tstposition) {

                    uomSpinner.setSelection(3);

                }
                else {
                    uomSpinner.setSelection(4);

                }
                if (typespinner.getSelectedItemPosition() == 2) {

                    othercomments.setText(RMTransactionData.get(0).getComments() + "");

                }
                else{
                    comment.setText(RMTransactionData.get(0).getComments()+"");
                }
               local_ImagePath = RMTransactionData.get(0).getFileLocation();
                String filePath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "3F_Pictures/" + "RM_Transactions/"+RMTransactionData.get(0).getTransactionId()+".jpg";
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(bmp);
                if (Activity_Name.equalsIgnoreCase("Others")) {
                    nameactivity.setVisibility(View.VISIBLE);
                nameofactivity.setText((RMTransactionData.get(0).getActivityName()+""));
                } else {
                    nameactivity.setVisibility(View.GONE);
                }
                Log.d(LOG_TAG, "transactionId=====prev232" + transactionId);
                imageView.setEnabled(false);

            } else if (Flag == 3) {
                Log.v(LOG_TAG, "===220"+RMTransactionData.size()+"");
                Log.v(LOG_TAG, "===221"+RMTransactionData.get(0).getComments());
                othercomments.setText(RMTransactionData.get(0).getComments());
                comment.setText(RMTransactionData.get(0).getComments());
                nurseryname.setText(CommonConstants.NurseryName + "");
                activity_name.setText(Activity_Name + "");
                mandaysmale.setText(precision.format(RMTransactionData.get(0).getMaleRegular()));
                mandaysfemale.setText(precision.format(RMTransactionData.get(0).getFemaleRegular()));
                mandaysmaleoutside.setText(precision.format(RMTransactionData.get(0).getMaleOutside()));
                mandaysfemaleoutside.setText(precision.format(RMTransactionData.get(0).getFemaleOutside()));
                expensetype.setText(RMTransactionData.get(0).getExpenseType()+"");
                quantity.setText(precision.format(RMTransactionData.get(0).getQuantity()));

          if (RMTransactionData.get(0).getActivityTypeId() == 379) {
                    typespinner.setSelection(2);

                } else if (RMTransactionData.get(0).getActivityTypeId() == 378) {

                    typespinner.setSelection(1);

                }
                int  Fstposition = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getlookupid(0));
                int  sstposition = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getlookupid(1));
                int  Tstposition = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getlookupid(2));
                if (RMTransactionData.get(0).getUOMId() == Fstposition) {
                    uomSpinner.setSelection(1);


                } else if (RMTransactionData.get(0).getUOMId() == sstposition) {

                    uomSpinner.setSelection(2);

                } else if (RMTransactionData.get(0).getUOMId() == Tstposition) {

                    uomSpinner.setSelection(3);

                }
                else {
                    uomSpinner.setSelection(4);

                }
                if (typespinner.getSelectedItemPosition() == 2) {

                    othercomments.setText(RMTransactionData.get(0).getComments() + "");

                }
                else{
                    comment.setText(RMTransactionData.get(0).getComments()+"");


                }
                Log.e("=========>316", RMTransactionData.get(0).getFileName().length()+"");
if( RMTransactionData.get(0).getFileLocation().length() < 25){
                String filePath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "3F_Pictures/" + "RM_Transactions/"+RMTransactionData.get(0).getTransactionId()+".jpg";
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(bmp);}
else{
    Bitmap bitmap = BitmapFactory.decodeFile(RMTransactionData.get(0).getFileLocation());

    imageView.setImageBitmap(bitmap);
}

                submitBtn.setVisibility(View.GONE);

                cost.setText(precision.format(RMTransactionData.get(0).getTotalCost()));


                comment.setText(RMTransactionData.get(0).getComments()+"");
                othercomments.setText(RMTransactionData.get(0).getComments()+"");
                nameofactivity.setEnabled(false);
                date.setEnabled(false);
                typespinner.setEnabled(false);
                uomSpinner.setEnabled(false);
                expensetype.setEnabled(false);
                quantity.setEnabled(false);
                othercomments.setEnabled(false);
                comment.setEnabled(false);
                imageView.setEnabled(false);
                if (Activity_Name.equalsIgnoreCase("Others")) {
                    nameactivity.setVisibility(View.VISIBLE);
                    nameofactivity.setText((RMTransactionData.get(0).getActivityName()+""));
                } else {
                    nameactivity.setVisibility(View.GONE);
                }

                if (RMTransactionData.get(0).getActivityTypeId() == 379) {
                    typespinner.setSelection(2);

                } else if (RMTransactionData.get(0).getActivityTypeId() == 378) {

                    typespinner.setSelection(1);

                }

            } else {
                activity_name.setText(Activity_Name + "");
                nurseryname.setText(CommonConstants.NurseryName + "");
                if (Activity_Name.equalsIgnoreCase("Others")) {
                    nameactivity.setVisibility(View.VISIBLE);
                } else {
                    nameactivity.setVisibility(View.GONE);
                }

            }

        }

//        String[] typeSpinnerArr = getResources().getStringArray(R.array.typespin_values);
//        List<String> typeSpinnerList = Arrays.asList(typeSpinnerArr);
//        ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<>(RMActivityFields.this, android.R.layout.simple_spinner_item, typeSpinnerList);
//        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        typespinner.setAdapter(typeSpinnerAdapter);



        typespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (typespinner.getSelectedItemPosition() != 0) {

                    int selectedPos = typespinner.getSelectedItemPosition();
                    Log.d("selectedType", typespinner.getSelectedItem().toString());
                    Log.d("selectedTypeId", activityTypeDataMap.keySet().toArray(new String[activityTypeDataMap.size()])[selectedPos - 1]);
                    activityTypeId = activityTypeDataMap.keySet().toArray(new String[activityTypeDataMap.size()])[selectedPos - 1];


                }


                if (typespinner.getSelectedItemPosition() == 0) {

                    labourlyt.setVisibility(View.GONE);
                    otherlyt.setVisibility(View.GONE);
                    expensetype.setText("");
                    cost.setText("");
                    quantity.setText("");
                    othercomments.setText("");
                    uomSpinner.setSelection(0);
                    mandaysmale.setText("");
                    mandaysfemale.setText("");
                    mandaysmaleoutside.setText("");
                    mandaysfemaleoutside.setText("");
                    comment.setText("");

                } else if (typespinner.getSelectedItemPosition() == 1) {
                    labourlyt.setVisibility(View.VISIBLE);
                    otherlyt.setVisibility(View.GONE);
                    expensetype.setText("");
                    cost.setText("");
                    quantity.setText("");
                    othercomments.setText("");
                    uomSpinner.setSelection(0);
                    imageView.setImageResource(R.drawable.addimage);
                } else {
                    mandaysmale.setText("");
                    mandaysfemale.setText("");
                    mandaysmaleoutside.setText("");
                    mandaysfemaleoutside.setText("");
                    comment.setText("");
                    labourlyt.setVisibility(View.GONE);
                    otherlyt.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


//        String[] uomSpinnerArr = getResources().getStringArray(R.array.uom_values);
//        List<String> uomSpinnerList = Arrays.asList(uomSpinnerArr);
//        ArrayAdapter<String> uomSpinnerAdapter = new ArrayAdapter<>(RMActivityFields.this, android.R.layout.simple_spinner_item, uomSpinnerList);
//        uomSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        uomSpinner.setAdapter(uomSpinnerAdapter);



        uomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (uomSpinner.getSelectedItemPosition() != 0) {

                    int selectedPos = uomSpinner.getSelectedItemPosition();
                    Log.d("selecteduom", uomSpinner.getSelectedItem().toString());
                    Log.d("selecteduomId", uomTypeDataMap.keySet().toArray(new String[uomTypeDataMap.size()])[selectedPos - 1]);
                    uomId = uomTypeDataMap.keySet().toArray(new String[uomTypeDataMap.size()])[selectedPos - 1];

                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        imageView.setOnClickListener(v1 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(this, Manifest.permission.CAMERA))) {
              Log.v(LOG_TAG, "Camera Permissions Not Granted");
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_CAM_PERMISSIONS
                );
            } else {
                takePhotoFromCamera();
            }
        });


        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        Sapcode = dataAccessHandler.getSingleValue(Queries.getSapcode(CommonConstants.NurseryCode));
        Transactionid_New = "TRANRM"+ CommonConstants.TAB_ID + Sapcode + activityId + "-" + (dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getRMActivityMaxNumber(CommonConstants.NurseryCode, activityId)) + 1);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validations()) {
if(Flag == 2){
    UpdateRMTransactionsData(transactionId);
    local_ImagePath = null; }
else {
    saveRMTransactionsData(Transactionid_New);
    local_ImagePath = null;
}
                    Toast.makeText(RMActivityFields.this, "Submit Success", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RMActivityFields.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void UpdateRMTransactionsData(String transactionId ) {

        String male_reg = dataAccessHandler.getSingleValue(Queries.getregmalerate(CommonConstants.NurseryCode));
        String femmale_reg = dataAccessHandler.getSingleValue(Queries.getregfemalerate(CommonConstants.NurseryCode));
        String male_contract = dataAccessHandler.getSingleValue(Queries.getcontractmalerate(CommonConstants.NurseryCode));
        String female_contract = dataAccessHandler.getSingleValue(Queries.getcontractfemalerate(CommonConstants.NurseryCode));
        Userid =  dataAccessHandler.getSingleValue(Queries.getRMuserid(transactionId));
        Date_history =  dataAccessHandler.getSingleValue(Queries.getRMupdateddate(transactionId));
        RMTransactionData = dataAccessHandler.getRMTransactions(Queries.getInstance().getRmTransactiondata(transactionId));
         Remarks = dataAccessHandler.getSingleValue(Queries.getInstance().getRejectstring(transactionId));
Log.e("===========Remarks519",Remarks+"");
        if ((male_reg != null && !male_reg.isEmpty() && !male_reg.equals("null") && mandaysmale != null && !TextUtils.isEmpty(mandaysmale.getText()) && !mandaysmale.getText().equals("null"))){
            male_cost =(Double.parseDouble(male_reg)  * Double.parseDouble(mandaysmale.getText().toString().trim()));

        }
        if ((femmale_reg != null && !femmale_reg.isEmpty() && !femmale_reg.equals("null") && mandaysfemale != null && !TextUtils.isEmpty(mandaysfemale.getText()) && !mandaysfemale.getText().equals("null"))){
            Female_cost =Double.parseDouble(femmale_reg) * Double.parseDouble(mandaysfemale.getText().toString().trim()) ;
        }
        if ((male_contract != null && !male_contract.isEmpty() && !male_contract.equals("null") && mandaysmaleoutside != null && !TextUtils.isEmpty(mandaysmaleoutside.getText()) && !mandaysmaleoutside.getText().equals("null"))){
            male_cost_contract = Double.parseDouble(male_contract) * Double.parseDouble(mandaysmaleoutside.getText().toString().trim());
        }

        if ((female_contract != null && !female_contract.isEmpty() && !female_contract.equals("null") && mandaysfemaleoutside != null && !TextUtils.isEmpty(mandaysfemaleoutside.getText()) && !mandaysfemaleoutside.getText().equals("null"))){
            Female_cost_contract =(Double.parseDouble(female_contract)  * Double.parseDouble(mandaysfemaleoutside.getText().toString().trim()));
        }


        Double TotalCost =(male_cost+Female_cost+male_cost_contract+Female_cost_contract);
        Log.e("==========>537",TotalCost+"");
        //    int Activity_typeID =
        //String transactionid = "TRANRM"+ CommonConstants.TAB_ID + CommonConstants.NurseryCode + activityId + "-" + (dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getRMActivityMaxNumber(CommonConstants.NurseryCode, activityTypeId)) + 1);
        Log.d("TransactionId===update", transactionId);


        LinkedHashMap mapStatus = new LinkedHashMap();
        mapStatus.put("TransactionId",transactionId);

        mapStatus.put("NurseryCode",CommonConstants.NurseryCode);
        mapStatus.put("ActivityId",activityId);
        if (Activity_Name.equalsIgnoreCase("Others")) {
            mapStatus.put("ActivityName",nameofactivity.getText().toString());}
        mapStatus.put("ActivityTypeId",RMTransactionData.get(0).getActivityTypeId());
        mapStatus.put("StatusTypeId",346);
        mapStatus.put("TransactionDate",CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY));

        if(mandaysmale.getText().length() == 0){
            mapStatus.put("MaleRegular","");
        }else{
            mapStatus.put("MaleRegular",mandaysmale.getText());
        }

        if(mandaysfemale.getText().length() == 0){
            mapStatus.put("FemaleRegular","");
        }else{
            mapStatus.put("FemaleRegular",mandaysfemale.getText());
        }

        if(mandaysmaleoutside.getText().length() == 0){
            mapStatus.put("MaleOutside","");
        }else{
            mapStatus.put("MaleOutside",mandaysmaleoutside.getText());
        }

        if(mandaysfemaleoutside.getText().length() == 0){
            mapStatus.put("FemaleOutside","");
        }else{
            mapStatus.put("FemaleOutside",mandaysfemaleoutside.getText());
        }
        if ((male_reg != null && !male_reg.isEmpty() && !male_reg.equals("null"))){
            mapStatus.put("MaleRegularCost",Double.parseDouble(male_reg));}
        if ((femmale_reg != null && !femmale_reg.isEmpty() && !femmale_reg.equals("null"))){
            mapStatus.put("FemaleRegularCost",Double.parseDouble(femmale_reg));}
        if ((male_contract != null && !male_contract.isEmpty() && !male_contract.equals("null"))){
            mapStatus.put("MaleOutsideCost",male_contract);}
        if ((female_contract != null && !female_contract.isEmpty() && !female_contract.equals("null"))){
            mapStatus.put("FemaleoutsideCost",female_contract);}


        mapStatus.put("ExpenseType",expensetype.getText());

        if(uomSpinner.getSelectedItemPosition() == 0){
            mapStatus.put("UOMId","");
        }else{
            mapStatus.put("UOMId",Integer.parseInt(uomId));
        }

        if(quantity.getText().equals("")){
            mapStatus.put("Quatity","");
        }else{
            mapStatus.put("Quantity",quantity.getText());
        }

//        if (!TextUtils.isEmpty(cost.getText().toString())){
//            mapStatus.put("TotalCost",cost.getText());
//        }else{
//            mapStatus.put("TotalCost"," ");
//        }
//
//        if(typespinner.getSelectedItemPosition() == 1) {
//            mapStatus.put("Comments",comment.getText());
//        }
//
//        if(typespinner.getSelectedItemPosition() == 2) {
//            mapStatus.put("Comments",othercomments.getText());
//        }


        if(typespinner.getSelectedItemPosition() == 2){
            mapStatus.put("FileName","");
            mapStatus.put("FileLocation",local_ImagePath);
            mapStatus.put("FileExtension",".jpg");
            mapStatus.put("TotalCost",cost.getText()+"");
            mapStatus.put("Comments",othercomments.getText());
        }else {
            mapStatus.put("FileName","");
            mapStatus.put("FileLocation","");
            mapStatus.put("FileExtension","");
            mapStatus.put("TotalCost",TotalCost);
            mapStatus.put("Comments",comment.getText());
        }
        mapStatus.put("Remarks","");
        mapStatus.put("CreatedByUserId",CommonConstants.USER_ID);
        mapStatus.put("CreatedDate",CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        mapStatus.put("UpdatedByUserId",CommonConstants.USER_ID);
        mapStatus.put("UpdatedDate",CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        mapStatus.put("ServerUpdatedStatus",0);

        final List<LinkedHashMap> rmactivityarr = new ArrayList<>();
        rmactivityarr.add(mapStatus);



        String whereCondition ="where  TransactionId = '"+transactionId+"'  ";
        dataAccessHandler.updateData("RMTransactions",
                rmactivityarr,false, whereCondition,  new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {

//                            Toast.makeText(RMActivityFields.this, "Data Saved Successfully ", Toast.LENGTH_SHORT).show();
//                            Intent newIntent = new Intent(RMActivityFields.this, HomeActivity.class);
//                            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(newIntent);
                        }else{
                            Toast.makeText(RMActivityFields.this, "Data Saved Failed try again :" + msg, Toast.LENGTH_SHORT).show();
                        }
                    }


                });



    LinkedHashMap status = new LinkedHashMap();
                    status.put("TransactionId", transactionId);
                    status.put("StatusTypeId", 349);
                    status.put("CreatedByUserId", Userid);
                    status.put("CreatedDate", Date_history);
                    status.put("Remarks", Remarks);
                    status.put("ServerUpdatedStatus", 0);

    final List<LinkedHashMap> historyList = new ArrayList<>();
                    historyList.add(status);
                    dataAccessHandler.insertMyDataa("RMTransactionStatusHistory", historyList, new ApplicationThread.OnComplete<String>() {
        @Override
        public void execute(boolean success, String result, String msg) {
            if (success) {
                if (CommonUtils.isNetworkAvailable(RMActivityFields.this)) {


                    DataSyncHelper.performRefreshTransactionsSync(RMActivityFields.this, new ApplicationThread.OnComplete() {
                        @Override
                        public void execute(boolean success, Object result, String msg) {
                            if (success) {
                                ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RMActivityFields.this, "Successfully data sent to server", Toast.LENGTH_SHORT).show();
//
                                        finish();
                                    }
                                });
                            } else {
                                ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                    @Override
                                    public void run() {


                                        ProgressBar.hideProgressBar();
                                        //    Toast.makeText(ActivityTask.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                                        finish();

                                    }
                                });
                            }
                        }
                    });
                }
                finish();

          Log.d(ActivityTask.class.getSimpleName(), "==> RMTransactionStatusHistory INSERT COMPLETED");}
        }
    });

    }






    private void saveRMTransactionsData(String newtransactionId) {

        String male_reg = dataAccessHandler.getSingleValue(Queries.getregmalerate(CommonConstants.NurseryCode));
        String femmale_reg = dataAccessHandler.getSingleValue(Queries.getregfemalerate(CommonConstants.NurseryCode));
        String male_contract = dataAccessHandler.getSingleValue(Queries.getcontractmalerate(CommonConstants.NurseryCode));
        String female_contract = dataAccessHandler.getSingleValue(Queries.getcontractfemalerate(CommonConstants.NurseryCode));

        if ((male_reg != null && !male_reg.isEmpty() && !male_reg.equals("null") && mandaysmale != null && !TextUtils.isEmpty(mandaysmale.getText()) && !mandaysmale.getText().equals("null"))){
             male_cost =(Double.parseDouble(male_reg)  * Double.parseDouble(mandaysmale.getText().toString().trim()));

        }
        if ((femmale_reg != null && !femmale_reg.isEmpty() && !femmale_reg.equals("null") && mandaysfemale != null && !TextUtils.isEmpty(mandaysfemale.getText()) && !mandaysfemale.getText().equals("null"))){
             Female_cost =Double.parseDouble(femmale_reg) * Double.parseDouble(mandaysfemale.getText().toString().trim()) ;
        }
        if ((male_contract != null && !male_contract.isEmpty() && !male_contract.equals("null") && mandaysmaleoutside != null && !TextUtils.isEmpty(mandaysmaleoutside.getText()) && !mandaysmaleoutside.getText().equals("null"))){
             male_cost_contract = Double.parseDouble(male_contract) * Double.parseDouble(mandaysmaleoutside.getText().toString().trim());
        }

        if ((female_contract != null && !female_contract.isEmpty() && !female_contract.equals("null") && mandaysfemaleoutside != null && !TextUtils.isEmpty(mandaysfemaleoutside.getText()) && !mandaysfemaleoutside.getText().equals("null"))){
             Female_cost_contract =(Double.parseDouble(female_contract)  * Double.parseDouble(mandaysfemaleoutside.getText().toString().trim()));
        }


        Double TotalCost =(male_cost+Female_cost+male_cost_contract+Female_cost_contract);
Log.e("==========>733",TotalCost+"");
        Log.d("==labour count==", mandaysmale.getText()+"" + mandaysfemale.getText() +" ");

        //String transactionid = "TRANRM"+ CommonConstants.TAB_ID + CommonConstants.NurseryCode + activityId + "-" + (dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getRMActivityMaxNumber(CommonConstants.NurseryCode, activityTypeId)) + 1);
        Log.d("TransactionId======new", newtransactionId);


        LinkedHashMap mapStatus = new LinkedHashMap();
        mapStatus.put("TransactionId",newtransactionId);

        mapStatus.put("NurseryCode",CommonConstants.NurseryCode);
        mapStatus.put("ActivityId",activityId);
        if (Activity_Name.equalsIgnoreCase("Others")) {
            mapStatus.put("ActivityName",nameofactivity.getText().toString());}
        mapStatus.put("ActivityTypeId",Integer.parseInt(activityTypeId));
        mapStatus.put("StatusTypeId",346);
        mapStatus.put("TransactionDate",CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY));

        if(mandaysmale.getText().length() == 0){
            mapStatus.put("MaleRegular","");
        }else{
            mapStatus.put("MaleRegular",mandaysmale.getText()+"");
        }

        if(mandaysfemale.getText().length() == 0){
            mapStatus.put("FemaleRegular","");
        }else{
            mapStatus.put("FemaleRegular",mandaysfemale.getText()+"");
        }

        if(mandaysmaleoutside.getText().length() == 0){
            mapStatus.put("MaleOutside","");
        }else{
            mapStatus.put("MaleOutside",mandaysmaleoutside.getText());
        }

        if(mandaysfemaleoutside.getText().length() == 0){
            mapStatus.put("FemaleOutside","");
        }else{
            mapStatus.put("FemaleOutside",mandaysfemaleoutside.getText());
        }
        if ((male_reg != null && !male_reg.isEmpty() && !male_reg.equals("null"))){
        mapStatus.put("MaleRegularCost",Double.parseDouble(male_reg));}
        if ((femmale_reg != null && !femmale_reg.isEmpty() && !femmale_reg.equals("null"))){
        mapStatus.put("FemaleRegularCost",Double.parseDouble(femmale_reg));}
        if ((male_contract != null && !male_contract.isEmpty() && !male_contract.equals("null"))){
        mapStatus.put("MaleOutsideCost",male_contract);}
        if ((female_contract != null && !female_contract.isEmpty() && !female_contract.equals("null"))){
        mapStatus.put("FemaleoutsideCost",female_contract);}


        mapStatus.put("ExpenseType",expensetype.getText());

        if(uomSpinner.getSelectedItemPosition() == 0){
            mapStatus.put("UOMId","");
        }else{
            mapStatus.put("UOMId",Integer.parseInt(uomId));
        }

        if(quantity.getText().equals("")){
            mapStatus.put("Quatity","");
        }else{
            mapStatus.put("Quantity",quantity.getText());
        }





//        if (!TextUtils.isEmpty(cost.getText().toString())){
//            mapStatus.put("TotalCost",cost.getText());
//        }else{
//            mapStatus.put("TotalCost"," ");
//        }



        if(typespinner.getSelectedItemPosition() == 2){
            mapStatus.put("FileName","");
            mapStatus.put("FileLocation",local_ImagePath);
            mapStatus.put("FileExtension",".jpg");
            mapStatus.put("TotalCost",cost.getText()+"");
            mapStatus.put("Comments",othercomments.getText());
        }else {
            mapStatus.put("FileName","");
            mapStatus.put("FileLocation","");
            mapStatus.put("FileExtension","");
            mapStatus.put("TotalCost",TotalCost);
            mapStatus.put("Comments",comment.getText());
        }

        mapStatus.put("Remarks","");
        mapStatus.put("CreatedByUserId",CommonConstants.USER_ID);
        mapStatus.put("CreatedDate",CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        mapStatus.put("UpdatedByUserId",CommonConstants.USER_ID);
        mapStatus.put("UpdatedDate",CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        mapStatus.put("ServerUpdatedStatus",0);

        final List<LinkedHashMap> rmactivityarr = new ArrayList<>();
        rmactivityarr.add(mapStatus);

        dataAccessHandler.insertMyDataa("RMTransactions",
                rmactivityarr, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {

                        if (success) {

                            Toast.makeText(RMActivityFields.this, "Insert Successful", Toast.LENGTH_SHORT).show();
                            if (CommonUtils.isNetworkAvailable(RMActivityFields.this)) {


                                DataSyncHelper.performRefreshTransactionsSync(RMActivityFields.this, new ApplicationThread.OnComplete() {
                                    @Override
                                    public void execute(boolean success, Object result, String msg) {
                                        if (success) {
                                            ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(RMActivityFields.this, "Successfully data sent to server", Toast.LENGTH_SHORT).show();
//
                                                    finish();
                                                }
                                            });
                                        } else {
                                            ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                                @Override
                                                public void run() {


                                                    ProgressBar.hideProgressBar();
                                                    //    Toast.makeText(ActivityTask.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                                                    finish();

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                    });

    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    //Handling on Activity Result
    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

//        final int width = myBitmap.getWidth();
//        final int height = myBitmap.getHeight();
//        Bitmap portraitBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(portraitBitmap);
//        c.rotate(90, height/2, width/2);
//        c.drawBitmap(myBitmap, 0,0,null);

        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/3F_Pictures/" + "RM_Transactions");
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


    public boolean validations() {
        Log.d("TAG", "File Saved::--->2" + local_ImagePath);
        if (Activity_Name.equalsIgnoreCase("Others")) {

            if (TextUtils.isEmpty(nameofactivity.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Name of the Activity", RMActivityFields.this, 0);
                return false;
            }
        }

        if (typespinner.getSelectedItemPosition() == 0) {
            UiUtils.showCustomToastMessage("Please Select Type", RMActivityFields.this, 0);
            return false;
        }

        if (typespinner.getSelectedItemPosition() == 1) {
            expensetype.setText("");
            cost.setText("");
            quantity.setText("");
            othercomments.setText("");
            uomSpinner.setSelection(0);

            if(labourlyt.getVisibility()== View.VISIBLE){
                if (TextUtils.isEmpty(comment.getText().toString())){
                    UiUtils.showCustomToastMessage("Please Enter Comments", RMActivityFields.this, 0);
                    return false;
                }}

            if (mandaysmale.length() != 0 || mandaysfemale.length() != 0 || mandaysmaleoutside.length() != 0 || mandaysfemaleoutside.length() != 0) {
                //UiUtils.showCustomToastMessage("Please enter atleast one value", RMActivityFields.this, 0);
                return true;
            } else {

                UiUtils.showCustomToastMessage("Please enter atleast one value", RMActivityFields.this, 0);
                return false;
            }
        }

        if (typespinner.getSelectedItemPosition() == 2) {
            mandaysmale.setText("");
            mandaysfemale.setText("");
            mandaysmaleoutside.setText("");
            mandaysfemaleoutside.setText("");
            comment.setText("");


            if (TextUtils.isEmpty(expensetype.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Expense Type", RMActivityFields.this, 0);
                return false;
            }

            if (TextUtils.isEmpty(cost.getText().toString())){
                UiUtils.showCustomToastMessage("Please Enter Cost", RMActivityFields.this, 0);
                return false;
            }


            if(otherlyt.getVisibility()== View.VISIBLE){
            if (TextUtils.isEmpty(othercomments.getText().toString())){
                UiUtils.showCustomToastMessage("Please Enter Comments", RMActivityFields.this, 0);
                return false;
            }}



            if (local_ImagePath == null) {


                UiUtils.showCustomToastMessage("Please Capture Image", RMActivityFields.this, 0);
                return false;
            }

        }
        return true;
    }

}