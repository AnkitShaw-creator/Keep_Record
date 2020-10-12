package com.example.keeprecords;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeprecords.data.RecordContracts.RecordEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText mName ;
    EditText mTotalAmount;
    EditText mAmountPaid;
    EditText mAmountRemaining;
    EditText mPaidToday;

    Uri currentUri;

    private static final int LOADER_ID = 1;
    private boolean mHasChanged = false;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent callerIntent = getIntent();
        currentUri = callerIntent.getData();

        if(currentUri != null){
            setTitle("Update Record");
            LoaderManager.getInstance(this).initLoader(LOADER_ID,null,this);
        }
        else{
            setTitle("New Record");
            invalidateOptionsMenu();
        }

        mName = findViewById(R.id.edit_name);
        mTotalAmount =findViewById(R.id.edit_tAmount);
        mAmountPaid = findViewById(R.id.edit_pAmount);
        mAmountRemaining = findViewById(R.id.edit_rAmount);
        mPaidToday = findViewById(R.id.edit_pAmount_today);

        mName.setOnTouchListener(onTouchListener);
        mTotalAmount.setOnTouchListener(onTouchListener);
        mAmountPaid.setOnTouchListener(onTouchListener);
        mPaidToday.setOnTouchListener(onTouchListener);
        mAmountRemaining.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onBackPressed() {
        if(!mHasChanged) {
            // if no changes are made, just exit the activity
            super.onBackPressed();
            return;
        }


        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Discard your changes and quit editing?");
        alertDialog.setPositiveButton("Discard",discardButtonClickListener);
        alertDialog.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User click Keep editing and continues to make changes
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void showClearFieldDialogBox() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Do you want to delete this record?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteRecord();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void deleteRecord() {
        if(currentUri != null){
            int rowDeleted = getContentResolver().delete(currentUri,null,null);
            if(rowDeleted != 0 ){
                Toast.makeText(getApplicationContext(),"Record Deleted successfully "+rowDeleted,Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Record was not Deleted "+rowDeleted,Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void saveRecord() {

        String name = mName.getText().toString();
        int TotalAmount = Integer.parseInt(String.valueOf(mTotalAmount.getText()));
        int AmountPaid = Integer.parseInt(String.valueOf(mAmountPaid.getText()));
        int PaidToday = Integer.parseInt(String.valueOf(mPaidToday.getText()));

        int AMOUNT_PAID = AmountPaid+PaidToday;

        DateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        String date = dateFormat.format(Calendar.getInstance().getTime());

        mAmountRemaining.setText(String.valueOf(TotalAmount-AMOUNT_PAID));

        ContentValues values = new ContentValues();
        values.put(RecordEntry.COLUMN_NAME,name);
        values.put(RecordEntry.COLUMN_TOTAL_AMOUNT,TotalAmount);
        values.put(RecordEntry.COLUMN_PAID,AMOUNT_PAID);
        values.put(RecordEntry.COLUMN_DATE,date);


        if(currentUri != null){
            int rowUpdated = getContentResolver().update(currentUri,values,null,null);

            if(rowUpdated != 0 ){
                Toast.makeText(getApplicationContext(),"Record Updated successfully "+rowUpdated,Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Record was not updated "+rowUpdated,Toast.LENGTH_SHORT).show();
            }
        }

        else {
            Uri insertUri =getContentResolver().insert(RecordEntry.CONTENT_URI,values);

            if(insertUri != null ){
                Toast.makeText(getApplicationContext(),"Record Inserted successfully ",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Record was not inserted ",Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_save:{
                saveRecord();
                finish();
                return true;
            }
            case R.id.action_clear_field: {
                showClearFieldDialogBox();
                return true;
            }
            case android.R.id.home:{
                if(!mHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(currentUri == null){
            MenuItem menuItem =menu.getItem(R.id.action_clear_field);
            menuItem.setVisible(false);
        }
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        // columns to extract from the table
        String[] projection ={  RecordEntry._ID,
                                RecordEntry.COLUMN_NAME,
                                RecordEntry.COLUMN_TOTAL_AMOUNT,
                                RecordEntry.COLUMN_PAID,
                                RecordEntry.COLUMN_DATE};

        return new CursorLoader(this,currentUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data == null){ return;}


        if(data.moveToFirst()){
            //extracting the contents of the cursor
            String name = data.getString(data.getColumnIndex(RecordEntry.COLUMN_NAME));
            int totalAmount = data.getInt(data.getColumnIndex(RecordEntry.COLUMN_TOTAL_AMOUNT));
            int amountPaid = data.getInt(data.getColumnIndex(RecordEntry.COLUMN_PAID));
            int amountRemaining = totalAmount - amountPaid;


            mName.setText(name);
            mTotalAmount.setText(String.valueOf(totalAmount));
            mAmountPaid.setText(String.valueOf(amountPaid));
            mAmountRemaining.setText(String.valueOf(amountRemaining));
            mPaidToday.setText("0");
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mName.setText("");
        mTotalAmount.setText("0");
        mAmountPaid.setText("0");
        mAmountRemaining.setText("0");
    }
}