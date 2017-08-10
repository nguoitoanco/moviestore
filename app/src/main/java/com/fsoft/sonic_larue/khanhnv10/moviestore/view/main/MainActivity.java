package com.fsoft.sonic_larue.khanhnv10.moviestore.view.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ProfileDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ReminderDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Profile;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Reminder;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.CircleImageView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.ImageUtil;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.ReminderFragment;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.DetailMovieFragment;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.MoviePagerAdapter;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.profile.EditProfileFragment;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.ReminderAdapter;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.profile.ProfileListFragment;

import java.util.List;

import static android.support.v4.view.ViewPager.*;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ProfileDao.*;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.model.Profile.Gender;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.MovieFragmentManager.*;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.MoviePagerAdapter.*;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private MoviePagerAdapter moviePagerAdapter;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private OnPageChangeListener pageChangeListener;
    private TextView optionTextView;

    private BroadcastReceiver receiverReminder;
    private BroadcastReceiver receiverProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup a drawer bar
        createDrawerLayout();
        createTabLayout();
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        setPageChangeListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_movies:
                viewPager.setCurrentItem(Tabs.DEFAULT.getIndex());
                break;
            case R.id.action_favourite:
                viewPager.setCurrentItem(Tabs.FAVORITE.getIndex());
                break;
            case R.id.action_settings:
                viewPager.setCurrentItem(Tabs.SETTING.getIndex());
                break;
            case R.id.action_about:
                viewPager.setCurrentItem(Tabs.ABOUT.getIndex());
                break;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
       return true;
    }


    private void createDrawerLayout() {
        // Setup a tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.movies_action_bar);
        setSupportActionBar(toolbar);

        optionTextView = (TextView) toolbar.findViewById(R.id.movie_type_option);

        // Setup a Drawer Layout
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        // Get latest profile id
        String profileId = SharedPreferencesManager
                .loadPreference(getApplicationContext(), "profileId", StringUtil.DEFAULT);

        Profile profile = new Profile();

        if (!profileId.equals(StringUtil.DEFAULT)) {
            ProfileDao profileDao = new ProfileDao(getApplicationContext());
            profile = profileDao.getProfileById(profileId);
            displayCurrentProfile(drawer, profile);
        }

        recyclerView = (RecyclerView) findViewById(R.id.reminder_list);

        LinearLayoutManager lineLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lineLayoutManager);

        setRecyclerAdapter();

        // Register broadcast update reminder movie
        receiverReminder = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Re-set Reminder text view
                setRecyclerAdapter();
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                receiverReminder, new IntentFilter("ReminderUpdated"));

        receiverProfile = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Profile pf = (Profile) intent.getSerializableExtra("profile");
                displayCurrentProfile(drawer, pf);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiverProfile, new IntentFilter("UpdateProfile"));

        registerShowAllReminderOnclick(drawer);
        registerEditProfileOnclick(drawer, profile);
        registerSwitchProfileOnclick(drawer, profile);
    }

    private void displayCurrentProfile(DrawerLayout drawer, Profile pf) {
        if (pf != null) {
            // Display info
            CircleImageView navProfileIcon = (CircleImageView)drawer.
                    findViewById(R.id.nav_profile_icon);

            Bitmap bitmap = ImageUtil.getBitmapFromPath(pf.getUserAvatar());
            if (bitmap != null) {
                ImageUtil.setAutoSizeOfCircleImageView(navProfileIcon, getApplicationContext(), ImageUtil.RATE_FOUR);
                navProfileIcon.setImageBitmap(bitmap);
            }

            TextView navName = (TextView)drawer.findViewById(R.id.nav_name);
            navName.setText(pf.getUserName());

            TextView navEmail = (TextView)drawer.findViewById(R.id.nav_mail);
            navEmail.setText(pf.getUserEmail());

            TextView navDate = (TextView)drawer.findViewById(R.id.nav_date);
            navDate.setText(pf.getUserBirthday());

            TextView navGender = (TextView)drawer.findViewById(R.id.nav_gender);
            int gender = pf.getUserGender();
            if (gender == Gender.MALE.getIndex()) {
                navGender.setText(Gender.MALE.getGender());
            } else {
                navGender.setText(Gender.FEMALE.getGender());
            }
        }
    }
    private void registerShowAllReminderOnclick(final DrawerLayout drawer) {
        Button btnShowAllReminder = (Button) drawer.findViewById(R.id.show_all_btn);
        btnShowAllReminder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(Tabs.SETTING.getIndex());
                FragmentManager fragmentManager =  moviePagerAdapter
                        .getRegisteredFragment(viewPager.getCurrentItem())
                        .getChildFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();

                ReminderFragment reminderFragment;
                Fragment fragment = fragmentManager.findFragmentByTag("reminderFragment");
                if (fragment == null) {
                    reminderFragment = new ReminderFragment();
                    ft.add(R.id.setting_tab_container, reminderFragment, "reminderFragment");
                    ft.addToBackStack(null);
                } else {
                    reminderFragment = (ReminderFragment)fragment;
                    ft.show(reminderFragment);
                }

                ft.commit();
                if (drawer.isShown()) {
                    drawer.closeDrawers();
                }
                setTitle(R.string.all_reminder_title);
            }
        });
    }

    private void registerSwitchProfileOnclick(final DrawerLayout drawer, final Profile pf) {
        Button btnSwitchProfile = (Button) drawer.findViewById(R.id.profile_switch_btn);
        btnSwitchProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileListFragment profileFragment = ProfileListFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putSerializable("profile", pf);
                profileFragment.setArguments(bundle);

                ft.add(R.id.all_main_activity, profileFragment,
                        MFragment.PROFILE_LIST_FRAGMENT.getTag());

                ft.addToBackStack(null);
                ft.commit();
                if (drawer.isShown()) {
                    drawer.closeDrawers();
                }
            }
        });
    }

    private void registerEditProfileOnclick(final DrawerLayout drawer, final Profile pf) {
        Button btnEditProfile = (Button) drawer.findViewById(R.id.profile_edit_btn);
        btnEditProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment editProfileFragment = EditProfileFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putSerializable("profile", pf);
                bundle.putInt("mergeType", MergeType.UPDATE.getType());
                editProfileFragment.setArguments(bundle);

                ft.add(R.id.all_main_activity, editProfileFragment, MFragment.EDIT_PROFILE_FRAGMENT.getTag());
                ft.addToBackStack(null);
                ft.commit();
                if (drawer.isShown()) {
                    drawer.closeDrawers();
                }
            }
        });
    }

    /**
     * Setup tap bar and view pager corresponding
     */
    public void createTabLayout() {
        // Setup the viewpager
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        moviePagerAdapter = new MoviePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(moviePagerAdapter);

        // Setup the tab bar
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_view);
        tabLayout.setTabsFromPagerAdapter(moviePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        moviePagerAdapter.setCustomView(getBaseContext(), tabLayout);

        // Refresh Fav tab bar
        refreshFavTab();
    }


    private void refreshFavTab() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_view);
        View view = tabLayout.getTabAt(Tabs.FAVORITE.getIndex()).getCustomView();
        TextView favNumberView = (TextView) view.findViewById(R.id.fav_number);

        String favString = SharedPreferencesManager
                .loadPreference(this, SharedPreferencesManager.FAV_NUMBER, "0");
        if (Integer.parseInt(favString) == 0) {
            favNumberView.setVisibility(View.GONE);
        } else {
            favNumberView.setVisibility(View.VISIBLE);
            favNumberView.setText(favString);
        }

    }

    private void setRecyclerAdapter() {
        ReminderDao reminderDao = new ReminderDao(getApplicationContext());
        List<Reminder> reminderList = reminderDao.getReminders("2");

        ReminderAdapter ra = new ReminderAdapter(reminderList, getApplicationContext(), 0);
        recyclerView.setAdapter(ra);
    }

    private void setPageChangeListener() {
        pageChangeListener = new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("MainActivity", "position:" + position);
                updateToolBar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        viewPager.addOnPageChangeListener(pageChangeListener);
    }

    private void updateToolBar(int pos) {
        Fragment currFragment = moviePagerAdapter.getRegisteredFragment(pos);
        FragmentManager currFragManager = currFragment.getChildFragmentManager();

        // Get size of back stack of current fragment
        int backStackSize = currFragManager.getBackStackEntryCount();
        if(backStackSize == 0) {
            if (pos == Tabs.DEFAULT.getIndex()) {
                optionTextView.setVisibility(View.VISIBLE);
                setTitle(StringUtil.EMPTY);
            } else {
                optionTextView.setVisibility(View.GONE);
                setTitle(Tabs.values()[pos].getTitle());
            }
        } else if(backStackSize > 0) {
            if (optionTextView != null && optionTextView.isShown()) {
                optionTextView.setVisibility(View.GONE);
            }

            Fragment frm = currFragManager.findFragmentByTag("DetailMovieFragment");
            if (frm != null) {
                DetailMovieFragment detailMovieFragment = (DetailMovieFragment)frm;
                setTitle(detailMovieFragment.getMovieTitle());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String favNumber = sharedPreferences.getString(key, "0");
        Log.d("MainActivity", "" + favNumber);
        refreshFavTab();
    }

    @Override
    public void onBackPressed() {
        int currentPosition = viewPager.getCurrentItem();
        Fragment currentFragment = moviePagerAdapter.getRegisteredFragment(currentPosition);

        // Get fragment manager of current fragment
        FragmentManager fragmentManager = currentFragment.getChildFragmentManager();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            // Get size of back stack of current fragment
            int childFrgCount = fragmentManager.getBackStackEntryCount();

            if (childFrgCount > 0) {
                fragmentManager.popBackStack();
                if (currentPosition == Tabs.DEFAULT.getIndex()) {
                    setTitle(StringUtil.EMPTY);
                    optionTextView.setVisibility(View.VISIBLE);
                } else if (currentPosition == Tabs.SETTING.getIndex()){
                    optionTextView.setVisibility(View.GONE);
                    if (childFrgCount == 2) {
                        setTitle(R.string.all_reminder_title);
                    } else if(childFrgCount == 1) {
                        setTitle(getString(Tabs.SETTING.getTitle()));
                    }
                } else {
                    setTitle(getString(Tabs.values()[currentPosition].getTitle()));
                    optionTextView.setVisibility(View.GONE);
                }
            } else {
                Log.d("Activity", "onBackPressed!!!");
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverReminder);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverProfile);
    }

    public int getCurrentPage() {
        return viewPager.getCurrentItem();
    }

}
