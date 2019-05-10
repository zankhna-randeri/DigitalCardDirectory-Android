package com.avengers.businesscardapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.db.DataControllerBusinessCard;

import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CARD_ID = "arg_cardId";
    private int cardId;
    private String appUserEmailId;

    private EditText edtNotes;
    private Button btnUpdate;
    private OnFragmentInteractionListener mListener;

    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Creates NotesFragment
     *
     * @param cardId Current cardId
     * @return A new instance of fragment NotesFragment.
     */
    public static NotesFragment newInstance(int cardId) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CARD_ID, cardId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardId = getArguments().getInt(ARG_CARD_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtNotes = view.findViewById(R.id.edt_notes);
        btnUpdate = view.findViewById(R.id.btn_update_notes);

        // Get email
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        appUserEmailId = sharedPrefs.getString("Email_Id", "");

        btnUpdate.setOnClickListener(this);
        edtNotes.setText(fetchNoteFromDb(cardId));
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update_notes) {
            if (edtNotes.getText().toString() != null &&
                    !edtNotes.getText().toString().trim().isEmpty()) {
                updateNotesInDb(edtNotes.getText().toString());
            } else {
                showErrorMsg(getString(R.string.txt_err_notes));
            }
        }
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

    private void showErrorMsg(String msg) {
        Toast.makeText(getActivity(), msg,
                Toast.LENGTH_SHORT).show();

    }

    private void updateNotesInDb(String notes) {
        DataControllerBusinessCard dataController = new DataControllerBusinessCard(getActivity());
        dataController.open();
        dataController.updateNotes(cardId, notes);
        dataController.close();
    }

    private String fetchNoteFromDb(int cardId) {
        DataControllerBusinessCard dataController = new DataControllerBusinessCard(getActivity());
        dataController.open();
        String notes = dataController.retrieveNotes(cardId);
        dataController.close();
        return notes;
    }
}