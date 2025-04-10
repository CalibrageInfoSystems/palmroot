package com.calibrage.palmroot.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.ConsignmentReports;
import com.calibrage.palmroot.dbmodels.FertilizerDetails;
import com.calibrage.palmroot.dbmodels.Visitdata;
import com.calibrage.palmroot.ui.Adapter.FertilizerdetailsAdapter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Cons_Reports_Screen extends AppCompatActivity {
LinearLayout basic_linear,basic_sub_linear,lytransplantdate,losssublinear,subferilizerdetails,pestsublinear,fungicidesublinear,weedsublinear,visitsublinear,weed_sublinear,visit_sublinear;
    private DataAccessHandler dataAccessHandler;
    String ConsignmentCode;
    LinearLayout activitynamelnF,activitynamelnp,activitynamelnFun;
    TextView activitynameFun,activitynameF,activitynamep;
TextView consignmentcode,txtnurseryname,sapid,originname,vendorName,sowingdate,sowingquantity,closingstock,txtAge,txtStatusTxt,vareityName,transplantdate;
TextView nofertdata,nopertdata,nofungdata,noweeddata,novisitdata;
    TextView Productnamefun,quantityfun,donebyfun,doneonfun;
    TextView Productnamep,quantityp,donebyp,doneonp;
    TextView Productnamew,quantityw,donebyw,doneonw;
    TextView nameofvisitor,ConsignmentID,Remarks,updatedby;
   ImageView image_down,image_up;
    ImageView image_downf,image_upf;
    ImageView image_downl,image_upl;
    ImageView image_downfun,image_upfun;
    ImageView image_downp,image_upp;
    ImageView image_downw,image_upw;
    ImageView image_downv,image_upv;
    RecyclerView fertilizerRecycleview,pesticideRecycleview,funcideRecycleview;
    private List<ConsignmentReports> consignmentList = new ArrayList<>();
    private List<Visitdata> VisiteddataList = new ArrayList<>();
    private List<FertilizerDetails> FertilizerdataList = new ArrayList<>();
   private List<FertilizerDetails> PesticidedataList = new ArrayList<>();
  private List<FertilizerDetails> FungicidedataList = new ArrayList<>();
    private List<FertilizerDetails> weedingdataList = new ArrayList<>();
    String sowing_quantity,GerminationLoss,CullingLoss,TransplantationLoss,TransitLoss,arrival_quantity;

    TextView saplingts,losstlper,saplingTL,lossTLper,saplingcl,lossClper,saplinggs,lossgsper,saplingtotal,totallossper;

    FertilizerdetailsAdapter FertilizerListAdapter;

    DecimalFormat precision = new DecimalFormat("0.00");

    float  percentageTr,percentageT,percentagec,percentageG;
    LinearLayout lytage,lyclosingstock,lntsapid,lnsowingquantity,lnsowingdate;

    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cons_reports_screen);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ConsignmentCode = extras.getString("ConsignmentCode");
        }

        Log.d("ConsignmentCode", ConsignmentCode + "");
        dataAccessHandler = new DataAccessHandler(this);
        String UserId = CommonConstants.USER_ID;
        Log.d("UserId Is : ", UserId);
        intview();
        setviews();
    }

    private void intview() {
        lytage = findViewById(R.id.lytage);
        lyclosingstock = findViewById(R.id.lyclosingstock);
        lntsapid= findViewById(R.id.lntsapid);
        lnsowingquantity = findViewById(R.id.lnsowingquantity);
        lnsowingdate = findViewById(R.id.lnsowingdate);
        basic_linear =  findViewById(R.id.basic_linear);
        basic_sub_linear = findViewById(R.id.basic_sub_linear);
        consignmentcode= findViewById(R.id.consignmentcode);
        txtnurseryname= findViewById(R.id.txtnurseryname);
        sapid= findViewById(R.id.sapid);
        originname= findViewById(R.id.originname);
        vendorName= findViewById(R.id.vendorName);
        sowingdate= findViewById(R.id.sowingdate);
        sowingquantity= findViewById(R.id.sowingquantity);
        vareityName= findViewById(R.id.vareityName);
        closingstock = findViewById(R.id.closingstock);
        txtAge = findViewById(R.id.txtAge);
        txtStatusTxt = findViewById(R.id.txtStatusTxt);
        nofertdata = findViewById(R.id.nofertdata);
        nopertdata = findViewById(R.id.nopertdata);
        nofungdata = findViewById(R.id.nofungdata);
        novisitdata = findViewById(R.id.novisitdata);
        fertilizerRecycleview = findViewById(R.id.fertilizerRecycleview);
        pesticideRecycleview = findViewById(R.id.pesticideRecycleview);
        funcideRecycleview = findViewById(R.id.funcideRecycleview);
        transplantdate = findViewById(R.id.transplantdate);
        lytransplantdate = findViewById(R.id.lytransplantdate);
        image_up = findViewById(R.id.image_up);
        image_down = findViewById(R.id.image_down);
        weed_sublinear = findViewById(R.id.weed_sublinear);
        image_downl = findViewById(R.id.image_downl);
        image_upl = findViewById(R.id.image_upl);
        image_downf = findViewById(R.id.image_downf);
        image_upf = findViewById(R.id.image_upf);
        image_downfun = findViewById(R.id.image_downfun);
        image_upfun = findViewById(R.id.image_upfun);
        image_downp = findViewById(R.id.image_downp);
        image_upp = findViewById(R.id.image_upp);
        image_downw = findViewById(R.id.image_downw);
        image_upw = findViewById(R.id.image_upw);
        image_downv = findViewById(R.id.image_downv);
        image_upv = findViewById(R.id.image_upv);

        noweeddata = findViewById(R.id.noweeddata);

        donebyw= findViewById(R.id.donebyw);
        quantityw= findViewById(R.id.quantityw);
        Productnamew = findViewById(R.id.Productnamew);
        doneonw = findViewById(R.id.doneonw);

        losssublinear = findViewById(R.id.losssublinear);
        subferilizerdetails = findViewById(R.id.subferilizerdetails);
        pestsublinear = findViewById(R.id.pestsublinear);
        fungicidesublinear =findViewById(R.id.fungicidesublinear);
        weedsublinear =findViewById(R.id.weedsublinear);
        visitsublinear = findViewById(R.id.visitsublinear);
        visit_sublinear = findViewById(R.id.visit_sublinear);
        activitynameFun = findViewById(R.id.activitynameFun);
        activitynameF = findViewById(R.id.activitynameF);
        activitynamep= findViewById(R.id.activitynamep);
        saplingts = findViewById(R.id.saplingts);
        losstlper = findViewById(R.id.losstlper);
        saplingTL = findViewById(R.id.saplingTL);
        lossTLper = findViewById(R.id.lossTLper);
        saplingcl = findViewById(R.id.saplingcl);
        lossClper = findViewById(R.id.lossClper);
        saplinggs = findViewById(R.id.saplinggs);
        lossgsper = findViewById(R.id.lossgsper);

        nameofvisitor = findViewById(R.id.nameofvisitor);
        ConsignmentID = findViewById(R.id.ConsignmentID);
        Remarks = findViewById(R.id.Remarks);
        updatedby = findViewById(R.id.updatedby);

        saplingtotal = findViewById(R.id.saplingtotal);
        totallossper = findViewById(R.id.totallossper);

        activitynamelnF = findViewById(R.id.activitynamelnF);
        activitynamelnp = findViewById(R.id.activitynamelnp);
        activitynamelnFun= findViewById(R.id.activitynamelnFun);



    }
    private void setviews() {

        fertilizerRecycleview.setHasFixedSize(true);
        fertilizerRecycleview.setLayoutManager(new LinearLayoutManager(this));
        pesticideRecycleview.setHasFixedSize(true);
        pesticideRecycleview.setLayoutManager(new LinearLayoutManager(this));
        funcideRecycleview.setHasFixedSize(true);
        funcideRecycleview.setLayoutManager(new LinearLayoutManager(this));

        consignmentList = dataAccessHandler.getConsignmentdetails(Queries.getInstance().getConsignmentDatadetails(ConsignmentCode));//  Consignment selection

        VisiteddataList =  dataAccessHandler.getvisiteddata(Queries.getInstance().getvisitdata(ConsignmentCode));
        FertilizerdataList =  dataAccessHandler.getfertilizerdata(Queries.getInstance().getFertilizerdata(ConsignmentCode));
        PesticidedataList =  dataAccessHandler.getfertilizerdata(Queries.getInstance().getPesticidedata(ConsignmentCode));
        FungicidedataList =  dataAccessHandler.getfertilizerdata(Queries.getInstance().getFungicidedata(ConsignmentCode));
        weedingdataList =  dataAccessHandler.getfertilizerdata(Queries.getInstance().getweedingdata(ConsignmentCode));


        if(weedingdataList.size()!=0){
            noweeddata.setVisibility(View.GONE);
            weed_sublinear.setVisibility(View.VISIBLE);
            Productnamew.setText (" :  "+weedingdataList.get(0).getActivityName());
            donebyw.setText (" :  "+weedingdataList.get(0).getDoneBy());

            try {
                Date oneWayTripDate = input.parse(weedingdataList.get(0).getDoneOn());
                String datetimevaluereq = output.format(oneWayTripDate);
                doneonw.setText(" :  "+ datetimevaluereq);

                android.util.Log.e("===============", "======currentData======99===" + output.format(oneWayTripDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            if(weedingdataList.get(0).getDoneOn().contains("T")){
//                android.util.Log.d("Done on ======>51", "" +weedingdataList.get(0).getDoneOn());
//                doneonw.setText(" :  "+CommonUtils.getProperDate2(weedingdataList.get(0).getDoneOn()));
//               ;
//            }else{
//               Log.d("Done on ======>54", "" +weedingdataList.get(0).getDoneOn());
//                doneonw.setText(" :  "+CommonUtils.getProperDate2(weedingdataList.get(0).getDoneOn()));
//
//
//            }



        }
        else{
            noweeddata.setVisibility(View.VISIBLE);
            weed_sublinear.setVisibility(View.GONE);
        }
        if (FungicidedataList.size() != 0) {
            nofungdata.setVisibility(View.GONE);
            funcideRecycleview.setVisibility(View.VISIBLE);
            activitynameFun.setText(" :  "+FungicidedataList.get(0).getActivityName());
            FertilizerListAdapter = new FertilizerdetailsAdapter(this, FungicidedataList);
            funcideRecycleview.setAdapter(FertilizerListAdapter);
        }
        else{
            nofungdata.setVisibility(View.VISIBLE);
            activitynamelnFun.setVisibility(View.GONE);
            fertilizerRecycleview.setVisibility(View.GONE);
        }
        Log.e("Fertilizerdata========>154",FertilizerdataList.size() +"Pesticidedata==" + PesticidedataList.size() +"Fungicidedata===="+FungicidedataList.size());


        if (PesticidedataList.size() != 0) {
            nopertdata.setVisibility(View.GONE);
            pesticideRecycleview.setVisibility(View.VISIBLE);
            activitynamep.setText(" :  "+PesticidedataList.get(0).getActivityName());
            FertilizerListAdapter = new FertilizerdetailsAdapter(this, PesticidedataList);
            pesticideRecycleview.setAdapter(FertilizerListAdapter);
        }
        else{
            nopertdata.setVisibility(View.VISIBLE);
            fertilizerRecycleview.setVisibility(View.GONE);
            activitynamelnp.setVisibility(View.GONE);
        }


        if (FertilizerdataList.size() != 0) {
            Log.e("Fertilizerdata========>154",FertilizerdataList.get(0).getDoneOn());
                    nofertdata.setVisibility(View.GONE);
            fertilizerRecycleview.setVisibility(View.VISIBLE);
            activitynameF.setText(" :  "+FertilizerdataList.get(0).getActivityName());
            FertilizerListAdapter = new FertilizerdetailsAdapter(this, FertilizerdataList);
            fertilizerRecycleview.setAdapter(FertilizerListAdapter);
        }
        else{
            nofertdata.setVisibility(View.VISIBLE);
            fertilizerRecycleview.setVisibility(View.GONE);
            activitynamelnF.setVisibility(View.GONE);
        }
        Log.e("VisiteddataList========>1232",VisiteddataList.size()+"");
       if (VisiteddataList.size() != 0) {
           novisitdata.setVisibility(View.GONE);
           visit_sublinear.setVisibility(View.VISIBLE);
           nameofvisitor.setText(" :  " + VisiteddataList.get(0).getVisitedBy());
           ConsignmentID.setText(" :  " + VisiteddataList.get(0).getConsignmentCode());
           Remarks.setText(" :  " + VisiteddataList.get(0).getRemarkes());
           updatedby.setText(" :  " + VisiteddataList.get(0).getUpdatedBy());
       }else {
           novisitdata.setVisibility(View.VISIBLE);
           visit_sublinear.setVisibility(View.GONE);
       }

        consignmentcode.setText(consignmentList.get(0).getConsignmentCode()+"");
        txtnurseryname.setText(" :  "+consignmentList.get(0).getNurseryName());
        if (consignmentList.get(0).getSAPId() != null && !consignmentList.get(0).getSAPId().isEmpty() && !consignmentList.get(0).getSAPId().equals("null"))

        {
        sapid.setText(consignmentList.get(0).getSAPId() +"");}
        else{
            lntsapid.setVisibility(View.GONE);
        }
        originname.setText(" :  "+consignmentList.get(0).getOriginname());
        vendorName.setText(" :  "+consignmentList.get(0).getVendorname());


        sowing_quantity = dataAccessHandler.getSingleValue(Queries.getshownquatity(consignmentList.get(0).getConsignmentCode()));
        arrival_quantity =consignmentList.get(0).getArrivalquantity()+"";

        GerminationLoss =dataAccessHandler.getSingleValue(Queries.getgerminationloss(consignmentList.get(0).getConsignmentCode()));
        CullingLoss = dataAccessHandler.getSingleValue(Queries.getcullingloss(consignmentList.get(0).getConsignmentCode()));
        TransplantationLoss = dataAccessHandler.getSingleValue(Queries.getTransplantationLoss(consignmentList.get(0).getConsignmentCode()));
        TransitLoss = dataAccessHandler.getSingleValue(Queries.getTransitLoss(consignmentList.get(0).getConsignmentCode()));

        Log.e("========>158",GerminationLoss + "==CullingLoss"+ CullingLoss+"==TransplantationLoss"+TransplantationLoss+"==TransitLoss"+TransitLoss);





        if(TransitLoss!=null){

//            float rCtWt = Float.parseFloat(TransitLoss);
//            float mk = Float.parseFloat(arrival_quantity);
//            String finalRWt = ((rCtWt * mk) / 100) + "";
//            Log.e("======>315",rCtWt+"========="+ " "+mk + "=========== ");
              percentageT = ( Float.parseFloat(TransitLoss) / Float.parseFloat(arrival_quantity)) * 100 ;
        saplingts.setText(" :  "+TransitLoss);
        losstlper.setText(" :  "+ precision.format(percentageT)+ " ");}
        else{
            TransitLoss = "0";
            saplingts.setText(" :  "+"0");
            losstlper.setText(" :  "+"0 ");
            percentageT= 0.00f;
        }

        if(TransplantationLoss!=null){

              percentageTr = ( Float.parseFloat(TransplantationLoss) / Float.parseFloat(arrival_quantity)) * 100 ;
        saplingTL.setText(" :  "+TransplantationLoss);
        lossTLper.setText(" :  "+precision.format(percentageTr)+ " ");}
        else{
            TransplantationLoss ="0";
            saplingTL.setText(" :  "+"0");
            lossTLper.setText(" :  "+ "0 ");
            percentageTr = 0.00f;
        }

        if(CullingLoss!=null){
              percentagec = ( Float.parseFloat(CullingLoss) / Float.parseFloat(arrival_quantity)) * 100 ;
        saplingcl.setText(" :  "+CullingLoss);
        lossClper.setText(" :  "+precision.format(percentagec)+ " ");}
        else{
            saplingcl.setText(" :  "+"0");
            CullingLoss="0";
            lossClper.setText(" :  "+ "0 ");
            percentagec = 0.00f;
        }
        if(GerminationLoss!=null) {
            percentageG = ( Float.parseFloat(GerminationLoss) / Float.parseFloat(arrival_quantity)) * 100 ;
            saplinggs.setText(" :  " + GerminationLoss);
            lossgsper.setText(" :  " + precision.format(percentageG)+ " ");
        }else{
            saplinggs.setText(" :  " + "0");
            GerminationLoss = "0";
            percentageG = 0.00f;
            lossgsper.setText(" :  " + "0 ");
        }
        Log.e("====>getpercentageTransitLoss " ,percentageT+"===getpercentageGerminationLoss" +percentageG +"getpercentageTransplantationLoss=="+percentageTr
                +"getpercentageCullingLoss=="+percentagec);
        saplingtotal.setText(" :  "+ precision.format(Float.parseFloat(TransitLoss)  + Float.parseFloat(GerminationLoss)+Float.parseFloat(TransplantationLoss) +
                Float.parseFloat(CullingLoss)) +"");
        totallossper.setText(" :  "+precision.format(percentageT+percentageTr+percentageG+percentagec)+"");

        sowingquantity.setText(" :  "+sowing_quantity);
        vareityName.setText(" :  "+consignmentList.get(0).getVarietyname());
        closingstock.setText(" :  "+consignmentList.get(0).getClosingStock());
        txtStatusTxt.setText(" :  "+consignmentList.get(0).getStatus());
        if(consignmentList.get(0).getStatus().equals("Pre Nursery")){
            lnsowingdate.setVisibility(View.GONE);
            lnsowingquantity.setVisibility(View.GONE);
            lytage.setVisibility(View.GONE);
            lyclosingstock.setVisibility(View.GONE);
            lntsapid.setVisibility(View.GONE);
        }

    // transplantdate.setText(" :  "+consignmentList.get(0).getTransplantDate());
        Log.e("====>getTransplantDate====241", " :  "+consignmentList.get(0).getTransplantDate());


            if (consignmentList.get(0).getTransplantDate() != null && !consignmentList.get(0).getTransplantDate().isEmpty() && !consignmentList.get(0).getTransplantDate().equals("null"))

            {
            lytransplantdate.setVisibility(View.VISIBLE);
                try {
                    Date oneWayTripDate = input.parse(consignmentList.get(0).getTransplantDate());
                    String datetimevaluereq = output.format(oneWayTripDate);
                    transplantdate.setText(" :  "+ datetimevaluereq);

                    android.util.Log.e("===============", "======currentData======374===" + output.format(oneWayTripDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
          //  transplantdate.setText(" :  "+CommonUtils.getProperDate2(consignmentList.get(0).getTransplantDate()));
        }
        else{
            lytransplantdate.setVisibility(View.GONE);
          //  transplantdate.setText(" :  "+CommonUtils.getProperComplaintsDate2(consignmentList.get(0).getTransplantDate()));

        }

        String dateofsowing = consignmentList.get(0).getSowingDate();
        if ((dateofsowing != null && !dateofsowing.isEmpty() && !dateofsowing.equals("null"))) {


            try {
                Date oneWayTripDate = input.parse(dateofsowing);
                String datetimevaluereq = output.format(oneWayTripDate);
                sowingdate.setText(" :  "+ datetimevaluereq);

                android.util.Log.e("===============", "======currentData======99===" + output.format(oneWayTripDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date d = null;
            try {
                d = formatter.parse(dateofsowing);//catch exception
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Calendar thatDay = Calendar.getInstance();
            thatDay.setTime(d);

            Calendar today = Calendar.getInstance();

            long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
            long days = diff / (24 * 60 * 60 * 1000);

            txtAge.setText(" :  " + days +" ");




        }
        else{
            lnsowingdate.setVisibility(View.GONE);
            lnsowingquantity.setVisibility(View.GONE);
            lytage.setVisibility(View.GONE);
        }


      //  Log.e("consignmentList=======67",consignmentList.get(0).getSAPId() + " ===" +consignmentList.get(0).getNurseryName()+"");


            image_down.setOnClickListener(new View.OnClickListener() {    // basic Information
                @Override
                public void onClick(View view) {

                    image_down.setVisibility(View.GONE);
                    image_up.setVisibility(View.VISIBLE);
                    basic_sub_linear.setVisibility(View.GONE);

                }
            });

        image_up.setOnClickListener(new View.OnClickListener() {    //
            @Override
            public void onClick(View view) {
                image_down.setVisibility(View.VISIBLE);
                image_up.setVisibility(View.GONE);
                basic_sub_linear.setVisibility(View.VISIBLE);


            }
        });

        image_downl.setOnClickListener(new View.OnClickListener() {    // Losses
            @Override
            public void onClick(View view) {

                image_downl.setVisibility(View.GONE);
                image_upl.setVisibility(View.VISIBLE);
                losssublinear.setVisibility(View.GONE);

            }
        });

        image_upl.setOnClickListener(new View.OnClickListener() {    //
            @Override
            public void onClick(View view) {
                image_downl.setVisibility(View.VISIBLE);
                image_upl.setVisibility(View.GONE);
                losssublinear.setVisibility(View.VISIBLE);


            }
        });


        image_downf.setOnClickListener(new View.OnClickListener() {    // Fertilizer
            @Override
            public void onClick(View view) {

                image_downf.setVisibility(View.GONE);
                image_upf.setVisibility(View.VISIBLE);
                subferilizerdetails.setVisibility(View.GONE);

            }
        });

        image_upf.setOnClickListener(new View.OnClickListener() {    //
            @Override
            public void onClick(View view) {
                image_downf.setVisibility(View.VISIBLE);
                image_upf.setVisibility(View.GONE);
                subferilizerdetails.setVisibility(View.VISIBLE);


            }
        });




        image_downp.setOnClickListener(new View.OnClickListener() {    // Fertilizer
            @Override
            public void onClick(View view) {

                image_downp.setVisibility(View.GONE);
                image_upp.setVisibility(View.VISIBLE);
                pestsublinear.setVisibility(View.GONE);

            }
        });

        image_upp.setOnClickListener(new View.OnClickListener() {    //
            @Override
            public void onClick(View view) {
                image_downp.setVisibility(View.VISIBLE);
                image_upp.setVisibility(View.GONE);
                pestsublinear.setVisibility(View.VISIBLE);


            }
        });


        image_downfun.setOnClickListener(new View.OnClickListener() {    // Fertilizer
            @Override
            public void onClick(View view) {

                image_downfun.setVisibility(View.GONE);
                image_upfun.setVisibility(View.VISIBLE);
                fungicidesublinear.setVisibility(View.GONE);

            }
        });

        image_upfun.setOnClickListener(new View.OnClickListener() {    //
            @Override
            public void onClick(View view) {
                image_downfun.setVisibility(View.VISIBLE);
                image_upfun.setVisibility(View.GONE);
                fungicidesublinear.setVisibility(View.VISIBLE);


            }
        });

        image_downw.setOnClickListener(new View.OnClickListener() {    // Fertilizer
            @Override
            public void onClick(View view) {

                image_downw.setVisibility(View.GONE);
                image_upw.setVisibility(View.VISIBLE);
                weedsublinear.setVisibility(View.GONE);

            }
        });

        image_upw.setOnClickListener(new View.OnClickListener() {    //
            @Override
            public void onClick(View view) {
                image_downw.setVisibility(View.VISIBLE);
                image_upw.setVisibility(View.GONE);
                weedsublinear.setVisibility(View.VISIBLE);


            }
        });


        image_downv.setOnClickListener(new View.OnClickListener() {    // Fertilizer
            @Override
            public void onClick(View view) {

                image_downv.setVisibility(View.GONE);
                image_upv.setVisibility(View.VISIBLE);
                visitsublinear.setVisibility(View.GONE);


            }
        });

        image_upv.setOnClickListener(new View.OnClickListener() {    //
            @Override
            public void onClick(View view) {
                image_downv.setVisibility(View.VISIBLE);
                image_upv.setVisibility(View.GONE);
                visitsublinear.setVisibility(View.VISIBLE);


            }
        });
}}

































