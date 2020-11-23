package com.xokundevs.cardsvshumanity.adapter.editdeckcardadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCardWhiteInfoOutput;

import java.util.ArrayList;

public class EditWhiteDeckCardAdapter extends RecyclerView.Adapter<EditWhiteDeckCardAdapter.ViewHolder> {

    private ArrayList<ServiceCardWhiteInfoOutput> cardData;
    private LayoutInflater mInflater;
    private OnClickCardListener mListener;

    public EditWhiteDeckCardAdapter(Context context, ArrayList<ServiceCardWhiteInfoOutput> _cardTextList) {
        mInflater = LayoutInflater.from(context);
        this.cardData = _cardTextList;
        if(context instanceof OnClickCardListener){
            mListener = (OnClickCardListener) context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement OnClickCardListener");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recicler_cartas_negras, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(cardData.get(position).getNombre());
    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }

    public ArrayList<ServiceCardWhiteInfoOutput> getCardData() {
        return cardData;
    }

    public void setCardData(ArrayList<ServiceCardWhiteInfoOutput> cardData) {
        this.cardData = cardData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.findViewById(R.id.vh_editcard_bc_container).setOnClickListener(this);
            textView = itemView.findViewById(R.id.vh_editcard_bc_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //mostrarCartaNegraSeleccionada(carta, getAdapterPosition());
            mListener.onWhiteCardClicked(getAdapterPosition());
        }
    }

    public interface OnClickCardListener {
        void onWhiteCardClicked(int pos);
    }
}
