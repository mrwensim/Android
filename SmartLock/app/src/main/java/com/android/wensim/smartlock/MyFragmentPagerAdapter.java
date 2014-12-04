package com.android.wensim.smartlock;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.material.widget.TabIndicator;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter implements TabIndicator.TabResourceProvider {

    protected static final String[] CONTENT = new String[]{"Bluetooth", "Location", "Server", "PAGE04"};

    private int mCount = CONTENT.length;

    public MyFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return BluetoothFragment.newInstance();
        } else if (position == 1) {
            return MyMapFragment.newInstance();
        }
        else if (position == 2) {
            return MessageFragment.newInstance();
        }
        else
            return TestFragment.newInstance(CONTENT[position]);
    }

    @Override
    public String getText(int position) {
        return MyFragmentPagerAdapter.CONTENT[position % CONTENT.length];
    }

    @Override
    public int getCount() {
        return mCount;
    }
}
