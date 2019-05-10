package com.avengers.businesscardapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avengers.businesscardapp.dto.LoginUser;
import com.avengers.businesscardapp.util.NetworkHelper;
import com.avengers.businesscardapp.webservice.BusinessCardWebservice;
import com.avengers.businesscardapp.webservice.LoginResponse;

import java.io.IOException;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static int REQUEST_CODE = 1;

    private Toolbar toolbar;
    private TextView title;
    private EditText edtEmail, edtPassword;
    private Button btnSubmit, btnSignup;
    private final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CODE);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
        edtEmail = findViewById(R.id.edt_emailId);
        edtPassword = findViewById(R.id.edt_password);
        btnSubmit = findViewById(R.id.btn_login_submit);
        btnSignup = findViewById(R.id.btn_signup);
        setUpToolbar();
        btnSubmit.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_submit:
                handleLogin();
                break;
            case R.id.btn_signup:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT).show();
    }

    //On login validate the user credentials and navigate to the customer home page
    //if the login details was not a match display message to user to verify again
    public void handleLogin() {

        String mailId = edtEmail.getText().toString();
        String pwd = edtPassword.getText().toString();

        if (mailId.trim().isEmpty() || pwd.trim().isEmpty()) {
            showMsg(getString(R.string.enter_login_info));
        } else {
            LoginUser user = new LoginUser();
            user.setEmailId(mailId);
            user.setPassword(pwd);
            new LoginTask(getApplicationContext(), user).execute();
//            DataControllerBusinessCard dataController = new DataControllerBusinessCard(getBaseContext());
//            dataController.open();
//            Cursor cursor = dataController.validateLoginCredentials(mailId);
//            if (cursor != null && cursor.moveToFirst()) {
//                //cursor.moveToFirst();
//                String dbMailId = cursor.getString(2);
//                String dbPassword = cursor.getString(3);
//                if (mailId.equalsIgnoreCase(dbMailId) && pwd.equalsIgnoreCase(dbPassword)) {
//                    String firstName = cursor.getString(0);
//                    String lastName = cursor.getString(1);
//
//                    //Set the email Id and first name and last name in shared preferences
//                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("Email_Id", dbMailId);
//                    editor.putString("First_Name", firstName);
//                    editor.putString("Last_Name", lastName);
//                    editor.commit();
//                    Intent intent = new Intent(this, NavigationActivity.class);
//                    startActivity(intent);
//
//                } else {
//                    Context context = getApplicationContext();
//                    CharSequence text = getString(R.string.login_validation);
//                    int duration = Toast.LENGTH_LONG;
//                    Toast.makeText(context, text, duration).show();
//                }
//            } else {
//                Context context = getApplicationContext();
//                CharSequence text = getString(R.string.no_customer_record);
//                int duration = Toast.LENGTH_LONG;
//                Toast.makeText(context, text, duration).show();
//            }
//            cursor.close();
//            dataController.close();
        }


    }

    private class LoginTask extends AsyncTask<Void, LoginResponse, LoginResponse> {

        private Context mContext;
        private LoginUser user;

        public LoginTask(Context mContext, LoginUser user) {
            this.mContext = mContext;
            this.user = user;
        }

        @Override
        protected LoginResponse doInBackground(Void... voids) {
            if (NetworkHelper.hasNetworkAccess(mContext)) {
                BusinessCardWebservice webservice = BusinessCardWebservice
                        .retrofit.create(BusinessCardWebservice.class);
                Call<LoginResponse> call = webservice.loginUser(user);
                try {
                    LoginResponse response = call.execute().body();
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "LoginTask: " + e.getMessage());
                    return null;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if (response != null && response.getMessage() != null) {
                if (response.getResponseCode() != 200) {
                    showMsg(response.getMessage());
                } else {
                    saveDataToPrefs(user.getEmailId(), response);
                    Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    private void saveDataToPrefs(String emailId, LoginResponse response) {
        //Set the email Id and first name and last name in shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email_Id", emailId);
        editor.putString("First_Name", response.getFirstName());
        editor.putString("Last_Name", response.getLastName());
        editor.apply();
    }
}


