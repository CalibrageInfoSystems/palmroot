package com.calibrage.palmroot.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.dbmodels.NurseryRMTransctions;
import com.calibrage.palmroot.ui.RMActivityFields;

import java.util.List;

public class RMTransactionRecyclerViewAdapter extends RecyclerView.Adapter<RMTransactionRecyclerViewAdapter.MyViewHolder> {

    public Context mContext;
    private List<NurseryRMTransctions> RMTransactionlist;
 String Activityname,RmActivityId;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionId, status, comment, createddate;
        ImageView editicon, statusSymbol;
        LinearLayout mainlyt, lyt_coments;

        public MyViewHolder(View view) {
            super(view);
            this.transactionId = (TextView) itemView.findViewById(R.id.transactionid);
             this.lyt_coments = itemView.findViewById(R.id.lyt_coments);
            this.status = (TextView) itemView.findViewById(R.id.status);
            this.comment = (TextView) itemView.findViewById(R.id.comment);
            editicon = itemView.findViewById(R.id.editicon);
            statusSymbol = itemView.findViewById(R.id.statusSymbol);
            this.createddate = (TextView) itemView.findViewById(R.id.createddate);
            this.mainlyt = itemView.findViewById(R.id.mainlyt);
        }
    }


    public RMTransactionRecyclerViewAdapter(Context context, List<NurseryRMTransctions> RMTransaction_list, String Activity_name,String RmActivityId) {
        this.mContext = context;
        this.Activityname = Activity_name;
        this.RMTransactionlist = RMTransaction_list;
        this.RmActivityId = RmActivityId;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rm_transactionlist_item, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {


        holder.transactionId.setText("" + RMTransactionlist.get(position).getTransactionId());
        if(RMTransactionlist.get(position).getActivityTypeId() == 379){
        holder.comment.setText("Other");}
        else if(RMTransactionlist.get(position).getActivityTypeId() == 378){

            holder.comment.setText("Labour" );}
     holder.status.setText("" +RMTransactionlist.get(position).getDesc());
        holder.createddate.setText("" +RMTransactionlist.get(position).getCreatedDate());


        if(RMTransactionlist.get(position).getStatusTypeId() == 349 ){
            holder.statusSymbol.setImageResource(R.drawable.rejected);

            holder.mainlyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  List<NurseryIrrigationLogXref> IrrigationLogXreflist =new ArrayList<>();


                    Intent intent = new Intent(mContext, RMActivityFields.class);
                 intent.putExtra("Name", Activityname);
                    intent.putExtra("camefrom",  2);
                    intent.putExtra("transactionId",   RMTransactionlist.get(position).getTransactionId());
                    intent.putExtra("ActivityId",   RmActivityId);
                    mContext.startActivity(intent);




                }
            });



        }
        else if(RMTransactionlist.get(position).getStatusTypeId() == 346){
            holder.statusSymbol.setImageResource(R.drawable.inprogress);
            holder.mainlyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  List<NurseryIrrigationLogXref> IrrigationLogXreflist =new ArrayList<>();


                    Intent intent = new Intent(mContext, RMActivityFields.class);
                    intent.putExtra("Name", Activityname);
                    intent.putExtra("camefrom",  3);
                    intent.putExtra("ActivityId",   RmActivityId);
                    intent.putExtra("transactionId",   RMTransactionlist.get(position).getTransactionId());
                    mContext.startActivity(intent);




                }
            });
        }
        else{
            holder.statusSymbol.setImageResource(R.drawable.done);
            holder.mainlyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  List<NurseryIrrigationLogXref> IrrigationLogXreflist =new ArrayList<>();


                    Intent intent = new Intent(mContext, RMActivityFields.class);
                    intent.putExtra("Name", Activityname);
                    intent.putExtra("camefrom",  3);
                    intent.putExtra("ActivityId",   RmActivityId);
                    intent.putExtra("transactionId",   RMTransactionlist.get(position).getTransactionId());
                    mContext.startActivity(intent);




                }
            });
        }



       //  Picasso.with(context).load(settings.getImage()).error(R.drawable.ic_user).transform(new CircleTransform()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return RMTransactionlist.size();
    }



//
//    public interface RequestAdapterListener {
//        void onContactSelected(GetServicesByStateCode.ListResult list);
//    }
}
