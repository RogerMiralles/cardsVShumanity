package com.xokundevs.cardsvshumanity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.cosasRecicler.DeckInfo;

import java.util.ArrayList;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

    private ArrayList<DeckInfo> deckInfoList;
    private LayoutInflater mInflater;
    private Context context;
    private OnDeckClickListener mListener;

    public DeckAdapter(@NonNull Context context, @NonNull ArrayList<DeckInfo> bar) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.deckInfoList = bar;
        if(context instanceof OnDeckClickListener){
            mListener = (OnDeckClickListener) context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement OnDeckClickListener");
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.adap, viewGroup, false);
        return new ViewHolder(mItemView);
    }

    @Override
    public int getItemCount() {
        return deckInfoList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String mCurrent = deckInfoList.get(i).getDeckName();
        viewHolder.texto.setText(mCurrent);
        if(!deckInfoList.get(i).isEditable()){
            viewHolder.editarBaraja.setEnabled(false);
            viewHolder.borrarBaraja.setEnabled(false);
        }
    }

    public Context getContext() {
        return context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView texto;
        private Button editarBaraja;
        private Button verBaraja;
        private Button borrarBaraja;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.etTextoViewHolder);
            editarBaraja = itemView.findViewById(R.id.btnEditarBaraja);
            editarBaraja.setOnClickListener(this);
            verBaraja = itemView.findViewById(R.id.btnVerBaraja);
            verBaraja.setOnClickListener(this);
            borrarBaraja = itemView.findViewById(R.id.btnBorrarBaraja);
            borrarBaraja.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnVerBaraja){
                mListener.onReadDeckClicked(deckInfoList.get(getAdapterPosition()));
            }
            else if(v.getId() == R.id.btnEditarBaraja){
                mListener.onEditDeckClicked(deckInfoList.get(getAdapterPosition()), getAdapterPosition());
            }
            else if(v.getId() == R.id.btnBorrarBaraja){
                mListener.onEraseDeckClicked(deckInfoList.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }

    public interface OnDeckClickListener {
        void onEditDeckClicked(DeckInfo deckInfo, int adapterPos);
        void onReadDeckClicked(DeckInfo deckInfo);
        void onEraseDeckClicked(DeckInfo deckInfo, int pos);
    }
}
