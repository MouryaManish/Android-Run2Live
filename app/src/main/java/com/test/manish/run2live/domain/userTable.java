package com.test.manish.run2live.domain;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;


/**
 * Created by manish on 12/15/2017.
 */


@Entity(primaryKeys = {"firstName", "lastName"})
public class userTable {
    @NonNull
    public String firstName;
    @NonNull
    public String lastName;
    public String gender;
    public long foot;
    public long inch;
    public long weight;
    public long age;
}
