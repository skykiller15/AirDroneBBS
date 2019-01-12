package com.kurofish.airdronebbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BbsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BbsFPAdapter bbsFPAdapter = null;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> tabIndicators = new ArrayList<>();
    private FloatingActionButton addPostFAB;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_bbs, container, false);
        tabLayout = view.findViewById(R.id.bTabLayout);
        viewPager = view.findViewById(R.id.bViewPager);
        addPostFAB = view.findViewById(R.id.addPostFAB);

        fragments.add(new BbsTechSectionFragment());
        fragments.add(new BbsChatSectionFragment());

        tabIndicators.add(getString(R.string.bbs_tech_section));
        tabIndicators.add(getString(R.string.bbs_chat_section));
        bbsFPAdapter = new BbsFPAdapter(getChildFragmentManager());
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
        public BbsFPAdapter(FragmentManager fm) {
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
