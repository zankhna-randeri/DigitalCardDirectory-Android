package com.avengers.businesscardapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.dto.Card;
import com.avengers.businesscardapp.util.NetworkHelper;
import com.avengers.businesscardapp.webservice.BusinessCardWebservice;
import com.avengers.businesscardapp.webservice.GenericResponse;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

/**
 * Card Fragment
 */
public class CardFragment extends Fragment {

    private final String TAG = "CardFragment";
    // the fragment initialization parameters
    private static final String ARG_CARD_NAME = "arg_card_name";

    private ImageView imgCard;
    private String emailId;
    private String cardName;
    private OnFragmentInteractionListener mListener;

    public CardFragment() {
        // Required empty public constructor
    }

    //    public static CardFragment newInstance(String cardName) {
//        CardFragment fragment = new CardFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_CARD_NAME, cardName);
//        fragment.setArguments(args);
//        return fragment;
//    }
    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardName = getArguments().getParcelable(ARG_CARD_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgCard = view.findViewById(R.id.img_card);
        // Get email
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        emailId = sharedPrefs.getString("Email_Id", "");
        String prefix = fetchDownloadPrefix();
//        String url = prefix + emailId + cardName;
//        loadImageFromUrl(url);
        loadImageFromUrl(prefix);
    }

    private String fetchDownloadPrefix() {
        if (NetworkHelper.hasNetworkAccess(getActivity().getApplicationContext())) {
            BusinessCardWebservice webservice = BusinessCardWebservice
                    .retrofit.create(BusinessCardWebservice.class);
            Call<GenericResponse> call = webservice.getCardUrl();
            try {
                GenericResponse response = call.execute().body();
                if (response.getMessage() != null) {
                    return response.getMessage();
                } else {
                    //TODO : Handle other response codes
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "handleActionRequestQuestion: " + e.getMessage());
                return null;
            }

        }
        return null;
    }


    private void loadImageFromUrl(String url) {
        Picasso.get()
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .into(imgCard);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
