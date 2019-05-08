package com.avengers.businesscardapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avengers.businesscardapp.db.DataControllerBusinessCard;

public class LoginActivity extends AppCompatActivity {

    private static int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CODE);
    }

    //Onlogin validate the user credentials and navigate to the customer home page
    //if the login details was not a match display message to user to verify again
    public void onLoginSubmit(View view) {
        EditText emailId = (EditText) findViewById(R.id.emailId);
        String mailId = emailId.getText().toString();
        EditText password = (EditText) findViewById(R.id.password);
        String pwd = password.getText().toString();

        if (mailId.equalsIgnoreCase("") || pwd.equalsIgnoreCase("")) {
            Context context = getApplicationContext();
            CharSequence text = getString(R.string.enter_login_info);
            int duration = Toast.LENGTH_LONG;
            Toast.makeText(context, text, duration).show();
        } else {
            DataControllerBusinessCard dataController = new DataControllerBusinessCard(getBaseContext());
            dataController.open();
            Cursor cursor = dataController.validateLoginCredentials(mailId);
            if (cursor != null && cursor.moveToFirst()) {
                //cursor.moveToFirst();
                String dbMailId = cursor.getString(2);
                String dbPassword = cursor.getString(3);
                if (mailId.equalsIgnoreCase(dbMailId) && pwd.equalsIgnoreCase(dbPassword)) {
                    String firstName = cursor.getString(0);
                    String lastName = cursor.getString(1);

                    //Set the email Id and first name and last name in shared preferences
                    SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("Email_Id", dbMailId);
                    editor.putString("First_Name", firstName);
                    editor.putString("Last_Name", lastName);
                    editor.commit();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.login_validation);
                    int duration = Toast.LENGTH_LONG;
                    Toast.makeText(context, text, duration).show();
                }
            } else {
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.no_customer_record);
                int duration = Toast.LENGTH_LONG;
                Toast.makeText(context, text, duration).show();
            }
            cursor.close();
            dataController.close();
        }


    }

    public void onSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}


