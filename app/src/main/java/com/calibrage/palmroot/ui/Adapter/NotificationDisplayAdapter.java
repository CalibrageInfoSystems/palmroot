package com.calibrage.palmroot.ui.Adapter;

import android.graphics.Typeface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.common.CommonUtils;
import com.calibrage.palmroot.dbmodels.Alerts;

import java.util.List;

public class NotificationDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Alerts> alertsList;

    public NotificationDisplayAdapter(List<Alerts> alertsList) {
        this.alertsList = alertsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.noti_display_item, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationViewHolder) {
            NotificationViewHolder notificationViewHolder = (NotificationViewHolder) holder;
            Alerts alerts = alertsList.get(position);
            notificationViewHolder.headerTextView.setText(""+alerts.getName());
            notificationViewHolder.descriptionTextView.setText(alerts.getDesc());
            notificationViewHolder.createdDateTextView.setText(CommonUtils.getProperDate(alerts.getCreatedDate()));

            if (alerts.getIsRead() == 0) {
                notificationViewHolder.descriptionTextView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                notificationViewHolder.headerTextView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                notificationViewHolder.createdDateTextView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            } else {
                notificationViewHolder.descriptionTextView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                notificationViewHolder.headerTextView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                notificationViewHolder.createdDateTextView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            }

        }
    }

    @Override
    public int getItemCount() {
        return alertsList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;
        TextView descriptionTextView;
        TextView createdDateTextView;

        NotificationViewHolder(View view) {
            super(view);
            headerTextView = (TextView) view.findViewById(R.id.notiHeaderTxt);
            descriptionTextView = (TextView) view.findViewById(R.id.notiDesc);
            createdDateTextView = (TextView) view.findViewById(R.id.notiDate);
        }
    }
}
