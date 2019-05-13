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

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {

    List<Card> cards;
    private Context context;


    public CardsAdapter(Context context, List<Card> cards) {
        this.cards = cards;
        this.context = context;
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
        cardViewHolder.initials.setText(getInitials(card.getName()));
        cardViewHolder.name.setText(card.getName());
        cardViewHolder.organization.setText(card.getOrganization());
    }

    private String getInitials(String name) {
        String[] names = name.split(" ");
        if (names.length > 0) {
            String initials = (names.length > 1) ? "" + names[0].charAt(0) + names[1].charAt(0) :
                    "" + names[0].charAt(0);
            return initials;
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView initials;
        TextView name;
        TextView organization;

        public CardViewHolder(@NonNull View cardItemView) {
            super(cardItemView);
            initials = cardItemView.findViewById(R.id.txt_initials);
            name = cardItemView.findViewById(R.id.txt_name);
            organization = cardItemView.findViewById(R.id.txt_org);
        }
    }
}
