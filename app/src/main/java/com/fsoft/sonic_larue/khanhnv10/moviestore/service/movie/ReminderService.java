package com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.MovieDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ReminderDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Reminder;

import java.util.Calendar;


/**
 * Created by KhanhNV10 on 2015/12/03.
 */
public class ReminderService {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public void createNewReminder(Context context, long startAlarmTime, Movie movie) {

        Intent intent = new Intent(context, AlarmService.class);

        intent.putExtra("movie_id", movie.getMovieId());
        intent.putExtra("movie_title", movie.getMovieTitle());
        String message = "Year: " + movie.getReleaseDate().substring(0, 4)
                + " Rate: " + movie.getRating();
        intent.putExtra("message_content", message);

        Reminder reminder = new Reminder(
                movie.getMovieId(),
                movie.getMovieTitle(),
                movie.getMoviePoster(),
                message,
                startAlarmTime);

        ReminderDao reminderDao = new ReminderDao(context);
        reminderDao.insert(reminder);
        MovieDao movieDao = new MovieDao(context);
        movieDao.insert(movie);

        int requestCode = Integer.parseInt(movie.getMovieId());
        alarmIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, startAlarmTime, alarmIntent);
    }
}
