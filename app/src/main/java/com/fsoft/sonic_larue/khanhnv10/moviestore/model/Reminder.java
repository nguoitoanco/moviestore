package com.fsoft.sonic_larue.khanhnv10.moviestore.model;

/**
 * Created by KhanhNV10 on 2015/12/07.
 */
public class Reminder {

    public static final String REMINDER_TABLE_NAME = "_REMINDER_TABLE";

    public static final String COLUMN_REMINDER_ID = "reminder_id";
    public static final String COLUMN_REMINDER_TITLE = "reminder_title";
    public static final String COLUMN_REMINDER_POSTER = "reminder_poster";
    public static final String COLUMN_REMINDER_CONTENT = "reminder_content";
    public static final String COLUMN_REMINDER_DATE = "reminder_date";

    public static final String REMINDER_TABLE_CREATE =
            "CREATE TABLE " + Reminder.REMINDER_TABLE_NAME + " (" +
                    Reminder.COLUMN_REMINDER_ID + " TEXT PRIMARY KEY, " +
                    Reminder.COLUMN_REMINDER_TITLE + " TEXT NOT NULL, " +
                    Reminder.COLUMN_REMINDER_POSTER + " TEXT, " +
                    Reminder.COLUMN_REMINDER_CONTENT + " TEXT, " +
                    Reminder.COLUMN_REMINDER_DATE + " INTEGER);";

    public static final String REMINDER_TABLE_DELETE =
            "DROP TABLE IF EXISTS " + Reminder.REMINDER_TABLE_NAME;

    private String reminderId;
    private String reminderTitle;
    private String reminderPoster;
    private String reminderContent;
    private long reminderDate;

    public Reminder(String reminderId, String reminderTitle, String reminderPoster, String reminderContent, long reminderDate) {
        this.reminderId = reminderId;
        this.reminderTitle = reminderTitle;
        this.reminderPoster = reminderPoster;
        this.reminderContent = reminderContent;
        this.reminderDate = reminderDate;
    }

    public Reminder() {

    }

    public String getReminderId() {
        return reminderId;
    }

    public String getReminderTitle() {
        return reminderTitle;
    }

    public String getReminderContent() {
        return reminderContent;
    }

    public long getReminderDate() {
        return reminderDate;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public void setReminderTitle(String reminderTitle) {
        this.reminderTitle = reminderTitle;
    }

    public void setReminderContent(String reminderContent) {
        this.reminderContent = reminderContent;
    }

    public void setReminderDate(int reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderPoster() {
        return reminderPoster;
    }

    public void setReminderPoster(String reminderPoster) {
        this.reminderPoster = reminderPoster;
    }
}
