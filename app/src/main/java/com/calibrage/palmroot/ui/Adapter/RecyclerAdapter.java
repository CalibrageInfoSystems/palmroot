package com.calibrage.palmroot.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.dbmodels.CheckNurseryAcitivity;
import com.calibrage.palmroot.dbmodels.SaplingActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


    private LayoutInflater layoutInflater;
    List<CheckNurseryAcitivity> saplingActivities_List = new ArrayList<>();
   // private List<RequestCompleteModel> allrequests;
    private Context ctx;
    private DataAccessHandler dataAccessHandler;
     ClickListner listner;

    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
    DecimalFormat dec = new DecimalFormat("####0.00");
    public RecyclerAdapter(Context ctx,  List<CheckNurseryAcitivity> saplingActivities_List, ClickListner listner) {
        this.layoutInflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.saplingActivities_List =saplingActivities_List;
        this.listner = listner;
   //     this.allrequests = allrequests;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.recycler_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        dataAccessHandler = new DataAccessHandler(ctx);
        holder.transactionid.setText(" :   " + saplingActivities_List.get(position).getName());
        holder.consignmentcode.setText(" :   " + saplingActivities_List.get(position).getConsignmentCode());
//        if(saplingActivities_List.get(position).getStatusTypeId() == 352 || saplingActivities_List.get(position).getStatusTypeId() == 349 || saplingActivities_List.get(position).getDesc() == null) {
//            holder.linear_layout.setVisibility(View.VISIBLE);
//        }
//        else{
//            holder.linear_layout.setVisibility(View.GONE);
//        }
     if( saplingActivities_List.get(position).getDesc()!=null){
        holder.status.setText(" :   " +saplingActivities_List.get(position).getDesc());
     }
     else{
         holder.status.setText(" :   " +"Open");

     }


        if(saplingActivities_List.get(position).getActivityDoneDate()!= null) {
            try {
                Date oneWayTripDate = input.parse(saplingActivities_List.get(position).getActivityDoneDate());
                String datetimevaluereq = output.format(oneWayTripDate);

                holder.jobdonedate.setText(" :   " +datetimevaluereq);
                android.util.Log.e("===============", "======currentData======" + output.format(oneWayTripDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            holder.jobdone_linear.setVisibility(View.GONE);
        }



//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                    listner.onNotificationClick(position, saplingActivities_List.get(position));
//                    Intent intent = new Intent(ctx, TransactionDataActivity.class);
//                    intent.putExtra("transactionId", saplingActivities_List.get(position).getTransactionId());
//
//                    ctx.startActivity(intent);
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return saplingActivities_List.size();
    }


    public interface ClickListner {
        void onNotificationClick(int po, SaplingActivity  saplings);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView transactionid,consignmentcode,status,jobdonedate;
        LinearLayout Status_linear,jobdone_linear,linear_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

          Status_linear = itemView.findViewById(R.id.Status_linear);
            jobdone_linear = itemView.findViewById(R.id.jobdone_linear);
            linear_layout = itemView.findViewById(R.id.linear_layout);
            ctx = itemView.getContext();

            transactionid = itemView.findViewById(R.id.transactionid);
            consignmentcode = itemView.findViewById(R.id.consignmentcode);
            status = itemView.findViewById(R.id.status);
            jobdonedate = itemView.findViewById(R.id.jobdonedate);
        }
    }
}

