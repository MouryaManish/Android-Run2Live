package com.test.manish.run2live;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.test.manish.run2live.database.AppDatabase;
import com.test.manish.run2live.domain.userData;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private GridView grid;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        grid = (GridView) findViewById(R.id.gridview);
        db = AppDatabase.getInMemoryDatabase(getApplicationContext());
        List<String> data = new ArrayList<String>();
            data.add("Date");
            data.add("Time");
            data.add("Span");
            data.add("Speed");
         List<userData> userdataSet = db.totalDb().getHistory();
         if(userdataSet == null){

         }else{//
             for(userData userdata : userdataSet){
               String[] a = userdata.getDate().split("/");
                 data.add(a[0]+"/" + a[1]);
                 data.add(userdata.getTime());
                 data.add(Double.toString(userdata.getDistance()));
                 data.add(Double.toString(userdata.getSpeed()));
                }
         }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.data_layout, data);
        grid.setAdapter(adapter);
    }




    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent();
        AppDatabase.destroyInstance();
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        AppDatabase.destroyInstance();
    }
}
