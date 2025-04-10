package com.calibrage.palmroot.ui.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.MutipleData;
import com.calibrage.palmroot.dbmodels.NurseryAcitivity;
import com.calibrage.palmroot.dbmodels.SaplingActivity;
import com.calibrage.palmroot.ui.ActivityTask;
import com.calibrage.palmroot.ui.MultipleEntryScreen;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivitiesRecyclerviewAdapter extends RecyclerView.Adapter<ActivitiesRecyclerviewAdapter.ViewHolder> {

    public Context context;

    List<NurseryAcitivity> mActivitiesList;
    private List<MutipleData> multiplelist;
    private DataAccessHandler dataAccessHandler;
    private List<SaplingActivity> saplingActivitiesList = new ArrayList<>();
    String ConsignmentCode;

    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
    String dependencyname, dependecyCode;

    public ActivitiesRecyclerviewAdapter(Context context, List<NurseryAcitivity> mActivitiesList, String ConsignmentCode) {
        this.context = context;
        this.mActivitiesList = mActivitiesList;
        this.ConsignmentCode = ConsignmentCode;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.activitieslayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        dataAccessHandler = new DataAccessHandler(context);
        android.util.Log.e("===============", "======currentData======77==" +mActivitiesList.get(position).getActivityName() +"==="+ mActivitiesList.get(position).getTargetDate());
        holder.activityName.setText(mActivitiesList.get(position).getActivityName());
        String status = "";
        if (mActivitiesList.get(position).getActivityStatus() == null || mActivitiesList.get(position).getActivityStatus().isEmpty()) {
            holder.txtStatusTxt.setText("");
        } else {
            holder.txtStatusTxt.setText(mActivitiesList.get(position).getActivityStatus());
            //    holder.txtStatusTxt.setTextColor(context.getColor(R.color.green));
        }
        holder.imgStatus.setImageDrawable(null);
        holder.imgNurStatus.setImageDrawable(null);
        holder.imgShStatus.setImageDrawable(null);
     //   holder.txtDoneDate.setText("");


        if (mActivitiesList.get(position).getActivityDoneDate() != null && !mActivitiesList.get(position).getActivityDoneDate().isEmpty() && !mActivitiesList.get(position).getActivityDoneDate().equals("null"))  {

            try {
                Date oneWayTripDate = input.parse(mActivitiesList.get(position).getActivityDoneDate());
                String datetimevaluereq = output.format(oneWayTripDate);
                holder.txtDoneDate.setText(datetimevaluereq);

                android.util.Log.e("===============", "======currentData======99===" + output.format(oneWayTripDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
         //   holder.txtDoneDate.setText(CommonUtils.getProperComplaintsDate2(mActivitiesList.get(position).getActivityDoneDate()));
 }
        else{
            holder.txtDoneDate.setText("");
        }


        if (mActivitiesList.get(position).getStatusTypeId() == 346) {
            holder.imgStatus.setImageResource(R.drawable.done);
            holder.imgNurStatus.setImageResource(R.drawable.inprogress);
            holder.imgShStatus.setImageResource(R.drawable.inprogress);
            holder.imglossStatus.setImageResource(R.drawable.inprogress);
            holder.imglossStatus.setVisibility(View.GONE);
            holder.imglossStatustext.setText("NA");
        } else if (mActivitiesList.get(position).getStatusTypeId() == 347) {
            holder.imgStatus.setImageResource(R.drawable.done);
            holder.imgNurStatus.setImageResource(R.drawable.done);
            holder.imgShStatus.setImageResource(R.drawable.inprogress);
            holder.imglossStatus.setImageResource(R.drawable.inprogress);
            holder.imglossStatus.setVisibility(View.GONE);
            holder.imglossStatustext.setText("NA");
        } else if (mActivitiesList.get(position).getStatusTypeId() == 348) {
            holder.imgStatus.setImageResource(R.drawable.done);
            holder.imgNurStatus.setImageResource(R.drawable.done);
            holder.imgShStatus.setImageResource(R.drawable.done);
            holder.imglossStatus.setVisibility(View.GONE);
            holder.imglossStatustext.setText("NA");
        } else if (mActivitiesList.get(position).getStatusTypeId() == 349) {
            holder.imgStatus.setImageResource(R.drawable.rejected);
            holder.imgNurStatus.setImageResource(R.drawable.rejected);
            holder.imgShStatus.setImageResource(R.drawable.rejected);
            holder.imglossStatus.setVisibility(View.GONE);
            holder.imglossStatustext.setText("NA");
        } else if (mActivitiesList.get(position).getStatusTypeId() == 352) {
            holder.imgStatus.setImageResource(R.drawable.inprogress);
            holder.imgNurStatus.setImageResource(R.drawable.inprogress);
            holder.imgShStatus.setImageResource(R.drawable.inprogress);
            holder.imglossStatus.setVisibility(View.GONE);
            holder.imglossStatustext.setText("NA");
        }else if (mActivitiesList.get(position).getStatusTypeId() == 354) {
            holder.imglossStatustext.setVisibility(View.GONE);
            holder.imglossStatus.setVisibility(View.VISIBLE);
            holder.imglossStatus.setImageResource(R.drawable.done);
            holder.imgStatus.setImageResource(R.drawable.done);
            holder.imgNurStatus.setImageResource(R.drawable.done);
            holder.imgShStatus.setImageResource(R.drawable.done);
        }
        else{
            holder.imglossStatus.setImageDrawable(null);
        }
        if(mActivitiesList.get(position).getTargetDate()!= null) {
            try {
                Date oneWayTripDate = input.parse(mActivitiesList.get(position).getTargetDate());
                String datetimevaluereq = output.format(oneWayTripDate);
                holder.expecteddate.setText(datetimevaluereq);

                android.util.Log.e("===============", "======currentData======" + output.format(oneWayTripDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            holder.expecteddate.setText("TBD");

        }



        Log.d("getColorIndicator :", mActivitiesList.get(position).getColorIndicator() + "");

        if (mActivitiesList.get(position).getColorIndicator() == 0) {
            holder.activityName.setTextColor(context.getColor(R.color.black));
        } else if (mActivitiesList.get(position).getColorIndicator() == 1) {
            holder.activityName.setTextColor(context.getColor(R.color.green));
        } else if (mActivitiesList.get(position).getColorIndicator() == 2) {
            holder.activityName.setTextColor(context.getColor(R.color.yellow));
        } else if (mActivitiesList.get(position).getColorIndicator() == 3) {
            holder.activityName.setTextColor(context.getColor(R.color.red));
        }

        holder.mainlyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saplingActivitiesList = dataAccessHandler.getSaplingActivityData(Queries.getInstance().getSaplingActivityCounttQuery(ConsignmentCode, mActivitiesList.get(position).getActivityId() + ""));
                Log.d("saplingActivitiesListSize", saplingActivitiesList.size() + "");
                Log.e("=============>",mActivitiesList.get(position).getIsMultipleEntries());

                if (validations(position)) {
                    if (mActivitiesList.get(position).getIsMultipleEntries().equalsIgnoreCase("true") && saplingActivitiesList.size() != 0) {

                        Intent met = new Intent(context, MultipleEntryScreen.class);
                        met.putExtra("consignmentcode", ConsignmentCode);
                        met.putExtra("ActivityTypeId1", mActivitiesList.get(position).getActivityTypeId() + "");
                        met.putExtra("ActivityName1", mActivitiesList.get(position).getActivityName() + "");
                        met.putExtra("Ismultipleentry1", mActivitiesList.get(position).getIsMultipleEntries());
                        met.putExtra("status", mActivitiesList.get(position).getActivityStatus());
                        met.putExtra("statusId", mActivitiesList.get(position).getStatusTypeId());
                        met.putExtra("addActivity", false);

                        Log.d("consignmentcode1", ConsignmentCode);
                        context.startActivity(met);

                    } else {
                        // Todo Check ALready hava data or not.
                        // TODO Check Data Exisitng Or Not
                        if (!StringUtils.isEmpty(mActivitiesList.get(position).getActivityDoneDate())) {
                            // EXISTING UI
                            Boolean enableEditing = false;
                            if (mActivitiesList.get(position).getStatusTypeId() == 349) {
                                enableEditing = true;
                            }
                            Intent at = new Intent(context, ActivityTask.class);
                            at.putExtra("multipleEntry", mActivitiesList.get(position).getIsMultipleEntries());
                            at.putExtra("consignmentcode", ConsignmentCode);
                            at.putExtra("ActivityTypeId", mActivitiesList.get(position).getActivityTypeId() + "");
                            at.putExtra("ActivityName", mActivitiesList.get(position).getActivityName() + "");
                            at.putExtra("Code", mActivitiesList.get(position).getActivityCode());
//                            at.putExtra("DependentActivityCode", mActivitiesList.get(position).get() + "");
                            at.putExtra("enableEditing", enableEditing);
                            at.putExtra(CommonConstants.SCREEN_CAME_FROM, CommonConstants.FROM_SINGLE_ENTRY_EDITDATA);
                            context.startActivity(at);
                        } else {
//                      NEW ACTIVITY
                            Intent at = new Intent(context, ActivityTask.class);
                            at.putExtra("multipleEntry", false);
                            at.putExtra("consignmentcode", ConsignmentCode);
                            at.putExtra("ActivityTypeId", mActivitiesList.get(position).getActivityTypeId() + "");
                            at.putExtra("ActivityName", mActivitiesList.get(position).getActivityName() + "");
                            at.putExtra("enableEditing", true);
                            at.putExtra(CommonConstants.SCREEN_CAME_FROM, CommonConstants.FROM_SINGLE_ENTRY_EDITDATA);
                            context.startActivity(at);
                        }

                    }
                } else {

                    dependencyname = dataAccessHandler.getSingleValue(Queries.dependencyname(dependecyCode));
                    Log.d("dependencyname=============", dependencyname);
                    Toast.makeText(context, " Please Complete Nursery Manager Approval " + dependencyname + " Activity ", Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    private boolean validations(int position) {
        boolean issuces = false;

        dependecyCode = mActivitiesList.get(position).getDependentActivityCode();
//        if(mActivitiesList.get(position).getActivityId() == 12) {
//            Integer status_code = dataAccessHandler.getSingleIntValue(Queries.SporoutsCountstatus(ConsignmentCode, 11));
//            if (status_code == 348) {
//                issuces = true;
//            } else {
//                Toast.makeText(context, "Please Complete StateHead Approval to Sprouts arrived Count (PN-Arrival Of Sprouts)", Toast.LENGTH_LONG).show();
//                return false;
//            }
//        }
//        if(mActivitiesList.get(position).getActivityId() == 13) {
//            Integer status_code = dataAccessHandler.getSingleIntValue(Queries.SporoutsCountstatus(ConsignmentCode, 12));
//            if (status_code == 352) {
//                Toast.makeText(context, "Please Change PN-Sprout Count and Transit Loss Status", Toast.LENGTH_LONG).show();
//                return false;
//            } else {
//
//                return true;
//            }
//        }

        Log.d("dependecyCode=============", dependecyCode);
        if (StringUtils.isEmpty(dependecyCode) || dependecyCode == null || dependecyCode.equalsIgnoreCase("null") || dependecyCode == "") {
            issuces = true;
        } else {

            Integer statuscode = dataAccessHandler.getSingleIntValue(Queries.dependencystatus(ConsignmentCode, dependecyCode));

            if (statuscode != null) {
                issuces = true;
                if ( statuscode == 347 || statuscode == 348 || statuscode == 354) {
                    return true;
            }
                else{
                    dependencyname = dataAccessHandler.getSingleValue(Queries.dependencyname(dependecyCode));
                    Log.d("dependencyname=============", dependencyname);
                    Toast.makeText(context, " Please Complete Nursery Manager Approval " + dependencyname + " Activity ", Toast.LENGTH_LONG).show();
                    return false;
                }
            }

        }
//        else{
//
//            Integer statuscode = dataAccessHandler.getSingleIntValue(Queries.dependencystatus(ConsignmentCode, dependecyCode));
//            if ( statuscode == 347 || statuscode == 348) {
//                issuces = true;
//            }
//            else{
//                dependencyname = dataAccessHandler.getSingleValue(Queries.dependencyname(dependecyCode));
//                Log.d("dependencyname=============", dependencyname);
//                Toast.makeText(context, " Please Complete Nursery Manager Approval " + dependencyname + " Activity ", Toast.LENGTH_LONG).show();
//                issuces = false;
//            }
//        }
//
//        if (StringUtils.isEmpty(dependecyCode) || dependecyCode == null || dependecyCode.equalsIgnoreCase("null") || dependecyCode == "") {
//            issuces = true;
//        } else {
//
//            Integer statuscode = dataAccessHandler.getSingleIntValue(Queries.dependencystatus(ConsignmentCode, dependecyCode));
//
//            if (statuscode != null) {
//                issuces = true;
//            }
//        }

//        int Arrival_Sprouts = Integer.parseInt(dataAccessHandler.getSingleValue(Queries.sproutsforSowingg(ConsignmentCode, 7)));
//        Log.d("Arrival_Sprouts=============", Arrival_Sprouts+"");
//        if (Arrival_Sprouts == 0)
//
//        {
//        Toast.makeText(context, "Please Approve  Sprouts arrived Count (PN-Arrival Of Sprouts)", Toast.LENGTH_SHORT).show();
//        return false;}


//        if(mActivitiesList.get(position).getCode().equalsIgnoreCase("A013") && StringUtils.isEmpty(mActivitiesList.get(position).getUpdatedDate())){
//
//
//            Toast.makeText(context, mActivitiesList.get(position).getDependentActivityCode() + " dependency" + mActivitiesList.get(position).getName(), Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if(mActivitiesList.get(position).getCode().equalsIgnoreCase("A014") && StringUtils.isEmpty(mActivitiesList.get(position).getUpdatedDate())){
//            dependencyname = dataAccessHandler.getSingleValue(Queries.dependencyvalue(ConsignmentCode,mActivitiesList.get(position).getDependentActivityCode()));
//            dependencyname = dataAccessHandler.getSingleValue(Queries.dependencyvalue(ConsignmentCode,mActivitiesList.get(position).getDependentActivityCode()));
//
//            if(dependencyname!=null){
//                Toast.makeText(context, mActivitiesList.get(position).getDependentActivityCode() +"dependency " + mActivitiesList.get(position).getName() , Toast.LENGTH_LONG).show();
//                return true;
//            }
//
//            Toast.makeText(context, "", Toast.LENGTH_LONG).show();
//
//            return false;
//        }
//        if(mActivitiesList.get(position).getCode().equalsIgnoreCase("A015") && StringUtils.isEmpty(mActivitiesList.get(position).getUpdatedDate())){
//
//
//            Toast.makeText(context, mActivitiesList.get(position).getDependentActivityCode() + " dependency" + mActivitiesList.get(position).getName(), Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
        return issuces;
    }

    @Override
    public int getItemCount() {
        return mActivitiesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView activityName, expecteddate, imglossStatustext, txtStatusTxt, txtDoneDate;
        public LinearLayout mainlyt;
        public ImageView imgStatus, imgNurStatus, imgShStatus,imglossStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.activityName = (TextView) itemView.findViewById(R.id.activityName);
            this.expecteddate = (TextView) itemView.findViewById(R.id.expecteddate);
            this.txtStatusTxt = (TextView) itemView.findViewById(R.id.txtStatusTxt);
            this.txtDoneDate = (TextView) itemView.findViewById(R.id.txtDoneDate);
    this.imglossStatustext = (TextView )itemView.findViewById(R.id.imglossStatustext);
            this.mainlyt = (LinearLayout) itemView.findViewById(R.id.mainlyt);
            this.imgStatus = itemView.findViewById(R.id.imgStatus);
            this.imgNurStatus = itemView.findViewById(R.id.imgNurStatus);
            this.imgShStatus = itemView.findViewById(R.id.imgShStatus);
            this.imglossStatus = itemView.findViewById(R.id.imglossStatus);
        }
    }

    public void showDialog(Context activity) {

        final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.questionsdialouge);


        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }

}
