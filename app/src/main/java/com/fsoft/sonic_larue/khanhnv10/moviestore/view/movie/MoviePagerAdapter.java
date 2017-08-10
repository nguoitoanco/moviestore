package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.map.CinemaMapFragment;

/**
 * Created by KhanhNV10 on 2015/11/21.
 */
public class MoviePagerAdapter extends FragmentStatePagerAdapter {

    public enum Tabs {
        DEFAULT(0, R.string.default_tab, R.mipmap.ic_home), // Default TAB
        FAVORITE(1, R.string.favourite_tab, R.mipmap.ic_favorite), // Favourite TAB
        SETTING(2, R.string.setting_tab, R.mipmap.ic_settings), // Setting TAB
        MAP(3, R.string.map_tab, R.mipmap.ic_my_location), // Map TAB
        ABOUT(4, R.string.about_tab, R.mipmap.ic_info); // About TAB

        private int index;
        private int title;
        private int icon;

        Tabs(int id, int title, int icon) {
            this.index = id;
            this.title = title;
            this.icon = icon;
        }

        public int getIndex() {
            return index;
        }

        public int getTitle() {
            return title;
        }

        public int getIcon() {
            return icon;
        }
    }

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public MoviePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == Tabs.DEFAULT.getIndex()) {
            Log.d("Tabs.DEFAULT", "show");
            return MovieListFragment.newInstance();
        }

        if (position == Tabs.SETTING.getIndex()) {
            Log.d("Tabs.SETTING", "show");
            return SettingTabFragment.newInstance();
        }

        if (position == Tabs.FAVORITE.getIndex()) {
            Log.d("Tabs.FAVORITE", "show");
            return FavouriteMovieFragment.newInstance();
        }

        if (position == Tabs.MAP.getIndex()) {
            Log.d("Tabs.MAP", "show");
            return CinemaMapFragment.newInstance();
        }

        if (position == Tabs.ABOUT.getIndex()) {
            Log.d("Tabs.ABOUT", "show");
            return AppAboutFragment.newInstance();
        }

        return MovieListFragment.newInstance();
    }

    @Override
    public int getCount() {
        return Tabs.values().length;
    }

    public void setCustomView(Context context, TabLayout tabLayout) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View customTab = LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null);
            ImageView imageView = (ImageView)customTab.findViewById(R.id.tab_icon);
            TextView textView = (TextView)customTab.findViewById(R.id.tab_title);

            imageView.setImageResource(getIcons()[i]);
            textView.setText(getTitles()[i]);

            tabLayout.getTabAt(i).setCustomView(customTab);
        }
    }

    private int[] getTitles() {
        return new int[]{
                Tabs.DEFAULT.getTitle(),
                Tabs.FAVORITE.getTitle(),
                Tabs.SETTING.getTitle(),
                Tabs.MAP.getTitle(),
                Tabs.ABOUT.getTitle()
        };
    }

    private int[] getIcons() {
        return new int[]{
                Tabs.DEFAULT.getIcon(),
                Tabs.FAVORITE.getIcon(),
                Tabs.SETTING.getIcon(),
                Tabs.MAP.getIcon(),
                Tabs.ABOUT.getIcon()
        };
    }

}
