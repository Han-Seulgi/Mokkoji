package com.example.project_test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MySpinnerAdapter extends ArrayAdapter<String> {
    public MySpinnerAdapter(Context context, int resource, List<String>objects){
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if (position == getCount()){
            ((TextView)v.findViewById(android.R.id.text1)).setText("");
            ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
        }
        return v;
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }
}
