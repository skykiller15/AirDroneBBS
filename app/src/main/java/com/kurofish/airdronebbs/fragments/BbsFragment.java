package com.kurofish.airdronebbs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kurofish.airdronebbs.activities.BbsAddPostActivity;
import com.kurofish.airdronebbs.R;

import java.util.ArrayList;

public class BbsFragment extends Fragment {
    private TabLayout tabLayout;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> tabIndicators = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_bbs, container, false);
        tabLayout = view.findViewById(R.id.bTabLayout);
        ViewPager viewPager = view.findViewById(R.id.bViewPager);
        FloatingActionButton addPostFAB = view.findViewById(R.id.addPostFAB);

        fragments.add(new BbsTechSectionFragment());
        fragments.add(new BbsChatSectionFragment());

        tabIndicators.add(getString(R.string.bbs_tech_section));
        tabIndicators.add(getString(R.string.bbs_chat_section));
        BbsFPAdapter bbsFPAdapter = new BbsFPAdapter(getChildFragmentManager());
        Log.d("BBSTAG", "test");

        viewPager.setAdapter(bbsFPAdapter);
        tabLayout.setupWithViewPager(viewPager);
        addPostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String section = tabIndicators.get(tabLayout.getSelectedTabPosition());
                Log.d("BBSTAG", "now section is " + section);
                Bundle bundle = new Bundle();
                bundle.putString("section_name", section);
                Intent intent = new Intent(getActivity(), BbsAddPostActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                //Objects.requireNonNull(getActivity()).finish();
            }
        });
        return view;
    }

    public class BbsFPAdapter extends FragmentPagerAdapter {
        BbsFPAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabIndicators.get(position);
        }
    }

}
