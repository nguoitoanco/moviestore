package com.fsoft.sonic_larue.khanhnv10.moviestore.view.actor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fsoft.sonic_larue.khanhnv10.moviestore.R;
import com.fsoft.sonic_larue.khanhnv10.moviestore.model.Cast;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.movie.MovieService;
import com.fsoft.sonic_larue.khanhnv10.moviestore.service.network.AppController;

import java.util.List;

/**
 * Created by KhanhNV10 on 12/13/2015.
 */
public class ActorPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflate;
    private List<Cast> castList;

    public ActorPagerAdapter(FragmentManager fm, List<Cast> casts) {
        super(fm);
        this.castList = casts;
    }

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    @Override
    public Fragment getItem(int position) {
        DetailActorFragment detailActorFragment = DetailActorFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable("actorDetail", castList.get(position));
        detailActorFragment.setArguments(bundle);
        return detailActorFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, final int position) {
//
//    }
//


    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return castList.size();
    }

//    @Override
//    public int getItemPosition(Object object) {
////        int position = super.getItemPosition(object);
////        Log.d("getItemPosition", "" + object.toString());
////        displayHeaderAndFooter((View)(object), castList.get(position), position);
////        return position;
//        Log.d("getItemPosition", object.toString());
//        return POSITION_NONE;
//    }


}
