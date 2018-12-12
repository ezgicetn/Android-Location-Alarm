package com.example.air.locationalarm;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReminderListAdapter extends BaseAdapter {
    List<Reminder> reminderArrayList;
    public Activity context;


    ReminderListAdapter (List<Reminder> arrayList, Activity context){
        reminderArrayList = arrayList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return reminderArrayList.size();
    }

    @Override
    public Reminder getItem(int position) {
        return reminderArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Reminder reminder = getItem(position);

        if (convertView == null){

            LayoutInflater inflater = context.getLayoutInflater();

            convertView = inflater.inflate(R.layout.list_row,null);

            TextView title = convertView.findViewById(R.id.titleTextView);
            TextView detail = convertView.findViewById(R.id.detailTextView);

            title.setText(reminder.title);
            detail.setText(reminder.detail);


            return convertView;
        }

        return null;
    }
}
