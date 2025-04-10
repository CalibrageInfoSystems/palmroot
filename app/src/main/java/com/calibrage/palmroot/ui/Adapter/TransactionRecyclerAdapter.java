package com.calibrage.palmroot.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.database.DataAccessHandler;
import com.calibrage.palmroot.database.Queries;
import com.calibrage.palmroot.dbmodels.DisplayData;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder> {


    private LayoutInflater layoutInflater;
    List<DisplayData> displayData = new ArrayList<>();
    private Context ctx;
    private DataAccessHandler dataAccessHandler;

    DecimalFormat dec = new DecimalFormat("####0.00");
    public TransactionRecyclerAdapter(Context ctx,   List<DisplayData> display_Datat) {
        this.layoutInflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.displayData =display_Datat;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.displaydata_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        dataAccessHandler = new DataAccessHandler(ctx);

        String Fieldid = displayData.get(position).getFieldId()+"";

        String Field_name =  dataAccessHandler.getSingleValue(Queries.getField(Fieldid));
        holder.fieldname.setText("" +Field_name);


        holder.value.setText("" + displayData.get(position).getValue());




    }

    @Override
    public int getItemCount() {
        return displayData.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fieldname,inputtype,value;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // LinearLayout  linearLayout = itemView.findViewById(R.id.consignmentcode);

            ctx = itemView.getContext();

            fieldname = itemView.findViewById(R.id.fieldname);

            value = itemView.findViewById(R.id.value);
        }
    }
}

