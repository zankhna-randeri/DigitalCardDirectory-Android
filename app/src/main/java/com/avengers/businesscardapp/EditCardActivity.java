package com.avengers.businesscardapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.businesscardapp.db.DataControllerBusinessCard;
import com.avengers.businesscardapp.dto.UploadCardResponse;
import com.avengers.businesscardapp.util.Constants;
import com.avengers.businesscardapp.util.Utility;

public class EditCardActivity extends AppCompatActivity {


    private final String TAG = "EditCardActivity";
    private Uri cardImageUri;
    private Context mContext;

    private Toolbar toolbar;
    private TextView title;
    private EditText edtName;
    private EditText edtOrg;
    private EditText edtContactNo;
    private EditText edtEmail;
    private EditText edtNotes;
    private ImageView imgCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        mContext = EditCardActivity.this;
        initView();
        getIntentData(getIntent());
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
        edtName = findViewById(R.id.edt_name);
        edtOrg = findViewById(R.id.edt_contact_org);
        edtNotes = findViewById(R.id.edt_contact_notes);
        edtEmail = findViewById(R.id.edt_contact_email);
        edtContactNo = findViewById(R.id.edt_contact_no);
        imgCard = findViewById(R.id.img_edit_card);
        setUpToolbar();
    }


    private void getIntentData(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(Constants.EXTRA_CARD_DETAIL)) {
            UploadCardResponse cardResponse = extras.getParcelable(Constants.EXTRA_CARD_DETAIL);
            cardImageUri = extras.getParcelable(Constants.EXTRA_IMG_URI);
            if (cardResponse != null) {
                displayCardDetail(cardImageUri, cardResponse);
            }
        }
    }

    private void displayCardDetail(Uri imgUri, UploadCardResponse cardResponse) {
        imgCard.setImageURI(imgUri);
        edtName.setText(cardResponse.getName());
        edtContactNo.setText(cardResponse.getNumber());
        edtEmail.setText(cardResponse.getEmailId());
        edtOrg.setText(cardResponse.getOrganization());
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        title.setText(getString(R.string.txt_card_detail));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_done:
                if (isCardDetailValid()) {
                    String fileName = getFileNameFromUri(cardImageUri);
                    Log.d(TAG, "fileName: " + fileName);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                    String userEmail = prefs.getString(Constants.PREFS_EMAIL_ID, "");
                    if (saveCardDetails(edtName.getText().toString().trim(),
                            edtOrg.getText().toString().trim(),
                            edtEmail.getText().toString().trim(),
                            edtContactNo.getText().toString().trim(),
                            fileName,
                            userEmail,
                            edtNotes.getText().toString().trim())) {
                        Utility.getInstance().showMsg(getApplicationContext(),
                                getString(R.string.txt_card_save_success));
                        finish();
                        Intent intent = new Intent(mContext, NavigationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
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

    private String getFileNameFromUri(Uri uri) {
        Cursor returnCursor = getContentResolver()
                .query(uri, null, null, null, null);
        int nameIndex = 0;
        if (returnCursor != null) {
            nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        } else {
            return "";
        }
    }

    private boolean isCardDetailValid() {
        if (TextUtils.isEmpty(edtName.getText()) ||
                TextUtils.isEmpty(edtContactNo.getText()) ||
                TextUtils.isEmpty(edtEmail.getText()) ||
                TextUtils.isEmpty(edtOrg.getText())) {
            Utility.getInstance().showMsg(getApplicationContext(),
                    getString(R.string.txt_err_edit_card));
            return false;
        }
        return true;
    }

    private boolean saveCardDetails(String name, String organization, String contactEmail,
                                    String contactNumber, String fileName, String userEmail,
                                    String notes) {
        DataControllerBusinessCard dataController = new DataControllerBusinessCard(getBaseContext());
        dataController.open();
        long rowId = dataController
                .insertCard(name, organization, contactEmail, contactNumber, fileName, userEmail,
                        notes);
        dataController.close();
        return rowId != -1;
    }
}
