package com.test.manish.run2live.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.test.manish.run2live.domain.userData;

import java.util.List;
import java.util.Set;

/**
 * Created by manish on 11/29/2017.
 */

@Dao
public interface TotalDb {
    /* insert into userData if error rmove the update and continue the transection*/

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    public void insertdb(userData... user);

    /*getting data for statistic*/

    @Query("SELECT * FROM userData WHERE id = (SELECT max(id) FROM userData)")
    public userData getStats();


    /* getting data for history FOR LAST 8 and keeping it always upto date by default*/

    @Query("SELECT * FROM userData ORDER BY date DESC, time DESC LIMIT 8")
    public List<userData> getHistory();

}
