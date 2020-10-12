package com.example.keeprecords.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class RecordDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "records.db";
    public static final int DATABASE_VERSION = 1;

    public RecordDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQLite_CREATE_RECORD_DATABASE = " CREATE TABLE " + RecordContracts.RecordEntry.TABLE_NAME + " ("
                + RecordContracts.RecordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RecordContracts.RecordEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + RecordContracts.RecordEntry.COLUMN_TOTAL_AMOUNT + " INTEGER NOT NULL, "
                + RecordContracts.RecordEntry.COLUMN_PAID +" INTEGER NOT NULL DEFAULT 0, "
                + RecordContracts.RecordEntry.COLUMN_DATE +" TEXT NOT NULL );";

        sqLiteDatabase.execSQL(SQLite_CREATE_RECORD_DATABASE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
