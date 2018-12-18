package com.test.manish.run2live.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.test.manish.run2live.dao.TotalDb;
import com.test.manish.run2live.dao.UserDb;
import com.test.manish.run2live.domain.userData;
import com.test.manish.run2live.domain.userTable;

/**
 * Created by manish on 11/29/2017.
 */

@Database(entities = {userData.class,userTable.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
public abstract TotalDb totalDb();
public abstract UserDb userDb();

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
// keeping it defaut
        }
    };

    public static AppDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,"Run2Live_database.db").addMigrations(MIGRATION_1_2)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


}

