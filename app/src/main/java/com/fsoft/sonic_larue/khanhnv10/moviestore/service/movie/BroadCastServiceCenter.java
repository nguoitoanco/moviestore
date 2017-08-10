package com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie;

/**
 * Created by KhanhNV10 on 2015/12/17.
 */
public class BroadCastServiceCenter {
    public enum BroadCastType {
        ACTOR_VIEW_MODE_CHANGE(0, "ChangeViewMode");

        private int id;
        private String title;

        BroadCastType(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}
