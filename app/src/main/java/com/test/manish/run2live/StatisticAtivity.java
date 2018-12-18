package com.test.manish.run2live;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.test.manish.run2live.database.AppDatabase;
import com.test.manish.run2live.domain.userData;

import java.util.ArrayList;
import java.util.List;

public class StatisticAtivity extends AppCompatActivity {
    private AppDatabase db;
    private TextView date;
    private TextView time;
    private TextView calories;
    private TextView distance;
    private TextView speed;
    private TextView generalText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_layout);
        db = AppDatabase.getInMemoryDatabase(getApplicationContext());
        userData  userdata = db.totalDb().getStats();
        date =(TextView) findViewById(R.id.date2);
        time =(TextView) findViewById(R.id.time2);
        calories =(TextView) findViewById(R.id.calories2);
        distance =(TextView) findViewById(R.id.distance2);
        speed =(TextView) findViewById(R.id.speed2);
        generalText = (TextView) findViewById(R.id.genraltext);
        if(userdata!= null){
            date.setText(userdata.getDate());
            time.setText(userdata.getTime());
            distance.setText(Double.toString(userdata.getDistance()));
            calories.setText(Double.toString(userdata.getCalories()));
            speed.setText(Double.toString(userdata.getSpeed()));
            if(userdata.getDistance() >= 8){
                generalText.setText("It's good to see you running with such dedication! Keep it up!");
            }else{
                generalText.setText("It's good to see you running with such dedication. Togather we can Improve!");
            }
        }
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
