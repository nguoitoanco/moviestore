package com.fsoft.sonic_larue.khanhnv10.moviestore.view.movie;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fsoft.sonic_larue.khanhnv10.moviestore.R;

public class AppAboutFragment extends Fragment {


    public static AppAboutFragment newInstance() {
        AppAboutFragment fragment = new AppAboutFragment();
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

        View view = inflater.inflate(R.layout.fragment_app_about, container, false);
        WebView myWebView = (WebView) view.findViewById(R.id.web_view);
        myWebView.setWebViewClient(new MyBrowser());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.loadUrl("https://www.themoviedb.org/about/our-history");

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            view.findViewById(R.id.loading_panel).setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.findViewById(R.id.loading_panel).setVisibility(View.GONE);
        }
    }
}


