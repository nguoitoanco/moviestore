package com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by KhanhNV10 on 2015/12/18.
 */
public class BroadCastReceiveManager {
    public enum IntentAction {
        UPDATE_PROFILE("UpdateProfile");

        private String action;
        IntentAction(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }

    public static void sendIntentBroadCast(String action, Context context) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


}
