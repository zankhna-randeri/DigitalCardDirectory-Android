package com.avengers.businesscardapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.businesscardapp.dto.UploadCardResponse;
import com.avengers.businesscardapp.util.Constants;

import java.io.File;

public class EditCardActivity extends AppCompatActivity {

    private UploadCardResponse cardResponse;
    private Uri cardImageUri;

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
            cardResponse = extras.getParcelable(Constants.EXTRA_CARD_DETAIL);
            cardImageUri = extras.getParcelable(Constants.EXTRA_IMG_URI);
            displayCardDetail(cardImageUri, cardResponse);
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
}
