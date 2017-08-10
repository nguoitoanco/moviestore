package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingTabFragment extends Fragment {

    public static SettingTabFragment newInstance(){
        return new SettingTabFragment();
    }
    public SettingTabFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting_tab, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        getActivity().setTitle(R.string.setting_tab_title);
        getActivity()
            .findViewById(R.id.movies_action_bar)
            .findViewById(R.id.movie_type_option)
            .setVisibility(View.GONE);
    }
}
