package com.calibrage.palmroot.ui.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.dbmodels.ConsignmentData;
import com.calibrage.palmroot.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MultiConsignmentRecyclerviewAdapter extends RecyclerView.Adapter<MultiConsignmentRecyclerviewAdapter.ViewHolder> {

    public Context context;

    List<ConsignmentData> consignmentList = new ArrayList<>();

    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
    String convertedcreatedDate, nurceryId;

    public MultiConsignmentRecyclerviewAdapter(Context context, List<ConsignmentData> consignmentList, String nurceryId) {
        this.context = context;
        this.consignmentList = consignmentList;
        this.nurceryId = nurceryId;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.consignmentlayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.consignmentcode.setText( consignmentList.get(position).getConsignmentCode()+"");
        holder.originname.setText( consignmentList.get(position).getOriginname());
        holder.vendorname.setText( consignmentList.get(position).getVendorname());
        holder.varietyname.setText( consignmentList.get(position).getVarietyname());
//        holder.txtStatusTxt.setText( consignmentList.get(position).getStatus());

        String status = consignmentList.get(position).getStatus();

        View strip = holder.statusColorStrip;
        holder.txtStatusTxt.setText("     "+status);

        holder.txtStatusTxt.setGravity(View.TEXT_ALIGNMENT_CENTER);

// Reset default background
        holder.txtStatusTxt.setBackground(null);

// Change background based on status
        switch (status.toLowerCase()) {
            case "primary":
                strip.setBackgroundColor(Color.parseColor("#00C853")); // Green
                holder.txtStatusTxt.setBackgroundResource(R.drawable.bg_status_primary);
                holder.txtStatusTxt.setTextColor(Color.parseColor("#0F5132")); // dark green text
                break;

            case "pre arrival":
                strip.setBackgroundColor(Color.parseColor("#FFD600")); // Yellow// light yellow bg
                holder.txtStatusTxt.setBackgroundResource(R.drawable.bg_status_prearrival);
                holder.txtStatusTxt.setTextColor(Color.parseColor("#795548")); // brown
                break;

            case "secondary":
                strip.setBackgroundColor(Color.parseColor("#2962FF")); // Blue; // light blue
                holder.txtStatusTxt.setBackgroundResource(R.drawable.bg_status_secondary);
                holder.txtStatusTxt.setTextColor(Color.parseColor("#1565C0")); // blue
                break;

            case "teritory":
                strip.setBackgroundColor(Color.parseColor("#D50000")); // Red // pink
                holder.txtStatusTxt.setBackgroundResource(R.drawable.bg_status_teritory);
                holder.txtStatusTxt.setTextColor(Color.parseColor("#880E4F")); // dark pink
                break;

            default:
                strip.setBackgroundColor(Color.GRAY); // fallback
                holder.txtStatusTxt.setBackgroundColor(Color.GRAY);
                holder.txtStatusTxt.setTextColor(Color.WHITE);
                break;
        }


        holder.estimatedqty.setText(consignmentList.get(position).getEstimatedQuantity() + "");
//        holder.ordereddate.setText(":  " +  CommonUtils.getProperComplaintsDate(consignmentList.get(position).getCreatedDate()));
        holder.ordereddate.setText( CommonUtils.getProperComplaintsDate2(consignmentList.get(position).getCreatedDate()));
//        Log.d("ArrivedDate", consignmentList.get(position).getArrivedDate());

        holder.lytarrivaldate.setVisibility(View.GONE);
        holder.lytarrivedqty.setVisibility(View.GONE);
        if (consignmentList.get(position).getArrivedDate() == null || consignmentList.get(position).getArrivedDate().equalsIgnoreCase("null") || consignmentList.get(position).getArrivedDate() == "null" || TextUtils.isEmpty(consignmentList.get(position).getArrivedDate())) {
            holder.lytarrivaldate.setVisibility(View.GONE);
            holder.arrivaldate.setText("" + "");

        } else {
            holder.lytarrivaldate.setVisibility(View.VISIBLE);
            holder.arrivaldate.setText( CommonUtils.getProperComplaintsDate2(consignmentList.get(position).getArrivedDate())+"");

        }

        if (consignmentList.get(position).getArrivedQuantity() == 0 || TextUtils.isEmpty(consignmentList.get(position).getArrivedQuantity() + "")) {
            holder.lytarrivedqty.setVisibility(View.GONE);
            holder.arrivedqty.setText("" + "");
        } else {
            holder.lytarrivedqty.setVisibility(View.VISIBLE);
            holder.arrivedqty.setText( consignmentList.get(position).getArrivedQuantity() + "");

        }
  /*      holder.consignmentcode.setText( consignmentList.get(position).getConsignmentCode()+"");
        holder.originname.setText(consignmentList.get(position).getOriginname()+"");
        holder.vendorname.setText( consignmentList.get(position).getVendorname()+"");
        holder.varietyname.setText( consignmentList.get(position).getVarietyname()+"");
        holder.txtStatusTxt.setText( consignmentList.get(position).getStatus()+"");
        holder.estimatedqty.setText( consignmentList.get(position).getEstimatedQuantity() + "");
        holder.ordereddate.setText( CommonUtils.getProperComplaintsDate2(consignmentList.get(position).getCreatedDate()));
//        Log.d("ArrivedDate", consignmentList.get(position).getArrivedDate());

        holder.lytarrivaldate.setVisibility(View.GONE);
        holder.lytarrivedqty.setVisibility(View.GONE);
        if (consignmentList.get(position).getArrivedDate() == null || consignmentList.get(position).getArrivedDate().equalsIgnoreCase("null") || consignmentList.get(position).getArrivedDate() == "null" || TextUtils.isEmpty(consignmentList.get(position).getArrivedDate())) {
            holder.lytarrivaldate.setVisibility(View.GONE);
            holder.arrivaldate.setText(":  " + "");

        } else {
            holder.lytarrivaldate.setVisibility(View.VISIBLE);
            holder.arrivaldate.setText(CommonUtils.getProperComplaintsDate2( consignmentList.get(position).getArrivedDate())+"");

        }

        if (consignmentList.get(position).getArrivedQuantity() == 0 || TextUtils.isEmpty(consignmentList.get(position).getArrivedQuantity() + "")) {
            holder.lytarrivedqty.setVisibility(View.GONE);
            holder.arrivedqty.setText("" + "");
        } else {
            holder.lytarrivedqty.setVisibility(View.VISIBLE);
            holder.arrivedqty.setText(consignmentList.get(position).getArrivedQuantity() + "");

        }*/

        holder.mainlyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consignmentList.get(position).setChecked(!   consignmentList.get(position).isChecked());
                holder.imageView.setVisibility(consignmentList.get(position).isChecked() ? View.VISIBLE : View.GONE);

//                Intent intent = new Intent(context, Activities.class);
//                intent.putExtra("nurceryId", nurceryId);
//                intent.putExtra("ConsignmentCode", consignmentList.get(position).getConsignmentCode());
//                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return consignmentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView consignmentcode, originname, vendorname, varietyname, estimatedqty, ordereddate, arrivaldate, arrivedqty,txtStatusTxt;
        LinearLayout mainlyt, lytarrivaldate, lytarrivedqty;
         ImageView imageView;
        View statusColorStrip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.consignmentcode = (TextView) itemView.findViewById(R.id.consignmentcode);
            this.originname = (TextView) itemView.findViewById(R.id.originname);
            this.vendorname = (TextView) itemView.findViewById(R.id.vendorName);
            this.varietyname = (TextView) itemView.findViewById(R.id.vareityName);
             this.txtStatusTxt =(TextView) itemView.findViewById(R.id.txtStatusTxt);
            this.estimatedqty = (TextView) itemView.findViewById(R.id.estimatedQty);
            this.ordereddate = (TextView) itemView.findViewById(R.id.ordereddate);
            this.arrivaldate = (TextView) itemView.findViewById(R.id.arrivaldate);
            this.arrivedqty = (TextView) itemView.findViewById(R.id.arrivedqty);
            this.lytarrivaldate = itemView.findViewById(R.id.lytarrivaldate);
            this.lytarrivedqty = itemView.findViewById(R.id.lytarrivedqty);
            this.imageView = itemView.findViewById(R.id.imageView);
            mainlyt = (LinearLayout) itemView.findViewById(R.id.mainnlyt);
            this.statusColorStrip = itemView.findViewById(R.id.statusColorStrip);
        }
    }
    public ArrayList<ConsignmentData> getSelected() {
        ArrayList<ConsignmentData> selected = new ArrayList<>();
        for (int i = 0; i < consignmentList.size(); i++) {
            if (consignmentList.get(i).isChecked()) {
                selected.add(consignmentList.get(i));
            }
        }
        return selected;
    }
}
