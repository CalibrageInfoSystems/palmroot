package com.calibrage.palmroot.ui.irrigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataSyncHelper;
import com.calibrage.palmroot.dbmodels.NurseryIrrigationLogXref;
import com.calibrage.palmroot.ui.ActivityTask;
import com.calibrage.palmroot.ui.HomeActivity;
import com.calibrage.palmroot.uihelper.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class IrrigationActivity extends AppCompatActivity {
    public static final String LOG_TAG = IrrigationActivity.class.getSimpleName();
    private TextView date, nursaryname, consignment_num;
    private EditText manregular_edt, femalereg_edt, manout_edt, femaleout_edt, mancostregular_edt, femalecostreg_edt, mancostout_edt, femalecostout_edt;
    private Button save_btn;
    private DataAccessHandler dataAccessHandler;
    private List<NurseryIrrigationLogXref> IrrigationLogXreflist = new ArrayList<>();
    String CONSINEMENTCODES, transactionId, NUrsery_code,NurseryName;
    List<String> list;
    int Flag;
    String Comments,Userid,Date_history;
    List<LinkedHashMap> mapStatusXrefArray = new ArrayList<>();
     List<LinkedHashMap> irrigationArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation);

        dataAccessHandler = new DataAccessHandler(this);

        date = findViewById(R.id.textView_date);
        nursaryname = findViewById(R.id.nurseryname);
        consignment_num = findViewById(R.id.consignment);
        manregular_edt = findViewById(R.id.manreg_edt);
        femalereg_edt = findViewById(R.id.Femalereg_edt);
        manout_edt = findViewById(R.id.manout_edt);
        femaleout_edt = findViewById(R.id.femaleout_edt);
        save_btn = findViewById(R.id.save_btn);


        Date date1 = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(date1);


        date.setText( formattedDate+"");


        if (getIntent() != null) {

          CONSINEMENTCODES = getIntent().getStringExtra("consignmentCode");
            Flag = getIntent().getIntExtra("camefrom", 1); // if Flag 2 ,irrigation log edit on rejection
            transactionId = getIntent().getStringExtra("transactionId");
            Log.d(IrrigationActivity.LOG_TAG, "Consignment Code :" + CONSINEMENTCODES);
            Log.d(IrrigationActivity.LOG_TAG, "Flag=====" + Flag);
            if (Flag == 2) {


                IrrigationLogXreflist = dataAccessHandler.getirigationlogxref(Queries.getInstance().getIrrigationlogxref(CONSINEMENTCODES));
                String CONSINEMENTCODE = IrrigationLogXreflist.get(0).getConsignmentCode();

                NUrsery_code = dataAccessHandler.getSingleValue(Queries.getnurseryfromirrigation(CONSINEMENTCODE));
                NurseryName = dataAccessHandler.getSingleValue(Queries.getnurserynamefromirrigation(NUrsery_code));
                Log.d(IrrigationActivity.LOG_TAG, "NUrsery_code=====" + NUrsery_code);
                Log.d(IrrigationActivity.LOG_TAG, "NurseryName=====" + NurseryName);
                String male_reg = dataAccessHandler.getSingleValue(Queries.getreg_male(CONSINEMENTCODES));
                String femmale_reg = dataAccessHandler.getSingleValue(Queries.getreg_female(CONSINEMENTCODES));
                String male_contract = dataAccessHandler.getSingleValue(Queries.getcontract_male(CONSINEMENTCODES));
                String female_contract = dataAccessHandler.getSingleValue(Queries.getcontract_female(CONSINEMENTCODES));
                manregular_edt.setText(male_reg);
                femalereg_edt.setText(femmale_reg);
                manout_edt.setText(male_contract);
                femaleout_edt.setText(female_contract);
                consignment_num.setText(CONSINEMENTCODE+"");
                nursaryname.setText( NurseryName+"");
            } else {
                list = Arrays.asList(CONSINEMENTCODES.split(","));
                Log.d(IrrigationActivity.LOG_TAG, "Consignment Codes Size :" + CONSINEMENTCODES);
                Log.d(IrrigationActivity.LOG_TAG, "Consignment Codes Size :" + list.size());
                consignment_num.setText( CONSINEMENTCODES+"");
                nursaryname.setText( CommonConstants.NurseryName+"");
            }

        }

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Flag == 2) {
                    if (manregular_edt.length() != 0 || femalereg_edt.length() != 0 ||
                            manout_edt.length() != 0 || femaleout_edt.length() != 0) {
                        Log.d(IrrigationActivity.class.getSimpleName(), "Update Irrigation Code :" + transactionId);
                        Comments =  dataAccessHandler.getSingleValue(Queries.getcomments(transactionId));
                        Userid =  dataAccessHandler.getSingleValue(Queries.getuserid(transactionId));
                        Date_history =  dataAccessHandler.getSingleValue(Queries.getupdateddate(transactionId));
                        String male_reg = dataAccessHandler.getSingleValue(Queries.getregmalerate(NUrsery_code));
                        String femmale_reg = dataAccessHandler.getSingleValue(Queries.getregfemalerate(NUrsery_code));
                        String male_contract = dataAccessHandler.getSingleValue(Queries.getcontractmalerate(NUrsery_code));
                        String female_contract = dataAccessHandler.getSingleValue(Queries.getcontractfemalerate(NUrsery_code));
                        LinkedHashMap mapStatus = new LinkedHashMap();

                        mapStatus.put("LogDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("IrrigationCode", transactionId);
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

                        mapStatus.put("RegularMaleRate", male_reg);
                        mapStatus.put("RegularFeMaleRate", femmale_reg);
                        mapStatus.put("ContractMaleRate", male_contract);
                        mapStatus.put("ContractFeMaleRate", female_contract);
                        mapStatus.put("StatusTypeId", 346);
                      mapStatus.put("Comments", "");
                        mapStatus.put("IsActive", 1);
                        mapStatus.put("CreatedByUserId", CommonConstants.USER_ID);
                        mapStatus.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("UpdatedByUserId", CommonConstants.USER_ID);
                        mapStatus.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("ServerUpdatedStatus", 0);


                        final List<LinkedHashMap> irrigationArray = new ArrayList<>();
                        irrigationArray.add(mapStatus);

                        String whereCondition ="where  IrrigationCode = '"+transactionId+"'  ";
                        dataAccessHandler.updateData("NurseryIrrigationLog",
                                irrigationArray,false, whereCondition,  new ApplicationThread.OnComplete<String>() {
                                    @Override
                                    public void execute(boolean success, String result, String msg) {
                                        if (success) {

                                            Toast.makeText(IrrigationActivity.this, "Data Saved Successfully ", Toast.LENGTH_SHORT).show();
                                            Intent newIntent = new Intent(IrrigationActivity.this, HomeActivity.class);
                                            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(newIntent);
                                        }else{
                                            Toast.makeText(IrrigationActivity.this, "Data Saved Failed try again :" + msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                });

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please enter at least one value", Toast.LENGTH_SHORT).show();
                    }


                    LinkedHashMap status = new LinkedHashMap();
                    status.put("IrrigationCode", transactionId);
                    status.put("StatusTypeId", 349);
                    status.put("Comments",Comments);
                    status.put("isActive", 1);
                    status.put("CreatedByUserId", Userid);
                    status.put("CreatedDate", Date_history);

                    status.put("ServerUpdatedStatus", 0);

                    final List<LinkedHashMap> historyList = new ArrayList<>();
                    historyList.add(status);
                    dataAccessHandler.insertMyDataa("IrrigationLogStatusHistory", historyList, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                if (CommonUtils.isNetworkAvailable(IrrigationActivity.this)) {


                                    DataSyncHelper.performRefreshTransactionsSync(IrrigationActivity.this, new ApplicationThread.OnComplete() {
                                        @Override
                                        public void execute(boolean success, Object result, String msg) {
                                            if (success) {
                                                ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(IrrigationActivity.this, "Successfully data sent to server", Toast.LENGTH_SHORT).show();
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
                            //    finish();

                                Log.d(ActivityTask.class.getSimpleName(), "==> IrrigationLogStatusHistory INSERT COMPLETED");}
                        }
                    });
                }


                else {
                    if (manregular_edt.length() != 0 || femalereg_edt.length() != 0 ||
                            manout_edt.length() != 0 || femaleout_edt.length() != 0) {
                        String IrrigationCode = "IRR" + CommonConstants.TAB_ID + CommonConstants.NurseryCode + "-" + (dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getIrrigationMaxNumber()) + 1);
                        Log.d(LOG_TAG, "==> Analysis ==> Irrigation Code ==> " + IrrigationCode);
                        String male_reg = dataAccessHandler.getSingleValue(Queries.getregmalerate(CommonConstants.NurseryCode));
                        String femmale_reg = dataAccessHandler.getSingleValue(Queries.getregfemalerate(CommonConstants.NurseryCode));
                        String male_contract = dataAccessHandler.getSingleValue(Queries.getcontractmalerate(CommonConstants.NurseryCode));
                        String female_contract = dataAccessHandler.getSingleValue(Queries.getcontractfemalerate(CommonConstants.NurseryCode));

                        Log.d(LOG_TAG, "==> Analysis ==>  lobour rates ==> " + male_reg + femmale_reg + male_contract + female_contract);
                        LinkedHashMap mapStatus = new LinkedHashMap();
                        mapStatus.put("Id", 0);
                        mapStatus.put("LogDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("IrrigationCode", IrrigationCode);
                        if (manregular_edt.length() != 0) {
                            mapStatus.put("RegularMale", manregular_edt.getText().toString());
                        }
                        if (femalereg_edt.length() != 0) {
                            mapStatus.put("RegularFemale", femalereg_edt.getText().toString());

                        }
                        if (manout_edt.length() != 0) {
                            mapStatus.put("ContractMale", manout_edt.getText().toString());

                        }
                        if (femaleout_edt.length() != 0) {
                            mapStatus.put("ContractFemale", femaleout_edt.getText().toString());

                        }

                        if (male_reg != null) {
                            mapStatus.put("RegularMaleRate", male_reg);

                        }
                        if (femmale_reg != null) {
                            mapStatus.put("RegularFeMaleRate", femmale_reg);
                        }
                        if (male_contract != null) {
                            mapStatus.put("ContractMaleRate", male_contract);
                        }
                        if (female_contract != null) {
                            mapStatus.put("ContractFeMaleRate", female_contract);
                        }

                        mapStatus.put("StatusTypeId", 346);

                        mapStatus.put("Comments", "");
                        mapStatus.put("IsActive", 1);
                        mapStatus.put("CreatedByUserId", CommonConstants.USER_ID);
                        mapStatus.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("UpdatedByUserId", CommonConstants.USER_ID);
                        mapStatus.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        mapStatus.put("ServerUpdatedStatus", 0);



                        irrigationArray.add(mapStatus);
// inserting data

                        dataAccessHandler.insertMyDataa("NurseryIrrigationLog",
                                irrigationArray, new ApplicationThread.OnComplete<String>() {
                                    @Override
                                    public void execute(boolean success, String result, String msg) {
                                        if (success) {

                                            for (int i = 0; i < list.size(); i++) {
                                                LinkedHashMap mapStatusXref = new LinkedHashMap();
                                                mapStatusXref.put("IrrigationCode", IrrigationCode);
                                                mapStatusXref.put("ConsignmentCode", list.get(i));
                                                mapStatusXref.put("IsActive", 1);
                                                mapStatusXref.put("CreatedByUserId", CommonConstants.USER_ID);
                                                mapStatusXref.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                mapStatusXref.put("UpdatedByUserId", CommonConstants.USER_ID);
                                                mapStatusXref.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                mapStatusXref.put("ServerUpdatedStatus", 0);

                                                // mapStatusXrefArray = new ArrayList<>();
                                                mapStatusXrefArray.add(mapStatusXref);
                                            }
                                                dataAccessHandler.insertMyDataa("NurseryIrrigationLogXref",
                                                        mapStatusXrefArray, new ApplicationThread.OnComplete<String>() {
                                                            @Override
                                                            public void execute(boolean success, String result, String msg) {
                                                                if (success) {
//

                                                                    if (CommonUtils.isNetworkAvailable(IrrigationActivity.this)) {


                                                                        DataSyncHelper.performRefreshTransactionsSync(IrrigationActivity.this, new ApplicationThread.OnComplete() {
                                                                            @Override
                                                                            public void execute(boolean success, Object result, String msg) {
                                                                                if (success) {
                                                                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            Toast.makeText(IrrigationActivity.this, "Successfully data sent to server", Toast.LENGTH_SHORT).show();
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

                                                                } else {

                                                                    Toast.makeText(IrrigationActivity.this, "Data Saved Failed try again :" + msg, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });


                                                Toast.makeText(IrrigationActivity.this, "Data Saved Successfully ", Toast.LENGTH_SHORT).show();
                                                Intent newIntent = new Intent(IrrigationActivity.this, HomeActivity.class);
                                                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(newIntent);
                                            }



                                        Log.d(ActivityTask.class.getSimpleName(),
                                                "==>  Analysis ==> irrigationlog details INSERT COMPLETED :" + success);

                                    }
                                });


                    } else {

                        Toast.makeText(getApplicationContext(), "Please enter at least one value", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


    }
}
