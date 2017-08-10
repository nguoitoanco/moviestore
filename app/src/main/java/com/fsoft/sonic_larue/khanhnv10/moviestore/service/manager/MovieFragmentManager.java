package com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager;

/**
 * Created by KhanhNV10 on 2015/12/15.
 */
public class MovieFragmentManager {

    public enum MFragment {
        MOVIE_LIST_FRAGMENT(0, "MovieListFragment"),
        DETAIL_MOVIE_FRAGMENT(1, "DetailMovieFragment"),
        FAV_MOVIE_FRAGMENT(2, "FavouriteMovieFragment"),
        SETTING_TAB_FRAGMENT(3, "SettingTabFragment"),
        SETTING_MOVIE_FRAGMENT(4, "SettingMovieFragment"),
        EDIT_PROFILE_FRAGMENT(6, "EditProfileFragment"),
        DROPDOWN_ANIMATION_FRAGMENT(7, "DropdownAnimationFragment"),
        APP_ABOUT_FRAGMENT(8, "AppAboutFragment"),
        REMINDER_FRAGMENT(9, "ReminderFragment"),
        DETAIL_ACTOR_FRAGMENT(10, "DetailActorFragment"),
        PROFILE_LIST_FRAGMENT(11, "ProfileListFragment");

        private int idFrag;
        private String tag;

        MFragment(int id, String t) {
            idFrag = id;
            tag = t;
        }

        public int toInt() {
            return idFrag;
        }

        public String getTag() {
            return tag;
        }
    }

}
