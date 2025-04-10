package com.calibrage.palmroot.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.pavlospt.CircleView;
import com.calibrage.palmroot.R;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.datasync.helpers.DataManager;
import com.calibrage.palmroot.dbmodels.ConsignmentDetails;
import com.calibrage.palmroot.dbmodels.NurseryDetails;

import java.util.LinkedHashMap;
import java.util.List;

public class  HomeActivity extends AppCompatActivity {

    LinearLayout newactivity;
    LinearLayout irrigation;
    LinearLayout irrigation_post,IrrigationRel,checkactivityRel,nurserylabourlogsrel,Nurseryvisitlogs,viewNurseryvisitlogs,nurseryrm,consignment_report;
    LinkedHashMap<String, Pair> nurserydatamap = null;
    LinkedHashMap<String, Pair> consignmentdatamap = null;
    List<NurseryDetails> nurseryDetails;
    List<ConsignmentDetails> consignmentDetails;
    private LinearLayout refreshRel;
    private Spinner nurserySpinner, consignmentSpinner;
    private CircleView circleView;
    private DataAccessHandler dataAccessHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        setviews();

    }

    private void init() {

        newactivity = findViewById(R.id.newactivityRel);
        irrigation = findViewById(R.id.irigationdetails);
        irrigation_post = findViewById(R.id.irigationdetails_post);
        refreshRel = (LinearLayout) findViewById(R.id.refreshRel1);
        IrrigationRel =findViewById(R.id.IrrigationRel);
        checkactivityRel = findViewById(R.id.checkactivityRel);
        nurserylabourlogsrel = findViewById(R.id.nurserylabourlogsrel);
        Nurseryvisitlogs = findViewById(R.id.Nurseryvisitlogs);
        nurseryrm = findViewById(R.id.nurseryrm);
        consignment_report= findViewById(R.id.consignment_report);
        viewNurseryvisitlogs = findViewById(R.id.viewNurseryvisitlogs);
        circleView = (CircleView) findViewById(R.id.countTxt);
        dataAccessHandler = new DataAccessHandler(this);
    }

    private void setviews() {

        String unreadNotificationsCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getUnreadNotificationsCountQuery());
        circleView.setTitleText(unreadNotificationsCount);

        RelativeLayout notificationsRel = (RelativeLayout) findViewById(R.id.notficationRel);

        notificationsRel.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, NotificationsScreen.class)));


        newactivity.setOnClickListener(new View.OnClickListener() {    // Nursery Selection
            @Override
            public void onClick(View view) {
                CommonConstants.COMMINGFROM = CommonConstants.NEWACTIVITYSCREEEN;
                Intent selectionscreen = new Intent(HomeActivity.this, NurserySelectionScreen.class);
                startActivity(selectionscreen);

                //  showDialog(HomeActivity.this);

            }
        });
//        irrigation.setOnClickListener(new View.OnClickListener() {          // Nursery Selection
//            @Override
//            public void onClick(View view) {
//
//                CommonConstants.COMMINGFROM = CommonConstants.PRE_CONSIGNMENT;
//                Intent selectionscreen = new Intent(HomeActivity.this, NurserySelectionScreen.class);
//                startActivity(selectionscreen);
//
//                //  showDialog(HomeActivity.this);
//
//            }
//        });

        irrigation_post.setOnClickListener(new View.OnClickListener() {  // Nursery Selection
            @Override
            public void onClick(View view) {

                CommonConstants.COMMINGFROM = CommonConstants.POST_CONSIGNMENT;
                Intent selectionscreen = new Intent(HomeActivity.this, NurserySelectionScreen.class);
                startActivity(selectionscreen);

                //  showDialog(HomeActivity.this);

            }
        });
        IrrigationRel.setOnClickListener(new View.OnClickListener() {  // Irrigation Status info
            @Override
            public void onClick(View view) {

                //  CommonConstants.COMMINGFROM = 2;
                Intent selectionscreen = new Intent(HomeActivity.this, IrrigationStatusActivity.class);
                startActivity(selectionscreen);

                //  showDialog(HomeActivity.this);

            }
        });
        checkactivityRel.setOnClickListener(new View.OnClickListener() {  // CheckActivity
            @Override
            public void onClick(View view) {

                  CommonConstants.COMMINGFROM = 2;
                Intent selectionscreen = new Intent(HomeActivity.this, CheckActivity.class);
                startActivity(selectionscreen);

                //  showDialog(HomeActivity.this);

            }
        });
        refreshRel.setOnClickListener(view -> {
          //  downloadImagesToSdCard("http://developer.android.com/images/activity_lifecycle.png","Roja");

            resetPrevRegData();
            startActivity(new Intent(HomeActivity.this, RefreshSyncActivity.class));
        });


        nurserylabourlogsrel.setOnClickListener(new View.OnClickListener() {  // CheckActivity
            @Override
            public void onClick(View view) {


                Intent selectionscreen = new Intent(HomeActivity.this, NurseryLabourLogActivity.class);
                startActivity(selectionscreen);

                //  showDialog(HomeActivity.this);

            }
        });

        Nurseryvisitlogs.setOnClickListener(new View.OnClickListener() {  // CheckActivity
            @Override
            public void onClick(View view) {


                Intent selectionscreen = new Intent(HomeActivity.this, NurseryVisitLogActivity.class);
                startActivity(selectionscreen);

                //  showDialog(HomeActivity.this);

            }
        });

        viewNurseryvisitlogs.setOnClickListener(new View.OnClickListener() {    // Nursery Selection
            @Override
            public void onClick(View view) {

                Intent Visitlogs = new Intent(HomeActivity.this, ViewVisitlogs.class);
                startActivity(Visitlogs);

                //  showDialog(HomeActivity.this);

            }
        });
        nurseryrm.setOnClickListener(new View.OnClickListener() {  // Nursery Selection
            @Override
            public void onClick(View view) {

                CommonConstants.COMMINGFROM = CommonConstants.Nursery_RM;
                Intent selectionscreen = new Intent(HomeActivity.this, NurserySelectionScreen.class);
                startActivity(selectionscreen);

                //  showDialog(HomeActivity.this);

            }
        });


        consignment_report.setOnClickListener(new View.OnClickListener() {  // Nursery Selection
            @Override
            public void onClick(View view) {

                CommonConstants.COMMINGFROM = CommonConstants.consignment_report;
                Intent selectionscreen = new Intent(HomeActivity.this, NurserySelectionScreen.class);
                startActivity(selectionscreen);

                //  showDialog(HomeActivity.this);

            }
        });
    }



//    public void viewimage()
//    {
//        String path = serialnumber+".png";
//        ContextWrapper cw = new ContextWrapper(getActivity());
//
//        //path to /data/data/yourapp/app_data/dirName
//        File directory = cw.getDir("files", Context.MODE_PRIVATE);
//
//        File mypath=new File(directory,"folder/"+path);
//
//        Bitmap b;
//        try {
//            b = BitmapFactory.decodeStream(new FileInputStream(mypath));
//            //  b.compress(format, quality, stream)
//            profile_image.setImageBitmap(Bitmap.createScaledBitmap(b, 120, 120, false));
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
    public static void resetPrevRegData() {
        DataManager.getInstance().deleteData(DataManager.SAPLING);
        DataManager.getInstance().deleteData(DataManager.SAPLING_ACTIVITY);
        DataManager.getInstance().deleteData(DataManager.SAPLING_ACTIVITY_XREF);
        DataManager.getInstance().deleteData(DataManager.SAPLING_ACTIVITY_HISTORY);
        //ConversionDigitalContractFragment.isContractAgreed = false;
        CommonConstants.PLOT_CODE = "";
        CommonConstants.FARMER_CODE = "";
    }


    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog);
        final DataAccessHandler dataAccessHandler;
        final LinearLayout detailslyt, consignmentdetailslyt;
        final TextView nurseryname, nurserycode, nurserypin;
        final TextView consignmentname, consignmentcode, consignmentpin;

        Button submitBtn;

        dataAccessHandler = new DataAccessHandler(HomeActivity.this);

        nurserySpinner = dialog.findViewById(R.id.nurseryspin);
        consignmentSpinner = dialog.findViewById(R.id.consignmentSpin);
        nurseryname = dialog.findViewById(R.id.nurseryname);
        nurserycode = dialog.findViewById(R.id.nurserycode);
        nurserypin = dialog.findViewById(R.id.nurserypin);
        detailslyt =  dialog.findViewById(R.id.detailslyt);
        consignmentdetailslyt = dialog.findViewById(R.id.consignmentdetailslyt);
        consignmentname = dialog.findViewById(R.id.consignmentname);
        consignmentcode = dialog.findViewById(R.id.consignmentcode);
        consignmentpin = dialog.findViewById(R.id.consignmentpin);
        submitBtn =  dialog.findViewById(R.id.submitBtn);
        nurserydatamap = dataAccessHandler.getPairData(Queries.getInstance().getNurseryMasterQuery());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(nurserydatamap, "Nursery"));
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
                    ArrayAdapter<String> consspinnerArrayAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_spinner_item, CommonUtils.arrayFromPair(consignmentdatamap, "Cons"));
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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validations()) {
                    Intent intent = new Intent(HomeActivity.this, Activities.class);
                    intent.putExtra("ConsignmentCode", consignmentcode.getText().toString() + "");

                    //CommonConstants.ConsignmentCode = consignmentcode.getText().toString();
                    //Log.d("ConsignmentCode", consignmentcode.getText().toString() + "");
                    //Log.d("ConsignmentCode",  CommonConstants.ConsignmentCode+ "");
                    startActivity(intent);

                }
            }
        });


        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }

    public boolean validations(){

        if (nurserySpinner.getSelectedItemPosition() ==0){

            Toast.makeText(this, "Please Select Nursery", Toast.LENGTH_SHORT).show();
            return false;
        }else if(consignmentSpinner.getSelectedItemPosition() == 0){

            Toast.makeText(this, "Please Select Consignment", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        String unreadNotificationsCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getUnreadNotificationsCountQuery());
        circleView.setTitleText(unreadNotificationsCount);

    }




}