package com.avengers.businesscardapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.adapters.CardsAdapter;
import com.avengers.businesscardapp.db.DataControllerBusinessCard;
import com.avengers.businesscardapp.dto.Card;

import java.util.List;

/**
 * Card List Fragment
 */
public class CardListFragment extends Fragment {
    private static final String ARG_EMAILS_ID = "args_emailId";

    private String emailId;

    private RecyclerView recyclerCardList;
    private List<Card> cards;

    public CardListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param emailId String representing app user emailId
     * @return
     */
    public static CardListFragment newInstance(String emailId) {
        CardListFragment fragment = new CardListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAILS_ID, emailId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            emailId = getArguments().getString(ARG_EMAILS_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cards_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        cards = loadCardsFromDB();
        displayCards(cards);
    }

    private void displayCards(List<Card> cards) {
        CardsAdapter adpCards = new CardsAdapter(getActivity(), cards);
        recyclerCardList.setAdapter(adpCards);
    }

    private List<Card> loadCardsFromDB() {
        List<Card> cards;
        DataControllerBusinessCard dataController = new DataControllerBusinessCard(getActivity());
        dataController.open();
        cards = dataController.retrieveAllCardsInfo(emailId);
        dataController.close();
        return cards;
    }

    private void initView(View view) {
        recyclerCardList = view.findViewById(R.id.recycler_cards);
        recyclerCardList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}
