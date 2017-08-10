package com.fsoft.sonic_larue.khanhnv10.moviestore.view.profile;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ProfileDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Profile;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.MovieFragmentManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.CallBackListener;

import java.util.ArrayList;
import java.util.List;

import static com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ProfileDao.*;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.MovieFragmentManager.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileListFragment extends Fragment {

    private ProfileListAdapter profileListAdapter;
    private RecyclerView recyclerView;
    private BroadcastReceiver receiverProfile;
    private List<Profile> profileList;


    public static ProfileListFragment newInstance() {
        ProfileListFragment prfListFragment = new ProfileListFragment();
        return prfListFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_list, container, false);
        view.findViewById(R.id.add_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Edit fragment
                startCreateProfile();
            }
        });

//        getActivity().setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.pro_recycle_view);

        final ProfileDao profileDao = new ProfileDao(getContext());
        profileList = profileDao.getAllProfile();
        displayProfiles();

        RelativeLayout toolbar = (RelativeLayout) view.findViewById(R.id.profile_action_bar);
        toolbar.findViewById(R.id.profile_action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        receiverProfile = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Profile pf = (Profile) intent.getSerializableExtra("profile");
                if (pf != null) {
                    profileList.add(pf);
                    profileListAdapter.notifyDataSetChanged();
                }
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiverProfile, new IntentFilter("AddProfile"));

        recyclerView.setAdapter(profileListAdapter);
        LinearLayoutManager lineLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lineLayoutManager);

        return view;
    }

    private void displayProfiles() {
        if (profileList == null) {
            profileList = new ArrayList<>();
        }

        profileListAdapter = new ProfileListAdapter(profileList, new CallBackListener() {
            @Override
            public void onResult(Object data) {
                // Switch profile
                Profile newProfile = (Profile) data;
                SharedPreferencesManager.savePreference(getContext(), "profileId", newProfile.getUserId());
                Intent intent = getContext()
                        .getPackageManager()
                        .getLaunchIntentForPackage(getContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onError() {

            }
        });


    }

    private void startCreateProfile(){
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("profile", new Profile(
                String.valueOf(System.currentTimeMillis()), null, null, null, null, 0));
        bundle.putInt("mergeType", MergeType.CREATE.getType());
        editProfileFragment.setArguments(bundle);

        ft.add(R.id.profile_all, editProfileFragment, "editProfileFragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiverProfile);
    }
}
