package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.MovieDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.ReminderDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Reminder;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.AlarmService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.DetailCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;

import java.util.List;

import static com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.MovieFragmentManager.*;

public class ReminderFragment extends Fragment {

    private List<Reminder> reminderList;
    private ReminderAdapter reminderAdapter;
    private ContextMenuRecyclerView recyclerView;
    private BroadcastReceiver receiverReminder;

    public static ReminderFragment newInstance() {
        ReminderFragment fragment = new ReminderFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_reminder, container, false);
        recyclerView = (ContextMenuRecyclerView) view.findViewById(R.id.all_reminder);
        LinearLayoutManager lineLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lineLayoutManager);

        resetAdapter();
        registerForContextMenu(recyclerView);

        // Register broadcast update reminder movie

        receiverReminder = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                resetAdapter();

            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiverReminder, new IntentFilter("ReminderUpdated"));

        return view;
    }

    private void resetAdapter() {
        ReminderDao reminderDao = new ReminderDao(getContext());
        reminderList = reminderDao.getReminders(null);

        reminderAdapter = new ReminderAdapter(reminderList, getContext(), 1, new DetailCallBackListener() {
            @Override
            public void displayDetailMovie(Object data) {
                showDetail((int)data);
            }

            @Override
            public void displayDetailCastCrew(int currentPos) {

            }
        });
        recyclerView.setAdapter(reminderAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Inflate Menu from xml resource
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_reminder_fragment, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        if (item.getItemId() == R.id.reminder_movie_delete) {
            ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
            Reminder rm = reminderList.get(info.position);
            showDeleteDialogConfirmation(rm);
        }
        return false;
    }

    private void showDeleteDialogConfirmation(final Reminder rm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.delete_confirm))
                .setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteSelectedReminder(rm);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteSelectedReminder(Reminder rm) {
        ReminderDao reminderDao = new ReminderDao(getContext());
        boolean deleteSuccess = reminderDao.delete(rm.getReminderId());
        if (deleteSuccess) {
            reminderList.remove(rm);
            reminderAdapter.notifyDataSetChanged();

            // Send broad cast to inform that reminder has been updated or deleted
            Intent intentInform = new Intent("ReminderUpdated");
            intentInform.putExtra("reminder_id", rm.getReminderId());

            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intentInform);

            // delete alarm
            Intent intent = new Intent(getContext(), AlarmService.class);
            int RequestCode = Integer.parseInt(rm.getReminderId());
            PendingIntent alarmIntent = PendingIntent.getService(getContext(),
                    RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (alarmIntent != null) {
                alarmIntent.cancel();
            }
        }
    }

    public void showDetail(int pos) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Reminder reminder = reminderList.get(pos);

        MovieDao movieDao = new MovieDao(getContext());
        Movie movie = movieDao.getMovieById(reminder.getReminderId());
        movie.setMoviePosterPath(MovieService.BASE_IMAGE_URL + movie.getMoviePoster());
        Bundle bundle = new Bundle();
        bundle.putSerializable("movie_detail", movie);

        DetailMovieFragment detailMovieFragment = DetailMovieFragment.newInstance();
        detailMovieFragment.setArguments(bundle);
        ft.add(R.id.all_reminder_content, detailMovieFragment, MFragment.DETAIL_MOVIE_FRAGMENT.getTag());
        ft.addToBackStack(null);
        ft.commit();

        getActivity()
                .findViewById(R.id.movies_action_bar)
                .findViewById(R.id.movie_type_option)
                .setVisibility(View.GONE);
        getActivity().setTitle(movie.getMovieTitle());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiverReminder);
    }
}
