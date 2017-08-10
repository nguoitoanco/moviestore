package com.fsoft.sonic_larue.khanhnv10.moviestore.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Reminder;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by KhanhNV10 on 2015/12/07.
 */
public class ReminderDao {
    private static String TAG = ReminderDao.class.getSimpleName();

    private Context context;
    private String profileId;

    public ReminderDao(Context context, String profileId) {
        this.context = context;
        this.profileId = profileId;
    }

    public ReminderDao(Context context) {
        this.context = context;
        this.profileId = SharedPreferencesManager.loadPreference(context, "profileId", StringUtil.DEFAULT);
    }

    public long insert(Reminder reminder) {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Reminder.COLUMN_REMINDER_ID, reminder.getReminderId());
        contentValues.put(Reminder.COLUMN_REMINDER_TITLE, reminder.getReminderTitle());
        contentValues.put(Reminder.COLUMN_REMINDER_POSTER, reminder.getReminderPoster());
        contentValues.put(Reminder.COLUMN_REMINDER_CONTENT, reminder.getReminderContent());
        contentValues.put(Reminder.COLUMN_REMINDER_DATE, reminder.getReminderDate());

        long rowId = db.replace(Reminder.REMINDER_TABLE_NAME, null, contentValues);
        return rowId;
    }

    /*
     * Get List of reminders in future
     */
    public List<Reminder> getReminders(String strNumRecord) {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();

        String[] projection = {
                Reminder.COLUMN_REMINDER_ID,
                Reminder.COLUMN_REMINDER_TITLE,
                Reminder.COLUMN_REMINDER_POSTER,
                Reminder.COLUMN_REMINDER_CONTENT,
                Reminder.COLUMN_REMINDER_DATE,
        };

        String condition = Reminder.COLUMN_REMINDER_DATE +
                " >= " + Calendar.getInstance().getTimeInMillis();
        Cursor cursor = db.query(Reminder.REMINDER_TABLE_NAME, projection, null, null, null, null, null, strNumRecord);

        Log.d(TAG, "read successfully:" + cursor.getCount());
        List<Reminder> reminders = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                reminders.add(
                    new Reminder(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4))
                );
            }
        }

        cursor.close();
        return reminders;
    }

    public Reminder getReminderById(String reminderId) {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();

        String[] projection = {
                Reminder.COLUMN_REMINDER_ID,
                Reminder.COLUMN_REMINDER_TITLE,
                Reminder.COLUMN_REMINDER_POSTER,
                Reminder.COLUMN_REMINDER_CONTENT,
                Reminder.COLUMN_REMINDER_DATE,
        };

        Cursor cursor = db.query(Reminder.REMINDER_TABLE_NAME, projection, Reminder.COLUMN_REMINDER_ID + " = ?", new String[]{reminderId}, null, null, null);

        Log.d(TAG, "read successfully:" + cursor.getCount());
        Reminder reminder = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                reminder = new Reminder(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getLong(4));
                Log.d(TAG, "read successfully:" + cursor.getString(1));
            }
        }

        cursor.close();
        return reminder;
    }

    public boolean delete(String reminderId) {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();
        return db.delete(Reminder.REMINDER_TABLE_NAME, Reminder.COLUMN_REMINDER_ID + "=" + reminderId, null) > 0;
    }
}
