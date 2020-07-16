package com.example.ximenamoure.petvacay.Adapters;

import android.app.Activity;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.ximenamoure.petvacay.Models.DayOfWeek;
import com.example.ximenamoure.petvacay.Models.Pet;
import com.example.ximenamoure.petvacay.R;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class DayOfWeekListAdapter extends ArrayAdapter<DayOfWeek> {

    private ArrayList<DayOfWeek> mDays;
    private Context mContext;

    public DayOfWeekListAdapter(Context context, ArrayList<DayOfWeek> days){
        super(context, 0, days);
        mContext = context;
        this.mDays =days;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_disponibility_item, parent, false);
        }

        DayOfWeek day = getItem(position);

        CheckBox item = (CheckBox) convertView.findViewById(R.id.itemBox);
        TextView dayText = (TextView) convertView.findViewById(R.id.textBox);
        item.setChecked(day.IsChecked());
        item.setTag(position);
        dayText.setText(day.GeWeekDayName());

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox) v;
                int pos = (int) c.getTag();
                DayOfWeek d = mDays.get(pos);
                d.SetChecked(!d.IsChecked());
            }
        });

        return convertView;
    }
}
