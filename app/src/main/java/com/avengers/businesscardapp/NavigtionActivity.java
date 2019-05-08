package com.avengers.businesscardapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avengers.businesscardapp.fragment.AddCardFragment;
import com.avengers.businesscardapp.fragment.MyCardFragment;


public class NavigtionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MyCardFragment.OnFragmentInteractionListener, AddCardFragment.OnFragmentInteractionListener {

    private static final int PERMISSION_CODE = 500;
    private static final int IMAGE_CAPTURE_CODE = 1000;
    TextView name, email;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigtion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String emailId = sharedPrefs.getString("Email_Id", "");
        String firstName = sharedPrefs.getString("First_Name", "");
        String lastName = sharedPrefs.getString("Last_Name", "");

        View header = navigationView.getHeaderView(0);
        name = (TextView) header.findViewById(R.id.text_name);
        email = (TextView) header.findViewById(R.id.text_email);
        name.setText(firstName+" "+lastName);
        email.setText(emailId);


    }


    @SuppressWarnings("StatementWithEmptyBody")
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
        } else if (id == R.id.nav_gallery) {
            Fragment myCarsdFragment = MyCardFragment.newInstance(emailId);
            setDefaultFragment(myCarsdFragment);

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public void onFragmentInteraction(Uri uri) {

    }
}
