package com.test.manish.run2live.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.test.manish.run2live.domain.userTable;

import java.util.List;

/**
 * Created by manish on 12/15/2017.
 */
@Dao
public interface UserDb {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(userTable... register);

    @Update
    public void updateUsers(userTable... register);

    @Query("SELECT * FROM userTable")
    public userTable listAllUsers();

   @Query("DELETE FROM userTable")
    public void deleteUser();
}
