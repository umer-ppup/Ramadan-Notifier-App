package com.atrule.ramadannotifier.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.atrule.ramadannotifier.R;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    private final List<String> strings;
    private final Context context;

    public SpinnerAdapter(List<String> strings, Context context) {
        this.context = context;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }
        TextView names = convertView.findViewById(R.id.tvCountryName);

        names.setText(strings.get(position));

        return convertView;
    }
}
