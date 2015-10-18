package com.boilermakeproject.hometownapp;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Created by noahrinehart on 10/17/15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs){
        super (fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                MapFragment contactFragment = new MapFragment();
                return contactFragment;
            case 1:
                MetricFragment mapFragment = new MetricFragment();
                return mapFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount(){
        return mNumOfTabs;
    }

}
