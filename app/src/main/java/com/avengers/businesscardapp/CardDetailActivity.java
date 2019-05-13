package com.avengers.businesscardapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avengers.businesscardapp.db.DataControllerBusinessCard;
import com.avengers.businesscardapp.dto.Card;
import com.avengers.businesscardapp.dto.GenericResponse;
import com.avengers.businesscardapp.fragment.CardFragment;
import com.avengers.businesscardapp.fragment.ContactsFragment;
import com.avengers.businesscardapp.fragment.NotesFragment;
import com.avengers.businesscardapp.util.Constants;
import com.avengers.businesscardapp.util.NetworkHelper;
import com.avengers.businesscardapp.webservice.BusinessCardWebservice;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;

public class CardDetailActivity extends AppCompatActivity implements
        View.OnClickListener, ContactsFragment.OnFragmentInteractionListener {

    private final String TAG = "CardDetailActivity";
    private Toolbar toolbar;
    private TextView title;
    private Context mContext;
    private Button btnContacts;
    private Button btnCard;
    private Button btnNotes;
    private TextView txtName;
    private TextView txtOrganization;

    private Card card;
    private String appUserEmail;
    private String cardUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        mContext = CardDetailActivity.this;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        appUserEmail = sharedPrefs.getString("Email_Id", "");
        initView();
        fetchCardDetail(getIntent());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_delete_card:
                confirmDelete();
                break;
            case R.id.menu_refer_card:
                displayReferDialog();
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_card_detail, menu);
        return true;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_contacts:
                handleContactsClick();
                break;
            case R.id.btn_card:
                handleCardClick();
                break;
            case R.id.btn_notes:
                handleNotesClick();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void fetchCardDetail(Intent intent) {
        if (intent.getExtras() != null &&
                getIntent().getExtras().containsKey(Constants.EXTRA_CARD_DETAIL)) {
            card = getIntent().getExtras().getParcelable(Constants.EXTRA_CARD_DETAIL);
            txtName.setText(card.getName());
            txtOrganization.setText(card.getOrganization());
            setSelected(btnContacts);
            Fragment contactsFragment = ContactsFragment.newInstance(card);
            setDefaultFragment(contactsFragment);
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
        btnCard = findViewById(R.id.btn_card);
        btnContacts = findViewById(R.id.btn_contacts);
        btnNotes = findViewById(R.id.btn_notes);
        txtName = findViewById(R.id.txt_name);
        txtOrganization = findViewById(R.id.txt_org);
        setUpToolbar();
        btnNotes.setOnClickListener(this);
        btnContacts.setOnClickListener(this);
        btnCard.setOnClickListener(this);
    }

    private void handleCardClick() {
        setSelected(btnCard);
        deselectButton(btnNotes);
        deselectButton(btnContacts);
        Fragment cardFragment = CardFragment.newInstance(card.getFileName(), cardUrl);
        replaceFragment(cardFragment);
    }

    private void handleContactsClick() {
        setSelected(btnContacts);
        deselectButton(btnCard);
        deselectButton(btnNotes);
        Fragment contactsFragment = ContactsFragment.newInstance(card);
        replaceFragment(contactsFragment);
    }

    private void handleNotesClick() {
        setSelected(btnNotes);
        deselectButton(btnContacts);
        deselectButton(btnCard);
        Fragment contactsFragment = NotesFragment.newInstance(card.getCardId());
        replaceFragment(contactsFragment);
    }

    private void deselectButton(Button tabButton) {
        tabButton.setSelected(false);
        tabButton.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
    }

    private void setSelected(Button tabButton) {
        tabButton.setSelected(true);
        tabButton.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        title.setText(getString(R.string.txt_card_detail));
    }

    private void setDefaultFragment(Fragment defaultFragment) {
        replaceFragment(defaultFragment);
    }

    private void replaceFragment(Fragment defaultFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_card_detail, defaultFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.txt_confirm))
                .setMessage(getString(R.string.txt_delete_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeCardFromDB(card.getCardId());
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void removeCardFromDB(int cardId) {
        DataControllerBusinessCard dataController = new DataControllerBusinessCard(mContext);
        dataController.open();
        dataController.deleteCard(cardId);
        dataController.close();
        Intent intent = new Intent(mContext, NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void displayReferDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_refer);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (window != null) {
            window
                    .setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        Button btnRefer = dialog.findViewById(R.id.btn_refer);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        final EditText edtToEmail = dialog.findViewById(R.id.edt_toEmailId);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!edtToEmail.getText().toString().trim().isEmpty()) {
                    new ReferCardTask(mContext, edtToEmail.getText().toString()).execute();
                } else {
                    //TODO : show toast msg
                }
            }
        });
        dialog.show();
    }

    private class ReferCardTask extends AsyncTask<Void, String, String> {

        private Context mContext;
        private String toEmail;

        public ReferCardTask(Context mContext, String email) {
            this.mContext = mContext;
            this.toEmail = email;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (NetworkHelper.hasNetworkAccess(mContext)) {
                BusinessCardWebservice webservice = BusinessCardWebservice
                        .retrofit.create(BusinessCardWebservice.class);
                try {
                    // Fetch cloudfront url
                    Call<GenericResponse> call = webservice.getCardUrl();
                    GenericResponse response = call.execute().body();
                    cardUrl = response.getMessage() + "/" +
                            appUserEmail + "/" + card.getFileName();

                    // Call refercard api
                    call = webservice.referCard(toEmail,
                            appUserEmail,
                            card.getFileName(),
                            card.getName(),
                            card.getEmailId(),
                            card.getOrganization(),
                            card.getPhoneNumber(), cardUrl);
                    response = call.execute().body();
                    if (response != null && response.getMessage() != null) {
                        return response.getMessage();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "handleActionRequestQuestion: " + e.getMessage());
                    return null;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "ReferCardTask: " + result);
            showMsg(result);
        }
    }

    private void showMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
