package com.example.keeprecords;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.keeprecords.data.RecordContracts;

public class RecordAdapter extends CursorAdapter {


    public RecordAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_items,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView mName = view.findViewById(R.id.textView_name);
        TextView totalAmount = view.findViewById(R.id.textView_tAmount);
        TextView mDate =view.findViewById(R.id.textView_date);
        TextView paidAmt = view.findViewById(R.id.textView_pAmount);
        TextView remainingAmt = view.findViewById(R.id.textView_rAmount);


        String name = cursor.getString(cursor.getColumnIndexOrThrow(RecordContracts.RecordEntry.COLUMN_NAME));
        int tAmount = cursor.getInt(cursor.getColumnIndexOrThrow(RecordContracts.RecordEntry.COLUMN_TOTAL_AMOUNT));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(RecordContracts.RecordEntry.COLUMN_DATE));
        int paid =cursor.getInt(cursor.getColumnIndexOrThrow(RecordContracts.RecordEntry.COLUMN_PAID));

        int remaining = tAmount-paid;

        mName.setText(name);
        totalAmount.setText(String.valueOf(tAmount));
        mDate.setText(date);
        paidAmt.setText(String.valueOf(paid));
        remainingAmt.setText(String.valueOf(remaining));


    }
}
