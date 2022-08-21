package com.example.travel_uk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.travel_uk.Fragment.HomeCommunity;
import com.example.travel_uk.Fragment.HomeJourney;
import com.example.travel_uk.Fragment.HomeMap;
import com.example.travel_uk.Fragment.HomeMyinfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {


    private Fragment[] fragments = new Fragment[4];
    private BottomNavigationView mbottomnavgation;
    private int fragmentflag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initView();

        initFragment();
        selectFragment();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String poster = bundle.getString("posterid");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",poster);
            editor.apply();
        }
    }

    private void initView() {
        mbottomnavgation = findViewById(R.id.bottomna);
    }

    private void initFragment() {
        fragments[0] = new HomeMap();
        fragments[1] = new HomeCommunity();
        fragments[2] = new HomeJourney();
        fragments[3] = new HomeMyinfo();
        initLoadFragment(R.id.frame, 0, fragments);

    }


    private void initLoadFragment(int containerId, int showFragment, Fragment... fragments) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.length; i++) {

            transaction.add(containerId, fragments[i], fragments[i].getClass().getName());
            if (i != showFragment)
                transaction.hide(fragments[i]);
        }

        transaction.commitAllowingStateLoss();

    }
    private void selectFragment() {

        mbottomnavgation.setItemIconTintList(null);
        mbottomnavgation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.homemap:
                    showAndHideFragment(fragments[0], fragments[fragmentflag]);
                    fragmentflag = 0;
                    break;
                case R.id.community:
                    showAndHideFragment(fragments[1], fragments[fragmentflag]);
                    fragmentflag = 1;
                    break;
                case R.id.journey:
                    showAndHideFragment(fragments[2], fragments[fragmentflag]);
                    fragmentflag = 2;
                    break;
                case R.id.me:
                    showAndHideFragment(fragments[3], fragments[fragmentflag]);
                    fragmentflag = 3;
                    break;
            }
            return true;
        });
    }


    private void showAndHideFragment(Fragment show, Fragment hide) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (show != hide)
            transaction.show(show).hide(hide).commitAllowingStateLoss();

    }
}






