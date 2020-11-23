package com.xokundevs.cardsvshumanity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.adapter.items.AdaptadorMazosItem;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoOutput;

import java.util.ArrayList;

public class AdaptadorMazos extends RecyclerView.Adapter<AdaptadorMazos.ViewHolder> {
    private ArrayList<AdaptadorMazosItem> deckCards;
    private LayoutInflater mInflater;

    public AdaptadorMazos(Context context, ArrayList<AdaptadorMazosItem> bar) {
        mInflater = LayoutInflater.from(context);
        this.deckCards = bar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(
                R.layout.recicler_mazos, viewGroup, false);
        return new AdaptadorMazos.ViewHolder(mItemView);
    }

    @Override
    public int getItemCount() {
        return deckCards.size();
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorMazos.ViewHolder viewHolder, int i) {
        String mCurrent = deckCards.get(i).getDeckName();
        viewHolder.texto.setText(mCurrent);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckBox texto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.cBMazo);
            texto.setOnClickListener(this);
        }

        public void onClick(View view) {
            if(view.getId() == texto.getId()){
                deckCards.get(getAdapterPosition()).setChecked(texto.isChecked());
            }
        }
    }
}