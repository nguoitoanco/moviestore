package com.fsoft.sonic_larue.khanhnv10.moviestore.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Profile;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Reminder;

/**
 * Created by KhanhNV10 on 2015/12/07.
 */
public class DBConfig extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";

    private static DBConfig mInstance;
    private static String profileId;

    public synchronized static DBConfig getInstance(Context context, String id) {
        profileId = id;
        if (mInstance == null) {
            mInstance = new DBConfig(context);
        }
        return mInstance;
    }

    private DBConfig(Context context) {
        super(context, DBConfig.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(profileId + Movie.MOVIE_TABLE_CREATE);
        db.execSQL(profileId + Profile.PROFILE_TABLE_CREATE);
        db.execSQL(profileId + Reminder.REMINDER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Movie.MOVIE_TABLE_DELETE);
        db.execSQL(Profile.PROFILE_TABLE_DELETE);
        db.execSQL(Reminder.REMINDER_TABLE_DELETE);
        onCreate(db);
    }

//    public synchronized void close(SQLiteDatabase db) {
//        if(db != null){
//            db.close();
//        }
//    }
}
