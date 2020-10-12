package com.example.keeprecords;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.keeprecords.data.RecordContracts.RecordEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView tv_content;
    ListView mListView;
    FloatingActionButton fab;
    RecordAdapter mAdapter;

    //Loader id
    private static final int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mListView =findViewById(R.id.list_view_records);
        tv_content=findViewById(R.id.tv_content);
        fab = findViewById(R.id.fab_editor_activity);
        mAdapter = new RecordAdapter(this,null);

        mListView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        LoaderManager.getInstance(this).initLoader(LOADER_ID,null,this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Uri uri = ContentUris.withAppendedId(RecordEntry.CONTENT_URI,id);
                Intent intent =new Intent(MainActivity.this,EditorActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_insert_dummy_data:
                //insert dummy data to the database
                insertData();
                return true;
            case R.id.menu_delete_data:
                //delete all the records from the database
                deleteAll();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void insertData(){

        ContentValues values = new ContentValues();
        values.put(RecordEntry.COLUMN_NAME,"Mr.Mukesh");
        values.put(RecordEntry.COLUMN_TOTAL_AMOUNT,1000);
        values.put(RecordEntry.COLUMN_PAID,100);
        values.put(RecordEntry.COLUMN_DATE,"Jan 1, 2020");

        Uri uri = getContentResolver().insert(RecordEntry.CONTENT_URI,values);

    }
    private void deleteAll(){

        int rowDeleted = getContentResolver().delete(RecordEntry.CONTENT_URI,null,null);

        if(rowDeleted != 0){
            Toast.makeText(getApplicationContext(),"Records Deleted Successfully",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Deletion unsuccessful",Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection  = {RecordEntry._ID,
                RecordEntry.COLUMN_DATE,
                RecordEntry.COLUMN_NAME,
                RecordEntry.COLUMN_TOTAL_AMOUNT,
                RecordEntry.COLUMN_PAID};

        return new CursorLoader(getApplicationContext(),
                RecordEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if(data == null)
            return;

        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }
}