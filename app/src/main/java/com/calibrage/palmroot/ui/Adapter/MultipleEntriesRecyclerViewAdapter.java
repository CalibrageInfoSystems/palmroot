package com.calibrage.palmroot.ui.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.cloudhelper.Log;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.dbmodels.LandlevellingFields;
import com.calibrage.palmroot.dbmodels.MutipleData;
import com.calibrage.palmroot.ui.ActivityTask;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class MultipleEntriesRecyclerViewAdapter extends RecyclerView.Adapter<MultipleEntriesRecyclerViewAdapter.ViewHolder> {

    public Context context;
    String convertedDate;
    private List<MutipleData> multiplelist;
    private List<LandlevellingFields> fieldslist;
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
    String ActivityTypeId, ActivityName, ismultipleentry;
    String ConsignmentCode;
    String status;

    public MultipleEntriesRecyclerViewAdapter(Context context, List<MutipleData> multiplelist, List<LandlevellingFields> fieldslist, String ActivityName, String ActivityTypeId, String ismultipleentry, String ConsignmentCode,String status) {
        this.context = context;
        this.multiplelist = multiplelist;
        this.fieldslist = fieldslist;
        this.ActivityTypeId = ActivityTypeId;
        this.ActivityName = ActivityName;
        this.ismultipleentry = ismultipleentry;
        this.ConsignmentCode = ConsignmentCode;
        this.status = status;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.multiplelayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MultipleEntriesRecyclerViewAdapter.ViewHolder holder, int position) {

        Log.d("ActivityTypeIdM", ActivityTypeId);
        Log.d("ActivityTypeNameM", ActivityName);
        holder.lyt_coments.setVisibility(View.GONE);


        if (multiplelist.get(position).getServerUpdatedStatus() == 1) {

            holder.editicon.setVisibility(View.GONE);
        } else {
            holder.editicon.setVisibility(View.VISIBLE);
        }

        holder.transactionId.setText("" + multiplelist.get(position).getTransactionId());
        holder.consignmentcode.setText("" + multiplelist.get(position).getConsignmentCode());
        holder.status.setText("" +multiplelist.get(position).getDesc());
        Log.d("Status=============80 ", "" +multiplelist.get(position).getDesc());
        Log.d("Status=============81 ", "" +status);
        Log.d("Status=============82", "" +multiplelist.get(position).getStatusTypeId());

        if (multiplelist.get(position).getComment() != null && !StringUtils.isEmpty(multiplelist.get(position).getComment())  && !multiplelist.get(position).getComment().equalsIgnoreCase("null"))
        {
            holder.lyt_coments.setVisibility(View.VISIBLE);
            holder.comment.setText("" + multiplelist.get(position).getComment());
        }

        if(multiplelist.get(position).getStatusTypeId() == 346){
            holder.lyt_coments.setVisibility(View.GONE);
        }

        if(multiplelist.get(position).getCreatedDate().contains("T")){
            Log.d("Created date =============95", "" +multiplelist.get(position).getCreatedDate());
            holder.createddate.setText("" + CommonUtils.getPropeCreateDate(multiplelist.get(position).getCreatedDate()));
        }else{
            Log.d("Created date =============98", "" +multiplelist.get(position).getCreatedDate());
            holder.createddate.setText("" + CommonUtils.getProperComplaintsDate(multiplelist.get(position).getCreatedDate()));

        }


        Log.d("Created date =============104", "" + CommonUtils.getProperComplaintsDate(multiplelist.get(position).getCreatedDate()));

        holder.infoicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(context);
            }
        });

        holder.mainlyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean showbutton = false;
                if(multiplelist.get(position).getStatusTypeId() == 349)
                {
                    showbutton = true;
                }
                Intent at = new Intent(context, ActivityTask.class);
                at.putExtra("consignmentcode", ConsignmentCode);
                at.putExtra("ActivityName", ActivityName);
                at.putExtra("multipleEntry", true);
                at.putExtra("ActivityTypeId", ActivityTypeId);
                at.putExtra("transactionId", multiplelist.get(position).getTransactionId());
                at.putExtra("enableEditing", showbutton);
                at.putExtra(CommonConstants.SCREEN_CAME_FROM, CommonConstants.FROM_MUTIPLE_ENTRY_EDITDATA);
                context.startActivity(at);
            }
        });


    }

    @Override
    public int getItemCount() {
        return multiplelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView transactionId, consignmentcode, status, comment, createddate;
        ImageView editicon, infoicon;
        LinearLayout mainlyt, lyt_coments;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.transactionId = (TextView) itemView.findViewById(R.id.transactionid);
            this.consignmentcode = (TextView) itemView.findViewById(R.id.conscode);
            this.lyt_coments = itemView.findViewById(R.id.lyt_coments);
            this.status = (TextView) itemView.findViewById(R.id.status);
            this.comment = (TextView) itemView.findViewById(R.id.comment);
            editicon = itemView.findViewById(R.id.editicon);
            infoicon = itemView.findViewById(R.id.infoicon);
            this.createddate = (TextView) itemView.findViewById(R.id.createddate);
            this.mainlyt = itemView.findViewById(R.id.mainlyt);
        }
    }

    public void showDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.infodialog);

        TextView maleregulartxt, maleregular, femaleregulartxt, femaleregular, maleoutsidetxt, maleoutside, femaleoutsidetxt, femaleoutside, tractorhirechargestxt, tractorhirecharges, diselchargestxt,
                diselcharges, completedtxt, completed;

        maleregulartxt = dialog.findViewById(R.id.maleregulartxt);
        maleregular = dialog.findViewById(R.id.maleregular);
        femaleregulartxt = dialog.findViewById(R.id.femaleregulartxt);
        femaleregular = dialog.findViewById(R.id.femaleregular);
        maleoutsidetxt = dialog.findViewById(R.id.maleoutsidetxt);
        maleoutside = dialog.findViewById(R.id.maleoutside);
        femaleoutsidetxt = dialog.findViewById(R.id.femaleoutsidetxt);
        femaleoutside = dialog.findViewById(R.id.femaleoutside);
        tractorhirechargestxt = dialog.findViewById(R.id.tractorhirechargestxt);
        tractorhirecharges = dialog.findViewById(R.id.tractorhirecharges);
        diselchargestxt = dialog.findViewById(R.id.diselchargestxt);
        diselcharges = dialog.findViewById(R.id.diselcharges);
        completedtxt = dialog.findViewById(R.id.completedtxt);
        completed = dialog.findViewById(R.id.completed);

        maleregulartxt.setText(fieldslist.get(0).getField());
        femaleregulartxt.setText(fieldslist.get(1).getField());
        maleoutsidetxt.setText(fieldslist.get(2).getField());
        femaleoutsidetxt.setText(fieldslist.get(3).getField());
        tractorhirechargestxt.setText(fieldslist.get(4).getField());
        diselchargestxt.setText(fieldslist.get(5).getField());
        completedtxt.setText(fieldslist.get(6).getField());

        String value;

        if (fieldslist.get(6).getValue() == 0) {
            value = "true";
        } else {
            value = "false";
        }

        maleregular.setText(":  " + fieldslist.get(0).getValue() + "");
        femaleregular.setText(":  " + fieldslist.get(1).getValue() + "");
        maleoutside.setText(":  " + fieldslist.get(2).getValue() + "");
        femaleoutside.setText(":  " + fieldslist.get(3).getValue() + "");
        tractorhirecharges.setText(":  " + fieldslist.get(4).getValue() + "");
        diselcharges.setText(":  " + fieldslist.get(5).getValue() + "");
        completed.setText(":  " + value);


        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }
}
