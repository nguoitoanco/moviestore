package com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public interface CallBackListener<T> {
    void onResult(T data);
    void onError();
}
