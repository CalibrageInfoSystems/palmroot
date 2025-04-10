package com.calibrage.palmroot.ui.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.dbmodels.FertilizerDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FertilizerdetailsAdapter extends RecyclerView.Adapter<FertilizerdetailsAdapter.ViewHolder> {

public Context context;
List<FertilizerDetails> Fertilizer_details = new ArrayList<>();


public FertilizerdetailsAdapter(Context context, List<FertilizerDetails> Fertilizer_list) {
        this.context = context;
        this.Fertilizer_details = Fertilizer_list;

        }

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.fertilizerdetails, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
        }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("======>",Fertilizer_details.get(position).getActivityName());
        Log.e("======>Date",Fertilizer_details.get(position).getDoneOn());

    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
        holder.Productnamef.setText(" :  "+Fertilizer_details.get(position).getProductname());
        holder.quantityf.setText(" :  "+Fertilizer_details.get(position).getQuantity() +" " +Fertilizer_details.get(position).getUOM());
        holder.donebyf.setText(" :  "+Fertilizer_details.get(position).getDoneBy());



        try {
            Date oneWayTripDate = input.parse(Fertilizer_details.get(position).getDoneOn());
            String datetimevaluereq = output.format(oneWayTripDate);
            holder.doneonf.setText(" :  "+ datetimevaluereq);

            android.util.Log.e("===============", "======currentData======99===" + output.format(oneWayTripDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
   //    Log.d("Done on ======>51", "" +Fertilizer_details.get(position).getDoneOn());
   //     holder.doneonf.setText(" :  "+CommonUtils.getPropeCreateDate(Fertilizer_details.get(position).getDoneOn()));
//    }else{
//    Log.d("Done on ======>54", "" +Fertilizer_details.get(position).getDoneOn());
//        holder.doneonf.setText(" :  "+CommonUtils.getProperComplaintsDate(Fertilizer_details.get(position).getDoneOn()));
//
//    }

   }

@Override
public int getItemCount() {
        return Fertilizer_details.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView Productnamef,quantityf,donebyf,doneonf;
    LinearLayout mainlyt;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);


        this.donebyf= (TextView )itemView.findViewById(R.id.donebyf);
        this.quantityf= (TextView )itemView.findViewById(R.id.quantityf);
        this.Productnamef = (TextView )itemView.findViewById(R.id.Productnamef);
        this.doneonf = (TextView )itemView.findViewById(R.id.doneonf);
        mainlyt = (LinearLayout ) itemView.findViewById(R.id.mainlyt);
    }
}
}
