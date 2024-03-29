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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avengers.businesscardapp.dto.GenericResponse;
import com.avengers.businesscardapp.dto.SignUpUser;
import com.avengers.businesscardapp.util.NetworkHelper;
import com.avengers.businesscardapp.util.Utility;
import com.avengers.businesscardapp.webservice.BusinessCardWebservice;

import java.io.IOException;

import retrofit2.Call;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView title;
    private EditText edtFname, edtLname, edtEmail, edtPassword, edtConfirmPassword;
    private final String TAG = "SignUpActivity";
    private LinearLayout progress;
    private TextView txtProgressMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
    }

    public void saveCustomerDetails() {
        String fName = edtFname.getText().toString();
        String lName = edtLname.getText().toString();
        String emailId = edtEmail.getText().toString();
        String pwd = edtPassword.getText().toString();
        String confirmPwd = edtConfirmPassword.getText().toString();

        if (fName.trim().isEmpty() || lName.trim().isEmpty() ||
                emailId.trim().isEmpty() || pwd.trim().isEmpty() ||
                confirmPwd.trim().isEmpty()) {
            Utility.getInstance().showMsg(getApplicationContext(),
                    getResources().getString(R.string.enter_all_signup_fields));
        } else if (!pwd.equalsIgnoreCase(confirmPwd)) {
            Utility.getInstance().showMsg(getApplicationContext(),
                    getResources().getString(R.string.password_match));
        } else {
            SignUpUser user = new SignUpUser();
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
        progress = findViewById(R.id.lyt_progress);
        txtProgressMsg = progress.findViewById(R.id.txt_progress_msg);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
        edtFname = findViewById(R.id.edt_first_name);
        edtLname = findViewById(R.id.edt_last_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        Button btnSubmit = findViewById(R.id.btn_submit);
        Button btnCancel = findViewById(R.id.btn_cancel);
        setUpToolbar();
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
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
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class SignUpTask extends AsyncTask<Void, Void, GenericResponse> {

        private Context mContext;
        private SignUpUser user;

        SignUpTask(Context mContext, SignUpUser user) {
            this.mContext = mContext;
            this.user = user;
            progress.setVisibility(View.VISIBLE);
            txtProgressMsg.setText(getString(R.string.txt_signup_progress));
        }

        @Override
        protected GenericResponse doInBackground(Void... voids) {
            if (NetworkHelper.hasNetworkAccess(mContext)) {
                BusinessCardWebservice webservice = BusinessCardWebservice
                        .retrofit.create(BusinessCardWebservice.class);
                Call<GenericResponse> call = webservice.registerUser(user);
                try {
                    return call.execute().body();
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
                    Utility.getInstance().showMsg(getApplicationContext(),
                            response.getMessage());
                } else {
                    Utility.getInstance().showMsg(getApplicationContext(),
                            getString(R.string.signup_success_msg));
                    finish();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
            progress.setVisibility(View.GONE);
        }
    }
}

