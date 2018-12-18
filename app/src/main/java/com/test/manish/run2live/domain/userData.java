package com.test.manish.run2live.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;


/**
 * Created by manish on 11/29/2017.
 */
@Entity(indices = {@Index(value = {"date","time"},unique = true)})
public class userData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String time;
    private double distance;
    private double calories;
    private double speed;


    public int getId() {
        return id;
    }
    public void setId(int key) {this.id = key;}

    public void setDate( String dt){
        this.date = dt;
    }

    public void setDistance(double dist){
        this.distance = dist;
    }

    public void setTime(String timedata){
        this.time = timedata;
    }

    public void setCalories(double caloriesdata){
        this.calories = caloriesdata;
    }

    public void setSpeed(double speeddata){
        this.speed = speeddata;
    }

    public String getDate(){
        return this.date;
    }

    public double getDistance(){
        return this.distance;
    }
    public String getTime(){
        return this.time;
    }

    public double getCalories(){
        return this.calories;
    }

    public double getSpeed(){
        return this.speed;
    }
}
