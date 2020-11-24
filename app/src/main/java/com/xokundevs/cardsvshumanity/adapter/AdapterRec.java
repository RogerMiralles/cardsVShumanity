package com.xokundevs.cardsvshumanity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetGameDataItemOutput;

import java.util.ArrayList;

public class AdapterRec extends RecyclerView.Adapter<AdapterRec.ViewHolder> {

    private Context context;
    private OnAdapterRecItemClick mListener;
    private ArrayList<ServiceGetGameDataItemOutput> itemGameInfo;

    public AdapterRec(Context _context, ArrayList<ServiceGetGameDataItemOutput> _itemGameInfo){
        itemGameInfo = _itemGameInfo;
        context = _context;
        if(context instanceof OnAdapterRecItemClick){
            mListener = (OnAdapterRecItemClick) context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement the interface OnAdapterRecItemClick");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.rv_unirsepartida_viewholder_layout, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        ServiceGetGameDataItemOutput info = itemGameInfo.get(i);
        viewHolder.nombreSala.setText(info.getGameName());
        viewHolder.nombreCreador.setText(info.getOwnerUsername());
    }

    @Override
    public int getItemCount() {
        return itemGameInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView nombreSala, nombreCreador;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreCreador = itemView.findViewById(R.id.tv__vhcreador_partida);
            nombreSala = itemView.findViewById(R.id.tv_namePartida_vh_unirsePartida);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(itemGameInfo.get(getAdapterPosition()));
        }
    }

    public interface OnAdapterRecItemClick {
        void onItemClick(ServiceGetGameDataItemOutput info);
    }
}
