package com.avengers.businesscardapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avengers.businesscardapp.R;
import com.avengers.businesscardapp.dto.Card;
import com.avengers.businesscardapp.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {

    List<Card> cards;
    List<Card> cardsCopy;
    private Context context;
    private final OnItemClickListener listener;

    public CardsAdapter(Context context, List<Card> cards, OnItemClickListener listener) {
        this.cards = cards;
        this.cardsCopy = new ArrayList<>(cards);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View scoreItemView = inflater.inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(scoreItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        Card card = cards.get(i);
        cardViewHolder.initials.setText(Utility.getInstance().
                getInitials(card.getName()));
        cardViewHolder.name.setText(card.getName());
        cardViewHolder.organization.setText(card.getOrganization());
        cardViewHolder.bind(cards.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void filter(String text) {
        text = text.toLowerCase();
        cards.clear();
        if (text.isEmpty()) {
            cards.addAll(cardsCopy);
        } else {
            text = text.toLowerCase();
            for (Card card : cardsCopy) {
                if (card.getName().toLowerCase().contains(text)) {
                    cards.add(card);
                }
            }
        }
        notifyDataSetChanged();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        TextView initials;
        TextView name;
        TextView organization;

        CardViewHolder(@NonNull View cardItemView) {
            super(cardItemView);
            initials = cardItemView.findViewById(R.id.txt_initials);
            name = cardItemView.findViewById(R.id.txt_name);
            organization = cardItemView.findViewById(R.id.txt_org);
        }

        void bind(final Card card, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(card);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Card item);
    }
}
