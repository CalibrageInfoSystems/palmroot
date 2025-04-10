package com.calibrage.palmroot.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.ConsignmentSelectionScreen;
import com.calibrage.palmroot.R;
import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.dbmodels.NurseryData;

import java.util.ArrayList;
import java.util.List;

public class NurseryRecyclerviewAdapternew extends RecyclerView.Adapter<NurseryRecyclerviewAdapternew.ViewHolder> {

    public Context context;

    List<NurseryData> nurserysList = new ArrayList<>();


    public NurseryRecyclerviewAdapternew(Context context, List<NurseryData> nurserysList) {
        this.context = context;
        this.nurserysList = nurserysList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.nurserylayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.nurseryName.setText(":  " +nurserysList.get(position).getName());
        holder.nurserycode.setText(":  "+nurserysList.get(position).getCode());
        holder.nurseryaddress.setText(":  "+nurserysList.get(position).getVillagename());
        holder.pincode.setText(":  "+nurserysList.get(position).getPinCode() + "");

        holder.mainlyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonConstants.NurseryCode  = nurserysList.get(position).getCode();
               // Intent intent = new Intent(context, ConsignmentmultiSelectionScreen.class);
                Intent intent = new Intent(context, ConsignmentSelectionScreen.class);
                intent.putExtra("NurseryCode",nurserysList.get(position).getCode());
                CommonConstants.NurseryCode = nurserysList.get(position).getCode();
                CommonConstants.NurseryName = nurserysList.get(position).getName();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nurserysList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nurseryName,nurserycode,nurseryaddress, pincode;
        LinearLayout mainlyt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nurseryName = (TextView )itemView.findViewById(R.id.nurseryname);
            this.nurserycode = (TextView )itemView.findViewById(R.id.nurserycode);
            this.nurseryaddress = (TextView )itemView.findViewById(R.id.nurseryaddress);
            this.pincode = (TextView )itemView.findViewById(R.id.nurserypincode);
            mainlyt = (LinearLayout ) itemView.findViewById(R.id.mainlyt);
        }
    }


}
