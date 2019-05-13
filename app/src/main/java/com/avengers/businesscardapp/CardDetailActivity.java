package com.avengers.businesscardapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avengers.businesscardapp.db.DataControllerBusinessCard;
import com.avengers.businesscardapp.dto.Card;
import com.avengers.businesscardapp.fragment.CardFragment;
import com.avengers.businesscardapp.fragment.ContactsFragment;
import com.avengers.businesscardapp.fragment.NotesFragment;
import com.avengers.businesscardapp.util.Constants;

public class CardDetailActivity extends AppCompatActivity implements
        View.OnClickListener, ContactsFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;
    private TextView title;
    private Context mContext;
    private Button btnContacts;
    private Button btnCard;
    private Button btnNotes;
    private TextView txtName;
    private TextView txtOrganization;

    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        mContext = CardDetailActivity.this;
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
            default:
                return false;
        }
        return false;
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
        Fragment cardFragment = CardFragment.newInstance(card.getFileName());
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
