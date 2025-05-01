package com.calibrage.palmroot.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.calibrage.palmroot.BuildConfig;

import com.calibrage.palmroot.R;
import com.google.android.material.textfield.TextInputLayout;

import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.dbmodels.ActivityTasks;
import com.calibrage.palmroot.dbmodels.CullinglossFileRepository;
import com.calibrage.palmroot.dbmodels.DisplayData;
import com.calibrage.palmroot.ui.Adapter.RVAdapter_ImageList;
import com.calibrage.palmroot.uihelper.ProgressBar;
import com.calibrage.palmroot.utils.ImageUtility;
import com.calibrage.palmroot.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static com.calibrage.palmroot.common.CommonUtils.REQUEST_CAM_PERMISSIONS;

public class ActivityTask extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, ImageClickListener, TextWatcher {
    private static final String LOG_TAG = ActivityTask.class.getName();
    String activityTypeId, consignmentCode, activityName, isMultipleentry, transactionIdFromMultiple;
    private List<ActivityTasks> activityTasklist = new ArrayList<>();
    private DataAccessHandler dataAccessHandler;
    LinkedHashMap<String, Pair> typeofLabourdatamap = null;
    List<KeyValues> dataValue = new ArrayList<>();
    int maxnumber;
    int Arrival_Sprouts, Received_sprouts;
    TextView textView5;

    private List<DisplayData> displayData = new ArrayList<>();
    boolean isUpdate = false;
    int activityStatus;
    int isjobDoneId = 0;
    int SCREEN_FROM = 0;
    int selectedPo;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_REQUEST2 = 1889;
    ActivityTasks showHideActivity;
    CheckBox chkShowHide;
    String Comments, Userid, Date_history;
    int yesnoCHeckbox = -10;
    int ButtonId = 100000001;
    int ImagId = 100000003;
    int rcvId = 100000002;
    int ImageId = 100000004;
    private String mCurrentPhotoPath, local_ImagePath;
    String errorMsg = "";
    String Code;
    ImageView image, FileImage;
    String Checked, Checked_new;
    int finalValue62, finalValue60;
    List<CullinglossFileRepository> imageRepo = new ArrayList<>();
    RVAdapter_ImageList adapter_imageList;
    private Bitmap currentBitmap = null;
    String imagepath;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    boolean enableEditing, Ismultipleentry;
    private List<String> multiplelist = new ArrayList<>();
    String transactionIdNew, transactionId;
    String intentTransactionId;
    int Arrivalsprouts;
    int value_edit, value2, value, Consignment_ID;
    String Sapcode, Nurserycode, Activity_ID;
    private List<ActivityTasks> groupedField = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                activityTypeId = extras.getString("ActivityTypeId");
                activityName = extras.getString("ActivityName");

                SCREEN_FROM = extras.getInt(CommonConstants.SCREEN_CAME_FROM);
                consignmentCode = extras.getString("consignmentcode");
//                dependency_code = extras.getString("DependentActivityCode");
                Code = extras.getString("Code");
                Log.e("=========>SCREEN_FROM", SCREEN_FROM + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // SETUP title For Activity
        textView5 = findViewById(R.id.textView5);
        textView5.setText(activityName);

        dataAccessHandler = new DataAccessHandler(this);
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout2);

        maxnumber = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getSaplingActivityMaxNumber(activityTypeId, consignmentCode));
        Log.d("maxnumber", maxnumber + "");

        activityTasklist = dataAccessHandler.getActivityTasksDetails(Queries.getInstance().getActivityTaskDetails(Integer.parseInt(activityTypeId)));
        CheckMantoryItem();
        createDynamicUI(ll);  // Create Dynamic Ui
        try {
            if (yesnoCHeckbox > 0) {
                CheckBox chk = findViewById(yesnoCHeckbox);
                if (chk != null) {
                    chk.setChecked(true);
                    checkBoxChecked(false); // default Check box check
                }
            }
        } catch (Exception exc) {

        }


        if (SCREEN_FROM == CommonConstants.FROM_MUTIPLE_ENTRY_EDITDATA) {
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> SCREEN CAME FROM :FROM_MUTIPLE_ENTRY_EDITDATA");
            // SCREEN CAME FROM UPDATE CURRENT SCREEN
            String consignmentcode = extras.getString("consignmentcode");
            intentTransactionId = extras.getString("transactionId");
            enableEditing = extras.getBoolean("enableEditing");
            Ismultipleentry = extras.getBoolean("multipleEntry");
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> FROM_MUTIPLE_ENTRY_EDITDATA  ###### transaction Id :" + intentTransactionId);
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> FROM_MUTIPLE_ENTRY_EDITDATA  ###### enableEditing :" + enableEditing);
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> FROM_MUTIPLE_ENTRY_EDITDATA  ###### Ismultipleentry :" + Ismultipleentry);
            Comments = dataAccessHandler.getOnlyOneValueFromDb(Queries.gethistory(consignmentCode, activityTypeId, intentTransactionId));
            Userid = dataAccessHandler.getOnlyOneValueFromDb(Queries.gethistoryuser(consignmentCode, activityTypeId, intentTransactionId));
            Date_history = dataAccessHandler.getOnlyOneValueFromDb(Queries.getDate(consignmentCode, activityTypeId, intentTransactionId));
            Log.d(ActivityTask.class.getSimpleName(), "==> Analysis Status History :" + Comments + Userid + Date_history);
            bindExistingData(intentTransactionId);

            imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(intentTransactionId));
            if (imageRepo.size() != 0) {

                addImageData(); //ToDO
            }

            Button btn = (Button) findViewById(ButtonId);
            ImageView img = (ImageView) findViewById(ImagId);
            if (enableEditing) {
                btn.setVisibility(View.VISIBLE);


            } else {
                // img.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
            }
            // TODO Bind DATA UsingTransactionID

        } else if (SCREEN_FROM == CommonConstants.FROM_MULTIPLE_ADD_NEW_TASK) {
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> SCREEN CAME FROM :FROM_MULTIPLE_ADD_NEW_TASK");
            String activityTypeId = extras.getString("ActivityTypeId");
            String consignmentcode = extras.getString("consignmentcode");
            Ismultipleentry = extras.getBoolean("Ismultipleentry");
            enableEditing = extras.getBoolean("enableEditing");
            // TODO Just Add New Task

        } else if (SCREEN_FROM == CommonConstants.FROM_SINGLE_ENTRY_EDITDATA) {
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> SCREEN CAME FROM :FROM_SINGLE_ENTRY_EDITDATA");
            String consignmentcode = extras.getString("consignmentcode");
            String activityTypeId = extras.getString("ActivityTypeId");
            Ismultipleentry = extras.getBoolean("multipleEntry");
            enableEditing = extras.getBoolean("enableEditing");

            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> FROM_SINGLE_ENTRY_EDITDATA  ###### enableEditing :" + enableEditing);
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> FROM_SINGLE_ENTRY_EDITDATA  ###### Ismultipleentry :" + Ismultipleentry);

            Button btn = (Button) findViewById(ButtonId);
            ImageView img = (ImageView) findViewById(ImagId);


            if (enableEditing) {
                multiplelist = dataAccessHandler.getMultipleDataDetails(Queries.getInstance().getMultiplerecordsDetailsQuery(consignmentcode, activityTypeId));


                btn.setVisibility(View.VISIBLE);
//            img.setVisibility(View.VISIBLE);

            } else {
                btn.setVisibility(View.GONE);
                //   img.setVisibility(View.GONE);
            }

            // TODO CHECK DATA EXIST OR NOT      IF EXIST BIND DATA


            Nurserycode = dataAccessHandler.getSingleValue(Queries.getnurserycode(consignmentcode));
            Sapcode = dataAccessHandler.getSingleValue(Queries.getSapcode(Nurserycode));
            Consignment_ID = dataAccessHandler.getSingleIntValue(Queries.getID(consignmentcode));
            Activity_ID = dataAccessHandler.getGenerateActivityid(activityTypeId);
            Log.d(ActivityTask.class.getSimpleName(), "==> analysis ==> Activity_ID  :" + Activity_ID);
            Log.e("Sapcode===================", Sapcode + "========>" + Nurserycode);
            transactionId = dataAccessHandler.getSingleValue(Queries.getInstance().getTransactionIdUsingConsimentCode(consignmentcode, activityTypeId));
            Log.e("transactionId===================", transactionId);
            // Sapcode,Nurserycode

            int statusTypeId;
            if (isjobDoneId != 0) {
                CheckBox chk = findViewById(isjobDoneId);
                if (chk.isChecked()) {
                    statusTypeId = 346;
                } else {
                    statusTypeId = 352;
                }
            } else {
                statusTypeId = 346;
            }
            if (null != transactionId && !transactionId.isEmpty() && !TextUtils.isEmpty(transactionId)) {
                bindExistingData(transactionId);
                //   updateSingleEntryData(consignmentcode, activityTypeId, transactionId, statusTypeId, enableEditing);
                imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(transactionId));
            } else {
                Log.d(ActivityTask.class.getSimpleName(), "==> Analysis  ==> New Task Creation Started ");
                transactionIdNew = "TRAN" + Sapcode + Consignment_ID + CommonConstants.TAB_ID + Activity_ID + "-" + (dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getSaplingActivityMaxNumber(activityTypeId, consignmentcode)) + 1);
                Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   New Transaction ID : 209" + transactionIdNew);
                imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(transactionIdNew));

            }
            Log.e("imageRepo===================", imageRepo.size() + "");
            //TRAN+NurserySAPCode(3)+ConsignmentId(4)+TabCode-Seq No(ActivityCount)
            if (imageRepo.size() != 0) {

                addImageData(); //ToDO
            }
        }
        if (Integer.parseInt(activityTypeId) == 1 || Integer.parseInt(activityTypeId) == 2 || Integer.parseInt(activityTypeId) == 4) {
            Button btn = (Button) findViewById(ButtonId);
            btn.setVisibility(View.GONE);
        }


    }

    private void bindExistingData(String transactionId) {
        displayData = dataAccessHandler.getdisplayDetails(Queries.getInstance().getDisplayData(transactionId));
        Log.d(ActivityTask.class.getSimpleName(), "==> Analysis Count Of DisplayData :" + displayData.size());


        for (int i = 0; i < displayData.size(); i++) {

            Log.d(ActivityTask.class.getSimpleName(), "==> Analysis name Of DisplayData :" + displayData.get(i).getInputType());
            if (displayData.get(i).getInputType().equalsIgnoreCase("File") || displayData.get(i).getInputType() == "File" || displayData.get(i).getInputType().contentEquals("File")) {
                Log.e("==============>702", activityTasklist.get(i).getInputType());
                String imagepath = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().localimagepath(transactionId, "NurseryImage"));
                Log.v(LOG_TAG, "imagepath ============" + imagepath);
                if (imagepath != null) {
                    ImageView File_Image = (ImageView) findViewById(ImageId);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagepath);

                    File_Image.setImageBitmap(bitmap);
                }
            } else if (displayData.get(i).getInputType().equalsIgnoreCase("Check box")) {
                CheckBox chk = (CheckBox) findViewById(displayData.get(i).getFieldId());
                if (displayData.get(i).getValue().equalsIgnoreCase("true")) {
                    chk.setChecked(true);
                } else
                    chk.setChecked(false);

                if (enableEditing) {

                    Checked = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().Checkboxdisable(displayData.get(i).getFieldId(), consignmentCode, activityTypeId));

                    Log.e("=================================>Checked", Checked + "");
                    if (Checked != null && Checked.equalsIgnoreCase("true")) {
                        chk.setEnabled(false);
                        checkBoxChecked(false);
                    } else {
                        chk.setEnabled(true);
                        checkBoxChecked(false);
                    }


                    if (displayData.get(i).getValue().equalsIgnoreCase("true")) {
                        chk.setEnabled(true);
                        checkBoxChecked(false);
                    }
                }

                try {

                    int Feild_id = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getRequiedFeildID(activityTypeId));
                    Log.e("=================================>Checked", Feild_id + "");
                    CheckBox chknew = (CheckBox) findViewById(Feild_id);
                    chknew.setEnabled(true);
                    int int1388 = Feild_id;
                    onClick(findViewById(int1388));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            else if (displayData.get(i).getInputType().equalsIgnoreCase("TextBox")) {
                EditText editText = (EditText) findViewById(displayData.get(i).getFieldId());
                Log.d(ActivityTask.class.getSimpleName(), "==> Analysis name Of DisplayData :" + displayData.get(i).getFieldId() + "=============" + displayData.get(i).getValue());
                //    editText.setText(displayData.get(i).getValue());
                if (!displayData.get(i).getValue().equalsIgnoreCase("null")) {
                    editText.setText(displayData.get(i).getValue());
                } else {
                    editText.setText("");
                }
            }

            else if (activityTasklist.get(i).getInputType().equalsIgnoreCase("TextBox") || activityTasklist.get(i).getInputType().equalsIgnoreCase("Label") || activityTasklist.get(i).getInputType().equalsIgnoreCase("Display") || activityTasklist.get(i).getInputType().equalsIgnoreCase("Formula") || activityTasklist.get(i).getInputType().equalsIgnoreCase("File")) {
                TextView textView = (TextView) findViewById(displayData.get(i).getFieldId());
                if (!TextUtils.isEmpty(displayData.get(i).getValue())) {
                    textView.setText(displayData.get(i).getValue());
                } else
                    textView.setText("");
            }
            else if (displayData.get(i).getInputType().equalsIgnoreCase("Dropdown") || displayData.get(i).getInputType().equalsIgnoreCase("dropdown")) {
                String value = displayData.get(i).getValue();
                int position = 0;
                String[] data = CommonUtils.arrayFromPair(typeofLabourdatamap, "Type of Labour");
                for (int j = 0; j < data.length; j++) {
                    if (value.equalsIgnoreCase(data[j])) {
                        position = j;
                    }
                }
                Spinner sp = (Spinner) findViewById(displayData.get(i).getFieldId());
                sp.setSelection(position);
            }

            //         else if (displayData.get(i).getInputType().equalsIgnoreCase("File")  ||displayData.get(i).getInputType() == "File" || displayData.get(i).getInputType().contentEquals("File")){
//                Log.e("==============>702", activityTasklist.get(i).getInputType());
//             String  imagepath=  dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().localimagepath(transactionId,"NurseryImage"));
//                Log.v(LOG_TAG, "imagepath ============" + imagepath);
//                if(imagepath!=null){
//                  ImageView  File_Image =  (ImageView) findViewById(ImageId);
//                    Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
//
//                    File_Image.setImageBitmap(bitmap);}
//              //  ll.addView(addFileimagebutton(activityTasklist.get(i).getId()));
//
//
//            }

            else {
                String value = displayData.get(i).getValue();

                TextView textView = (TextView) findViewById(displayData.get(i).getFieldId());
                if (!TextUtils.isEmpty(displayData.get(i).getValue())) {
                    textView.setText(displayData.get(i).getValue());
                }


//            }

            }
        }

    }


    private boolean goValidate() {
        Log.d("##############################", "YESNO CHECK VALUE :" + yesnoCHeckbox);

        return GroupValidate();
    }

    private void addNewSingleEntryActivity(String _consignmentCode, String _activityId, int _statusTypeId, String _transactionId, boolean isFromMultipleEntry) {

        String male_reg = dataAccessHandler.getSingleValue(Queries.getregmalerate(CommonConstants.NurseryCode));
        String femmale_reg = dataAccessHandler.getSingleValue(Queries.getregfemalerate(CommonConstants.NurseryCode));
        String male_contract = dataAccessHandler.getSingleValue(Queries.getcontractmalerate(CommonConstants.NurseryCode));
        String female_contract = dataAccessHandler.getSingleValue(Queries.getcontractfemalerate(CommonConstants.NurseryCode));
        String smallPolyBag = dataAccessHandler.getSingleValue(Queries.getsmallPolyBag(CommonConstants.NurseryCode));
        String bigPolyBag = dataAccessHandler.getSingleValue(Queries.getBigBag(CommonConstants.NurseryCode));
        String TractorHireCharges = dataAccessHandler.getSingleValue(Queries.getTractorHireCharges(CommonConstants.NurseryCode));
        String DieselCharges = dataAccessHandler.getSingleValue(Queries.DieselCharges(CommonConstants.NurseryCode));

        // DATA Validated next saving data locally
        final List<LinkedHashMap> listKey = new ArrayList<>();
        for (int j = 0; j < dataValue.size(); j++) {

            LinkedHashMap mapXref = new LinkedHashMap();
            mapXref.put("Id", 0);
            mapXref.put("TransactionId", _transactionId);
            Log.e("selectedPo================", selectedPo + "=========value" + dataValue.get(j).value);


            mapXref.put("FieldId", dataValue.get(j).id);
            mapXref.put("Value", dataValue.get(j).value);


            Log.e("=============>", local_ImagePath + "");
            if (dataValue.get(j).value == "NurseryImage" || dataValue.get(j).value.equalsIgnoreCase("NurseryImage")) {
                if (local_ImagePath != null) {

                    mapXref.put("FilePath", local_ImagePath);

                }
            } else {
                mapXref.put("FilePath", "");
            }

            mapXref.put("IsActive", 1);
            mapXref.put("CreatedByUserId", CommonConstants.USER_ID);
            mapXref.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            mapXref.put("UpdatedByUserId", CommonConstants.USER_ID);
            mapXref.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            mapXref.put("ServerUpdatedStatus", 0);
            int id = dataValue.get(j).id;

            Log.e("ids================", id + "=========value" + dataValue.get(j).id);

            if (id == 8 || id == 15 || id == 32 || id == 46 || id == 55 || id == 63 || id == 68 || id == 74 || id == 81 || id == 85 || id == 90 || id == 97 || id == 103 || id == 114 || id == 123 || id == 132 || id == 141 || id == 151 || id == 166 || id == 175 || id == 183 || id == 192 || id == 211 || id == 225 || id == 229 || id == 237 || id == 243 || id == 247 || id == 255 || id == 261 || id == 265 || id == 271 || id == 282 || id == 291 || id == 300 || id == 309 || id == 319 || id == 334 || id == 343 || id == 352 || id == 360 || id == 370 || id == 375 || id == 379 || id == 387 || id == 393 || id == 399 || id == 405 || id == 416 || id == 425 || id == 434 || id == 443 || id == 453 || id == 468 || id == 477 || id == 486 || id == 494 || id == 538 || id == 553 || id == 568 || id == 583 || id == 598 || id == 613 || id == 628 || id == 643 || id == 658 || id == 673 || id == 688 || id == 703 || id == 718 || id == 733 || id == 748 || id == 763 || id == 778 || id == 793 || id == 801 || id == 809 || id == 817 || id == 825 || id == 833 || id == 841 || id == 849 || id == 857 || id == 865 || id == 873 || id == 881 || id == 889 || id == 897 || id == 905 || id == 913 || id == 921 || id == 931 || id == 941 || id == 951 || id == 961 || id == 971 || id == 981 || id == 991 || id == 1001 || id == 1011 || id == 1021 || id == 1031 || id == 1041 || id == 1051 || id == 1061 || id == 1071 || id == 1081 || id == 1090 || id == 1099 || id == 1108 || id == 1117 || id == 1126 || id == 1135 || id == 1144 || id == 1153 || id == 1162 || id == 1171 || id == 1180 || id == 1189 || id == 1198 || id == 1207 || id == 1216 || id == 1225 || id == 1232 || id == 1238 || id == 1244 || id == 1251 || id == 1257 || id == 1263 || id == 1270 || id == 1276 || id == 1282 || id == 1289 || id == 1295 || id == 1301 || id == 1307 || id == 1313 || id == 1319 || id == 1325 || id == 1331 || id == 1337 || id == 1343 || id == 1349 || id == 1355 || id == 1361 || id == 1367 || id == 1373 || id == 1384 || id == 1395 || id == 1404 || id == 1413 || id == 1422 || id == 1431 || id == 1440 || id == 1449 || id == 1459 || id == 1472 || id == 1478 || id == 1484 || id == 1490 || id == 1496 || id == 1502 || id == 1508 || id == 1514 || id == 1520 || id == 1526 || id == 1532 || id == 1538 || id == 1544 || id == 1550 || id == 1556 || id == 1562 || id == 1568 || id == 1574 || id == 1580 || id == 1586 || id == 1592 || id == 1598 || id == 1604 || id == 1619 || id == 1634 || id == 1649 || id == 1664 || id == 1679 || id == 1694 || id == 1709 || id == 1724 || id == 1739 || id == 1754 || id == 1762 || id == 1770 || id == 1778 || id == 1786 || id == 1794 || id == 1802 || id == 1810 || id == 1818 || id == 1826 || id == 1834 || id == 1843 || id == 1852 || id == 1861 || id == 1870 || id == 1879 || id == 1888 || id == 1897 || id == 1906 || id == 1915 || id == 1924 || id == 1934 || id == 1944 || id == 1954 || id == 1964 || id == 1974 || id == 1984 || id == 1994 || id == 2004 || id == 2014 || id == 2024 || id == 2033 || id == 2042 || id == 2051 || id == 2060 || id == 2069 || id == 2078 || id == 2087 || id == 2096 || id == 2105 || id == 2114 || id == 2125 || id == 2136 || id == 2147 || id == 2158 || id == 2169 || id == 2180 || id == 2191 || id == 2202 || id == 2213 || id == 2224 || id == 2233 || id == 2242 || id == 2251 || id == 2260 || id == 2269 || id == 2278 || id == 2287 || id == 2296 || id == 2305 || id == 2314 || id == 2323 || id == 2332 || id == 2341 || id == 2350 || id == 2359 || id == 2368 || id == 2377 || id == 2386 || id == 2395 || id == 2404 || id == 2413 || id == 2422 || id == 2431 || id == 2440 || id == 2449 || id == 2458 || id == 2467 || id == 2476 || id == 2485 || id == 2494 || id == 2504 || id == 2514 || id == 2524 || id == 2534 || id == 2544 || id == 2554 || id == 2564 || id == 2574 || id == 2584 || id == 2594 || id == 2600 || id == 2606 || id == 2612 || id == 2623 || id == 2634 || id == 2645 || id == 2656 || id == 2665 || id == 2674 || id == 2683 || id == 2692 || id == 2701 || id == 2710 || id == 2719 || id == 2728 || id == 2737 || id == 2746 || id == 2755 || id == 2764 || id == 2774 || id == 2784 || id == 2794 || id == 2804 || id == 2819 || id == 2834 || id == 2849 || id == 2864 || id == 2872 || id == 2880 || id == 2888 || id == 2896 || id == 2905 || id == 2914 || id == 2923 || id == 2932 || id == 2942 || id == 2952 || id == 2962 || id == 2972 || id == 2981 || id == 2990 || id == 2999 || id == 3008 || id == 3014 || id == 3020 || id == 3026 || id == 3032 || id == 3038 || id == 3044 || id == 3050 || id == 3056 || id == 3064 || id == 3079) {

                if ((male_reg != null && !male_reg.isEmpty() && !male_reg.equals("null"))) {
                    mapXref.put("LabourRate", male_reg);
                }
            }


            if (id == 9 || id == 16 || id == 33 || id == 47 || id == 56 || id == 64 || id == 69 || id == 75 || id == 82 || id == 86 || id == 91 || id == 98 || id == 104 || id == 115 || id == 124 || id == 133 || id == 142 || id == 152 || id == 167 || id == 176 || id == 184 || id == 193 || id == 212 || id == 226 || id == 230 || id == 238 || id == 244 || id == 248 || id == 256 || id == 262 || id == 266 || id == 272 || id == 283 || id == 292 || id == 301 || id == 310 || id == 320 || id == 335 || id == 344 || id == 353 || id == 361 || id == 371 || id == 376 || id == 380 || id == 388 || id == 394 || id == 400 || id == 406 || id == 417 || id == 426 || id == 435 || id == 444 || id == 454 || id == 469 || id == 478 || id == 487 || id == 495 || id == 539 || id == 554 || id == 569 || id == 584 || id == 599 || id == 614 || id == 629 || id == 644 || id == 659 || id == 674 || id == 689 || id == 704 || id == 719 || id == 734 || id == 749 || id == 764 || id == 779 || id == 794 || id == 802 || id == 810 || id == 818 || id == 826 || id == 834 || id == 842 || id == 850 || id == 858 || id == 866 || id == 874 || id == 882 || id == 890 || id == 898 || id == 906 || id == 914 || id == 922 || id == 932 || id == 942 || id == 952 || id == 962 || id == 972 || id == 982 || id == 992 || id == 1002 || id == 1012 || id == 1022 || id == 1032 || id == 1042 || id == 1052 || id == 1062 || id == 1072 || id == 1082 || id == 1091 || id == 1100 || id == 1109 || id == 1118 || id == 1127 || id == 1136 || id == 1145 || id == 1154 || id == 1163 || id == 1172 || id == 1181 || id == 1190 || id == 1199 || id == 1208 || id == 1217 || id == 1226 || id == 1233 || id == 1239 || id == 1245 || id == 1252 || id == 1258 || id == 1264 || id == 1271 || id == 1277 || id == 1283 || id == 1290 || id == 1296 || id == 1302 || id == 1308 || id == 1314 || id == 1320 || id == 1326 || id == 1332 || id == 1338 || id == 1344 || id == 1350 || id == 1356 || id == 1362 || id == 1368 || id == 1374 || id == 1385 || id == 1396 || id == 1405 || id == 1414 || id == 1423 || id == 1432 || id == 1441 || id == 1450 || id == 1460 || id == 1473 || id == 1479 || id == 1485 || id == 1491 || id == 1497 || id == 1503 || id == 1509 || id == 1515 || id == 1521 || id == 1527 || id == 1533 || id == 1539 || id == 1545 || id == 1551 || id == 1557 || id == 1563 || id == 1569 || id == 1575 || id == 1581 || id == 1587 || id == 1593 || id == 1599 || id == 1605 || id == 1620 || id == 1635 || id == 1650 || id == 1665 || id == 1680 || id == 1695 || id == 1710 || id == 1725 || id == 1740 || id == 1755 || id == 1763 || id == 1771 || id == 1779 || id == 1787 || id == 1795 || id == 1803 || id == 1811 || id == 1819 || id == 1827 || id == 1835 || id == 1844 || id == 1853 || id == 1862 || id == 1871 || id == 1880 || id == 1889 || id == 1898 || id == 1907 || id == 1916 || id == 1925 || id == 1935 || id == 1945 || id == 1955 || id == 1965 || id == 1975 || id == 1985 || id == 1995 || id == 2005 || id == 2015 || id == 2025 || id == 2034 || id == 2043 || id == 2052 || id == 2061 || id == 2070 || id == 2079 || id == 2088 || id == 2097 || id == 2106 || id == 2115 || id == 2126 || id == 2137 || id == 2148 || id == 2159 || id == 2170 || id == 2181 || id == 2192 || id == 2203 || id == 2214 || id == 2225 || id == 2234 || id == 2243 || id == 2252 || id == 2261 || id == 2270 || id == 2279 || id == 2288 || id == 2297 || id == 2306 || id == 2315 || id == 2324 || id == 2333 || id == 2342 || id == 2351 || id == 2360 || id == 2369 || id == 2378 || id == 2387 || id == 2396 || id == 2405 || id == 2414 || id == 2423 || id == 2432 || id == 2441 || id == 2450 || id == 2459 || id == 2468 || id == 2477 || id == 2486 || id == 2495 || id == 2505 || id == 2515 || id == 2525 || id == 2535 || id == 2545 || id == 2555 || id == 2565 || id == 2575 || id == 2585 || id == 2595 || id == 2601 || id == 2607 || id == 2613 || id == 2624 || id == 2635 || id == 2646 || id == 2657 || id == 2666 || id == 2675 || id == 2684 || id == 2693 || id == 2702 || id == 2711 || id == 2720 || id == 2729 || id == 2738 || id == 2747 || id == 2756 || id == 2765 || id == 2775 || id == 2785 || id == 2795 || id == 2805 || id == 2820 || id == 2835 || id == 2850 || id == 2865 || id == 2873 || id == 2881 || id == 2889 || id == 2897 || id == 2906 || id == 2915 || id == 2924 || id == 2933 || id == 2943 || id == 2953 || id == 2963 || id == 2973 || id == 2982 || id == 2991 || id == 3000 || id == 3009 || id == 3015 || id == 3021 || id == 3027 || id == 3033 || id == 3039 || id == 3045 || id == 3051 || id == 3057 || id == 3065 || id == 3080) {
                if ((femmale_reg != null && !femmale_reg.isEmpty() && !femmale_reg.equals("null"))) {
                    mapXref.put("LabourRate", femmale_reg);
                }

            }
            if (id == 10 || id == 17 || id == 34 || id == 48 || id == 57 || id == 65 || id == 70 || id == 76 || id == 83 || id == 87 || id == 92 || id == 99 || id == 105 || id == 116 || id == 125 || id == 134 || id == 143 || id == 153 || id == 168 || id == 177 || id == 185 || id == 194 || id == 213 || id == 227 || id == 231 || id == 239 || id == 245 || id == 249 || id == 257 || id == 263 || id == 267 || id == 273 || id == 284 || id == 293 || id == 302 || id == 311 || id == 321 || id == 336 || id == 345 || id == 354 || id == 362 || id == 372 || id == 377 || id == 381 || id == 389 || id == 395 || id == 401 || id == 407 || id == 418 || id == 427 || id == 436 || id == 445 || id == 455 || id == 470 || id == 479 || id == 488 || id == 496 || id == 540 || id == 555 || id == 570 || id == 585 || id == 600 || id == 615 || id == 630 || id == 645 || id == 660 || id == 675 || id == 690 || id == 705 || id == 720 || id == 735 || id == 750 || id == 765 || id == 780 || id == 795 || id == 803 || id == 811 || id == 819 || id == 827 || id == 835 || id == 843 || id == 851 || id == 859 || id == 867 || id == 875 || id == 883 || id == 891 || id == 899 || id == 907 || id == 915 || id == 923 || id == 933 || id == 943 || id == 953 || id == 963 || id == 973 || id == 983 || id == 993 || id == 1003 || id == 1013 || id == 1023 || id == 1033 || id == 1043 || id == 1053 || id == 1063 || id == 1073 || id == 1083 || id == 1092 || id == 1101 || id == 1110 || id == 1119 || id == 1128 || id == 1137 || id == 1146 || id == 1155 || id == 1164 || id == 1173 || id == 1182 || id == 1191 || id == 1200 || id == 1209 || id == 1218 || id == 1227 || id == 1234 || id == 1240 || id == 1246 || id == 1253 || id == 1259 || id == 1265 || id == 1272 || id == 1278 || id == 1284 || id == 1291 || id == 1297 || id == 1303 || id == 1309 || id == 1315 || id == 1321 || id == 1327 || id == 1333 || id == 1339 || id == 1345 || id == 1351 || id == 1357 || id == 1363 || id == 1369 || id == 1375 || id == 1386 || id == 1397 || id == 1406 || id == 1415 || id == 1424 || id == 1433 || id == 1442 || id == 1451 || id == 1461 || id == 1474 || id == 1480 || id == 1486 || id == 1492 || id == 1498 || id == 1504 || id == 1510 || id == 1516 || id == 1522 || id == 1528 || id == 1534 || id == 1540 || id == 1546 || id == 1552 || id == 1558 || id == 1564 || id == 1570 || id == 1576 || id == 1582 || id == 1588 || id == 1594 || id == 1600 || id == 1606 || id == 1621 || id == 1636 || id == 1651 || id == 1666 || id == 1681 || id == 1696 || id == 1711 || id == 1726 || id == 1741 || id == 1756 || id == 1764 || id == 1772 || id == 1780 || id == 1788 || id == 1796 || id == 1804 || id == 1812 || id == 1820 || id == 1828 || id == 1836 || id == 1845 || id == 1854 || id == 1863 || id == 1872 || id == 1881 || id == 1890 || id == 1899 || id == 1908 || id == 1917 || id == 1926 || id == 1936 || id == 1946 || id == 1956 || id == 1966 || id == 1976 || id == 1986 || id == 1996 || id == 2006 || id == 2016 || id == 2026 || id == 2035 || id == 2044 || id == 2053 || id == 2062 || id == 2071 || id == 2080 || id == 2089 || id == 2098 || id == 2107 || id == 2116 || id == 2127 || id == 2138 || id == 2149 || id == 2160 || id == 2171 || id == 2182 || id == 2193 || id == 2204 || id == 2215 || id == 2226 || id == 2235 || id == 2244 || id == 2253 || id == 2262 || id == 2271 || id == 2280 || id == 2289 || id == 2298 || id == 2307 || id == 2316 || id == 2325 || id == 2334 || id == 2343 || id == 2352 || id == 2361 || id == 2370 || id == 2379 || id == 2388 || id == 2397 || id == 2406 || id == 2415 || id == 2424 || id == 2433 || id == 2442 || id == 2451 || id == 2460 || id == 2469 || id == 2478 || id == 2487 || id == 2496 || id == 2506 || id == 2516 || id == 2526 || id == 2536 || id == 2546 || id == 2556 || id == 2566 || id == 2576 || id == 2586 || id == 2596 || id == 2602 || id == 2608 || id == 2614 || id == 2625 || id == 2636 || id == 2647 || id == 2658 || id == 2667 || id == 2676 || id == 2685 || id == 2694 || id == 2703 || id == 2712 || id == 2721 || id == 2730 || id == 2739 || id == 2748 || id == 2757 || id == 2766 || id == 2776 || id == 2786 || id == 2796 || id == 2806 || id == 2821 || id == 2836 || id == 2851 || id == 2866 || id == 2874 || id == 2882 || id == 2890 || id == 2898 || id == 2907 || id == 2916 || id == 2925 || id == 2934 || id == 2944 || id == 2954 || id == 2964 || id == 2974 || id == 2983 || id == 2992 || id == 3001 || id == 3010 || id == 3016 || id == 3022 || id == 3028 || id == 3034 || id == 3040 || id == 3046 || id == 3052 || id == 3058 || id == 3066 || id == 3081) {
                if ((male_contract != null && !male_contract.isEmpty() && !male_contract.equals("null"))) {
                    mapXref.put("LabourRate", male_contract);
                }

            }
            if (id == 11 || id == 18 || id == 35 || id == 49 || id == 58 || id == 66 || id == 71 || id == 77 || id == 84 || id == 88 || id == 93 || id == 100 || id == 106 || id == 117 || id == 126 || id == 135 || id == 144 || id == 154 || id == 169 || id == 178 || id == 186 || id == 195 || id == 214 || id == 228 || id == 232 || id == 240 || id == 246 || id == 250 || id == 258 || id == 264 || id == 268 || id == 274 || id == 285 || id == 294 || id == 303 || id == 312 || id == 322 || id == 337 || id == 346 || id == 355 || id == 363 || id == 373 || id == 378 || id == 382 || id == 390 || id == 396 || id == 402 || id == 408 || id == 419 || id == 428 || id == 437 || id == 446 || id == 456 || id == 471 || id == 480 || id == 489 || id == 497 || id == 541 || id == 556 || id == 571 || id == 586 || id == 601 || id == 616 || id == 631 || id == 646 || id == 661 || id == 676 || id == 691 || id == 706 || id == 721 || id == 736 || id == 751 || id == 766 || id == 781 || id == 796 || id == 804 || id == 812 || id == 820 || id == 828 || id == 836 || id == 844 || id == 852 || id == 860 || id == 868 || id == 876 || id == 884 || id == 892 || id == 900 || id == 908 || id == 916 || id == 924 || id == 934 || id == 944 || id == 954 || id == 964 || id == 974 || id == 984 || id == 994 || id == 1004 || id == 1014 || id == 1024 || id == 1034 || id == 1044 || id == 1054 || id == 1064 || id == 1074 || id == 1084 || id == 1093 || id == 1102 || id == 1111 || id == 1120 || id == 1129 || id == 1138 || id == 1147 || id == 1156 || id == 1165 || id == 1174 || id == 1183 || id == 1192 || id == 1201 || id == 1210 || id == 1219 || id == 1228 || id == 1235 || id == 1241 || id == 1247 || id == 1254 || id == 1260 || id == 1266 || id == 1273 || id == 1279 || id == 1285 || id == 1292 || id == 1298 || id == 1304 || id == 1310 || id == 1316 || id == 1322 || id == 1328 || id == 1334 || id == 1346 || id == 1352 || id == 1358 || id == 1364 || id == 1370 || id == 1376 || id == 1387 || id == 1398 || id == 1407 || id == 1416 || id == 1425 || id == 1434 || id == 1443 || id == 1452 || id == 1462 || id == 1475 || id == 1481 || id == 1487 || id == 1493 || id == 1499 || id == 1505 || id == 1511 || id == 1517 || id == 1523 || id == 1529 || id == 1535 || id == 1541 || id == 1547 || id == 1553 || id == 1559 || id == 1565 || id == 1571 || id == 1577 || id == 1583 || id == 1589 || id == 1595 || id == 1601 || id == 1607 || id == 1622 || id == 1637 || id == 1652 || id == 1667 || id == 1682 || id == 1697 || id == 1712 || id == 1727 || id == 1742 || id == 1757 || id == 1765 || id == 1773 || id == 1781 || id == 1789 || id == 1797 || id == 1805 || id == 1813 || id == 1821 || id == 1829 || id == 1837 || id == 1846 || id == 1855 || id == 1864 || id == 1873 || id == 1882 || id == 1891 || id == 1900 || id == 1909 || id == 1918 || id == 1927 || id == 1937 || id == 1947 || id == 1957 || id == 1967 || id == 1977 || id == 1987 || id == 1997 || id == 2007 || id == 2017 || id == 2027 || id == 2036 || id == 2045 || id == 2054 || id == 2063 || id == 2072 || id == 2081 || id == 2090 || id == 2099 || id == 2108 || id == 2117 || id == 2128 || id == 2139 || id == 2150 || id == 2161 || id == 2172 || id == 2183 || id == 2194 || id == 2205 || id == 2216 || id == 2227 || id == 2236 || id == 2245 || id == 2254 || id == 2263 || id == 2272 || id == 2281 || id == 2290 || id == 2299 || id == 2308 || id == 2317 || id == 2326 || id == 2335 || id == 2344 || id == 2353 || id == 2362 || id == 2371 || id == 2380 || id == 2389 || id == 2398 || id == 2407 || id == 2416 || id == 2425 || id == 2434 || id == 2443 || id == 2452 || id == 2461 || id == 2470 || id == 2479 || id == 2488 || id == 2497 || id == 2507 || id == 2517 || id == 2527 || id == 2537 || id == 2547 || id == 2557 || id == 2567 || id == 2577 || id == 2587 || id == 2597 || id == 2603 || id == 2609 || id == 2615 || id == 2626 || id == 2637 || id == 2648 || id == 2659 || id == 2668 || id == 2677 || id == 2686 || id == 2695 || id == 2704 || id == 2713 || id == 2722 || id == 2731 || id == 2740 || id == 2749 || id == 2758 || id == 2767 || id == 2777 || id == 2787 || id == 2797 || id == 2807 || id == 2822 || id == 2837 || id == 2852 || id == 2867 || id == 2875 || id == 2883 || id == 2891 || id == 2899 || id == 2908 || id == 2917 || id == 2926 || id == 2935 || id == 2945 || id == 2955 || id == 2965 || id == 2975 || id == 2984 || id == 2993 || id == 3002 || id == 3011 || id == 3017 || id == 3023 || id == 3029 || id == 3035 || id == 3041 || id == 3047 || id == 3053 || id == 3059 || id == 3067 || id == 3082) {
                if ((female_contract != null && !female_contract.isEmpty() && !female_contract.equals("null"))) {
                    mapXref.put("LabourRate", female_contract);
                }

            }

            if (id == 21) {
                // Small poly Bag filling
                if ((smallPolyBag != null && !smallPolyBag.isEmpty() && !smallPolyBag.equals("null"))) {
                    mapXref.put("LabourRate", smallPolyBag);
                }

            }
            if (id == 200) {
                // Small poly Bag filling
                if ((bigPolyBag != null && !bigPolyBag.isEmpty() && !bigPolyBag.equals("null"))) {
                    mapXref.put("LabourRate", bigPolyBag);
                }

            }

            if (id == 12 || id == 196 || id == 242) {
                // 12,196,242 Tractor Hire Charges
                if ((TractorHireCharges != null && !TractorHireCharges.isEmpty() && !TractorHireCharges.equals("null"))) {
                    mapXref.put("LabourRate", TractorHireCharges);
                }

            }
            if (id == 13 || id == 197 || id == 241) {
                // 13,197,241 Diesel Charges for Tractor
                if ((DieselCharges != null && !DieselCharges.isEmpty() && !DieselCharges.equals("null"))) {
                    mapXref.put("LabourRate", DieselCharges);
                }

            }


            listKey.add(mapXref);

        }

        dataAccessHandler.insertMyDataa("SaplingActivityXref", listKey, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                Log.d(ActivityTask.class.getSimpleName(), "==>  Analysis ==> SaplingActivityXref INSERT COMPLETED");

                if (success) {
//
                    LinkedHashMap sapling = new LinkedHashMap();
                    sapling.put("TransactionId", _transactionId);
                    sapling.put("ConsignmentCode", _consignmentCode);
                    sapling.put("ActivityId", _activityId);
                    sapling.put("StatusTypeId", 346);  // TODO CHECK DB
                    sapling.put("Comment", "");
                    sapling.put("IsActive", 1);
                    sapling.put("CreatedByUserId", CommonConstants.USER_ID);
                    sapling.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    sapling.put("UpdatedByUserId", CommonConstants.USER_ID);
                    sapling.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    sapling.put("ServerUpdatedStatus", 0);
                    final List<LinkedHashMap> saplingList = new ArrayList<>();

                    saplingList.add(sapling);
                    dataAccessHandler.insertMyDataa("SaplingActivity", saplingList, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {

                                Log.d(ActivityTask.class.getSimpleName(), "==>  Analysis ==> SaplingActivity INSERT COMPLETED");
                                Log.d(ActivityTask.class.getSimpleName(), "==>  Analysis ==> Add new Task Completed");


                            }
                        }
                    });


                    // -------------------------
                    if (isFromMultipleEntry) {
                        // Came from Multiple entry then we can update Status only
                        LinkedHashMap status = new LinkedHashMap();

                        status.put("ConsignmentCode", _consignmentCode);
                        status.put("ActivityId", _activityId);
                        status.put("StatusTypeId", _statusTypeId);
                        status.put("CreatedByUserId", CommonConstants.USER_ID);
                        status.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        status.put("UpdatedByUserId", CommonConstants.USER_ID);
                        status.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        status.put("JobCompletedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        status.put("ServerUpdatedStatus", 0);

                        final List<LinkedHashMap> statusList = new ArrayList<>();
                        statusList.add(status);
                        dataAccessHandler.updateData("SaplingActivityStatus", statusList, true, " where ConsignmentCode = " + "'" + _consignmentCode + "' AND ActivityId ='" + _activityId + "'", new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                Log.d(ActivityTask.class.getSimpleName(), "==>  Analysis ==> SaplingActivityStatus INSERT COMPLETED");
                                Log.d(ActivityTask.class.getSimpleName(), "==>  Analysis ==> Update Task Completed");
                                if (success) {
                                    if (CommonUtils.isNetworkAvailable(ActivityTask.this)) {


                                        DataSyncHelper.performRefreshTransactionsSync(ActivityTask.this, new ApplicationThread.OnComplete() {
                                            @Override
                                            public void execute(boolean success, Object result, String msg) {
                                                if (success) {

                                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(ActivityTask.this, "Successfully data sent to server", Toast.LENGTH_SHORT).show();
                                                            finish();

                                                        }
                                                    });
                                                } else {
                                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            UiUtils.showCustomToastMessage("Data sync failed", ActivityTask.this, 1);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                    finish();
                                    Toast.makeText(ActivityTask.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    } else {
                        LinkedHashMap mapStatus = new LinkedHashMap();
                        mapStatus.put("Id", 0);
                        mapStatus.put("ConsignmentCode", _consignmentCode);
                        mapStatus.put("ActivityId", _activityId);
                        mapStatus.put("StatusTypeId", _statusTypeId);
                        mapStatus.put("CreatedByUserId", CommonConstants.USER_ID);
                        mapStatus.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("UpdatedByUserId", CommonConstants.USER_ID);
                        mapStatus.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("JobCompletedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("ServerUpdatedStatus", 0);

                        final List<LinkedHashMap> statusArray = new ArrayList<>();
                        statusArray.add(mapStatus);

                        dataAccessHandler.insertMyDataa("SaplingActivityStatus", statusArray, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                if (success) {
                                    if (CommonUtils.isNetworkAvailable(ActivityTask.this)) {


                                        DataSyncHelper.performRefreshTransactionsSync(ActivityTask.this, new ApplicationThread.OnComplete() {
                                            @Override
                                            public void execute(boolean success, Object result, String msg) {
                                                if (success) {

                                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(ActivityTask.this, "Successfully data sent to server", Toast.LENGTH_SHORT).show();
//
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            UiUtils.showCustomToastMessage("Data sync failed", ActivityTask.this, 1);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                    finish();
                                    Toast.makeText(ActivityTask.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                }
            }
        });


    }

    private void createDynamicUI(LinearLayout ll) {

        List<ActivityTasks> groupView = new ArrayList<>();

        for (int i = 0; i < activityTasklist.size(); i++) {


            if (activityTasklist.get(i).getInputType().equalsIgnoreCase("File")) {
                Log.e("==============>677", activityTasklist.get(i).getBucket());

                ll.addView(addTexView(activityTasklist.get(i).getField() + " * ", activityTasklist.get(i).getId())); // add  Texextview ui Dynamic}
            }

            if (activityTasklist.get(i).getInputType().equalsIgnoreCase("File") || activityTasklist.get(i).getInputType() == "File" || activityTasklist.get(i).getInputType().contentEquals("File")) {
                Log.e("==============>702", activityTasklist.get(i).getBucket());
                Log.e("==============>686", activityTasklist.get(i).getSortOrder() + "");
                int f = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getid(activityTasklist.get(i).getSortOrder()));
                ll.addView(addFileimagebutton(f));


            }

            if (activityTasklist.get(i).getInputType().equalsIgnoreCase("Check box")) {
                if (activityTasklist.get(i).getField().equalsIgnoreCase("Is the activity completed")) {
                    isjobDoneId = activityTasklist.get(i).getId();
                    ll.addView(addCheckbox
                            (activityTasklist.get(i).getField(), activityTasklist.get(i).getId())); // add checkbox ui Dynamic

                } else {
                    ll.addView(addCheckbox(activityTasklist.get(i).getField(), activityTasklist.get(i).getId()));
                }
            } else if (activityTasklist.get(i).getInputType().equalsIgnoreCase("TextBox") || activityTasklist.get(i).getInputType().equalsIgnoreCase("Label") || activityTasklist.get(i).getInputType().equalsIgnoreCase("Display") || activityTasklist.get(i).getInputType().equalsIgnoreCase("Formula")) {
                {
                    String UOm = activityTasklist.get(i).getUom().equalsIgnoreCase("null") ? "" : " (" + activityTasklist.get(i).getUom() + ")";
                    String isoptional = activityTasklist.get(i).getIsOptional() == 1 ? "" : " * ";
                    String content = activityTasklist.get(i).getField() + isoptional + UOm;
                    ll.addView(addEdittext(content, activityTasklist.get(i).getId(), activityTasklist.get(i).getDataType())); // add EditText ui Dynamic
                }
            } else if (activityTasklist.get(i).getInputType().equalsIgnoreCase("Label") || activityTasklist.get(i).getInputType().equalsIgnoreCase(".")) {
                ll.addView(addTexView(activityTasklist.get(i).getField(), activityTasklist.get(i).getId())); // add  Texextview ui Dynamic
            } else if (activityTasklist.get(i).getInputType().equalsIgnoreCase("Dropdown") || activityTasklist.get(i).getInputType().equalsIgnoreCase("dropdown")) {
                ll.addView(addSpinner(activityTasklist.get(i).getId())); // add Spinner ui Dynamic
            } else if (activityTasklist.get(i).getInputType().equalsIgnoreCase("Dropdown") || activityTasklist.get(i).getInputType().equalsIgnoreCase("dropdown")) {
                ll.addView(addSpinner(activityTasklist.get(i).getId())); // add Spinner ui Dynamic
            } else if (activityTasklist.get(i).getInputType().equalsIgnoreCase("TextBox with Camera / Attachment") || activityTasklist.get(i).getInputType().contains("TextBox with Camera / Attachment")) {
                String value = activityTasklist.get(i).getField();
                Log.e("==============>", value);
                ll.addView(addImageTexView(activityTasklist.get(i).getField(), activityTasklist.get(i).getId()));
                ll.addView(addRecyclerimageview());
                ll.addView(addimagebutton(activityTasklist.get(i).getId()));

                addImageData();
                ImageView img = (ImageView) findViewById(ImagId);
//                if (enableEditing){
//                    img.setVisibility(View.VISIBLE);
//
//                }
//                else{
//
//                    img.setVisibility(View.GONE);
//                }

            }


            // GetForeachGruoupItems


        }

        ll.addView(addButton("Submit", ButtonId));
    }

    private View addFileimagebutton(Integer id) {
        // Container with dashed border
        LinearLayout container = new LinearLayout(this);
        container.setId(id);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        container.setBackgroundResource(R.drawable.dashed_border);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200 // height
        );
        containerParams.setMargins(16, 16, 16, 16); // Optional margins
        container.setLayoutParams(containerParams);

        // ImageView inside container
        FileImage = new ImageView(this);
        FileImage.setId(ImageId);
        FileImage.setPadding(5, 5, 5, 10);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200);
        FileImage.setLayoutParams(lp);
        FileImage.setImageResource(R.drawable.addimage);


        FileImage.setOnClickListener(v1 -> {
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


      //  return FileImage;
        container.addView(FileImage);
        return container;
    }



/*    private View addFileimagebutton(Integer id) {


        FileImage = new ImageView(this);
        FileImage.setId(ImageId);
        FileImage.setPadding(5, 5, 5, 10);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200);
        FileImage.setLayoutParams(lp);
        FileImage.setImageResource(R.drawable.addimage);


        FileImage.setOnClickListener(v1 -> {
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


        return FileImage;

    }*/

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST2);
    }


    private View addImageTexView(String field, Integer id) {

        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setTextSize(20);
        tv.setText(field);

        return tv;

    }

    private View addimagebutton(Integer id) {

        image = new ImageView(this);
        image.setId(ImagId);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200);
        image.setLayoutParams(lp);
        Glide.with(this)
                .load(R.drawable.addimage)
                .override(200, 200)
                .into(image);


        image.setOnClickListener(v1 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(this, Manifest.permission.CAMERA))) {
                android.util.Log.v(LOG_TAG, "Camera Permissions Not Granted");
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_CAM_PERMISSIONS
                );
            } else {
                dispatchTakeFilePictureIntent(CAMERA_REQUEST, id);
            }
        });

        return image;

    }

    private View addRecyclerimageview() {


        RecyclerView rv = new RecyclerView(this);
        rv.setId(rcvId);
        RecyclerView.LayoutParams params = new
                RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
        rv.setLayoutParams(params);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(llm);
        rv.setVisibility(View.VISIBLE);

        return rv;
    }

    private void addImageData() {
        RecyclerView rcv = findViewById(rcvId);

        if (SCREEN_FROM == CommonConstants.FROM_SINGLE_ENTRY_EDITDATA) {
            Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   transactionId : 631" + transactionId);
            Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   New Transaction ID : 632" + transactionIdNew);
            if (null != transactionId && !transactionId.isEmpty() && !TextUtils.isEmpty(transactionId)) {


                ImageView img = (ImageView) findViewById(ImagId);
                if (enableEditing) {
                    img.setVisibility(View.VISIBLE);

                } else {
                    img.setVisibility(View.GONE);

                }
                imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(transactionId));
            } else {
                imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(transactionIdNew));

            }
        }
        adapter_imageList = new RVAdapter_ImageList(this, imageRepo, this);
        rcv.setAdapter(adapter_imageList);

    }


    private void dispatchTakeFilePictureIntent(int cameraRequest, Integer id) {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        switch (cameraRequest) {
            case CAMERA_REQUEST:
                File f = null;
                mCurrentPhotoPath = null;
                try {
                    f = setUpPhotoFile(id);
                    mCurrentPhotoPath = f.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider", f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                } catch (IOException e) {
                    android.util.Log.v(LOG_TAG, "IOException " + e.getMessage());
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }

                break;

            default:
                break;
        } // switch
        android.util.Log.v(LOG_TAG, "dispatchTakePictureIntent2 ");
        startActivityForResult(takePictureIntent, cameraRequest);
    }


    private File setUpPhotoFile(int id) throws IOException {

        File f = createImageFile(id);
        mCurrentPhotoPath = f.getAbsolutePath();
        //  Log.e("================>622", mCurrentPhotoPath);
        return f;
    }

    private File createImageFile(int ActivityId) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDirectory = new File(root + "/3F_Pictures");
        File pictureDirectory = new File(root + "/3F_Pictures/" + "NurseryPhotos");

        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }

        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }

        File finalFile = new File(pictureDirectory, Calendar.getInstance().getTimeInMillis() + CommonConstants.JPEG_FILE_SUFFIX);
        return finalFile;
    }


    private boolean GroupValidate() {
        // TOdo comment Testing
//


        if (Ismultipleentry) {
            if (null != intentTransactionId && !intentTransactionId.isEmpty() && !TextUtils.isEmpty(intentTransactionId)) {
                imagepath = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().localimagepath(intentTransactionId, "NurseryImage"));
                Log.v(LOG_TAG, "imagepath ============" + imagepath);

            } else {
                imagepath = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().localimagepath(transactionIdNew, "NurseryImage"));

            }
        } else {
            imageRepo = new ArrayList<>();

            if (null != transactionId && !transactionId.isEmpty() && !TextUtils.isEmpty(transactionId)) {
                imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(transactionId));
                imagepath = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().localimagepath(transactionId, "NurseryImage"));
                Log.v(LOG_TAG, "imagepath ============" + imagepath);
            } else {
                imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(transactionIdNew));
                imagepath = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().localimagepath(transactionIdNew, "NurseryImage"));
            }
        }
        Log.e("transactionId===================exsit913", intentTransactionId + "single ==" + transactionId);
        Log.e("transactionId===================New914", transactionIdNew);
        Log.v(LOG_TAG, "transactionId===================imagepath915" + imagepath);

        dataValue = new ArrayList<>();

        for (int i = 0; i < activityTasklist.size(); i++) {
            int id = activityTasklist.get(i).getId();
            if (activityTasklist.get(i).getInputType().equalsIgnoreCase("Check box")) {

                CheckBox chk = (CheckBox) findViewById(id);
                Log.d("TESTING", "IS CHECKED  " + chk.isChecked() + "id===============" + activityTasklist.get(i).getId());
                dataValue.add(new KeyValues(activityTasklist.get(i).getId(), chk.isChecked() + ""));


                if (enableEditing) {
                    if (Ismultipleentry) {
                        int Field_complete = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getFeildID(activityTypeId));

                        Checked_new = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().Checkboxdisablevalidation(Field_complete, consignmentCode, activityTypeId, intentTransactionId));
                        CheckBox chk_new = (CheckBox) findViewById(Field_complete);
                        Log.e("=================================>Checked_new", Checked_new + "");
                        if (Checked_new != null && Checked_new.equalsIgnoreCase("true")) {
                            if (chk_new.isChecked()) {
                                Toast.makeText(this, "Is the activity completed is done  For this  Activity", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                        }
                    }
                }

            }
//            if (findViewById(id).getVisibility() == View.VISIBLE && activityTasklist.get(i).getInputType().equalsIgnoreCase("TextBox")) {
            if (activityTasklist.get(i).getInputType().equalsIgnoreCase("TextBox") || activityTasklist.get(i).getInputType().equalsIgnoreCase("Label") ||
                    activityTasklist.get(i).getInputType().equalsIgnoreCase("Display") || activityTasklist.get(i).getInputType().equalsIgnoreCase("Formula")
            ) {

                EditText et = findViewById(id);


                dataValue.add(new KeyValues(activityTasklist.get(i).getId(), et.getText() + ""));


                Log.d("Groupvalidation ", " Field Id :" + activityTasklist.get(i).getField() + "IS OPTIONAL :" + activityTasklist.get(i).getIsOptional());
                if (findViewById(id).getVisibility() == View.VISIBLE && activityTasklist.get(i).getIsOptional() == 0 && activityTasklist.get(i).getGroupId() == 0 && TextUtils.isEmpty(et.getText().toString())) {
                    //TOdo  need to check already exist or not
                    Toast.makeText(this, "Please Enter  " + activityTasklist.get(i).getField(), Toast.LENGTH_SHORT).show();
                    return false;
                }

            }

            if (activityTasklist.get(i).getInputType().equalsIgnoreCase("Dropdown") || activityTasklist.get(i).getInputType().equalsIgnoreCase("dropdown")) {

                Spinner spinnner = findViewById(id);
                int selectedPo = spinnner.getSelectedItemPosition();
                dataValue.add(new KeyValues(activityTasklist.get(i).getId(), spinnner.getSelectedItem().toString()));
                Log.d(ActivityTask.class.getSimpleName(), "DropDownn Selected String :" + spinnner.getSelectedItem().toString());
                if (spinnner.getSelectedItemPosition() == 0) {
                    //TOdo  need to check already exist or not

                    Toast.makeText(this, "Please Select " + activityTasklist.get(i).getField(), Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
            if (activityTasklist.get(i).getInputType().equalsIgnoreCase("File")) {
                if (FileImage.getVisibility() == View.VISIBLE) {

                    Log.v(LOG_TAG, "transactionId===================imagepath" + imagepath);
                    if (local_ImagePath != null || imagepath != null) {

                        dataValue.add(new KeyValues(activityTasklist.get(i).getId(), "NurseryImage"));

                    } else {
                        Toast.makeText(this, "Please  Add Image ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            if (activityTasklist.get(i).getActivityTypeId() == 12) {

                int int52 = 52, int51 = 51, int53 = 53, int54 = 54;
                ;
                EditText edt53 = findViewById(int53);
                int lossValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int51))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int52)));

                edt53.setText(lossValue + "");

                EditText edt54 = findViewById(int54);
                int Sowing_value = CommonUtils.getIntFromEditText(((EditText) findViewById(int51))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int53)));
                edt54.setText(Sowing_value + "");
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int51))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int52)));
                if (finalValue < 0) {
                    Toast.makeText(this, "Please  Enter Total No of Healthy Sprouts Less than or equal to  Total received Sprouts  ", Toast.LENGTH_SHORT).show();
                    return false;
                }


                if (enableEditing) {

                    int count_edit = 0;
                    try {
                        count_edit = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowingEdit(consignmentCode, 51, intentTransactionId)));
                    } catch (Exception exception) {
                        exception.printStackTrace();

                    }

                    Log.e("============>arrival Sprout 821", Arrival_Sprouts + "");
                    int Received_sprouts_edit = count_edit + CommonUtils.getIntFromEditText(((EditText) findViewById(int51)));

                    Log.e("============>Received Sprout 855", Received_sprouts_edit + "");
                    value_edit = Arrival_Sprouts - (count_edit + CommonUtils.getIntFromEditText(((EditText) findViewById(int51))));

                    Log.e("============>arrival Sprout 824", value_edit + "");

                    if (value_edit < 0) {
                        Toast.makeText(this, "Please  Enter  Total received Sprouts  Less than or equal to Sprouts arrived Count (PN-Arrival Of Sprouts)", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int LossCheckboxx = 59;
                    CheckBox chkk = findViewById(LossCheckboxx);


                    if (chkk.isChecked()) {

                        if (value_edit != 0) {
                            chkk.setChecked(false);
                            Toast.makeText(this, "Please  Enter  Total received Sprouts  Less than or equal to Sprouts arrived Count (PN-Arrival Of Sprouts)", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                    }

                } else {
                    try {


                        int count = 0;
                        try {
                            count = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowing(consignmentCode, 51)));
                            Log.e("============>count", count + "");
                        } catch (Exception exception) {
                            exception.printStackTrace();

                        }

                        Log.e("============>arrival Sprout 821", Arrival_Sprouts + "");
                        Received_sprouts = count + CommonUtils.getIntFromEditText(((EditText) findViewById(int51)));

                        Log.e("============>Received Sprout 855", Received_sprouts + "");
                        value2 = Arrival_Sprouts - Received_sprouts;

                        Log.e("============>arrival Sprout 824", value2 + "");
                        if (value2 < 0) {
                            Toast.makeText(this, "Please  Enter  Total received Sprouts  Less than or  equal to Sprouts arrived Count (PN-Arrival Of Sprouts)", Toast.LENGTH_SHORT).show();
                            return false;
                        }


                        int LossCheckboxx = 59;
                        CheckBox chkk = findViewById(LossCheckboxx);


                        if (chkk.isChecked()) {

                            if (value2 != 0) {
                                chkk.setChecked(false);
                                Toast.makeText(this, "Please  Enter PN-Arrival Of Sprouts Equal to  Total received Sprouts ", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            if (activityTasklist.get(i).getActivityTypeId() == 13) {
                try {
                    int int60 = 60, int61 = 61, int62 = 62;
                    EditText edt62 = findViewById(int62);
                    try {
                        int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int60))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int61)));

                        edt62.setText(finalValue + "");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (enableEditing) {

                        int value_61 = 0;
                        try {
                            value_61 = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowingEdit(consignmentCode, 61, intentTransactionId)));
                            int finalValue = finalValue60 - value_61 - CommonUtils.getIntFromEditText(((EditText) findViewById(int61)));
                            edt62.setText(finalValue + "");
                        } catch (Exception exception) {
                            exception.printStackTrace();

                        }


                    } else {

                        int value_61 = 0;
                        try {
                            value_61 = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowing(consignmentCode, 61)));
                            int finalValue = finalValue60 - value_61 - CommonUtils.getIntFromEditText(((EditText) findViewById(int61)));
                            edt62.setText(finalValue + "");
                        } catch (Exception exception) {
                            exception.printStackTrace();

                        }


                    }

                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int60))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int61)));
                    Log.d("TESTING  finalValue", +finalValue + "");

                    //   dataValue.add(new KeyValues(int62, finalValue + ""));

                    if (finalValue < 0) {
                        Toast.makeText(this, "Please  Enter No of Sprouts Sown Less than or equal to  Sprouts to sown =  (sprouts for Sowing in Sprout Counting ) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    if ((CommonUtils.getIntFromEditText(((EditText) findViewById(int62))) < 0)) {
                        Toast.makeText(this, "Balance to sown Should be Zero or Positive value only ", Toast.LENGTH_SHORT).show();
                        return false;

                    }
                    int LossCheckbox = 67;
                    CheckBox chk = findViewById(LossCheckbox);


                    if (chk.isChecked()) {
                        Integer statuscode = dataAccessHandler.getSingleIntValue(Queries.SporoutsCountstatus(consignmentCode, 12));
                        if (statuscode == 352 || statuscode == 349) {
                            chk.setChecked(false);
                            Toast.makeText(this, "Please Complete PN-Sprout Count and Transit Loss ", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (CommonUtils.getIntFromEditText(((EditText) findViewById(int62))) != 0) {
                            chk.setChecked(false);
                            Toast.makeText(this, "Balance to sown Should be Zero while Activity complete  ", Toast.LENGTH_SHORT).show();
                            return false;
                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();

                    //  return  false;
                }

            }

            if (activityTasklist.get(i).getActivityTypeId() == 20) {
                try {
                    int int502 = 502, int504 = 504, int505 = 505, int503 = 503;
                    EditText edt505 = findViewById(int505);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int502))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int503)));
                    //    edt55.setText(finalValue + "");
                    EditText edt504 = findViewById(int504);
                    edt504.setText(finalValue + "");


                    try {

                        DecimalFormat df = new DecimalFormat("####0.00");
                        // int percentage = (CommonUtils.getIntFromEditText(((EditText) findViewById(int503))) * 100 / CommonUtils.getIntFromEditText(((EditText) findViewById(int502))));
                        //   double res = (amount / 100.0f) * 10;
                        double percentage = ((double) CommonUtils.getIntFromEditText(((EditText) findViewById(int503))) * 100 / (double) CommonUtils.getIntFromEditText(((EditText) findViewById(int502))));

                        edt505.setText(df.format(percentage) + "");
                        Log.e("Germnationpercentage=============", percentage + "");
                    } catch (NumberFormatException e) {
                        Log.e("Germnationpercentage=============", e.getMessage() + "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (activityTasklist.get(i).getActivityTypeId() == 32) {
                try {
                    int int506 = 506, int507 = 507, int508 = 508, int509 = 509;


                    EditText edt508 = findViewById(int508);
                    int germination_loss = CommonUtils.getIntFromEditText(((EditText) findViewById(int506))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int507)));

                    edt508.setText(germination_loss + "");


                    EditText edt509 = findViewById(int509);
                    int new_closingbal = CommonUtils.getIntFromEditText(((EditText) findViewById(int506))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int508)));

                    edt509.setText(new_closingbal + "");

                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int506))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int507)));
                    Log.d("TESTING  finalValue", +finalValue + "");

                    //    dataValue.add(new KeyValues(int62, finalValue + ""));
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please  Enter No of healthy Saplings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int506))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int508)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please  Enter germination Loss saplings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (activityTasklist.get(i).getActivityTypeId() == 42) {
                try {
                    int int510 = 510, int511 = 511, int512 = 512, int513 = 513;
                    EditText edt512 = findViewById(int512);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int510))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int511)));
                    edt512.setText(finalValue + "");
                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please  Enter No of healthy Saplings Less than or equal to Current Closing Stock  ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EditText edt513 = findViewById(int513);

                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int510))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int512)));
                    edt513.setText(finalValue2 + "");
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please  Enter germination Loss saplings Less than or equal to Current Closing Stock  ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (activityTasklist.get(i).getActivityTypeId() == 63) {
                try {
                    int int514 = 514, int515 = 515, int516 = 516, int517 = 517;

                    EditText edt516 = findViewById(int516);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int514))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int515)));
                    Log.d("TESTING  finalValue", +finalValue + "");
                    edt516.setText(finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EditText edt517 = findViewById(int517);


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int514))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int516)));
                    edt517.setText(finalValue2 + "");
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Mortality Loss-1 Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (activityTasklist.get(i).getActivityTypeId() == 94) {
                try {
                    int int518 = 518, int519 = 519, int520 = 520, int521 = 521;

                    EditText edt520 = findViewById(int520);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int518))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int519)));
                    Log.d("TESTING  finalValue", +finalValue + "");
                    edt520.setText(finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    EditText edt521 = findViewById(int521);


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int518))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int520)));
                    edt521.setText(finalValue2 + "");
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Mortality Loss-2 Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (activityTasklist.get(i).getActivityTypeId() == 126) {
                try {
                    int int522 = 522, int523 = 523, int524 = 524;
                    EditText edt524 = findViewById(int524);

                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int522))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int523)));
                    Log.d("Transplationloss============", +finalValue + "");
                    edt524.setText(finalValue + "");
                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter Transplantation Loss Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (activityTasklist.get(i).getActivityTypeId() == 157) {
                try {
                    int int525 = 525, int526 = 526, int527 = 527, int528 = 528, int535 = 535;
                    EditText edt528 = findViewById(int528);

                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int525))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int526)));
                    Log.d("TESTING  finalValue", +finalValue + "");
                    edt528.setText(finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or e qual to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int525))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int527)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EditText edt535 = findViewById(int535);
                    int culling_1 = CommonUtils.getIntFromEditText(((EditText) findViewById(int525))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int528)));
                    Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis finalValue535 : " + culling_1);
                    edt535.setText(culling_1 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
// Culling Loss 2
            if (activityTasklist.get(i).getActivityTypeId() == 183) {
                try {
                    int int3086 = 3086, int3087 = 3087, int3088 = 3088, int3089 = 3089, int3096 = 3096;
                    EditText edt3089 = findViewById(int3089);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3086))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3087)));
                    edt3089.setText(finalValue + "");

                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3086))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3088)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    EditText edt3096 = findViewById(int3096);
                    int Culling2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3086))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3089)));
                    Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis finalValue535 : " + Culling2);
                    edt3096.setText(Culling2 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
//SN-Culling loss-3

            if (activityTasklist.get(i).getActivityTypeId() == 208) {
                try {
                    int int3097 = 3097, int3099 = 3099, int3098 = 3098, int3100 = 3100, int3107 = 3107;
                    EditText edt3100 = findViewById(int3100);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3097))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3098)));
                    edt3100.setText(finalValue + "");

                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3097))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3099)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EditText edt3107 = findViewById(int3107);
                    int culling_3 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3097))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3100)));
                    Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis finalValue535 : " + culling_3);
                    edt3107.setText(culling_3 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //SN-Culling loss-4
            if (activityTasklist.get(i).getActivityTypeId() == 222) {
                try {
                    int int3108 = 3108, int3109 = 3109, int3110 = 3110, int3111 = 3111, int3118 = 3118;
                    EditText edt3111 = findViewById(int3111);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3108))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3109)));

                    edt3111.setText(finalValue + "");

                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3108))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3110)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    EditText edt3118 = findViewById(int3118);
                    int culling_4 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3108))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3111)));
                    Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis culling_4 : " + culling_4);
                    edt3118.setText(culling_4 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //SN-Culling loss-5
            if (activityTasklist.get(i).getActivityTypeId() == 235) {
                try {
                    int int3119 = 3119, int3120 = 3120, int3121 = 3121, int3122 = 3122, int3130 = 3130;

                    EditText edt3122 = findViewById(int3122);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3119))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3120)));

                    edt3122.setText(finalValue + "");

                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3119))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3121)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    EditText edt3130 = findViewById(int3130);
                    int Cullingloss5 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3119))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3122)));
                    Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis finalValue535 : " + Cullingloss5);
                    edt3130.setText(Cullingloss5 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            //SN-Culling loss-6
            if (activityTasklist.get(i).getActivityTypeId() == 249) {
                try {
                    int int3131 = 3131, int3132 = 3132, int3133 = 3133, int3134 = 3134, int3141 = 3141;
                    EditText edt3134 = findViewById(int3134);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3131))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3132)));

                    edt3134.setText(finalValue + "");

                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3131))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3133)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    EditText edt3141 = findViewById(int3141);
                    int culling_6 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3131))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3134)));

                    edt3141.setText(culling_6 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            //SN-Culling loss-7
            if (activityTasklist.get(i).getActivityTypeId() == 262) {
                try {
                    int int3142 = 3142, int3143 = 3143, int3144 = 3144, int3145 = 3145, int3152 = 3152;
                    EditText edt3145 = findViewById(int3145);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3142))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3143)));

                    edt3145.setText(finalValue + "");

                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3142))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3144)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EditText edt3152 = findViewById(int3152);
                    int culling_7 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3142))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3145)));

                    edt3152.setText(culling_7 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            //TN-Culling loss-8
            if (activityTasklist.get(i).getActivityTypeId() == 277) {
                Log.e("image size=>", imageRepo.size() + "");
                try {
                    int int3153 = 3153, int3154 = 3154, int3155 = 3155, int3156 = 3156, int3163 = 3163;
                    EditText edt3156 = findViewById(int3156);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3153))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3154)));

                    edt3156.setText(finalValue + "");

                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3153))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3155)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    EditText edt3163 = findViewById(int3163);
                    int culling_8 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3153))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3156)));

                    edt3163.setText(culling_8 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //TN-Culling loss-9
            if (activityTasklist.get(i).getActivityTypeId() == 290) {
                Log.e("image size=>", imageRepo.size() + "");
                try {
                    int int3164 = 3164, int3165 = 3165, int3166 = 3166, int3167 = 3167, int3174 = 3174;

                    EditText edt3167 = findViewById(int3167);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3164))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3165)));

                    edt3167.setText(finalValue + "");


                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3164))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3166)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EditText edt3174 = findViewById(int3174);
                    int culling_9 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3164))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3167)));

                    edt3174.setText(culling_9 + "");

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (imageRepo.size() == 0) {
                    Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            //TN-Culling loss-10
            if (activityTasklist.get(i).getActivityTypeId() == 304) {

                Log.e("image size=>", imageRepo.size() + "");
                try {
                    int int3175 = 3175, int3176 = 3176, int3177 = 3177, int3178 = 3178, int3185 = 3185;
                    EditText edt3178 = findViewById(int3178);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3175))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3176)));

                    edt3178.setText(finalValue + "");

                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3175))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3177)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EditText edt3185 = findViewById(int3185);
                    int culling_10 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3175))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3178)));

                    edt3185.setText(culling_10 + "");
                    if (imageRepo.size() == 0) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //TN-Culling loss-11
            if (activityTasklist.get(i).getActivityTypeId() == 317) {
                Log.e("image size=>", imageRepo.size() + "");
                try {
                    int int3186 = 3186, int3187 = 3187, int3188 = 3188, int3189 = 3189, int3196 = 3196;
                    EditText edt3189 = findViewById(int3189);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3186))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3187)));

                    edt3189.setText(finalValue + "");
                    // int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3186))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3187)));
                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3186))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3188)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    EditText edt3196 = findViewById(int3196);
                    int culling_11 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3186))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3189)));

                    edt3196.setText(culling_11 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //TN-Culling loss-12
            if (activityTasklist.get(i).getActivityTypeId() == 331) {
                Log.e("image size=>", imageRepo.size() + "");
                try {
                    int int3197 = 3197, int3198 = 3198, int3199 = 3199, int3200 = 3200, int3207 = 3207;
                    EditText edt3200 = findViewById(int3200);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3197))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3198)));

                    edt3200.setText(finalValue + "");

                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3197))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3199)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    EditText edt3207 = findViewById(int3207);
                    int culling_12 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3197))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3200)));

                    edt3207.setText(culling_12 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            //SN-Culling loss-13
            if (activityTasklist.get(i).getActivityTypeId() == 344) {
                try {
                    int int3208 = 3208, int3209 = 3209, int3210 = 3210, int3211 = 3211, int3218 = 3218;

                    EditText edt3211 = findViewById(int3211);
                    int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3208))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3209)));

                    edt3211.setText(finalValue + "");
                    Log.d("TESTING  finalValue", +finalValue + "");
                    if (finalValue < 0) {
                        Toast.makeText(this, "Please Enter No of healthy Saplings Less than or equal to Current Closing Stock", Toast.LENGTH_SHORT).show();
                        return false;
                    }


                    int finalValue2 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3208))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3210)));
                    if (finalValue2 < 0) {
                        Toast.makeText(this, "Please Enter Weaklings Less than or equal to Current Closing Stock ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EditText edt3218 = findViewById(int3218);
                    int culling_13 = CommonUtils.getIntFromEditText(((EditText) findViewById(int3208))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3211)));

                    edt3218.setText(culling_13 + "");
                    if (imageRepo.size() == 0 || imageRepo == null) {
                        Toast.makeText(this, "Please add At Least one Image( Attachment) ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        if (Integer.parseInt(activityTypeId) == 9) {

            boolean isvalid = true;
            List<Integer> groupids = dataAccessHandler.getGroupids(Queries.getGroupIds(activityTypeId));
            Log.e("========>1871", groupids.size() + "");
            for (int i = 0; i < groupids.size(); i++) {
                List<ActivityTasks> groupedField = dataAccessHandler.getActivityTasksDetails(Queries.getInstance().getActivityTaskDetailsUsingGroupId(Integer.parseInt(activityTypeId), groupids.get(i)));
                Log.e("========>1874", groupedField.get(i).getField() + "");
                if (!validateGroup(groupedField)) {
                    Log.e("========>errorMsg==1874", errorMsg+ "");
                    Toast.makeText(this, "Please Enter Atleast One value for Following \n " + errorMsg, Toast.LENGTH_SHORT).show();
                    isvalid = false;
                }

            }
            return isvalid;

        }
        if (yesnoCHeckbox > 0 && Integer.parseInt(activityTypeId) != 9) {
            CheckBox chk = findViewById(yesnoCHeckbox);
            if (chk.isChecked()) {
                boolean isvalid = true;
                List<Integer> groupids = dataAccessHandler.getGroupids(Queries.getGroupIds(activityTypeId));

                for (int i = 0; i < groupids.size(); i++) {
                    List<ActivityTasks> groupedField = dataAccessHandler.getActivityTasksDetails(Queries.getInstance().getActivityTaskDetailsUsingGroupId(Integer.parseInt(activityTypeId), groupids.get(i)));

                    if (!validateGroup(groupedField)) {
                        Toast.makeText(this, "Please Enter Atleast One value for Following \n " + errorMsg, Toast.LENGTH_SHORT).show();
                        isvalid = false;
                    }

                }
                return isvalid;
            }
        } else {
            List<Integer> groupids = dataAccessHandler.getGroupids(Queries.getGroupIds(activityTypeId));
            boolean isvalid = true;
            for (int i = 0; i < groupids.size(); i++) {
                List<ActivityTasks> groupedField = dataAccessHandler.getActivityTasksDetails(Queries.getInstance().getActivityTaskDetailsUsingGroupId(Integer.parseInt(activityTypeId), groupids.get(i)));

                if (!validateGroup(groupedField)) {
                    Toast.makeText(this, "Please Enter Atleast One value for Following \n " + errorMsg, Toast.LENGTH_SHORT).show();
                    isvalid = false;
                }


            }
            return isvalid;
        }

    // write validation
    // get first edit text and second one validate

       return true;

}

    private void addorupdate() {

    }

    private boolean validateGroup(List<ActivityTasks> groupFields) {
        errorMsg = "";
        for (int i = 0; i < groupFields.size(); i++) {


            EditText editText = (EditText) findViewById(groupFields.get(i).getId());
            errorMsg = errorMsg + "\n" + groupFields.get(i).getField();
            Log.e("=====>1920",errorMsg);
            if (editText.getVisibility() == View.GONE || editText != null & editText.getText() != null & !StringUtils.isEmpty(editText.getText()) & !editText.getText().toString().equalsIgnoreCase("0")) {
                return true;
            }
        }


        return false;
    }

    public Spinner addSpinner(int id) {
        List<Integer> sortids = dataAccessHandler.getsortids(Queries.getSortorderid(activityTypeId));
        Log.e("=====1929",sortids+"");
        Log.e("=====1930",sortids.size()+"");
        for (int ii = 0; ii < sortids.size(); ii++) {
            groupedField = dataAccessHandler.getActivityTasksDetails(Queries.getInstance().getActivityTaskDetailsUsingsortId(Integer.parseInt(activityTypeId), sortids.get(ii)));
            Log.e("=====1933",groupedField.size()+"");}
       // List<ActivityTasks> groupedField = ne;
        Spinner sp = new Spinner(this);

        typeofLabourdatamap = dataAccessHandler.getPairData(Queries.getInstance().getTypeofLabourQuery());

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(ActivityTask.this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(typeofLabourdatamap, "Type of Labour"));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(spinnerArrayAdapter);

        sp.setId(id);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
           //  Toast.makeText(ActivityTask.this, "Selected PO :" + i, Toast.LENGTH_SHORT).show();
                selectedPo = i;
                if (Integer.parseInt(activityTypeId) == 9) {

                    Log.e("=====1951",groupedField.size()+"");

                    for (int j = 0; j < groupedField.size(); j++) {
                                int sortid = groupedField.get(j).getSortOrder();
                                Log.e("=====1955",sortid+"");
                                if (i == 2) {
                                for (sortid = 2; sortid <= 14; sortid++) {

                                    int f = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getid(sortid));
                                    Log.e("=====1955",f+"");
                                    try {
                                        findViewById(f).setVisibility(View.VISIBLE);
                                        findViewById(f + 9000).setVisibility(View.VISIBLE);
//
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                for (sortid = 15; sortid <= 30; sortid++) {
                                    int f = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getid(sortid));

                                    try {
                                        findViewById(f).setVisibility(View.GONE);


                                        findViewById(f + 9000).setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        EditText txt = findViewById(f);
                                        txt.setText("");


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
//
                            }
                                else if (i == 1) {
                                    for (sortid = 2; sortid <= 14; sortid++) {

                                        int f = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getid(sortid));
                                        Log.e("=====1955",f+"");
                                        try {
                                            findViewById(f).setVisibility(View.GONE);
                                            findViewById(f + 9000).setVisibility(View.GONE);
//                                if(f < 31 && f > 22){
//                                    try {
//                                        ((EditText)findViewById(f)).setText("");
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            EditText txt = findViewById(f);
                                            txt.setText("");


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    for (sortid = 15; sortid <= 30; sortid++) {
                                        int f = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getid(sortid));

                                        try {
                                            findViewById(f).setVisibility(View.VISIBLE);


                                            findViewById(f + 9000).setVisibility(View.VISIBLE);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
//                                        try {
//                                            EditText txt = findViewById(f);
//                                            txt.setText("");
//
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }

                                    }
                                }
                            }

                }
                else if (Integer.parseInt(activityTypeId) == 91) {
                    if (i == 2) {


                        for (int f = 200; f < 212; f++) {

                            try {
                                findViewById(f).setVisibility(View.VISIBLE);
                                findViewById(f + 9000).setVisibility(View.VISIBLE);
//                                if(f < 31 && f > 22){
//                                    try {
//                                        ((EditText)findViewById(f)).setText("");
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        for (int f = 210; f < 224; f++) {

                            try {
                                findViewById(f).setVisibility(View.GONE);
                                findViewById(f + 9000).setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                EditText txt = findViewById(f);
                                txt.setText("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } else if (i == 1) {



                        for (int f = 200; f < 212; f++) {

                            try {
                                findViewById(f).setVisibility(View.GONE);
                                findViewById(f + 9000).setVisibility(View.GONE);
//                                if(f < 31 && f > 22){
//                                    try {
//                                        ((EditText)findViewById(f)).setText("");
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                EditText txt = findViewById(f);
                                txt.setText("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        for (int f = 211; f < 224; f++) {

                            try {
                                findViewById(f).setVisibility(View.VISIBLE);
                                findViewById(f + 9000).setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return sp;


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public CheckBox addCheckbox(String content, int id) {  // default checked Checkbox ids
        CheckBox cb = new CheckBox(this);
        cb.setText(content);
        cb.setTextSize(20);
        cb.setId(id);


        cb.setPadding(20, 10, 20, 10);

// Create ColorStateList for the checkbox tint
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked}, // checked
                        new int[]{-android.R.attr.state_checked} // unchecked
                },
                new int[]{
                        ContextCompat.getColor(this, R.color.colorPrimary), // checked color
                        ContextCompat.getColor(this, android.R.color.darker_gray) // unchecked color
                }
        );

// Apply tint to checkbox
        cb.setButtonTintList(colorStateList);

// Click listener (optional logic)
        cb.setOnClickListener(this::onClick);

//        cb.setOnClickListener(this::onClick);
        Log.d(ActivityTask.class.getSimpleName(), "===> Analysis YES NO CHK  ID:  before Assign :" + id + "And Name :" + content);
        if (id == 173 || id == 72 || id == 79 || id == 95 || id == 101 || id == 112 || id == 121 || id == 130 || id == 139 || id == 149 || id == 164 || id == 253 || id == 259 || id == 269 || id == 280 || id == 289 || id == 298 || id == 307 || id == 317 || id == 332 || id == 341 || id == 368 || id == 385 || id == 391 || id == 397 || id == 403 || id == 414 || id == 423 || id == 432 || id == 611 || id == 626 || id == 641 || id == 656 || id == 671 || id == 686 || id == 701 || id == 716 || id == 731 || id == 746 || id == 761 || id == 776 || id == 1079 || id == 1088 || id == 1097 || id == 1106 || id == 1115 || id == 1124 || id == 1133 || id == 1142 || id == 1151 || id == 1160 || id == 1169 || id == 1178 || id == 1178 || id == 1187 || id == 1196 || id == 1205 || id == 1214 || id == 1223 || id == 1230 || id == 1236 || id == 1242 || id == 1249 || id == 1255 || id == 1261 || id == 1268 || id == 1274 || id == 1280 || id == 1287 || id == 1293 || id == 1317 || id == 1323 || id == 1329 || id == 1335 || id == 1341 || id == 1347 || id == 1353 || id == 1359 || id == 1365 || id == 1371 || id == 1382 || id == 1393 || id == 1402 || id == 1411 || id == 1420 || id == 1429 || id == 1438 || id == 1447 || id == 1457 || id == 1470 || id == 1476 || id == 1482 || id == 1488 || id == 1494 || id == 1500 || id == 1506 || id == 1512 || id == 1518 || id == 1524 || id == 1530 || id == 1536 || id == 1542 || id == 1548 || id == 1554 || id == 1560 || id == 1566 || id == 1572 || id == 1578 || id == 1584 || id == 1590 || id == 1596 || id == 1602 || id == 1617 || id == 1632 || id == 1647 || id == 1662 || id == 1677 || id == 1692 || id == 1707 || id == 1722 || id == 1737 || id == 1832 || id == 1841 || id == 1850 || id == 1859 || id == 1868 || id == 1877 || id == 1886 || id == 1895 || id == 1904 || id == 1913 || id == 2022 || id == 2031 || id == 2040 || id == 2049 || id == 2058 || id == 2067 || id == 2076 || id == 2085 || id == 1187 || id == 1196 || id == 1205 || id == 1214 || id == 1223 || id == 1230 || id == 1236 || id == 1242 || id == 1249 || id == 2094 || id == 2103 || id == 2112 || id == 2123 || id == 2145 || id == 2156 || id == 2167 || id == 2178 || id == 2189 || id == 2200 || id == 2211 || id == 2222 || id == 2231 || id == 2240 || id == 2249 || id == 2258 || id == 2267 || id == 2276 || id == 2285 || id == 2294 || id == 2303 || id == 2312 || id == 2321 || id == 2330 || id == 2339 || id == 2348 || id == 2357 || id == 2366 || id == 2375 || id == 2384 || id == 2393 || id == 2402 || id == 2411 || id == 2420 || id == 2429 || id == 2438 || id == 2447 || id == 2456 || id == 2465 || id == 2474 || id == 2483 || id == 2492 || id == 2502 || id == 2512 || id == 2522 || id == 2532 || id == 2542 || id == 2552 || id == 2562 || id == 2572 || id == 2582 || id == 2592 || id == 2598 || id == 2604 || id == 2610 || id == 2621 || id == 2632 || id == 2643 || id == 2654 || id == 2663 || id == 2672 || id == 2681 || id == 2690 || id == 2699 || id == 2708 || id == 2717 || id == 2726 || id == 2735 || id == 2744 || id == 2753 || id == 2762 || id == 2772 || id == 2782 || id == 2792 || id == 2802 || id == 2817 || id == 2832 || id == 2847 || id == 2894 || id == 2903 || id == 2912 || id == 2921 || id == 2970 || id == 2979 || id == 3006 || id == 3012 || id == 3018 || id == 3024 || id == 3030 || id == 3036 || id == 3042 || id == 3048 || id == 3054 || id == 3062 || id == 3077 ||
                id == 1752 || id == 1922 || id == 350 || id == 358 || id == 1760 || id == 1932 || id == 2134 ||
                id == 181 || id == 253 || id == 259 || id == 269 || id == 280 || id == 289 || id == 298 || id == 307 || id == 317 || id == 332 || id == 341 || id == 368 ||
                id == 385 || id == 391 || id == 397 || id == 403 || id == 414 || id == 423 || id == 432 || id == 536 || id == 581 || id == 791 || id == 799 || id == 611 || id == 626 || id == 641 || id == 656 ||
                id == 671 || id == 686 || id == 701 || id == 716 || id == 731 || id == 746 || id == 761 || id == 776 || id == 1079 || id == 1088 || id == 1097 ||
                id == 1106 || id == 1115 || id == 1124 || id == 1133 || id == 1142 || id == 1151 || id == 1160 || id == 1169 || id == 1178 || id == 1178 ||
                id == 1187 || id == 1196 || id == 1205 || id == 1214 || id == 1230 || id == 1236 || id == 1242 || id == 1249 || id == 1255 ||
                id == 1261 || id == 1268 || id == 1274 || id == 1280 || id == 1287 || id == 1293 || id == 1299 || id == 1305 || id == 1311 || id == 1317 ||
                id == 1323 || id == 1329 || id == 1335 || id == 1341 || id == 1347 || id == 1353 || id == 1359 || id == 1365 || id == 1371 || id == 1382 ||
                id == 1393 || id == 1402 || id == 1411 || id == 1420 || id == 1429 || id == 1438 || id == 1447 || id == 1457 || id == 1470 || id == 1476 || id == 1482 ||
                id == 1488 || id == 1494 || id == 1500 || id == 1506 || id == 1512 || id == 1518 || id == 1524 || id == 1530 || id == 1536 || id == 1542 || id == 1548 || id == 959 || id == 441 ||
                id == 1554 || id == 1560 || id == 1566 || id == 1572 || id == 1578 || id == 1584 || id == 1590 || id == 1596 || id == 1602 || id == 1617 ||
                id == 1632 || id == 1647 || id == 1662 || id == 1677 || id == 1692 || id == 1707 || id == 1722 || id == 1737 || id == 1832 || id == 1841 ||
                id == 1850 || id == 1859 || id == 1868 || id == 1877 || id == 1886 || id == 1895 || id == 1904 || id == 1913 || id == 2022 || id == 2031 ||
                id == 2040 || id == 2049 || id == 2058 || id == 2067 || id == 2076 || id == 2085 || id == 1187 || id == 1196 || id == 1205 || id == 1214 ||
                id == 1230 || id == 1236 || id == 1242 || id == 1249 || id == 2094 || id == 2103 || id == 2112 || id == 2123 || id == 2145 ||
                id == 2156 || id == 2167 || id == 2178 || id == 2189 || id == 2200 || id == 2211 || id == 2222 || id == 2231 || id == 2240 || id == 2249 ||
                id == 2258 || id == 2267 || id == 2276 || id == 2285 || id == 2294 || id == 2303 || id == 2312 || id == 2321 || id == 2330 || id == 2339 ||
                id == 2348 || id == 2357 || id == 2366 || id == 2375 || id == 2384 || id == 2393 || id == 2402 || id == 2411 || id == 2420 || id == 2429 ||
                id == 2438 || id == 2447 || id == 2456 || id == 2465 || id == 2474 || id == 2483 || id == 2492 || id == 2502 || id == 2512 || id == 2522 ||
                id == 2532 || id == 2542 || id == 2552 || id == 2562 || id == 2572 || id == 2582 || id == 2592 || id == 2598 || id == 2604 || id == 2610 ||
                id == 2621 || id == 2632 || id == 2643 || id == 2654 || id == 2663 || id == 2672 || id == 2681 || id == 2690 || id == 2699 || id == 2708 ||
                id == 2717 || id == 2726 || id == 2735 || id == 2744 || id == 2753 || id == 2762 || id == 2772 || id == 2782 || id == 2792 || id == 2802 ||
                id == 2817 || id == 2832 || id == 2847 || id == 2894 || id == 2903 || id == 2912 || id == 2921 || id == 2970 || id == 2979 || id == 3006 ||
                id == 3012 || id == 3018 || id == 3024 || id == 3030 || id == 3036 || id == 3042 || id == 3048 || id == 3054 || id == 3062 || id == 3077 ||
                id == 1019 || id == 871 || id == 2940 || id == 1962 || id == 1972 || id == 1792 || id == 1800 || id == 1982 || id == 1808 || id == 1992 || id == 1816 || id == 2002 || id == 1824 || id == 2012 || id == 475 || id == 484 || id == 451 || id == 466 || id == 492 || id == 475 || id == 2862 || id == 2930 || id == 2870 || id == 2940 || id == 492 || id == 466 || id == 475 || id == 2862 || id == 2930 || id == 2878 || id == 2950 || id == 2988 || id == 2886 || id == 2997 || id == 2960 ||
                id == 979 || id == 551 || id == 791 || id == 919 || id == 566 || id == 929 || id == 807 || id == 939 || id == 596 || id == 815 || id == 949 || id == 823 || id == 951 || id == 831 || id == 969 || id == 839 || id == 847 || id == 989 || id == 855 || id == 979 || id == 999 || id == 863 || id == 1009 || id == 879 || id == 1029 || id == 887 || id == 1049 || id == 1039 || id == 895 || id == 903 || id == 1059 || id == 911 || id == 1069 || id == 350 || id == 1922 || id == 358 || id == 1942 || id == 1768 || id == 1776 || id == 1952 || id == 1784) {
            yesnoCHeckbox = id;
            cb.setChecked(true);


            Log.d(ActivityTask.class.getSimpleName(), "===> Analysis YES NO CHK  ID:" + yesnoCHeckbox);
        }


        return cb;
    }



    public TextInputLayout addEdittext(String content, int id, String dataType) {
// Create LayoutParams with MATCH_PARENT width and WRAP_CONTENT height
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

// Set margin (left, top, right, bottom)
        params.setMargins(0, 10, 0,0 ); // Example: 20dp top and bottom margin

   //  TextInputLayout textInputLayout = new TextInputLayout(this, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
 // TextInputLayout textInputLayout = new TextInputLayout(this, null, R.style.RoundedTextInputLayout);
        TextInputLayout textInputLayout = new TextInputLayout(
                new ContextThemeWrapper(this, R.style.RoundedTextInputLayout),
                null
        );

        textInputLayout.setId(id + 9000);
        textInputLayout.setHintTextAppearance(R.style.text_in_layout_hint_Style);
        textInputLayout.setHint(content);
        textInputLayout.setLayoutParams(params);
// Set the box background mode to outlined (rectangle box)
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.colorPrimary)); // Optional stroke color

// Create and configure the EditText
        EditText et = new EditText(this);
        et.setId(id);
        et.setTextSize(16); // Use SP units in layout; 16 is standard
        et.setMinLines(1);
        et.setMaxLines(1);
        et.setBackground(null);
        et.setPadding(20,20,20,20);
        et.setHintTextColor(ContextCompat.getColor(this, R.color.header_txt));
        et.setSingleLine(true);
        et.setOnFocusChangeListener(this::onFocusChange);
        et.setHintTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        textInputLayout.setDefaultHintTextColor(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.colorGray)
        ));

// Add EditText to TextInputLayout
       // textInputLayout.addView(et);

        if (dataType.equalsIgnoreCase("Integer")) {
            et.setInputType(InputType.TYPE_CLASS_NUMBER);  // Number Keyboard & max & digits
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        }
        if (dataType.equalsIgnoreCase("String")) {
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if (dataType.equalsIgnoreCase("Float")) {

            localeDecimalInput(et);


        }


        et.setOnFocusChangeListener(this::onFocusChange);


        if (id == 60 || id == 62 || id == 506 || id == 510 || id == 514 || id == 518 || id == 522 || id == 525 || id == 3086 || id == 3097 || id == 3108 || id == 3119 || id == 3131 || id == 3142 || id == 3153 || id == 3164
                || id == 3175
                || id == 3186 || id == 3197 || id == 3208) {
            et.setFocusable(false);
            et.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            et.setClickable(false);
        }



        if (id == 51) {
            try {

                Arrival_Sprouts = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowingg(consignmentCode, 7)));

                Log.e("================> Arrival Sprouts ", Arrival_Sprouts + "");

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 60) {
            try {
                finalValue60 = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowing(consignmentCode, 52)));
                et.setText(finalValue60 + "");
                Log.e("finalValue60===========", finalValue60 + "");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 62) {

            int value_61 = 0;
            try {

                value_61 = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowing(consignmentCode, 61)));
                finalValue62 = finalValue60 - value_61;

                Log.e("Value62===========", finalValue62 + "");
                et.setText(finalValue62 + "");

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (id == 506) {
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 509)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (id == 510) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 513)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (id == 514) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 517)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (id == 518) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 521)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 522) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 524)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        if (id == 525) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 535)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 3086) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3096)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (id == 3097) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3107)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (id == 3108) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3118)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 3119) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3130)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 3131) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3141)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (id == 3142) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3152)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (id == 3153) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3163)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 3164) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3174)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (id == 3175) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3185)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 3186) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3196)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 3197) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3207)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (id == 3208) {
            // DOne
            try {

                int finalValueold = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.getCurrentClosingStock(consignmentCode, 3218)));
                et.setText(finalValueold + "");


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        textInputLayout.setHint(content);
        textInputLayout.addView(et);


        return textInputLayout;

    }

    private void localeDecimalInput(final EditText editText) {

        DecimalFormat decFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols = decFormat.getDecimalFormatSymbols();
        final String defaultSeperator = Character.toString(symbols.getDecimalSeparator());
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789." + defaultSeperator));
        editText.setKeyListener(DigitsKeyListener.getInstance(false, true));

    }


    public TextView addTexView(String content, int id) {
        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setTextSize(20);
        tv.setText(content);
        return tv;
    }
    public Button addButton(String content, int id) {
        Button btn = new Button(this);
        btn.setText(content);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundResource(R.drawable.rounded_button); // Use drawable with radius
        btn.setId(id);
        btn.setOnClickListener(this::onClick);
        return btn;
    }

    private void saveData() {


        Bundle extras = getIntent().getExtras();
        if (SCREEN_FROM == CommonConstants.FROM_MUTIPLE_ENTRY_EDITDATA) {
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> SCREEN CAME FROM :FROM_MUTIPLE_ENTRY_EDITDATA");
            // SCREEN CAME FROM UPDATE CURRENT SCREEN
            intentTransactionId = extras.getString("transactionId");
            String consignmentcode = extras.getString("consignmentcode");
            String ActivityTypeId = extras.getString("ActivityTypeId");
            enableEditing = extras.getBoolean("enableEditing");
            Ismultipleentry = extras.getBoolean("multipleEntry");
            int statusTypeId;
            if (isjobDoneId != 0) {
                CheckBox chk = findViewById(isjobDoneId);
                if (chk.isChecked()) {
                    statusTypeId = 346;
                } else {
                    statusTypeId = 352;
                }
            } else {
                statusTypeId = 346;
            }
            Log.d(ActivityTask.class.getSimpleName(), "==> Analysis => FROM CHECKBOX  STATUS TYPEID : " + statusTypeId);
            Log.d(ActivityTask.class.getSimpleName(), "==> Analysis =>enableEditing: " + enableEditing);
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> FROM_MUTIPLE_ENTRY_EDITDATA  ###### Ismultipleentry :" + Ismultipleentry);


            updateSingleEntryData(consignmentcode, ActivityTypeId, intentTransactionId, statusTypeId, enableEditing);

        }
        else if (SCREEN_FROM == CommonConstants.FROM_MULTIPLE_ADD_NEW_TASK) {
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> SCREEN CAME FROM :FROM_MULTIPLE_ADD_NEW_TASK");
            String activityTypeId = extras.getString("ActivityTypeId");
            String consignmentcode = extras.getString("consignmentcode");
             Ismultipleentry = extras.getBoolean("multipleEntry");
            int statusTypeId;
            if (isjobDoneId != 0) {
                CheckBox chk = findViewById(isjobDoneId);
                if (chk.isChecked()) {
                    statusTypeId = 346;
                } else {
                    statusTypeId = 352;
                }
            } else {
                statusTypeId = 346;
            }Log.d(ActivityTask.class.getSimpleName(), "==> Analysis => FROM multipleentry 2542 : " + Ismultipleentry);
            Nurserycode = dataAccessHandler.getSingleValue(Queries.getnurserycode(consignmentcode));
            Sapcode = dataAccessHandler.getSingleValue(Queries.getSapcode(Nurserycode));
            Consignment_ID = dataAccessHandler.getSingleIntValue(Queries.getID(consignmentcode));
            Activity_ID = dataAccessHandler.getGenerateActivityid(activityTypeId);
            Log.e("Sapcode===================2195 ", Sapcode+"========>"+Nurserycode);
            transactionIdNew = "TRAN" + Sapcode + Consignment_ID + CommonConstants.TAB_ID  + Activity_ID + "-" + (dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getSaplingActivityMaxNumber(activityTypeId,consignmentcode)) + 1);
            imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(transactionIdNew));
            Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   New Transaction ID : 1872" + transactionIdNew);

            String[] strArray = null;

            strArray = transactionIdNew.split("/");

            for (int i = 0; i < strArray.length; i++) {
                System.out.println(strArray[i]);
            }
            if (null != transactionId && !transactionId.isEmpty() && !TextUtils.isEmpty(transactionId)) {
                bindExistingData(transactionId);
                //   updateSingleEntryData(consignmentcode, activityTypeId, transactionId, statusTypeId, enableEditing);
                imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(transactionId));
            } else {
                Log.d(ActivityTask.class.getSimpleName(), "==> Analysis  ==> New Task Creation Started ");
                transactionIdNew = "TRAN" + Sapcode + Consignment_ID + CommonConstants.TAB_ID  + Activity_ID +"-" + (dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getSaplingActivityMaxNumber(activityTypeId,consignmentcode)) + 1);
                Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   New Transaction ID : 209" + transactionIdNew);
                imageRepo = dataAccessHandler.getCullinglossRepoDetails(Queries.getimagepath(transactionIdNew));

            }

            //TRAN+NurserySAPCode(3)+ConsignmentId(4)+TabCode-Seq No(ActivityCount)
            if (imageRepo.size() != 0) {

                addImageData(); //ToDO
            }

            addNewSingleEntryActivity(consignmentcode, activityTypeId, statusTypeId, transactionIdNew, Ismultipleentry);

        }
        else if (SCREEN_FROM == CommonConstants.FROM_SINGLE_ENTRY_EDITDATA) {
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis  ==> SCREEN CAME FROM :FROM_SINGLE_ENTRY_EDITDATA");
            String consignmentcode = extras.getString("consignmentcode");
            String activityTypeId = extras.getString("ActivityTypeId");
          //  Ismultipleentry = extras.getBoolean("multipleEntry");

            int statusTypeId;
            if (isjobDoneId != 0) {
                CheckBox chk = findViewById(isjobDoneId);
                if (chk.isChecked()) {
                    statusTypeId = 346;
                } else {
                    statusTypeId = 352;
                }
            } else {
                statusTypeId = 346;
            }



            Log.d(ActivityTask.class.getSimpleName(), "==> Analysis => FROM enableEditing  : " + enableEditing);
            Log.d(ActivityTask.class.getSimpleName(), "==> Analysis => FROM multipleentry 2600 : " + Ismultipleentry);
            transactionId = dataAccessHandler.getSingleValue(Queries.getInstance().getTransactionIdUsingConsimentCode(consignmentcode, activityTypeId));
          //  updateSingleEntryData(consignmentcode, activityTypeId, transactionId, statusTypeId, enableEditing);

            if (null != transactionId && !transactionId.isEmpty() && !TextUtils.isEmpty(transactionId)) {

                Log.d(ActivityTask.class.getSimpleName(), "==> Analysis =>  transactionId  : " + transactionId);
                updateSingleEntryData(consignmentcode, activityTypeId, transactionId, statusTypeId, enableEditing);
            }
            else {
                // TODO dont have any Existind data add new activity
                Log.d(ActivityTask.class.getSimpleName(), "==> Analysis  ==> New Task Creation Started ");
                Nurserycode = dataAccessHandler.getSingleValue(Queries.getnurserycode(consignmentcode));
                Sapcode = dataAccessHandler.getSingleValue(Queries.getSapcode(Nurserycode));
                Consignment_ID = dataAccessHandler.getSingleIntValue(Queries.getID(consignmentcode));
                Activity_ID = dataAccessHandler.getGenerateActivityid(activityTypeId);
                transactionIdNew = "TRAN" + Sapcode + Consignment_ID + CommonConstants.TAB_ID  + Activity_ID +"-" + (dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getSaplingActivityMaxNumber(activityTypeId,consignmentcode)) + 1);


                Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   New Transaction ID :" + transactionIdNew);

//TRAN+NurserySAPCode(3)+ConsignmentId(4)+TabCode-Seq No(ActivityCount)
                addNewSingleEntryActivity(consignmentcode, activityTypeId, statusTypeId, transactionIdNew, false);


            }


        }
//                    setSaplingActivity();
//                    finish();
//                    Toast.makeText(ActivityTask.this, "Task Completed Successfully", Toast.LENGTH_SHORT).show();

    }

    private void CheckMantoryItem() {
        for (int i = 0; i < activityTasklist.size(); i++) {


            if (activityTasklist.get(i).getActivityTypeId() == 14 || activityTasklist.get(i).getActivityTypeId() == 19 || activityTasklist.get(i).getActivityTypeId() == 31 || activityTasklist.get(i).getActivityTypeId() == 45 || activityTasklist.get(i).getActivityTypeId() == 59 || activityTasklist.get(i).getActivityTypeId() == 67 || activityTasklist.get(i).getActivityTypeId() == 78 || activityTasklist.get(i).getActivityTypeId() == 89 || activityTasklist.get(i).getActivityTypeId() == 94 || activityTasklist.get(i).getActivityTypeId() == 198 || activityTasklist.get(i).getActivityTypeId() == 210 || activityTasklist.get(i).getActivityTypeId() == 224 || activityTasklist.get(i).getActivityTypeId() == 236 || activityTasklist.get(i).getActivityTypeId() == 1229 || activityTasklist.get(i).getActivityTypeId() == 1248 || activityTasklist.get(i).getActivityTypeId() == 1267 || activityTasklist.get(i).getActivityTypeId() == 1286 || activityTasklist.get(i).getActivityTypeId() == 3219 || activityTasklist.get(i).getActivityTypeId() == 3220 || activityTasklist.get(i).getActivityTypeId() == 3221 || activityTasklist.get(i).getActivityTypeId() == 3222 || activityTasklist.get(i).getActivityTypeId() == 3223 || activityTasklist.get(i).getActivityTypeId() == 3224 || activityTasklist.get(i).getActivityTypeId() == 3226 || activityTasklist.get(i).getActivityTypeId() == 3227 || activityTasklist.get(i).getActivityTypeId() == 3228 || activityTasklist.get(i).getActivityTypeId() == 3229 || activityTasklist.get(i).getActivityTypeId() == 3230 || activityTasklist.get(i).getActivityTypeId() == 3231 || activityTasklist.get(i).getActivityTypeId() == 3232 || activityTasklist.get(i).getActivityTypeId() == 3233 || activityTasklist.get(i).getActivityTypeId() == 3234 || activityTasklist.get(i).getActivityTypeId() == 3236 || activityTasklist.get(i).getActivityTypeId() == 3237 || activityTasklist.get(i).getActivityTypeId() == 3238 || activityTasklist.get(i).getActivityTypeId() == 3239 || activityTasklist.get(i).getActivityTypeId() == 3240 || activityTasklist.get(i).getActivityTypeId() == 3241 || activityTasklist.get(i).getActivityTypeId() == 3242 || activityTasklist.get(i).getActivityTypeId() == 3243 || activityTasklist.get(i).getActivityTypeId() == 3244 || activityTasklist.get(i).getActivityTypeId() == 3246 || activityTasklist.get(i).getActivityTypeId() == 3247 || activityTasklist.get(i).getActivityTypeId() == 3248 || activityTasklist.get(i).getActivityTypeId() == 3249 || activityTasklist.get(i).getActivityTypeId() == 3250 || activityTasklist.get(i).getActivityTypeId() == 3251 || activityTasklist.get(i).getActivityTypeId() == 3252 || activityTasklist.get(i).getActivityTypeId() == 3253 || activityTasklist.get(i).getActivityTypeId() == 3254 || activityTasklist.get(i).getActivityTypeId() == 3255 || activityTasklist.get(i).getActivityTypeId() == 3256 || activityTasklist.get(i).getActivityTypeId() == 3257 || activityTasklist.get(i).getActivityTypeId() == 3258 || activityTasklist.get(i).getActivityTypeId() == 3259 || activityTasklist.get(i).getActivityTypeId() == 3260 || activityTasklist.get(i).getActivityTypeId() == 3261 || activityTasklist.get(i).getActivityTypeId() == 3262 || activityTasklist.get(i).getActivityTypeId() == 3263 || activityTasklist.get(i).getActivityTypeId() == 3265 || activityTasklist.get(i).getActivityTypeId() == 3266 || activityTasklist.get(i).getActivityTypeId() == 3267 || activityTasklist.get(i).getActivityTypeId() == 3268 || activityTasklist.get(i).getActivityTypeId() == 3269 || activityTasklist.get(i).getActivityTypeId() == 3270 || activityTasklist.get(i).getActivityTypeId() == 3271 || activityTasklist.get(i).getActivityTypeId() == 3272 || activityTasklist.get(i).getActivityTypeId() == 3273 || activityTasklist.get(i).getActivityTypeId() == 3274 || activityTasklist.get(i).getActivityTypeId() == 3275 || activityTasklist.get(i).getActivityTypeId() == 3276 || activityTasklist.get(i).getActivityTypeId() == 3277 || activityTasklist.get(i).getActivityTypeId() == 3278 || activityTasklist.get(i).getActivityTypeId() == 3279 || activityTasklist.get(i).getActivityTypeId() == 3280 || activityTasklist.get(i).getActivityTypeId() == 3281 || activityTasklist.get(i).getActivityTypeId() == 3282 || activityTasklist.get(i).getActivityTypeId() == 3283 || activityTasklist.get(i).getActivityTypeId() == 3284 || activityTasklist.get(i).getActivityTypeId() == 3285 || activityTasklist.get(i).getActivityTypeId() == 3286 || activityTasklist.get(i).getActivityTypeId() == 3287 || activityTasklist.get(i).getActivityTypeId() == 3288 || activityTasklist.get(i).getActivityTypeId() == 3289 || activityTasklist.get(i).getActivityTypeId() == 3291 || activityTasklist.get(i).getActivityTypeId() == 3292 || activityTasklist.get(i).getActivityTypeId() == 3293 || activityTasklist.get(i).getActivityTypeId() == 3294 || activityTasklist.get(i).getActivityTypeId() == 3295 || activityTasklist.get(i).getActivityTypeId() == 3296 || activityTasklist.get(i).getActivityTypeId() == 3297 || activityTasklist.get(i).getActivityTypeId() == 3298 || activityTasklist.get(i).getActivityTypeId() == 3299 || activityTasklist.get(i).getActivityTypeId() == 3300 || activityTasklist.get(i).getActivityTypeId() == 3301 || activityTasklist.get(i).getActivityTypeId() == 3302 || activityTasklist.get(i).getActivityTypeId() == 3303 || activityTasklist.get(i).getActivityTypeId() == 3304 || activityTasklist.get(i).getActivityTypeId() == 3305 || activityTasklist.get(i).getActivityTypeId() == 3306 || activityTasklist.get(i).getActivityTypeId() == 3307 || activityTasklist.get(i).getActivityTypeId() == 3308 || activityTasklist.get(i).getActivityTypeId() == 3309 || activityTasklist.get(i).getActivityTypeId() == 3310 || activityTasklist.get(i).getActivityTypeId() == 3311 || activityTasklist.get(i).getActivityTypeId() == 3312 || activityTasklist.get(i).getActivityTypeId() == 3313 || activityTasklist.get(i).getActivityTypeId() == 3314 || activityTasklist.get(i).getActivityTypeId() == 3315 || activityTasklist.get(i).getActivityTypeId() == 3316 || activityTasklist.get(i).getActivityTypeId() == 3317 || activityTasklist.get(i).getActivityTypeId() == 3318 || activityTasklist.get(i).getActivityTypeId() == 3319 || activityTasklist.get(i).getActivityTypeId() == 3320 || activityTasklist.get(i).getActivityTypeId() == 3321 || activityTasklist.get(i).getActivityTypeId() == 3323 || activityTasklist.get(i).getActivityTypeId() == 3324 || activityTasklist.get(i).getActivityTypeId() == 3325 || activityTasklist.get(i).getActivityTypeId() == 3326 || activityTasklist.get(i).getActivityTypeId() == 3327 || activityTasklist.get(i).getActivityTypeId() == 3328 || activityTasklist.get(i).getActivityTypeId() == 3329 || activityTasklist.get(i).getActivityTypeId() == 3330 || activityTasklist.get(i).getActivityTypeId() == 3331 || activityTasklist.get(i).getActivityTypeId() == 3332 || activityTasklist.get(i).getActivityTypeId() == 3333 || activityTasklist.get(i).getActivityTypeId() == 3334 || activityTasklist.get(i).getActivityTypeId() == 3335 || activityTasklist.get(i).getActivityTypeId() == 3336 || activityTasklist.get(i).getActivityTypeId() == 3337 || activityTasklist.get(i).getActivityTypeId() == 3338 || activityTasklist.get(i).getActivityTypeId() == 3339 || activityTasklist.get(i).getActivityTypeId() == 3340 || activityTasklist.get(i).getActivityTypeId() == 3341 || activityTasklist.get(i).getActivityTypeId() == 3342 || activityTasklist.get(i).getActivityTypeId() == 3343 || activityTasklist.get(i).getActivityTypeId() == 3344 || activityTasklist.get(i).getActivityTypeId() == 3345 || activityTasklist.get(i).getActivityTypeId() == 3346 || activityTasklist.get(i).getActivityTypeId() == 3347 || activityTasklist.get(i).getActivityTypeId() == 3348 || activityTasklist.get(i).getActivityTypeId() == 3349 || activityTasklist.get(i).getActivityTypeId() == 3350 || activityTasklist.get(i).getActivityTypeId() == 3352 || activityTasklist.get(i).getActivityTypeId() == 3353 || activityTasklist.get(i).getActivityTypeId() == 3354 || activityTasklist.get(i).getActivityTypeId() == 3355 || activityTasklist.get(i).getActivityTypeId() == 3356 || activityTasklist.get(i).getActivityTypeId() == 3357 || activityTasklist.get(i).getActivityTypeId() == 3358 || activityTasklist.get(i).getActivityTypeId() == 3359 || activityTasklist.get(i).getActivityTypeId() == 3360 || activityTasklist.get(i).getActivityTypeId() == 3361 || activityTasklist.get(i).getActivityTypeId() == 3362 || activityTasklist.get(i).getActivityTypeId() == 3363 || activityTasklist.get(i).getActivityTypeId() == 3364 || activityTasklist.get(i).getActivityTypeId() == 3365 || activityTasklist.get(i).getActivityTypeId() == 3366 || activityTasklist.get(i).getActivityTypeId() == 3367 || activityTasklist.get(i).getActivityTypeId() == 3368 || activityTasklist.get(i).getActivityTypeId() == 3369 || activityTasklist.get(i).getActivityTypeId() == 3370 || activityTasklist.get(i).getActivityTypeId() == 3371 || activityTasklist.get(i).getActivityTypeId() == 3372 || activityTasklist.get(i).getActivityTypeId() == 3373 || activityTasklist.get(i).getActivityTypeId() == 3374 || activityTasklist.get(i).getActivityTypeId() == 3375 || activityTasklist.get(i).getActivityTypeId() == 3376 || activityTasklist.get(i).getActivityTypeId() == 3377 || activityTasklist.get(i).getActivityTypeId() == 3379 || activityTasklist.get(i).getActivityTypeId() == 3380 || activityTasklist.get(i).getActivityTypeId() == 3381 || activityTasklist.get(i).getActivityTypeId() == 3382 || activityTasklist.get(i).getActivityTypeId() == 3383 || activityTasklist.get(i).getActivityTypeId() == 3384 || activityTasklist.get(i).getActivityTypeId() == 3385 || activityTasklist.get(i).getActivityTypeId() == 3386 || activityTasklist.get(i).getActivityTypeId() == 3387 || activityTasklist.get(i).getActivityTypeId() == 3388 || activityTasklist.get(i).getActivityTypeId() == 3389 || activityTasklist.get(i).getActivityTypeId() == 3390 || activityTasklist.get(i).getActivityTypeId() == 3391 || activityTasklist.get(i).getActivityTypeId() == 3392 || activityTasklist.get(i).getActivityTypeId() == 3393 || activityTasklist.get(i).getActivityTypeId() == 3394 || activityTasklist.get(i).getActivityTypeId() == 3395 || activityTasklist.get(i).getActivityTypeId() == 3396 || activityTasklist.get(i).getActivityTypeId() == 3397 || activityTasklist.get(i).getActivityTypeId() == 3398 || activityTasklist.get(i).getActivityTypeId() == 3399 || activityTasklist.get(i).getActivityTypeId() == 3400 || activityTasklist.get(i).getActivityTypeId() == 3401 || activityTasklist.get(i).getActivityTypeId() == 3402 || activityTasklist.get(i).getActivityTypeId() == 3403 || activityTasklist.get(i).getActivityTypeId() == 3405 || activityTasklist.get(i).getActivityTypeId() == 3406 || activityTasklist.get(i).getActivityTypeId() == 3407 || activityTasklist.get(i).getActivityTypeId() == 3408 || activityTasklist.get(i).getActivityTypeId() == 3409 || activityTasklist.get(i).getActivityTypeId() == 3410 || activityTasklist.get(i).getActivityTypeId() == 3411 || activityTasklist.get(i).getActivityTypeId() == 3412 || activityTasklist.get(i).getActivityTypeId() == 3413 || activityTasklist.get(i).getActivityTypeId() == 3414 || activityTasklist.get(i).getActivityTypeId() == 3415 || activityTasklist.get(i).getActivityTypeId() == 3416 || activityTasklist.get(i).getActivityTypeId() == 3417 || activityTasklist.get(i).getActivityTypeId() == 3419 || activityTasklist.get(i).getActivityTypeId() == 3420 || activityTasklist.get(i).getActivityTypeId() == 3421 || activityTasklist.get(i).getActivityTypeId() == 3422 || activityTasklist.get(i).getActivityTypeId() == 3423 || activityTasklist.get(i).getActivityTypeId() == 3424 || activityTasklist.get(i).getActivityTypeId() == 3425 || activityTasklist.get(i).getActivityTypeId() == 3426 || activityTasklist.get(i).getActivityTypeId() == 3427 || activityTasklist.get(i).getActivityTypeId() == 3428 || activityTasklist.get(i).getActivityTypeId() == 3429 || activityTasklist.get(i).getActivityTypeId() == 3430 || activityTasklist.get(i).getActivityTypeId() == 3432 || activityTasklist.get(i).getActivityTypeId() == 3433 || activityTasklist.get(i).getActivityTypeId() == 3434 || activityTasklist.get(i).getActivityTypeId() == 3435 || activityTasklist.get(i).getActivityTypeId() == 3436 || activityTasklist.get(i).getActivityTypeId() == 3437 ||
                    activityTasklist.get(i).getActivityTypeId() == 3438 || activityTasklist.get(i).getActivityTypeId() == 3439 || activityTasklist.get(i).getActivityTypeId() == 3440 || activityTasklist.get(i).getActivityTypeId() == 3441 || activityTasklist.get(i).getActivityTypeId() == 3442 || activityTasklist.get(i).getActivityTypeId() == 3443 || activityTasklist.get(i).getActivityTypeId() == 3444 || activityTasklist.get(i).getActivityTypeId() == 3446 || activityTasklist.get(i).getActivityTypeId() == 3447 || activityTasklist.get(i).getActivityTypeId() == 3448 || activityTasklist.get(i).getActivityTypeId() == 3449 || activityTasklist.get(i).getActivityTypeId() == 3450 || activityTasklist.get(i).getActivityTypeId() == 3451 || activityTasklist.get(i).getActivityTypeId() == 3452 || activityTasklist.get(i).getActivityTypeId() == 3453 || activityTasklist.get(i).getActivityTypeId() == 3454 || activityTasklist.get(i).getActivityTypeId() == 3455 || activityTasklist.get(i).getActivityTypeId() == 3456 || activityTasklist.get(i).getActivityTypeId() == 3457 || activityTasklist.get(i).getActivityTypeId() == 3459 || activityTasklist.get(i).getActivityTypeId() == 3460 || activityTasklist.get(i).getActivityTypeId() == 3461 || activityTasklist.get(i).getActivityTypeId() == 3462 || activityTasklist.get(i).getActivityTypeId() == 3463 || activityTasklist.get(i).getActivityTypeId() == 3464 || activityTasklist.get(i).getActivityTypeId() == 3465 || activityTasklist.get(i).getActivityTypeId() == 3466 || activityTasklist.get(i).getActivityTypeId() == 3467 || activityTasklist.get(i).getActivityTypeId() == 3468 || activityTasklist.get(i).getActivityTypeId() == 3469 || activityTasklist.get(i).getActivityTypeId() == 3470 || activityTasklist.get(i).getActivityTypeId() == 3471 || activityTasklist.get(i).getActivityTypeId() == 3472 || activityTasklist.get(i).getActivityTypeId() == 3474 || activityTasklist.get(i).getActivityTypeId() == 3475 || activityTasklist.get(i).getActivityTypeId() == 3476 || activityTasklist.get(i).getActivityTypeId() == 3477 || activityTasklist.get(i).getActivityTypeId() == 3478 || activityTasklist.get(i).getActivityTypeId() == 3479 || activityTasklist.get(i).getActivityTypeId() == 3480 || activityTasklist.get(i).getActivityTypeId() == 3481 || activityTasklist.get(i).getActivityTypeId() == 3482 || activityTasklist.get(i).getActivityTypeId() == 3483 || activityTasklist.get(i).getActivityTypeId() == 3484 || activityTasklist.get(i).getActivityTypeId() == 3485 || activityTasklist.get(i).getActivityTypeId() == 3487 || activityTasklist.get(i).getActivityTypeId() == 3488 || activityTasklist.get(i).getActivityTypeId() == 3489 || activityTasklist.get(i).getActivityTypeId() == 3490 || activityTasklist.get(i).getActivityTypeId() == 3491 || activityTasklist.get(i).getActivityTypeId() == 3492 || activityTasklist.get(i).getActivityTypeId() == 3493 || activityTasklist.get(i).getActivityTypeId() == 3494 || activityTasklist.get(i).getActivityTypeId() == 3495 || activityTasklist.get(i).getActivityTypeId() == 3496 || activityTasklist.get(i).getActivityTypeId() == 3497 || activityTasklist.get(i).getActivityTypeId() == 3498 || activityTasklist.get(i).getActivityTypeId() == 3499 || activityTasklist.get(i).getActivityTypeId() == 3501 || activityTasklist.get(i).getActivityTypeId() == 3502 || activityTasklist.get(i).getActivityTypeId() == 3503 || activityTasklist.get(i).getActivityTypeId() == 3504 || activityTasklist.get(i).getActivityTypeId() == 3505 || activityTasklist.get(i).getActivityTypeId() == 3506 || activityTasklist.get(i).getActivityTypeId() == 3507 || activityTasklist.get(i).getActivityTypeId() == 3508 || activityTasklist.get(i).getActivityTypeId() == 3509 || activityTasklist.get(i).getActivityTypeId() == 3510 || activityTasklist.get(i).getActivityTypeId() == 3511 || activityTasklist.get(i).getActivityTypeId() == 3513 || activityTasklist.get(i).getActivityTypeId() == 3514 || activityTasklist.get(i).getActivityTypeId() == 3515 || activityTasklist.get(i).getActivityTypeId() == 3516 || activityTasklist.get(i).getActivityTypeId() == 3517 || activityTasklist.get(i).getActivityTypeId() == 3518 || activityTasklist.get(i).getActivityTypeId() == 3519 || activityTasklist.get(i).getActivityTypeId() == 3520 || activityTasklist.get(i).getActivityTypeId() == 3521 || activityTasklist.get(i).getActivityTypeId() == 3522 || activityTasklist.get(i).getActivityTypeId() == 3523 || activityTasklist.get(i).getActivityTypeId() == 3524 || activityTasklist.get(i).getActivityTypeId() == 3525 || activityTasklist.get(i).getActivityTypeId() == 3526 || activityTasklist.get(i).getActivityTypeId() == 3528 || activityTasklist.get(i).getActivityTypeId() == 3529 || activityTasklist.get(i).getActivityTypeId() == 3530 || activityTasklist.get(i).getActivityTypeId() == 3531 || activityTasklist.get(i).getActivityTypeId() == 3532 || activityTasklist.get(i).getActivityTypeId() == 3533 || activityTasklist.get(i).getActivityTypeId() == 3534 || activityTasklist.get(i).getActivityTypeId() == 3535 || activityTasklist.get(i).getActivityTypeId() == 3536 || activityTasklist.get(i).getActivityTypeId() == 3537 || activityTasklist.get(i).getActivityTypeId() == 3538 || activityTasklist.get(i).getActivityTypeId() == 3539) {

                showHideActivity = activityTasklist.get(i);


                // if True We will get id
            }

        }

    }

    private void updateSingleEntryData(String _consignmentcode, String _activityTypeId, String _transactionId, int _statusTypeId, boolean inSertInHistory) {
        displayData = dataAccessHandler.getdisplayDetails(Queries.getInstance().getDisplayData(_transactionId));
        Log.d(ActivityTask.class.getSimpleName(), "==> Analysis Count Of DisplayData :" + displayData.size());
        Log.d(ActivityTask.class.getSimpleName(), "==> Analysis Count Of Statusid :" + _statusTypeId);
        Log.d(ActivityTask.class.getSimpleName(), "==> Analysis inSertInHistory :" + inSertInHistory);
        Comments = dataAccessHandler.getOnlyOneValueFromDb(Queries.gethistory(consignmentCode, activityTypeId,_transactionId));
        Userid = dataAccessHandler.getOnlyOneValueFromDb(Queries.gethistoryuser(consignmentCode, activityTypeId,_transactionId));
        Date_history = dataAccessHandler.getOnlyOneValueFromDb(Queries.getDate(consignmentCode, activityTypeId,_transactionId));
        Log.d(ActivityTask.class.getSimpleName(), "==> Analysis Status History :2337" + Comments + Userid + Date_history);
        if (displayData != null && displayData.size() > 0) {


            for (int j = 0; j < dataValue.size(); j++) {

                final List<LinkedHashMap> listKeyUpdate = new ArrayList<>();
                LinkedHashMap updateXref = new LinkedHashMap();
                // map2.put("Id", 0);
                updateXref.put("TransactionId", _transactionId);
                updateXref.put("FieldId", dataValue.get(j).id);
                updateXref.put("Value", dataValue.get(j).value);

                Log.e("=============>",local_ImagePath+"");
                if ( dataValue.get(j).value == "NurseryImage"  ||  dataValue.get(j).value.equalsIgnoreCase("NurseryImage")) {
                    if (local_ImagePath != null) {

                        updateXref.put("FilePath", local_ImagePath);

                    }
                }else{
                    updateXref.put("FilePath","");
                }


               // updateXref.put("FilePath", "");
                updateXref.put("IsActive", 1);
//                updateXref.put("CreatedByUserId", CommonConstants.USER_ID);
                updateXref.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                updateXref.put("UpdatedByUserId", CommonConstants.USER_ID);
                updateXref.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                updateXref.put("ServerUpdatedStatus", 0);

                listKeyUpdate.add(updateXref);

                dataAccessHandler.updateData("SaplingActivityXref", listKeyUpdate, true, " where TransactionId = " + "'" + _transactionId + "'" + " AND FieldId = " + dataValue.get(j).getId(), new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {


                        }
                        Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   => Update of SaplingXref Done");
                        // Update Sapling Activity status


                    }
                });

            }


            LinkedHashMap activityMap = new LinkedHashMap();
            activityMap.put("TransactionId", _transactionId);
            activityMap.put("ConsignmentCode", _consignmentcode);
            activityMap.put("ActivityId", _activityTypeId);
            activityMap.put("StatusTypeId", 346);  // TODO Check with In DB
            activityMap.put("Comment", "");
            activityMap.put("IsActive", 1);
            activityMap.put("CreatedByUserId", CommonConstants.USER_ID);
            activityMap.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            activityMap.put("UpdatedByUserId", CommonConstants.USER_ID);
            activityMap.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            activityMap.put("ServerUpdatedStatus", 0);
            final List<LinkedHashMap> activityList = new ArrayList<>();
            activityList.add(activityMap);

            dataAccessHandler.updateData("SaplingActivity", activityList, true, " where TransactionId = " + "'" + _transactionId + "'", new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   => Update of SaplingActivity");

                    LinkedHashMap status = new LinkedHashMap();

                    status.put("ConsignmentCode", _consignmentcode);
                    status.put("ActivityId", _activityTypeId);
                    status.put("StatusTypeId", _statusTypeId);
                    status.put("CreatedByUserId", CommonConstants.USER_ID);
                    status.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    status.put("UpdatedByUserId", CommonConstants.USER_ID);
                    status.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    status.put("JobCompletedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    status.put("ServerUpdatedStatus", 0);

                    final List<LinkedHashMap> statusList = new ArrayList<>();
                    statusList.add(status);
                    dataAccessHandler.updateData("SaplingActivityStatus", statusList, true, " where ConsignmentCode = " + "'" + _consignmentcode + "' AND ActivityId ='" + _activityTypeId + "'", new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {

                            Log.d(ActivityTask.class.getSimpleName(), "==>  Analysis ==> SaplingActivityStatus INSERT COMPLETED");
                            Log.d(ActivityTask.class.getSimpleName(), "==>  Analysis ==> Update Task Completed");
                            finish();
                            Toast.makeText(ActivityTask.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            if (inSertInHistory) {

                LinkedHashMap status = new LinkedHashMap();
                status.put("TransactionId", _transactionId);
                status.put("StatusTypeId", 349);
          status.put("Comments",Comments);
               status.put("CreatedByUserId", Userid);
                status.put("CreatedDate", Date_history);
//                status.put("UpdatedByUserId", CommonConstants.USER_ID);
//                status.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                status.put("ServerUpdatedStatus", 0);

                final List<LinkedHashMap> historyList = new ArrayList<>();
                historyList.add(status);
                dataAccessHandler.insertMyDataa("SaplingActivityHistory", historyList, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {

                        if (success) {
                            if (CommonUtils.isNetworkAvailable(ActivityTask.this)) {


                                DataSyncHelper.performRefreshTransactionsSync(ActivityTask.this, new ApplicationThread.OnComplete() {
                                    @Override
                                    public void execute(boolean success, Object result, String msg) {
                                        if (success)     {
                                            ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ActivityTask.this, "Successfully data sent to server", Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(ActivityTask.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
//                                                    UiUtils.showCustomToastMessage("Successfully data sent to server",ActivityTask.this, 0);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                                @Override
                                                public void run() {

                                                  //  Toasty.error(ActivityTask.this, "Data sending failed", 10).show();
//                                        Toast.makeText(RefreshSyncActivity.this, "Data sending failed", Toast.LENGTH_SHORT).show();
                                                    ProgressBar.hideProgressBar();
                                                //    Toast.makeText(ActivityTask.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                                                    finish();

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                         //   finish();
                            Toast.makeText(ActivityTask.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                            Log.d(ActivityTask.class.getSimpleName(), "==> SaplingActivityHistoryf INSERT COMPLETED");
                        }

                    }
                });
            }

        }
    }


    @Override
    public void onClick(View view) {
        int btnid = 1;
        int id = view.getId();

        if (view.getId() == ButtonId) {
            if (goValidate())
                saveData();

        }

        // check box check default
        if (id == 173 || id == 72 || id == 79 || id == 95 || id == 101 || id == 112 || id == 121 || id == 130 || id == 139 || id == 149 || id == 164 || id == 536 || id == 253 || id == 259 || id == 269 || id == 280 || id == 289 || id == 298 || id == 307 || id == 317 || id == 332 || id == 341 || id == 368 || id == 385 || id == 391 || id == 397 || id == 403 || id == 414 || id == 423 || id == 432 || id == 611 || id == 626 || id == 641 || id == 656 || id == 671 || id == 686 || id == 701 || id == 716 || id == 731 || id == 746 || id == 761 || id == 776 || id == 1079 || id == 1088 || id == 1097 || id == 1106 || id == 1115 || id == 1124 || id == 1133 || id == 1142 || id == 1151 || id == 1160 || id == 1169 || id == 1178 || id == 1178 || id == 1187 || id == 1196 || id == 1205 || id == 1214 || id == 1223 || id == 1230 || id == 1236 || id == 1242 || id == 1249 || id == 1255 || id == 1261 || id == 1268 || id == 1274 || id == 1280 || id == 1287 || id == 1293 || id == 1299 || id == 1305 || id == 1311 || id == 1317 || id == 1323 || id == 1329 || id == 1335 || id == 1341 || id == 1347 || id == 1353 || id == 1359 || id == 1365 || id == 1371 || id == 1382 || id == 1393 || id == 1402 || id == 1411 || id == 1420 || id == 1429 || id == 1438 || id == 1447 || id == 1457 || id == 1470 || id == 1476 || id == 1482 || id == 1488 || id == 1494 || id == 1500 || id == 1506 || id == 1512 || id == 1518 || id == 1524 || id == 1530 || id == 1536 || id == 1542 || id == 1548 || id == 1554 || id == 1560 || id == 1566 || id == 1572 || id == 1578 || id == 1584 || id == 1590 || id == 1596 || id == 1602 || id == 1617 || id == 1632 || id == 1647 || id == 1662 || id == 1677 || id == 1692 || id == 1707 || id == 1722 || id == 1737 || id == 1832 || id == 1841 || id == 1850 || id == 1859 || id == 1868 || id == 1877 || id == 1886 || id == 1895 || id == 1904 || id == 1913 || id == 2022 || id == 2031 || id == 2040 || id == 2049 || id == 2058 || id == 2067 || id == 2076 || id == 2085 || id == 1187 || id == 1196 || id == 1205 || id == 1214 || id == 1230 || id == 1236 || id == 1242 || id == 1249 || id == 2094 || id == 2103 || id == 2112 || id == 2123 || id == 2145 || id == 2156 || id == 2167 || id == 2178 || id == 2189 || id == 2200 || id == 2211 || id == 2222 || id == 2231 || id == 2240 || id == 2249 || id == 2258 || id == 2267 || id == 2276 || id == 2285 || id == 2294 || id == 2303 || id == 2312 || id == 2321 || id == 2330 || id == 2339 || id == 2348 || id == 2357 || id == 2366 || id == 2375 || id == 2384 || id == 2393 || id == 2402 || id == 2411 || id == 2420 || id == 2429 || id == 2438 || id == 2447 || id == 2456 || id == 2465 || id == 2474 || id == 2483 || id == 2492 || id == 2502 || id == 2512 || id == 2522 || id == 2532 || id == 2542 || id == 2552 || id == 2562 || id == 2572 || id == 2582 || id == 2592 || id == 2598 || id == 2604 || id == 2610 || id == 2621 || id == 2632 || id == 2643 || id == 2654 || id == 2663 || id == 2672 || id == 2681 || id == 2690 || id == 2699 || id == 2708 || id == 2717 || id == 2726 || id == 2735 || id == 2744 || id == 2753 || id == 2762 || id == 2772 || id == 2782 || id == 2792 || id == 2802 || id == 2817 || id == 2832 || id == 2847 || id == 2894 || id == 2903 || id == 2912 || id == 2921 || id == 2970 || id == 2979 || id == 3006 || id == 3012 || id == 3018 || id == 3024 || id == 3030 || id == 3036 || id == 3042 || id == 3048 || id == 3054 || id == 3062 || id == 3077 || id == 1760 || id == 1932 || id == 2134 || id == 959 || id == 441 ||
                id == 1752 || id == 181 || id == 253 || id == 259 || id == 269 || id == 280 || id == 1019 || id == 871 || id == 2940 || id == 1962 || id == 1972 || id == 1792 || id == 1800 || id == 1982 || id == 1808 || id == 1992 || id == 1816 || id == 2002 || id == 1824 || id == 2012 || id == 475 || id == 484 || id == 451 || id == 466 || id == 492 || id == 475 || id == 2862 || id == 2930 || id == 2870 || id == 2940 || id == 492 || id == 466 || id == 475 || id == 2862 || id == 2930 || id == 2878 || id == 2950 || id == 2988 || id == 2886 || id == 2997 || id == 2960 || id == 289 || id == 298 || id == 307 || id == 317 || id == 332 || id == 341 || id == 368 || id == 385 || id == 391 || id == 397 || id == 403 || id == 414 || id == 423 || id == 432 || id == 611 || id == 626 || id == 641 || id == 656 || id == 671 || id == 686 || id == 701 || id == 716 || id == 731 || id == 746 || id == 761 || id == 776 || id == 581 || id == 791 || id == 799 || id == 1079 || id == 1088 || id == 1097 || id == 1106 || id == 1115 || id == 1124 || id == 1133 || id == 1142 || id == 1151 || id == 1160 || id == 1169 || id == 1178 || id == 1178 || id == 1187 || id == 1196 || id == 1205 || id == 1214 || id == 1223 || id == 1230 || id == 1236 || id == 1242 || id == 1249 || id == 1255 || id == 1261 || id == 1268 || id == 1274 || id == 1280 || id == 1287 || id == 1293 || id == 1317 || id == 1323 || id == 1329 || id == 1335 || id == 1341 || id == 1347 || id == 1353 || id == 1359 || id == 1365 || id == 1371 || id == 1382 || id == 1393 || id == 1402 || id == 1411 || id == 1420 || id == 1429 || id == 1438 || id == 1447 || id == 1457 || id == 1470 || id == 1476 || id == 1482 || id == 1488 || id == 1494 || id == 1500 || id == 1506 || id == 1512 || id == 1518 || id == 1524 || id == 1530 || id == 1536 || id == 1542 || id == 1548 || id == 1554 || id == 1560 || id == 1566 || id == 1572 || id == 1578 || id == 1584 || id == 1590 || id == 1596 || id == 1602 || id == 1617 || id == 1632 || id == 1647 || id == 1662 || id == 1677 || id == 1692 || id == 1707 || id == 1722 || id == 1737 || id == 1832 || id == 1841 || id == 1850 || id == 1859 || id == 1868 || id == 1877 || id == 1886 || id == 1895 || id == 1904 || id == 1913 || id == 2022 || id == 2031 || id == 2040 || id == 2049 || id == 2058 || id == 2067 || id == 2076 || id == 2085 || id == 1187 || id == 1196 || id == 1205 || id == 1214 || id == 1223 || id == 1230 || id == 1236 || id == 1242 || id == 1249 || id == 2094 || id == 2103 || id == 2112 || id == 2123 || id == 2145 || id == 2156 || id == 2167 || id == 2178 || id == 2189 || id == 2200 || id == 2211 || id == 2222 || id == 2231 || id == 2240 || id == 2249 || id == 2258 || id == 2267 || id == 2276 || id == 2285 || id == 2294 || id == 2303 || id == 2312 || id == 2321 || id == 2330 || id == 2339 || id == 2348 || id == 2357 || id == 2366 || id == 2375 || id == 2384 || id == 2393 || id == 2402 || id == 2411 || id == 2420 || id == 2429 || id == 2438 || id == 2447 || id == 2456 || id == 2465 || id == 2474 || id == 2483 || id == 2492 || id == 2502 || id == 2512 || id == 2522 || id == 2532 || id == 2542 || id == 2552 || id == 2562 || id == 2572 || id == 2582 || id == 2592 || id == 2598 || id == 2604 || id == 2610 || id == 2621 || id == 2632 || id == 2643 || id == 2654 || id == 2663 || id == 2672 || id == 2681 || id == 2690 || id == 2699 || id == 2708 || id == 2717 || id == 2726 || id == 2735 || id == 2744 || id == 2753 || id == 2762 || id == 2772 || id == 2782 || id == 2792 || id == 2802 || id == 2817 || id == 2832 || id == 2847 || id == 2894 || id == 2903 || id == 2912 || id == 2921 || id == 2970 || id == 2979 || id == 3006 || id == 3012 || id == 3018 || id == 3024 || id == 3030 || id == 3036 || id == 3042 || id == 3048 || id == 3054 || id == 3062 || id == 3077 || id == 979 || id == 551 || id == 791 || id == 919 || id == 566 || id == 929 || id == 807 || id == 939 || id == 596 || id == 815 || id == 949 || id == 823 || id == 951 || id == 831 || id == 969 || id == 839 || id == 847 || id == 989 || id == 855 || id == 979 || id == 999 || id == 863 || id == 1009 || id == 879 || id == 1029 || id == 887 || id == 1049 || id == 1039 || id == 895 || id == 903 || id == 1059 || id == 911 || id == 1069 || id == 350 || id == 1922 || id == 358 || id == 1942 || id == 1768 || id == 1776 || id == 1952 || id == 1784) {
// check box check & Uncheck
            if (((CheckBox) view).isChecked()) {

                checkBoxChecked(true);

                try {


                    int Feild_id = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getFeildID(activityTypeId));

                    CheckBox chk_is = findViewById(Feild_id);

                    chk_is.setEnabled(true);
                    if(enableEditing){
                     //   chk_is.setChecked(false);
                        String reject_check = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().rejectcheck(Feild_id, intentTransactionId));
                       Log.e("=================================>reject_check", reject_check + "");
                        if (reject_check != null && reject_check.equalsIgnoreCase("true")) {
                            chk_is.setEnabled(true);
                        } else if (reject_check == null){
                        chk_is.setChecked(false);}
                        else{
                            chk_is.setChecked(false);
                      chk_is.setEnabled(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else {
                try {

                    int Feild_id = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getFeildID(activityTypeId));

                    CheckBox chk_is = findViewById(Feild_id);

                    chk_is.setChecked(true);
                    chk_is.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Need to disble remainign widgets
                for (ActivityTasks widget : activityTasklist) {

                    String optional = dataAccessHandler.getSingleValueInt(Queries.getIsoptionalField(widget.getId()));
                    Log.d(ActivityTask.class.getSimpleName(), "===> analysis ==> isOptional :" + optional);
                    if (optional != null && !StringUtils.isEmpty(optional)) {
                        try {
                            findViewById(widget.getId()).setVisibility(View.GONE);
                            findViewById(widget.getId() + 9000).setVisibility(View.GONE);
                            FileImage.setVisibility(View.GONE);
                        } catch (Exception exc) {

                        }

                            try {
                            EditText txt = findViewById(widget.getId());
                            txt.setText("");
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    else {

                        try {
                            findViewById(widget.getId() + 9000).setVisibility(View.VISIBLE);
                            findViewById(widget.getId()).setVisibility(View.VISIBLE);
                            FileImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


                // Toast.makeText(ActivityTask.this, "UN-CHECKED", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void checkBoxChecked(boolean cleardata) {


        for (ActivityTasks widget : activityTasklist) {

            String optional = dataAccessHandler.getSingleValueInt(Queries.getIsoptionalField2(widget.getId()));
            Log.d(ActivityTask.class.getSimpleName(), "===> analysis ==> isOptional 1396:" + optional);
            if (optional != null && !StringUtils.isEmpty(optional))
            {
                findViewById(widget.getId()).setVisibility(View.GONE);
                findViewById(widget.getId() + 9000).setVisibility(View.GONE);
                FileImage.setVisibility(View.VISIBLE);
//
                try {
                    EditText txt = findViewById(widget.getId());
                    if(cleardata){
                        txt.setText("");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {

                try {
                           findViewById(widget.getId()).setVisibility(View.VISIBLE);
                           findViewById(widget.getId() + 9000).setVisibility(View.VISIBLE);
                           FileImage.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    Log.d(ActivityTask.class.getSimpleName(), "===> Analysis YES NO CHK  ID:2397==== " + yesnoCHeckbox);

        }
    }


    @Override
    public void onFocusChange(View view, boolean b) {


        if (findViewById(ButtonId).getVisibility() == View.VISIBLE){
            // SetTextFor formula
            Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis onFocusChange() id : " + view.getId() + "   isView showing :" + b);
        int id = view.getId();


        if (id == 51 || id == 52 || id == 53) {
            try {
                int int52 = 52, int53 = 53, int51 = 51, int7 = 7;
                EditText edt53 = findViewById(int53);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int51))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int52)));

                edt53.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (id == 52 || id == 51 || id == 54) {
            try {
                int int51 = 51, int53 = 53, int54 = 54;
                EditText edt54 = findViewById(int54);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int51))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int53)));
                edt54.setText(finalValue + "");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id == 52 || id == 54) {
            try {
                int int52 = 52, int54 = 54, int55 = 55, int61 = 61;
                EditText edt55 = findViewById(int55);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int52))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int54)));
                //    edt55.setText(finalValue + "");
                EditText edt61 = findViewById(int61);
                edt61.setText(finalValue + "");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id == 60 || id == 61) {

            int int60 = 60, int61 = 61, int62 = 62;
            EditText edt62 = findViewById(int62);
            try {
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int60))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int61)));

                edt62.setText(finalValue + "");

            } catch (Exception e) {
                e.printStackTrace();
            }
            Button btn = (Button) findViewById(ButtonId);

            if (enableEditing) {

                int value_61 = 0;
                try {
                    value_61 = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowingEdit(consignmentCode, 61, intentTransactionId)));
                    int finalValue = finalValue60 - value_61 - CommonUtils.getIntFromEditText(((EditText) findViewById(int61)));
                    edt62.setText(finalValue + "");
                } catch (Exception exception) {
                    exception.printStackTrace();

                }


            } else {

                int value_61 = 0;
                try {
                    value_61 = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowing(consignmentCode, 61)));
                    int finalValue = finalValue60 - value_61 - CommonUtils.getIntFromEditText(((EditText) findViewById(int61)));
                    edt62.setText(finalValue + "");
                } catch (Exception exception) {
                    exception.printStackTrace();

                }


            }


        } else if (id == 505 || id == 504 || id == 503) {
            try {
                int int502 = 502, int504 = 504, int505 = 505, int503 = 503;
                EditText edt505 = findViewById(int505);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int502))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int503)));
                //    edt55.setText(finalValue + "");
                EditText edt504 = findViewById(int504);
                edt504.setText(finalValue + "");


                try {
                    DecimalFormat df = new DecimalFormat("####0.00");
                   // int percentage = (CommonUtils.getIntFromEditText(((EditText) findViewById(int503))) * 100 / CommonUtils.getIntFromEditText(((EditText) findViewById(int502))));
                 //   double res = (amount / 100.0f) * 10;
                    double  percentage =((double)CommonUtils.getIntFromEditText(((EditText) findViewById(int503))) * 100 / (double)CommonUtils.getIntFromEditText(((EditText) findViewById(int502))));

                    edt505.setText(df.format(percentage )+ "");
                    Log.e("Germnationpercentage=============", percentage + "");
                } catch (NumberFormatException e) {
                    Log.e("Germnationpercentage=============", e.getMessage() + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (id == 508 || id == 507) {

            try {

                int int506 = 506, int507 = 507, int508 = 508;


                EditText edt508 = findViewById(int508);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int506))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int507)));

                edt508.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 508 || id == 507 || id == 509) {

            try {

                int int506 = 506, int508 = 508, int509 = 509;


                EditText edt509 = findViewById(int509);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int506))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int508)));

                edt509.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (id == 512 || id == 511) {

            try {

                int int510 = 510, int512 = 512, int511 = 511;


                EditText edt512 = findViewById(int512);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int510))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int511)));
                edt512.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 512 || id == 513 || id == 511) {

            try {

                int int510 = 510, int512 = 512, int513 = 513;


                EditText edt513 = findViewById(int513);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int510))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int512)));

                edt513.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 516 || id == 517 || id == 515) {

            try {

                int int514 = 514, int516 = 516, int515 = 515;


                EditText edt516 = findViewById(int516);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int514))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int515)));

                edt516.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 516 || id == 517 || id == 515) {

            try {

                int int514 = 514, int516 = 516, int517 = 517;


                EditText edt517 = findViewById(int517);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int514))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int516)));

                edt517.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 520 || id == 519) {

            try {

                int int518 = 518, int520 = 520, int519 = 519;
                EditText edt520 = findViewById(int520);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int518))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int519)));

                edt520.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (id == 520 || id == 521 || id == 519) {

            try {

                int int518 = 518, int520 = 520, int521 = 521;
                EditText edt521 = findViewById(int521);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int518))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int520)));

                edt521.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (id == 523 || id == 524) {

            try {

                int int523 = 523, int524 = 524, int522 = 522;


                EditText edt524 = findViewById(int524);

                int finalValue =  CommonUtils.getIntFromEditText(((EditText) findViewById(int522))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int523)));
                Log.d("Transplationloss============", +finalValue + "");
                edt524.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (id == 526 || id == 527 || id == 528) {

            try {

                int int525 = 525, int526 = 526, int528 = 528;
                EditText edt528 = findViewById(int528);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int525))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int526)));

                edt528.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (id == 535 || id == 528 || id == 530 || id == 531 || id == 532 || id == 533 || id == 534) {
            // DOne
            try {
                int int528 = 528, int535 = 535, int525 = 525;
                EditText edt535 = findViewById(int535);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int525))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int528)));
                Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis finalValue535 : " + finalValue);
                edt535.setText(finalValue + "");
            } catch (Exception e) { e.printStackTrace();
            }

        }


        if (id == 3087 || id == 3088 || id == 3089) {

            try {

                int int3086 = 3086, int3087 = 3087, int3089 = 3089;
                EditText edt3089 = findViewById(int3089);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3086))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3087)));

                edt3089.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 3089 || id == 3091 || id == 3092 || id == 3093 || id == 3094 || id == 3095 || id == 3096) {
            // DOne
            try {
                int int3096 = 3096, int3086 = 3086, int3089 = 3089;
                EditText edt3096 = findViewById(int3096);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3086))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3089)));
                Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis finalValue535 : " + finalValue);
                edt3096.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (id == 3097 || id == 3098 || id == 3099) {

            try {

                int int3100 = 3100, int3097 = 3097, int3098 = 3098;
                EditText edt3100 = findViewById(int3100);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3097))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3098)));

                edt3100.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 3100 || id == 3102 || id == 3103 || id == 3104 || id == 3105 || id == 3106 || id == 3107) {
            // DOne
            try {
                int int3097 = 3097, int3107 = 3107, int3100 = 3100;
                EditText edt3107 = findViewById(int3107);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3097))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3100)));
                Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis finalValue535 : " + finalValue);
                edt3107.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (id == 3109 || id == 3110 || id == 3111) {

            try {

                int int3111 = 3111, int3108 = 3108, int3109 = 3109;
                EditText edt3111 = findViewById(int3111);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3108))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3109)));

                edt3111.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 3113 || id == 3114 || id == 3115 || id == 3116 || id == 3117 || id == 3118 || id == 3111) {
            // DOne
            try {
                int int3111 = 3111, int3118 = 3118, int3108 = 3108;
                EditText edt3118 = findViewById(int3118);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3108))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3111)));
                Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis finalValue535 : " + finalValue);
                edt3118.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (id == 3120 || id == 3121 || id == 3122) {

            try {

                int int3119 = 3119, int3120 = 3120, int3122 = 3122;
                EditText edt3122 = findViewById(int3122);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3119))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3120)));

                edt3122.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (id == 3122 || id == 3124 || id == 3125 || id == 3126 || id == 3127 || id == 3130 || id == 3129) {
            // DOne
            try {
                int int3122 = 3122, int3130 = 3130, int3119 = 3119;
                EditText edt3130 = findViewById(int3130);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3119))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3122)));
                Log.d(ActivityTask.class.getSimpleName(), " ===> Analysis finalValue535 : " + finalValue);
                edt3130.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (id == 3134 || id == 3132 || id == 3133) {

            try {

                int int3134 = 3134, int3131 = 3131, int3132 = 3132;
                EditText edt3134 = findViewById(int3134);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3131))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3132)));

                edt3134.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 3134 || id == 3136 || id == 3137 || id == 3138 || id == 3139 || id == 3140 || id == 3141) {
            // DOne
            try {
                int int3131 = 3131, int3141 = 3141, int3134 = 3134;
                EditText edt3141 = findViewById(int3141);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3131))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3134)));

                edt3141.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (id == 3143 || id == 3144 || id == 3145) {

            try {

                int int3145 = 3145, int3143 = 3143, int3142 = 3142;
                EditText edt3145 = findViewById(int3145);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3142))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3143)));

                edt3145.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 3145 || id == 3147 || id == 3152 || id == 3148 || id == 3149 || id == 3150 || id == 3151) {
            // DOne
            try {
                int int3145 = 3145, int3142 = 3142, int3152 = 3152;
                EditText edt3152 = findViewById(int3152);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3142))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3145)));

                edt3152.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        if (id == 3154 || id == 3155 || id == 3156) {

            try {

                int int3156 = 3156, int3153 = 3153, int3154 = 3154;
                EditText edt3156 = findViewById(int3156);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3153))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3154)));

                edt3156.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 3156 || id == 3158 || id == 3159 || id == 3160 || id == 3161 || id == 3162 || id == 3163) {
            // DOne
            try {
                int int3153 = 3153, int3156 = 3156, int3163 = 3163;
                EditText edt3163 = findViewById(int3163);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3153))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3156)));

                edt3163.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (id == 3165 || id == 3166 || id == 3167) {

            try {

                int int3167 = 3167, int3164 = 3164, int3165 = 3165;
                EditText edt3167 = findViewById(int3167);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3164))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3165)));

                edt3167.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 3167 || id == 3169 || id == 3170 || id == 3171 || id == 3172 || id == 3173 || id == 3174) {
            // DOne
            try {
                int int3174 = 3174, int3164 = 3164, int3167 = 3167;
                EditText edt3174 = findViewById(int3174);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3164))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3167)));

                edt3174.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (id == 3176 || id == 3177 || id == 3178) {

            try {

                int int3178 = 3178, int3175 = 3175, int3176 = 3176;
                EditText edt3178 = findViewById(int3178);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3175))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3176)));

                edt3178.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (id == 3178 || id == 3185 || id == 3180 || id == 3181 || id == 3182 || id == 3183 || id == 3184) {
            // DOne
            try {
                int int3175 = 3175, int3185 = 3185, int3178 = 3178;
                EditText edt3185 = findViewById(int3185);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3175))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3178)));

                edt3185.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (id == 3187 || id == 3188 || id == 3189) {

            try {

                int int3186 = 3186, int3187 = 3187, int3189 = 3189;
                EditText edt3189 = findViewById(int3189);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3186))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3187)));

                edt3189.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (id == 3189 || id == 3195 || id == 3196 || id == 3191 || id == 3192 || id == 3193 || id == 3194) {
            // DOne
            try {
                int int3196 = 3196, int3186 = 3186, int3189 = 3189;
                EditText edt3196 = findViewById(int3196);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3186))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3189)));

                edt3196.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (id == 3198 || id == 3199 || id == 3200) {

            try {

                int int3197 = 3197, int3198 = 3198, int3200 = 3200;
                EditText edt3200 = findViewById(int3200);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3197))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3198)));

                edt3200.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (id == 3200 || id == 3202 || id == 3203 || id == 3204 || id == 3205 || id == 3206 || id == 3207) {
            // DOne
            try {
                int int3197 = 3197, int3207 = 3207, int3200 = 3200;
                EditText edt3207 = findViewById(int3207);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3197))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3200)));

                edt3207.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (id == 3211 || id == 3209 || id == 3210) {

            try {

                int int3211 = 3211, int3208 = 3208, int3209 = 3209;
                EditText edt3211 = findViewById(int3211);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3208))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3209)));

                edt3211.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == 3211 || id == 3213 || id == 3214 || id == 3215 || id == 3216 || id == 3217 || id == 3218) {
            // DOne
            try {
                int int3208 = 3208, int3211 = 3211, int3218 = 3218;
                EditText edt3218 = findViewById(int3218);
                int finalValue = CommonUtils.getIntFromEditText(((EditText) findViewById(int3208))) - CommonUtils.getIntFromEditText(((EditText) findViewById(int3211)));

                edt3218.setText(finalValue + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
        else{
            int id = view.getId();
            try {
                EditText et = findViewById(id);
                if (findViewById(ButtonId).getVisibility() == View.GONE) {
                    et.setFocusable(false);
                    et.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                    et.setClickable(false);
                }  } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (!isFinishing()) {
                    try {
                        handleBigCameraPhoto();
                    } catch (WindowManager.BadTokenException e) {
                        Log.e("WindowManagerBad ", e.toString());
                    }
                }

//                if (resultCode == RESULT_OK && typeSelected == Manual_Weigh) {
//                    handleBigCameraPhoto1();
//                }
                break;
            } // ACTION_TAKE_PHOTO_B
            case CAMERA_REQUEST2: {


                    if (resultCode == RESULT_OK) {


                        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                        FileImage.setImageBitmap(thumbnail);
                        saveImage(thumbnail);
                        //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();





                }
//
                break;
            }

        } // switch
    }



        public String saveImage(Bitmap myBitmap) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            File wallpaperDirectory = new File(
                    Environment.getExternalStorageDirectory() + "/3F_Pictures/" + "NurseryPhotos_loss");
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
                android.util.Log.d("TAG", "File Saved::--->1" + f.getAbsolutePath());
                local_ImagePath = f.getAbsolutePath();
                android.util.Log.d("TAG", "File Saved::--->2" + local_ImagePath);
                return f.getAbsolutePath();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return "";
        }



    private void handleBigCameraPhoto() {
        Log.e("================>622",mCurrentPhotoPath);
        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();

        }

    }



    private void setPic() {
        Log.e("================>622",mCurrentPhotoPath);
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        int targetW = image.getWidth();
        int targetH = image.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        bitmap = ImageUtility.rotatePicture(90, bitmap);

        currentBitmap = bitmap;

       // image.setImageBitmap(currentBitmap);
        Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   New Transaction ID  2918:" + transactionId);
        Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   New Transaction ID  2919:" + transactionIdNew);

        List<LinkedHashMap> repodetails = new ArrayList<>();
        LinkedHashMap lossrepo = new LinkedHashMap();

        transactionId = dataAccessHandler.getSingleValue(Queries.getInstance().getTransactionIdUsingConsimentCode(consignmentCode, activityTypeId));
            if (null != transactionId && !transactionId.isEmpty() && !TextUtils.isEmpty(transactionId)) {
                Log.d(ActivityTask.class.getSimpleName(), "==> Analysis   transactionId  2919:" + transactionId);
        lossrepo.put("TransactionId", transactionId);}
        else{
            lossrepo.put("TransactionId", transactionIdNew);
        }
        lossrepo.put("FileName",  "");
        if(mCurrentPhotoPath!=null){
            lossrepo.put("FileLocation", mCurrentPhotoPath);}
        lossrepo.put("FileExtension", ".jpg");
        lossrepo.put("CreatedByUserId", CommonConstants.USER_ID);
        lossrepo.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        lossrepo.put("ServerUpdatedStatus", 0);

        repodetails.add(lossrepo);
        Log.d(ActivityTask.class.getSimpleName(), "repodetails====="+ repodetails +"");
        dataAccessHandler.insertMyDataa("CullingLossFileRepository", repodetails, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    Log.d(ActivityTask.class.getSimpleName(), "==>  Analysis ==> CullingLossFileRepository INSERT COMPLETED");
                    Log.d(ActivityTask.class.getSimpleName(), "==>  Analysis ==> Add new Task Completed");
                    addImageData();

               //     Toast.makeText(ActivityTask.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
       sendBroadcast(mediaScanIntent);
    }


    @Override
    public void onImageClicked(int id) {
Log.e("============>",id+"");
        dataAccessHandler.deleteRow("CullingLossFileRepository", "id", id+"", true, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {

                    addImageData();
                    Toast.makeText(ActivityTask.this, "Image Deleted Successfully", Toast.LENGTH_SHORT).show();
                    android.util.Log.v(LOG_TAG, "@@@  image deletion success for " + "CullingLossFileRepository");
                } else {
                    android.util.Log.v(LOG_TAG, "@@@ image  deletion failed for " + "CullingLossFileRepository");
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}

class KeyValues {
    int id;
    String value;

    public KeyValues(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}