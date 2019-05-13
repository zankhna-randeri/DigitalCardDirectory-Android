package com.avengers.businesscardapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.avengers.businesscardapp.fragment.AddCardFragment;
import com.avengers.businesscardapp.fragment.CardListFragment;


public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    private Toolbar toolbar;
    private TextView txtName, txtEmail;
    private TextView title;
    private String emailId, firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mContext = NavigationActivity.this;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        emailId = sharedPrefs.getString("Email_Id", "");
        firstName = sharedPrefs.getString("First_Name", "");
        lastName = sharedPrefs.getString("Last_Name", "");
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
//        setSupportActionBar(toolbar);
        setUpToolbar();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        txtName = header.findViewById(R.id.text_name);
        txtEmail = header.findViewById(R.id.text_email);
        txtName.setText((firstName + " " + lastName));
        txtEmail.setText(emailId);
        Fragment myCardsFragment = CardListFragment.newInstance(emailId);
        setDefaultFragment(myCardsFragment);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        title.setText(getString(R.string.app_name));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String emailId = sharedPrefs.getString("Email_Id", "");
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addCard) {
            // Handle the camera action
            Fragment addCardFragment = AddCardFragment.newInstance(emailId);
            setDefaultFragment(addCardFragment);
        } else if (id == R.id.nav_my_cards) {
            Fragment myCardsFragment = CardListFragment.newInstance(emailId);
            setDefaultFragment(myCardsFragment);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setDefaultFragment(Fragment defaultFragment) {
        replaceFragment(defaultFragment);
    }

    private void replaceFragment(Fragment defaultFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_navigation, defaultFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
