package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.dao.MovieDao;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.DetailCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;

import java.util.List;

import static com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.MovieFragmentManager.*;

public class FavouriteMovieFragment extends Fragment implements SearchView.OnQueryTextListener{

    private ContextMenuRecyclerView recyclerView;
    private List<Movie> movieList;
    private FavouriteMovieAdapter favouriteMovieAdapter;

    private BroadcastReceiver receiver;

    public static FavouriteMovieFragment newInstance() {
        FavouriteMovieFragment fragment = new FavouriteMovieFragment();
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
        View view = inflater.inflate(R.layout.fragment_favourite_movie, container, false);
        recyclerView = (ContextMenuRecyclerView) view.findViewById(R.id.rv_fav_movie);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MovieDao movieDao = new MovieDao(getContext());
        movieList = movieDao.getFavMovies();
        Log.d("refreshView", "movieList: " + movieList.size());

        favouriteMovieAdapter = new FavouriteMovieAdapter(this, movieList, new DetailCallBackListener() {
            @Override
            public void displayDetailMovie(Object data) {
                showDetail((int)data);
            }

            @Override
            public void displayDetailCastCrew(int currentPos) {

            }
        });

        registerForContextMenu(recyclerView);
        recyclerView.setAdapter(favouriteMovieAdapter);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshView();
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiver, new IntentFilter("UpdateUI"));


        return view;
    }

    private void refreshView() {
        MovieDao movieDao = new MovieDao(getContext());
        List<Movie> buffList = movieDao.getFavMovies();

        Log.d("refreshView", "buffList: " + buffList.size());
        if (buffList.size() > 0) {
            movieList.clear();
            movieList.addAll(buffList);

        }

        favouriteMovieAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        favouriteMovieAdapter.getFilter().filter(newText);
        return false;
    }

    private void refreshMovieFavouriteView(Movie mv) {
        movieList.remove(mv);
        favouriteMovieAdapter.notifyDataSetChanged();
        SharedPreferencesManager.updatePreference(getContext(),
                SharedPreferencesManager.FAV_NUMBER, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // Inflate Menu from xml resource
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_favourite_fragment, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        if (item.getItemId() == R.id.fav_movie_delete) {
            ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
            Movie movie = movieList.get(info.position);
            showDeleteDialogConfirmation(movie);
        }
        return false;
    }

    private void showDeleteDialogConfirmation(final Movie mv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.delete_confirm))
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MovieDao movieDao = new MovieDao(getContext());
                        if (movieDao.updateMovie(mv) > 0) {
                            movieList.remove(mv);
                            favouriteMovieAdapter.notifyDataSetChanged();
                            Log.d("refreshView", "" + movieList.size());
                            SharedPreferencesManager.savePreference(getContext(),
                                    SharedPreferencesManager.FAV_NUMBER, String.valueOf(movieList.size()));
                            // Send broad cast to update favourite ui
                            Intent intent = new Intent("UpdateFavMovie");
                            intent.putExtra("movie_id", mv.getMovieId());
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDetail(int pos) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        DetailMovieFragment detailMovieFragment = DetailMovieFragment.newInstance();
        Movie movie = movieList.get(pos);
        Bundle bundle = new Bundle();
        bundle.putSerializable("movie_detail", movie);
        detailMovieFragment.setArguments(bundle);
        ft.add(R.id.fav_movie, detailMovieFragment, MFragment.DETAIL_MOVIE_FRAGMENT.getTag());
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
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}
