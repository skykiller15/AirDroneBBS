package com.kurofish.airdronebbs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BbsFPAdapter extends FragmentPagerAdapter {
    public BbsFPAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return new BbsTechSectionFragment();
    }

    @Override
    public int getCount() {
        return 0;
    }
}
