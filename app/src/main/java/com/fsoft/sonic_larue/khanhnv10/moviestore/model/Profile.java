package com.fsoft.sonic_larue.khanhnv10.moviestore.model;

import android.provider.BaseColumns;

import java.io.Serializable;

/**
 * Created by KhanhNV10 on 2015/12/07.
 */
public class Profile implements Serializable, BaseColumns {

    public enum Gender {
        MALE(0, "Male"),
        FEMALE(1, "Female");

        int index;
        String gender;

        Gender(int index, String gender) {
            this.index = index;
            this.gender = gender;
        }

        public String getGender() {
            return gender;
        }

        public int getIndex() {
            return index;
        }
    }

    public static final String PROFILE_TABLE_NAME = "_PROFILE_TABLE";

    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_AVATAR = "user_avatar";
    public static final String COLUMN_USER_BIRTHDAY = "user_birthday";
    public static final String COLUMN_USER_EMAIL = "user_email";
    public static final String COLUMN_USER_GENDER = "user_gender";

    public static final String PROFILE_TABLE_CREATE =
            "CREATE TABLE " + Profile.PROFILE_TABLE_NAME + " (" +
                    Profile.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                    Profile.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                    Profile.COLUMN_USER_AVATAR + " TEXT, " +
                    Profile.COLUMN_USER_BIRTHDAY + " TEXT, " +
                    Profile.COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                    Profile.COLUMN_USER_GENDER + " INTEGER DEFAULT 0);";

    public static final String PROFILE_TABLE_DELETE =
            "DROP TABLE IF EXISTS " + Profile.PROFILE_TABLE_NAME;


    private String userId;
    private String userName;
    private String userAvatar;
    private String userBirthday;
    private String userEmail;
    private int userGender = 0;

    public Profile(String userId, String userName, String userAvatar, String userBirthday, String userEmail, int userGender) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.userBirthday = userBirthday;
        this.userEmail = userEmail;
        this.userGender = userGender;
    }

    public Profile() {

    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getUserGender() {
        return userGender;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserGender(int userGender) {
        this.userGender = userGender;
    }
}
