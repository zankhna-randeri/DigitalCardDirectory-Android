package com.avengers.businesscardapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avengers.businesscardapp.CardDetailActivity;
import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.adapters.CardsAdapter;
import com.avengers.businesscardapp.db.DataControllerBusinessCard;
import com.avengers.businesscardapp.dto.Card;
import com.avengers.businesscardapp.util.Constants;

import java.util.List;

/**
 * Card List Fragment
 */
public class CardListFragment extends Fragment {
    private static final String ARG_EMAILS_ID = "args_emailId";
    private final String TAG = "CardListFragment";
    private String emailId;

    private RecyclerView recyclerCardList;
    private List<Card> cards;
    private EditText edtSearch;
    private TextView emptyView;
    private RelativeLayout contentView;
    private CardsAdapter adpCards;

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
        if (cards != null && cards.size() > 0) {
            emptyView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            displayCards(cards);
        } else {
            contentView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void displayCards(final List<Card> cards) {
        adpCards = new CardsAdapter(getActivity(), cards, new CardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Card item) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra(Constants.EXTRA_CARD_DETAIL, item);
                startActivity(intent);
            }
        });
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
        emptyView = view.findViewById(R.id.txt_empty);
        contentView = view.findViewById(R.id.lyt_cards);
        edtSearch = view.findViewById(R.id.edt_search);
        recyclerCardList = view.findViewById(R.id.recycler_cards);
        recyclerCardList.setLayoutManager(new LinearLayoutManager(getActivity()));
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adpCards.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
