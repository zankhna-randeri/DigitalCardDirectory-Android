package com.avengers.businesscardapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.avengers.businesscardapp.db.DataControllerBusinessCard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    String fName, lName, mailId, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void saveCustomerDetails(View view) {
        EditText firstName = (EditText) findViewById(R.id.firstName);
        fName = firstName.getText().toString();
        EditText lastName = (EditText) findViewById(R.id.lastName);
        lName = lastName.getText().toString();
        EditText emailId = (EditText) findViewById(R.id.emailId);
        mailId = emailId.getText().toString();
        EditText password = (EditText) findViewById(R.id.password);
        pwd = password.getText().toString();
        EditText confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        String confirmPwd = confirmPassword.getText().toString();

        if (fName.equalsIgnoreCase("") || lName.equalsIgnoreCase("") ||
                mailId.equalsIgnoreCase("") || pwd.equalsIgnoreCase("") ||
                confirmPwd.equalsIgnoreCase("")) {
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.enter_all_signup_fields);
            int duration = Toast.LENGTH_LONG;
            Toast.makeText(context, text, duration).show();
        } else {
            if (pwd.equalsIgnoreCase(confirmPwd)) {
                signUpValidation();
            } else {
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.password_match);
                int duration = Toast.LENGTH_LONG;
                Toast.makeText(context, text, duration).show();
            }
        }
    }

    public void signUpValidation() {
        DataControllerBusinessCard dataController = new DataControllerBusinessCard(getBaseContext());
        dataController.open();

        //Check if whether the record already exists
        String existingEmailId = dataController.retrieveCustomerInfo(mailId);

        if (existingEmailId != null && existingEmailId != "") {
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.customer_exists);
            int duration = Toast.LENGTH_LONG;
            Toast.makeText(context, text, duration).show();
        } else {
            long retValue = dataController.insert(fName, lName, mailId, pwd);

            if (retValue != -1) {
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.success_msg);
                int duration = Toast.LENGTH_LONG;
                Toast.makeText(context, text, duration).show();
            }

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        dataController.close();
    }

    public void signupCancel(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

