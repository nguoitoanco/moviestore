package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.MovieDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Reminder;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.DetailCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class ReminderAdapter
        extends ContextMenuRecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private Context context;
    private int viewFrom;
    private DetailCallBackListener detailCallBackListener;

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        private TextView reminderTitle;
        private TextView reminderContent;
        private ImageView mIconView;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            reminderTitle = (TextView) itemView.findViewById(R.id.reminder_title);
            reminderContent = (TextView) itemView.findViewById(R.id.reminder_content);
            mIconView = (ImageView) itemView.findViewById(R.id.app_icon);
        }

        public TextView getReminderTitle() {
            return reminderTitle;
        }

        public TextView getReminderContent() {
            return reminderContent;
        }

        public ImageView getIconView() {
            return mIconView;
        }
    }


    public ReminderAdapter(List<Reminder> reminders, Context context, int viewFrom) {
        reminderList = reminders;
        this.context = context;
        this.viewFrom = viewFrom;
    }

    public ReminderAdapter(List<Reminder> reminders, Context context,int viewFrom, DetailCallBackListener callBack) {
        this.context = context;
        reminderList = new ArrayList<>(reminders);
        this.viewFrom = viewFrom;
        this.detailCallBackListener = callBack;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        // In case view on drawer layout
        if (viewFrom == 0) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.sub_reminder_list, parent, false);
        } else {
            // In case show all
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.reminder_list, parent, false);

        }

        ReminderViewHolder reminderViewHolder = new ReminderViewHolder(view);
        return reminderViewHolder;
    }

    @Override
    public void onBindViewHolder(final ReminderViewHolder reminderViewHolder, final int position) {

        setViewContent(reminderViewHolder, position);
        ImageView iconView = reminderViewHolder.getIconView();
        if (iconView != null) {
            ImageUtil.setAutoSizeOfImageView(iconView, context, ImageUtil.RATE_FOUR);
        }

        if (viewFrom == 0) {
            reminderViewHolder.itemView.setLongClickable(false);
            reminderViewHolder.itemView.setOnClickListener(null);
        } else {
            reminderViewHolder.itemView.setLongClickable(true);
            reminderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   detailCallBackListener.displayDetailMovie(position);
                }
            });
        }
    }

    private void setViewContent(ReminderViewHolder rmv, int pos) {
        final Reminder reminder = reminderList.get(pos);
        rmv.getReminderTitle().setText(reminder.getReminderTitle());
        rmv.getReminderContent().setText(reminder.getReminderContent());
    }

    @Override
    public int getItemCount() {
        return this.reminderList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}
