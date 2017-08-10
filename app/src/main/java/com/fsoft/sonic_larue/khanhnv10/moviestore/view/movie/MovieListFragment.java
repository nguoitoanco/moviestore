package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.DetailCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.ServiceCallBackListener;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.MovieFragmentManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.manager.SharedPreferencesManager;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.util.StringUtil;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.support.v7.widget.RecyclerView.OnScrollListener;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService.Category;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService.clearCache;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService.makeCacheData;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService.parsJsonToMovies;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.DropDownAnimationFragment.DropDown;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.DropDownAnimationFragment.OptionView;
import static com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie.SettingMovieFragment.Key;

public class MovieListFragment extends Fragment implements Response.Listener,
                Response.ErrorListener {

    private ViewType viewType;
    private RecyclerView recyclerView;
    private int currentPage = 1;
    private List<Movie> movieList;
    private LinearLayoutManager lineLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private MovieRVAdapter movieRVAdapter;
    private View view;
    private DetailCallBackListener detailCallBackListener;
    private DropDown dropDown;
    private String movieTitle = "";
    private TextView optionTextView;
    private Menu movieListMenu;

    private int filterRate;
    private int category;
    private int filterYear;
    private int sortBy;

    private BroadcastReceiver receiver;

    public enum ViewType {
        DEFAULT(0), // Default view
        GRID(1); // Grid view

        private int type;

        ViewType(int type) {
            this.type = type;
        }

        boolean isGridView() {
            return (this.type == 1);
        }

        boolean isDefaultView() {
            return (this.type == 0);
        }
    }

    public enum SortBy {
        RELEASE_DATE(0),
        RATING(1);

        private int index;
        SortBy(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static boolean isSortOnDate(int ind) {
            return ind == RELEASE_DATE.getIndex();
        }

        public static boolean isSortOnRating(int ind) {
            return ind == RATING.getIndex();
        }
    }

    public static MovieListFragment newInstance() {
        MovieListFragment fragment = new MovieListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        movieList = new ArrayList<>();

        movieTitle = getString(R.string.category_pop_movie);

        viewType = ViewType.DEFAULT;

        category = SharedPreferencesManager.
                loadIntPreference(getContext(), Key.CATEGORY.toString());

        filterRate = SharedPreferencesManager.loadIntPreference(getContext(),
                Key.SEEK_BAR.toString());

        filterYear = SharedPreferencesManager.
                loadIntPreference(getContext(), Key.FILTER_ON_YEAR.toString());

        sortBy = SharedPreferencesManager.
                loadIntPreference(getContext(), Key.SORT_BY.toString());

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                resetAdapter();
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiver, new IntentFilter("UpdateFavMovie"));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiver, new IntentFilter("UpdateUI"));

        detailCallBackListener = new DetailCallBackListener() {
            @Override
            public void displayDetailMovie(Object data) {
                showDetailMovie(Integer.parseInt(data.toString()));
            }

            @Override
            public void displayDetailCastCrew(int pos) {
            }
        };

        dropDown = DropDown.HIDE_OPTION;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MovieListFragment", "onCreateView");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        optionTextView = (TextView) getActivity()
                .findViewById(R.id.movies_action_bar)
                .findViewById(R.id.movie_type_option);
        registerOptionViewOnClickListener();

        // Load more data and display
        onLoadMoreData(currentPage);

        reFreshLayoutManager(recyclerView);
        registerScrollListener(recyclerView);
        final SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        clearCache();
                        reFreshRecyclerView(recyclerView);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        return  view;
    }

    private void registerScrollListener(RecyclerView rv) {

        rv.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisibleItem = 0;
                if (viewType.isDefaultView()) {
                    lastVisibleItem = lineLayoutManager.findLastVisibleItemPosition();
                } else {
                    lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                }

                if (lastVisibleItem >= (movieList.size() - 1)) {
                    currentPage++;
                    onLoadMoreData(currentPage);
                }
            }
        });
    }

    private void resetAdapter() {
        movieRVAdapter = new MovieRVAdapter(movieList, viewType, getContext(), detailCallBackListener);
        recyclerView.setAdapter(movieRVAdapter);
    }
    private void reFreshRecyclerView(RecyclerView rv) {
        movieList = new ArrayList<>();
        currentPage = 1;
        movieRVAdapter = null;
        reFreshLayoutManager(rv);
        onLoadMoreData(1);

    }

    private void reFreshLayoutManager(RecyclerView rv) {
        lineLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        if (viewType.isDefaultView()) {
            rv.setLayoutManager(lineLayoutManager);
        } else {
            rv.setLayoutManager(gridLayoutManager);
        }

    }

    private void onLoadMoreData(int page) {
        view.findViewById(R.id.loading_panel).setVisibility(View.VISIBLE);
        String conUrl = Category.getUrlFromIndex(category) + page;
        Log.d("onLoadMoreData", String.valueOf(filterRate));
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(conUrl);
        try {
            if (entry != null) {
                // Load data from cached first
                String resultJson = new String(entry.data, "UTF-8");
                displayMovies(resultJson);
                view.findViewById(R.id.loading_panel).setVisibility(View.GONE);
            } else {
                // Cached response doesn't exists. Make network call here
                makeCacheData(conUrl, new ServiceCallBackListener() {
                    @Override
                    public void onResult(Object data) {
                        displayMovies(data.toString());
                        view.findViewById(R.id.loading_panel).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                    }
                });
            }

        } catch (UnsupportedEncodingException e) {
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.movieListMenu = menu;
        FragmentManager fragmentManager = this.getChildFragmentManager();
//         In case movie detail is shown
        if (fragmentManager.getBackStackEntryCount() > 0) {
            inflater.inflate(R.menu.movies_detail, menu);
        } else {
            // In case movie list is shown
            inflater.inflate(R.menu.movies_list, menu);
            menu.findItem(R.id.view_type_change).setVisible(true);
            if (!StringUtil.isEmpty(movieTitle)) {
                optionTextView.setText(movieTitle);
            } else {
                optionTextView.setText(R.string.category_pop_movie);
            }
        }

        Log.d("MovieListFragment","onCreateOptionsMenu:show");
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.view_type_change) {
            if (viewType.isDefaultView()) {
                viewType = ViewType.GRID;
                item.setIcon(R.mipmap.ic_list_white);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                viewType = ViewType.DEFAULT;
                item.setIcon(R.mipmap.ic_grid_on_white);
                recyclerView.setLayoutManager(lineLayoutManager);
            }

            movieRVAdapter = new MovieRVAdapter(movieList, viewType, getContext(), detailCallBackListener);
            recyclerView.setAdapter(movieRVAdapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerOptionViewOnClickListener() {
        optionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnimationFragment();
            }
        });
    }

    private void showAnimationFragment() {
        if (!dropDown.isShown()) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            DropDownAnimationFragment dropDownAnimationFragment = new DropDownAnimationFragment();

            ServiceCallBackListener callBackListener = new ServiceCallBackListener() {
                @Override
                public void onResult(Object data) {
                    category = (int)data;
                    dropDown = DropDown.HIDE_OPTION;
                    reFreshRecyclerView(recyclerView);
                    optionTextView.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.mipmap.ic_arrow_drop_down, 0);
                    optionTextView.setText(getString(OptionView.values()[category].getTitleId()));
                    movieTitle = optionTextView.getText().toString();
                }

                @Override
                public void onError() {

                }
            };

            dropDownAnimationFragment.setServiceCallBackListener(callBackListener);

            fragmentTransaction.add(R.id.main_screen, dropDownAnimationFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            optionTextView.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.mipmap.ic_arrow_drop_up, 0);
            dropDown = DropDown.SHOW_OPTION;

        } else {
            getFragmentManager().popBackStack();
            optionTextView.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.mipmap.ic_arrow_drop_down, 0);
            dropDown = DropDown.HIDE_OPTION;
        }
    }
    private void displayMovies(String resultJson) {
        List<Movie> movies = Collections.EMPTY_LIST;
        if (resultJson != null && resultJson.length() > 0) {
            try {
                movies = parsJsonToMovies(resultJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (movies != null && movies.size() > 0) {
            movies = MovieService.movieFilter(movies, filterRate, filterYear);

            Log.d("displayMovies","" + movies.size());
            movieList.addAll(movies);

            // Sort
            if (SortBy.isSortOnDate(sortBy)) {
                Collections.sort(movieList, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie lhs, Movie rhs) {
                        return lhs.getReleaseDate().compareTo(rhs.getReleaseDate());
                    }

                });
            } else if (SortBy.isSortOnRating(sortBy)) {
                Collections.sort(movieList, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie lhs, Movie rhs) {
                        return lhs.getRating().compareTo(rhs.getRating());
                    }

                });
            }

            if (movieRVAdapter != null) {
                movieRVAdapter.notifyDataSetChanged();
            } else {
                movieRVAdapter = new MovieRVAdapter(movieList, viewType, getContext(), detailCallBackListener);

                recyclerView.setAdapter(movieRVAdapter);
            }
        }
    }

    private void showDetailMovie(int pos) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        DetailMovieFragment detailMovieFragment = DetailMovieFragment.newInstance();
        Movie movie = movieList.get(pos);

        Bundle bundle = new Bundle();
        bundle.putSerializable("movie_detail", movie);
        detailMovieFragment.setArguments(bundle);

        ft.add(R.id.movie_list_content, detailMovieFragment, MovieFragmentManager.MFragment.DETAIL_MOVIE_FRAGMENT.toString());
        ft.addToBackStack(null);
        ft.commit();

        getActivity()
                .findViewById(R.id.movies_action_bar)
                .findViewById(R.id.movie_type_option)
                .setVisibility(View.GONE);
        getActivity().setTitle(movie.getMovieTitle());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(Object o) {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }
}
