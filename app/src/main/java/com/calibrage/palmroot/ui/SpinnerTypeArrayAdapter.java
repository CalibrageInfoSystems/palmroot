package com.calibrage.palmroot.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.calibrage.palmroot.R;
import com.calibrage.palmroot.dbmodels.TypeItem;

import java.util.ArrayList;

public class SpinnerTypeArrayAdapter extends ArrayAdapter<TypeItem> {
    ArrayList<TypeItem> _algorithmList= new ArrayList<>();
    public SpinnerTypeArrayAdapter(Context context,
                                   ArrayList<TypeItem> algorithmList) {
        super(context, 0, algorithmList);
        this._algorithmList = algorithmList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public TypeItem getItem(int position) {
        return _algorithmList.get(position);
    }

    private View initView(int position, View convertView,
                          ViewGroup parent) {
        // It is used to set our custom view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinneritem, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.text);
        TypeItem currentItem = getItem(position);

        // It is used the name to the TextView when the
        // current item is not null.
        if (currentItem != null) {
            textViewName.setText(currentItem.getName());
        }
        return convertView;
    }
}