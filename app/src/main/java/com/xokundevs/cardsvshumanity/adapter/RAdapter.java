package com.xokundevs.cardsvshumanity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.actiPartida.Jugador;

import java.util.ArrayList;

public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder>{

    ArrayList<Jugador> jugadores;
    Context context;

    public RAdapter(Context _context, ArrayList<Jugador> _jugadores){
        context = _context;
        jugadores = _jugadores;
    }

    @NonNull
    @Override
    public RAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.viewholder_prepartida_rv_jugadores_layout, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RAdapter.ViewHolder viewHolder, int i) {
        viewHolder.editText.setText(jugadores.get(i).getNombre());
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView editText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.viewholder_prepartida_rv_jugadores_muestrajugador);
        }
    }
}