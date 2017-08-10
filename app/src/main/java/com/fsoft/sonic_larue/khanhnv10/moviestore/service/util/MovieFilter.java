package com.fsoft.sonic_larue.khanhnv10.moviestore.service.util;

import com.android.internal.util.Predicate;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Movie;

import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Created by KhanhNV10 on 2015/12/09.
 */
public class MovieFilter implements Predicate<Movie> {

    private Pattern pattern;
    @Override
    public boolean apply(Movie movie) {
        return false;
    }

    public MovieFilter(String strRate, String rleaseDate, String sortby) {
        pattern = Pattern.compile(strRate);
    }
}
