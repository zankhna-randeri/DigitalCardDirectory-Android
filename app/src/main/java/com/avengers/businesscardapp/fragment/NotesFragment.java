package com.avengers.businesscardapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.db.DataControllerBusinessCard;
import com.avengers.businesscardapp.util.Utility;

/**
 * Card Notes Fragment
 */
public class NotesFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CARD_ID = "arg_cardId";
    private int cardId;

    private EditText edtNotes;
    private Button btnUpdate;

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
        btnUpdate.setOnClickListener(this);
        edtNotes.setText(fetchNoteFromDb(cardId));
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update_notes) {
            if (!TextUtils.isEmpty(edtNotes.getText())) {
                int result = updateNotesInDb(edtNotes.getText().toString());
                if (result >= 0) {
                    Utility.getInstance().showMsg(getActivity().getApplicationContext(),
                            getString(R.string.txt_notes_success));
                }
            } else {
                Utility.getInstance().showMsg(getActivity().getApplicationContext(),
                        getString(R.string.txt_err_notes));
            }
        }
    }

    private int updateNotesInDb(String notes) {
        DataControllerBusinessCard dataController = new DataControllerBusinessCard(getActivity());
        dataController.open();
        int result = dataController.updateNotes(cardId, notes);
        dataController.close();
        return result;
    }

    private String fetchNoteFromDb(int cardId) {
        DataControllerBusinessCard dataController = new DataControllerBusinessCard(getActivity());
        dataController.open();
        String notes = dataController.retrieveNotes(cardId);
        dataController.close();
        return notes;
    }
}
