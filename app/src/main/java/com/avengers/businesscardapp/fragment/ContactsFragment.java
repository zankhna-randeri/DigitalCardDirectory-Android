package com.avengers.businesscardapp.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.dto.Card;

import java.util.List;

/**
 * Contacts Fragment
 */
public class ContactsFragment extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters
    private static final String ARG_CONTACTS = "arg_contacts";
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    private TextView txtPhone;
    private TextView txtEmail;
    private ImageButton btnPhone;
    private ImageButton btnMail;

    private Card card;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * create a new instance of this fragment using the provided parameters.
     *
     * @param card Card detail parameter
     * @return A new instance of fragment ContactsFragment.
     */
    public static ContactsFragment newInstance(Card card) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONTACTS, card);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            card = getArguments().getParcelable(ARG_CONTACTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtPhone = view.findViewById(R.id.txt_phone_no);
        txtEmail = view.findViewById(R.id.txt_email);
        btnMail = view.findViewById(R.id.btn_send_mail);
        btnPhone = view.findViewById(R.id.btn_call);
        if (card != null) {
            setContactDetail();
        }
        btnMail.setOnClickListener(this);
        btnPhone.setOnClickListener(this);

    }

    private void setContactDetail() {
        txtPhone.setText(card.getPhoneNumber());
        txtEmail.setText(card.getEmailId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call:
                makeCall();
                break;
            case R.id.btn_send_mail:
                sendMail();
                break;
        }
    }

    private void sendMail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{txtEmail.getText().toString()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Business Card Mail");
        intent.putExtra(Intent.EXTRA_TEXT, "Type your mail here");
        try {
            startActivity(Intent.createChooser(intent, "Send mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeCall() {
        if (!card.getPhoneNumber().isEmpty()) {
            Log.d("Phone no :", card.getPhoneNumber());
            Uri number = Uri.parse("tel:" + card.getPhoneNumber());
            Intent callIntent = new Intent(Intent.ACTION_CALL, number);
            if (isPackageExist(callIntent)) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this.getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    startActivity(callIntent);
                }
            } else {
                showErrorMsg(getResources().getString(R.string.txt_err_call));
            }
        } else {
            showErrorMsg(getResources().getString(R.string.txt_err_empty_call));
        }
    }

    private boolean isPackageExist(Intent intent) {
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        return resolveInfos.size() > 0;
    }

    private void showErrorMsg(String msg) {
        Toast.makeText(getActivity(), msg,
                Toast.LENGTH_SHORT).show();
    }
}
