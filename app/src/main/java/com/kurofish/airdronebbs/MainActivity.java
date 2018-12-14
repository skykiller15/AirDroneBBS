package com.kurofish.airdronebbs;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private TeachingFragment teachingFragment;
    private TradeFragment tradeFragment;
    private BbsFragment bbsFragment;
    private DoingFragment doingFragment;
    private MeFragment meFragment;

    private android.app.FragmentManager fragmentManager;

    private BottomNavigationView bottomNavigationView;

    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        teachingFragment = new TeachingFragment();
        tradeFragment = new TradeFragment();
        bbsFragment = new BbsFragment();
        doingFragment = new DoingFragment();
        meFragment = new MeFragment();
        fragments = new Fragment[]{bbsFragment, teachingFragment, tradeFragment, doingFragment, meFragment};

        fragmentManager = getFragmentManager();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_bbs : {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        hideFragments(transaction);
                        if(!fragments[0].isAdded())
                        {
                            transaction.add(R.id.mainView, fragments[0]);
                        }
                        transaction.show(fragments[0]).commitAllowingStateLoss();

                        return true;
                    }

                    case R.id.navigation_teaching : {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        hideFragments(transaction);
                        if(!fragments[1].isAdded())
                        {
                            transaction.add(R.id.mainView, fragments[1]);
                        }
                        transaction.show(fragments[1]).commitAllowingStateLoss();

                        return true;
                    }


                    case R.id.navigation_trade : {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        hideFragments(transaction);
                        if(!fragments[2].isAdded())
                        {
                            transaction.add(R.id.mainView, fragments[2]);
                        }
                        transaction.show(fragments[2]).commitAllowingStateLoss();

                        return true;
                    }

                    case R.id.navigation_doing : {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        hideFragments(transaction);
                        if(!fragments[3].isAdded())
                        {
                            transaction.add(R.id.mainView, fragments[3]);
                        }
                        transaction.show(fragments[3]).commitAllowingStateLoss();

                        return true;
                    }

                    case R.id.navigation_me : {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        hideFragments(transaction);
                        if(!fragments[4].isAdded())
                        {
                            transaction.add(R.id.mainView, fragments[4]);
                        }
                        transaction.show(fragments[4]).commitAllowingStateLoss();

                        return true;
                    }
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_bbs);
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (teachingFragment != null) {
            transaction.hide(teachingFragment);
        }
        if (bbsFragment != null) {
            transaction.hide(bbsFragment);
        }
        if (tradeFragment != null) {
            transaction.hide(tradeFragment);
        }
        if (doingFragment != null) {
            transaction.hide(doingFragment);
        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }
    }
}


