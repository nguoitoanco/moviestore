package com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ReminderDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.main.MainActivity;

/**
 * Created by KhanhNV10 on 2015/12/03.
 */
public class AlarmService extends IntentService {

    private Context context;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public AlarmService() {
        super(AlarmService.class.getSimpleName());
        context = this;
    }

        @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        String movieId = bundle.getString("movie_id");
        String movieTitle = bundle.getString("movie_title");
        String message = bundle.getString("message_content");

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentText(message);
        builder.setContentTitle(movieTitle);
        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Integer.parseInt(movieId), builder.build());

        // Remove reminder in DB
        ReminderDao reminderDao = new ReminderDao(context);
        reminderDao.delete(movieId);
        // Send broad cast to inform that reminder has been updated or deleted
        Intent intentInform = new Intent("ReminderUpdated");
        intentInform.putExtra("reminder_id", movieId);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intentInform);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentInform);
    }

}
