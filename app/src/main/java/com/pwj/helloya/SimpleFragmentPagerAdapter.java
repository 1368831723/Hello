package com.pwj.helloya;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pwj.fragment.FragmentBids;
import com.pwj.fragment.FragmentCustomer;
import com.pwj.fragment.FragmentMe;
import com.pwj.fragment.FragmentProduct;
import com.pwj.fragment.FragmentRequire;
import com.pwj.fragment.FragmentRecruit;

/**
 * Created by leon on 3/7/18.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 6;
    private Context context;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new FragmentProduct();
            case 2:
                return new FragmentBids();
            case 3:
                return new FragmentCustomer();
            case 4:
                return new FragmentRecruit();
            case 5:
                return new FragmentMe();
            default:
                return new FragmentRequire();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
