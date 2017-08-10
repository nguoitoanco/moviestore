package com.fsoft.sonic_larue.khanhnv10.moviestore.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Profile;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.DateUtil;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KhanhNV10 on 2015/12/07.
 */
public class ProfileDao {
    private static String TAG = ProfileDao.class.getSimpleName();

    private Context context;
    private String profileId;
    String[] allColumn = {
            Profile.COLUMN_USER_ID,
            Profile.COLUMN_USER_NAME,
            Profile.COLUMN_USER_AVATAR,
            Profile.COLUMN_USER_BIRTHDAY,
            Profile.COLUMN_USER_EMAIL,
            Profile.COLUMN_USER_GENDER,
    };

    public enum MergeType {
        CREATE(0),
        UPDATE(1);

        private int type;
        MergeType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        boolean isCreate() {
            return type == CREATE.getType();
        }

        boolean isUpdate() {
            return type == UPDATE.getType();
        }
    }

    public ProfileDao(Context context, String profileId) {
        this.context = context;
        this.profileId = profileId;
    }

    public ProfileDao(Context context) {
        this.context = context;
        this.profileId = SharedPreferencesManager.loadPreference(context, "profileId", StringUtil.DEFAULT);
    }

    /*
     * Inserting a profile
     */
    public long insert(Profile profile) {

        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Profile.COLUMN_USER_ID, profile.getUserId());
        contentValues.put(Profile.COLUMN_USER_NAME, profile.getUserName());
        contentValues.put(Profile.COLUMN_USER_AVATAR, profile.getUserAvatar());
        contentValues.put(Profile.COLUMN_USER_BIRTHDAY, profile.getUserBirthday());
        contentValues.put(Profile.COLUMN_USER_EMAIL, profile.getUserEmail());
        contentValues.put(Profile.COLUMN_USER_GENDER, profile.getUserGender());

        long rowId = db.insert(Profile.PROFILE_TABLE_NAME, null, contentValues);
        return rowId;
    }

    /*
     * Get a profile
     */
    public Profile getProfileById(String profileId) {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();

        String condition = Profile.COLUMN_USER_ID + " = ?";
        Cursor cursor = db.query(Profile.PROFILE_TABLE_NAME, allColumn, condition, new String[]{profileId}, null, null, null);
        Log.d(TAG, "read successfully:" + cursor.getCount());
        Profile prf = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                prf = new Profile(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getInt(5));
            }
        }
        cursor.close();
        return prf;
    }

    public List<Profile> getAllProfile() {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();

        List<Profile> allProfile = new ArrayList<>();

        Cursor cursor = db.query(Profile.PROFILE_TABLE_NAME, allColumn, null, null, null, null, null);

        Profile prf = null;
        if (cursor != null) {
            Log.d(TAG, "getAllProfile successfully:" + cursor.getCount());
            while (cursor.moveToNext()) {
                prf = new Profile(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5));
                allProfile.add(prf);
            }
        }

        cursor.close();
        return allProfile;
    }

    /*
     * Updating a profile
     */
    public int update(Profile profile) {

        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Profile.COLUMN_USER_NAME, profile.getUserName());
        contentValues.put(Profile.COLUMN_USER_AVATAR, profile.getUserAvatar());
        contentValues.put(Profile.COLUMN_USER_BIRTHDAY, profile.getUserBirthday());
        contentValues.put(Profile.COLUMN_USER_EMAIL, profile.getUserEmail());
        contentValues.put(Profile.COLUMN_USER_GENDER, profile.getUserGender());

        // updating row
        int record = db.update(Profile.PROFILE_TABLE_NAME, contentValues, Profile.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(profile.getUserId())});
        return record;
    }

    /*
    * Merging a profile
    */
    public long merge(Profile profile, int mergeType) {
        if (mergeType == MergeType.CREATE.getType() && !isExisted(profile)) {
            return insert(profile);
        }

        if (mergeType == MergeType.UPDATE.getType()) {
            if (isExisted(profile)) {
                return update(profile);
            } else {
                return insert(profile);
            }
        }

        return -1;
    }

    /*
     * Checking a new profile able to create or not
     *
     * If userId and userName and userEmail all dose not exists yet => return true
     * else return false
     */
    private boolean isExisted(Profile profile) {
        DBConfig dbConfig = DBConfig.getInstance(context, profileId);
        SQLiteDatabase db = dbConfig.getReadableDatabase();

        String condition = Profile.COLUMN_USER_NAME + " = ? OR "
                + Profile.COLUMN_USER_EMAIL + " = ? ";

        String[] params = new String[]{
                profile.getUserName(),
                profile.getUserEmail()};

        Cursor cursor = db.query(
                Profile.PROFILE_TABLE_NAME,
                new String[]{
                    Profile.COLUMN_USER_ID
                },
                condition, params, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        }
        return false;
    }
}
