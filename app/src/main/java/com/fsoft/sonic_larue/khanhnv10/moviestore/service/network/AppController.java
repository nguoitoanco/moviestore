package com.fsoft.sonic_larue.khanhnv10.moviestore.service.network;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by KhanhNV10 on 2015/11/21.
 */
public class AppController extends Application implements ImageLoader.ImageListener {

    public static final String TAG = AppController.class
            .getSimpleName();

    private static final int MAX_CACHE_SIZE = 1000;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
//            mImageLoader = new ImageLoader(this.mRequestQueue,
//                    new LruBitmapCache(MAX_CACHE_SIZE));
            mImageLoader = new ImageLoader(mRequestQueue,
                    new ImageLoader.ImageCache() {
                        private final LruBitmapCache
                                cache = new LruBitmapCache(MAX_CACHE_SIZE);

                        @Override
                        public Bitmap getBitmap(String url) {
                            Log.d("getBitmap", "url:" + url);
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            Log.d("putBitmap", "url:" + url);
                            cache.put(url, bitmap);
                        }

                    });
        }
        return this.mImageLoader;
    }

    public ImageLoader getImageLoader(int maxSize) {
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruBitmapCache
                            cache = new LruBitmapCache(MAX_CACHE_SIZE);

                    @Override
                    public Bitmap getBitmap(String url) {
                        Log.d("getBitmap", "url:" + url);
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        Log.d("putBitmap", "url:" + url);
                        cache.put(url, bitmap);
                    }

                });

        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {

    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.d("AppController", "clear cache!!!!!");
        if (mRequestQueue != null) {
            mRequestQueue.getCache().clear();
        }
    }
}
