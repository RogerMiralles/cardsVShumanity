package com.xokundevs.cardsvshumanity.actiPartida;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.cosasRecicler.CartaNegra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PartidaEscogeCartasActivity extends AppCompatActivity {

    private RecyclerView mCartasblancas;
    private LinearLayout mCartanegra;

    private ArrayList<String> cartasBlancas;
    private HashMap<Integer, Integer> orden;
    private int cuenta = 0;
    private CartaNegra cartaNegra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partida_escoge_cartas_activity);

        mCartanegra = findViewById(R.id.linear_layout_muestracartanegra_escogecarta);
        mCartasblancas = findViewById(R.id.rv_cartasblancas_escoger);
        orden = new LinkedHashMap<>();

        Intent intent = getIntent();

        String cartaNegraTexto = intent.getStringExtra("cartaNegraText");
        int cartaNegraEspacios = intent.getIntExtra("cartaNegraEspacios", -1);

        cartasBlancas = intent.getStringArrayListExtra("cartasBlancas");
        if(cartaNegraEspacios == -1 || cartaNegraTexto == null || cartasBlancas == null){
            setResult(RESULT_CANCELED);
            finish();
        }
        else{
            cartaNegra = new CartaNegra(cartaNegraTexto);
            cartaNegra.setNumEspacios(cartaNegraEspacios);

            View view = getLayoutInflater().inflate(R.layout.recicler_cartas_negras, mCartanegra);
            TextView textView = view.findViewById(R.id.etTextoCartasNViewHolder);
            textView.setText(cartaNegra.getNombre());

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mCartasblancas.setLayoutManager(linearLayoutManager);
            mCartasblancas.setAdapter(new CartaAdapter());
        }
    }

    public void clickedEnviarCartas(View v){
        int limite = cartaNegra.getNumEspacios();
        int[] ids = new int[orden.size()];
        Iterator<Map.Entry<Integer, Integer>> entryIterator = orden.entrySet().iterator();

        while(entryIterator.hasNext()){
            Map.Entry<Integer, Integer> entry = entryIterator.next();
            ids[entry.getValue()] = entry.getKey();
        }

        if(ids.length == limite) {
            Intent data = new Intent();
            data.putExtra("ids", ids);
            setResult(RESULT_OK, data);
            finish();
        }
        else if(ids.length < limite){
            String texto = getString(R.string.game_selectcards_noselection_f1) + limite +getString(R.string.game_selectcards_noselection_f2);
            Toast.makeText(this,texto, Toast.LENGTH_SHORT).show();
        }else{
            String texto = getString(R.string.game_selectcards_noselection_f1) + limite +getString(R.string.game_selectcards_noselection_f2);
            Toast.makeText(this,texto, Toast.LENGTH_SHORT).show();
        }
    }

    private class CartaAdapter extends RecyclerView.Adapter<CartaAdapter.ViewHolder>{

        @NonNull
        @Override
        public CartaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            ViewHolder viewHolder = new ViewHolder(getLayoutInflater().inflate(R.layout.recicler_cartas_blancas, viewGroup, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final CartaAdapter.ViewHolder viewHolder, int i) {
            viewHolder.textoCarta.setText(cartasBlancas.get(i));
            //viewHolder.itemView.setOnClickListener(this);


        }

        @Override
        public int getItemCount() {
            return cartasBlancas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private TextView textoCarta;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textoCarta = itemView.findViewById(R.id.etTextoCartasBViewHolder);
                textoCarta.setOnClickListener(this);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Integer integer = orden.get(getAdapterPosition());

                if(integer == null){
                    orden.put(getAdapterPosition(), cuenta);
                    cuenta++;
                    textoCarta.setTextColor(0xFFFFEF00);
                }
                else{
                    orden.remove(getAdapterPosition());
                    Iterator<Map.Entry<Integer, Integer>> iterator = orden.entrySet().iterator();
                    while(iterator.hasNext()){
                        Map.Entry<Integer, Integer> it = iterator.next();
                        if(it.getValue() > integer){
                            it.setValue(it.getValue()-1);
                        }
                    }
                    cuenta--;
                    textoCarta.setTextColor(0xFF000000);
                }

            }
        }
    }

}
