package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;

import java.util.Arrays;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class SettingMovieFragment extends PreferenceFragmentCompat
        implements OnSharedPreferenceChangeListener {

    public enum Key {
        FILTER_ON_RATE("filter_on_rate"),
        SEEK_BAR("SeekBar_Progress"),
        CATEGORY("category_list"),
        FILTER_ON_YEAR("filter_on_year"),
        SORT_BY("sort_by_list");

        private String strKey;

        Key(String strKey) {
            this.strKey = strKey;
        }

        @Override
        public String toString() {
            return strKey;
        }

    }

    private SeekBar mSeekBar;
    private Preference filterOnRate;
    private ListPreference categoriesPrf;
    private EditTextPreference filterOnYear;
    private ListPreference sortBy;


    public static SettingMovieFragment newInstance() {
        SettingMovieFragment fragment = new SettingMovieFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.app_setting);

        categoriesPrf = (ListPreference) findPreference(Key.CATEGORY.toString());
        int category = SharedPreferencesManager.
                loadIntPreference(getContext(), Key.CATEGORY.toString());
        categoriesPrf.setSummary(
                Arrays.asList(categoriesPrf.getEntries()).get(category));

        filterOnRate = findPreference(Key.FILTER_ON_RATE.toString());
        int rate = SharedPreferencesManager.loadIntPreference(getContext(),
                Key.SEEK_BAR.toString());
        filterOnRate.setSummary(String.valueOf(rate));

        filterOnYear = (EditTextPreference) findPreference(Key.FILTER_ON_YEAR.toString());
        filterOnYear.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return StringUtil.isNumeric(o.toString());
            }
        });

        int year = SharedPreferencesManager.
                loadIntPreference(getContext(), Key.FILTER_ON_YEAR.toString());
                        filterOnYear.setSummary(String.valueOf(year));

        sortBy = (ListPreference) findPreference(Key.SORT_BY.toString());
        int sortIndex = SharedPreferencesManager.loadIntPreference(getContext(),
                Key.SORT_BY.toString());
        sortBy.setSummary(
                Arrays.asList(sortBy.getEntries()).get(sortIndex));

        setFilterOnRateOnclickListener(filterOnRate);

    }

    private void setFilterOnRateOnclickListener(Preference filterOnRate) {
        filterOnRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                createDialogView();
                return true;
            }
        });
    }

    private void createDialogView() {

        mSeekBar = new SeekBar(getContext());
        mSeekBar.setMax(10);

        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        alert.setTitle(getString(R.string.from_rate_title));
        alert.setView(mSeekBar);
        alert.setView(layout);

        alert.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int mProgress = mSeekBar.getProgress();
                SharedPreferencesManager.savePreference(getContext(), Key.SEEK_BAR.toString(),
                        String.valueOf(mProgress));
            }
        }).setNegativeButton(getString(R.string.btn_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Key.SEEK_BAR.toString())) {
            filterOnRate.setSummary(String.valueOf(mSeekBar.getProgress()));
        } else if (key.equals(categoriesPrf.getKey())) {
            categoriesPrf.setSummary(categoriesPrf.getEntry());
        } else if (key.equals(filterOnYear.getKey())) {
            filterOnYear.setSummary(filterOnYear.getText());
        } else if (key.equals(sortBy.getKey())) {
            sortBy.setSummary(sortBy.getEntry());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
