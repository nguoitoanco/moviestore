package com.fsoft.sonic_larue.khanhnv10.moviestore.view.profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Profile;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.CallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.ContextMenuRecyclerView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.ReminderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguoitoanco on 12/17/2015.
 */
public class ProfileListAdapter extends
        ContextMenuRecyclerView.Adapter<ProfileListAdapter.ViewHolder> {
    private List<Profile> allProfile;
    private CallBackListener callBackListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView profileInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            profileInfo = (TextView) itemView.findViewById(R.id.profileInfo);
        }

        public TextView getProfileInfo() {
            return profileInfo;
        }

        public void setProfileInfo(TextView profileInfo) {
            this.profileInfo = profileInfo;
        }
    }

    public ProfileListAdapter(List<Profile> profiles, CallBackListener callBack) {
        allProfile = new ArrayList<>(profiles);
        callBackListener = callBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.profile_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.getProfileInfo().setText(allProfile.get(position).getUserName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBackListener.onResult(allProfile.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return allProfile.size();
    }
}
