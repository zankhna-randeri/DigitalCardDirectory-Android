package com.avengers.businesscardapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avengers.businesscardapp.dto.SignupUser;
import com.avengers.businesscardapp.util.NetworkHelper;
import com.avengers.businesscardapp.webservice.BusinessCardWebservice;
import com.avengers.businesscardapp.webservice.GenericResponse;

import java.io.IOException;

import retrofit2.Call;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView title;
    private Context mContext;
    private EditText edtFname, edtLname, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnSubmit, btnCancel;
    private String fName, lName, emailId, pwd, confirmPwd;
    private final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = SignUpActivity.this;
        initView();
    }

    public void saveCustomerDetails() {
        fName = edtFname.getText().toString();
        lName = edtLname.getText().toString();
        emailId = edtEmail.getText().toString();
        pwd = edtPassword.getText().toString();
        confirmPwd = edtConfirmPassword.getText().toString();

        if (fName.trim().isEmpty() || lName.trim().isEmpty() ||
                emailId.trim().isEmpty() || pwd.trim().isEmpty() ||
                confirmPwd.trim().isEmpty()) {
            showMsg(getResources().getString(R.string.enter_all_signup_fields));
        } else if (!pwd.equalsIgnoreCase(confirmPwd)) {
            showMsg(getResources().getString(R.string.password_match));
        } else {
            SignupUser user = new SignupUser();
            user.setFirstName(fName);
            user.setLastName(lName);
            user.setEmailId(emailId);
            user.setPassword(pwd);
            new SignUpTask(SignUpActivity.this, user).execute();
        }
    }

//    public void signUpValidation() {


//        DataControllerBusinessCard dataController = new DataControllerBusinessCard(getBaseContext());
//        dataController.open();
//
//        //Check if whether the record already exists
//        String existingEmailId = dataController.retrieveCustomerInfo(emailId);
//
//        if (existingEmailId != null && existingEmailId != "") {
//            Context context = getApplicationContext();
//            CharSequence text = getString(R.string.customer_exists);
//            int duration = Toast.LENGTH_LONG;
//            Toast.makeText(context, text, duration).show();
//        } else {
//            long retValue = dataController.insert(fName, lName, emailId, pwd);
//
//            if (retValue != -1) {
//                Context context = getApplicationContext();
//                CharSequence text = getString(R.string.success_msg);
//                int duration = Toast.LENGTH_LONG;
//                Toast.makeText(context, text, duration).show();
//            }
//
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        }
//        dataController.close();
//    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
        edtFname = findViewById(R.id.edt_first_name);
        edtLname = findViewById(R.id.edt_last_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnSubmit = findViewById(R.id.btn_submit);
        btnCancel = findViewById(R.id.btn_cancel);
        setUpToolbar();
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void showMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT).show();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        title.setText(getString(R.string.txt_sign_up));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                saveCustomerDetails();
                break;
            case R.id.btn_cancel:
                finish();
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

    private class SignUpTask extends AsyncTask<Void, GenericResponse, GenericResponse> {

        private Context mContext;
        private SignupUser user;

        public SignUpTask(Context mContext, SignupUser user) {
            this.mContext = mContext;
            this.user = user;
        }

        @Override
        protected GenericResponse doInBackground(Void... voids) {
            if (NetworkHelper.hasNetworkAccess(mContext)) {
                BusinessCardWebservice webservice = BusinessCardWebservice
                        .retrofit.create(BusinessCardWebservice.class);
                Call<GenericResponse> call = webservice.registerUser(user);
                try {
                    GenericResponse response = call.execute().body();
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "SignUpTask: " + e.getMessage());
                    return null;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(GenericResponse response) {
            super.onPostExecute(response);
            if (response != null && response.getMessage() != null) {
                if (response.getResponseCode() != 200) {
                    showMsg(response.getMessage());
                } else {

                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        }
    }
}

