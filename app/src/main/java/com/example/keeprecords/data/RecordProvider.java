package com.example.keeprecords.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.keeprecords.data.RecordContracts.RecordEntry;

public class RecordProvider extends ContentProvider {

    private RecordDbHelper mdbHelper;
    private static final int RECORDS = 100; // constant for querying the entire table
    private static final int RECORDS_ID = 101; // constant for querying a single record

    private static final UriMatcher mUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // uri for acting on the entire table
        mUrimatcher.addURI(RecordContracts.CONTENT_AUTHORITY,RecordContracts.PATH_RECORDS,RECORDS);
        // uri for acting ona single record
        mUrimatcher.addURI(RecordContracts.CONTENT_AUTHORITY,RecordContracts.PATH_RECORDS+"/#",RECORDS_ID);

    }
    @Override
    public boolean onCreate() {
        mdbHelper = new RecordDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase database = mdbHelper.getReadableDatabase();
        Cursor cursor ;

        int match = mUrimatcher.match(uri);

        switch (match){
            case RECORDS:{
                //querying the entire database
                cursor = database.query(RecordEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case RECORDS_ID:{
                //querying for a single record
                selection = RecordEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                /* SQLite implementation (SELECT database FROM _TABLE_NAME_ WHERE SELECTION = SELECTION_ARGS[] SORT BY sortOrder)*/
                cursor = database.query(RecordEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new IllegalArgumentException("Query not supported"+match);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match =  mUrimatcher.match(uri);
        switch (match){
            case RECORDS:{ return RecordEntry.CONTENT_LIST_TYPE;}
            case RECORDS_ID:{ return RecordEntry.CONTENT_ITEM_TYPE;}
            default:{ throw new IllegalArgumentException("Unknown uri "+uri+" with id "+match);}
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = mUrimatcher.match(uri);
        switch (match){
            case RECORDS:{
                return insertRecords(uri,contentValues);
            }
            default:
                throw new IllegalArgumentException("Insertion failed "+ match);
        }
    }

    private Uri insertRecords(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mdbHelper.getWritableDatabase();
        long resultId = database.insert(RecordEntry.TABLE_NAME,
                        null,
                        contentValues);
        if(resultId != -1){
            Toast.makeText(getContext(),"Insertion successful "+resultId,Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"Failed to insert record ",Toast.LENGTH_SHORT).show();
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,resultId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mdbHelper.getWritableDatabase();

        int match = mUrimatcher.match(uri);
        int rowDeleted;
        switch (match){
            case RECORDS:{
                rowDeleted = database.delete(RecordEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            case RECORDS_ID:{
                selection = RecordEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = database.delete(RecordEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Deletion failed "+match);
        }
        if(rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = mUrimatcher.match(uri);
        switch (match){
            case RECORDS:{
                return updateRecords(uri,contentValues,selection,selectionArgs);
            }
            case RECORDS_ID:{
                selection = RecordEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateRecords(uri,contentValues,selection,selectionArgs);
            }
            default:
                throw new IllegalArgumentException("Update failed "+match);
        }
    }

    private int updateRecords(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int rowUpdated;
        SQLiteDatabase database = mdbHelper.getWritableDatabase();
        rowUpdated = database.update(RecordEntry.TABLE_NAME,contentValues,selection,selectionArgs);

        if(rowUpdated !=0 ){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowUpdated;
    }
}
