package com.example.cardsvshumanity.actiPartida;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardsvshumanity.MainActivity;
import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.cosasRecicler.CartaBlanca;
import com.example.cardsvshumanity.cosasRecicler.CartaNegra;
import com.example.cardsvshumanity.javaConCod.Connection;
import com.example.cardsvshumanity.javaConCod.GameController;

import java.util.ArrayList;

public class Partida extends AppCompatActivity {

    private static int ENVIA_CARTAS = 1;
    RecyclerView mRecyclerView_jugadores, mRecyclerView_cartas;
    LinearLayout mCartaNegraMostrar;

    CartaNegra cartaNegraActual;
    ArrayList<Jugador> jugadores;
    ArrayList<CartaBlanca> cartasEnMano;
    ArrayList<Boolean> isCartaSelected;
    ArrayList<ArrayList<CartaBlanca>> cartasEscogidas;
    boolean eresCzar = false;

    RAdapter mAdapterPlayers;
    CartaAdapter mAdapterCartas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partida);

        jugadores = new ArrayList<>();
        cartasEnMano = new ArrayList<>();
        cartasEscogidas = new ArrayList<>();
        cartaNegraActual = new CartaNegra("");

        mRecyclerView_jugadores = findViewById(R.id.rv_jugadores_partida);
        mRecyclerView_cartas = findViewById(R.id.rv_cartas_escoger_winner);
        mCartaNegraMostrar = findViewById(R.id.linearlayout_mostrarcartanegra_partida);

        mAdapterPlayers = new RAdapter();
        mAdapterCartas = new CartaAdapter();

        mRecyclerView_jugadores.setAdapter(mAdapterPlayers);
        mRecyclerView_jugadores.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView_cartas.setLayoutManager(llm);
        mRecyclerView_cartas.setAdapter(mAdapterCartas);
        Intent intent = getIntent();
        if(intent != null) {
            ArrayList<String> emails = intent.getStringArrayListExtra("peopleEmail");
            ArrayList<String> nombres = intent.getStringArrayListExtra("peopleName");

            if (emails != null && nombres != null) {
                for (int i = 0; i < emails.size(); i++) {
                    jugadores.add(new Jugador(emails.get(i), nombres.get(i)));
                }
                mAdapterPlayers.notifyDataSetChanged();
            }
        }

        GameController.getInstance().StartListeningGame(this, new GameController.ArgumentableRunnable() {
            @Override
            public void run() {
                switch (getOrder()){
                    case GameController.ERROR_PLAYER_DISCONNECTED:
                        String argument = (String) getArgument();
                        for(int i = 0; i< jugadores.size(); i++){
                            Jugador j = jugadores.get(i);
                            if(j.getEmail().equals(argument)){
                                jugadores.remove(i);
                                mAdapterPlayers.notifyItemRemoved(i);
                                String texto = j.getNombre()+ " se ha desconectado";
                                Toast.makeText(getBaseContext(), texto, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        break;
                    case GameController.ESCOGER_TZAR:
                        String tzarEmail = (String) getArgument();
                        for(int i = 0; i< jugadores.size(); i++){
                            Jugador j = jugadores.get(i);
                            TextView v = (TextView) mRecyclerView_jugadores.getChildAt(i);
                            if(j.getEmail().equals(tzarEmail)){
                                v.setTextColor(getColor(R.color.colorPrimary));
                                if(Connection.getEmail().equals(tzarEmail)){
                                    Toast.makeText(getBaseContext(), "You are the tzar", Toast.LENGTH_SHORT).show();
                                    eresCzar = true;
                                }
                                else {
                                    Toast.makeText(getBaseContext(), "Tzar: " + j.getNombre(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                v.setTextColor(0xFF000000);
                            }
                        }
                        break;

                    case GameController.REPARTIR_CARTAS:
                        cartasEnMano = (ArrayList<CartaBlanca>) getArgument();
                        isCartaSelected = new ArrayList<>();
                        for(int i = 0; i<cartasEnMano.size(); i++){
                            isCartaSelected.add(false);
                        }
                        mAdapterCartas.notifyDataSetChanged();
                        break;

                    case GameController.MUESTRA_CARTA_NEGRA:
                        cartaNegraActual = (CartaNegra) getArgument();
                        Log.d(Partida.class.getSimpleName(), cartaNegraActual.getNombre());
                        mCartaNegraMostrar.removeAllViews();
                        View v = getLayoutInflater().inflate(R.layout.recicler_cartas_negras, mCartaNegraMostrar, true);
                        ((TextView)v.findViewById(R.id.etTextoCartasNViewHolder)).setText(cartaNegraActual.getNombre());
                        break;

                    case GameController.ESCOGER_CARTAS:
                        if(!eresCzar) {
                            Intent intent1 = new Intent(Partida.this, PartidaEscogeCartasActivity.class);
                            intent1.putExtra("cartaNegraText", cartaNegraActual.getNombre());
                            intent1.putExtra("cartaNegraEspacios", cartaNegraActual.getNumEspacios());
                            ArrayList<String> cartastexto = new ArrayList<>();
                            for (CartaBlanca c : cartasEnMano) {
                                cartastexto.add(c.getNombre());
                            }
                            intent1.putStringArrayListExtra("cartasBlancas", cartastexto);
                            startActivityForResult(intent1, ENVIA_CARTAS);
                        }
                        break;
                    case GameController.TZAR_ESCOGE_GANADOR:
                        cartasEscogidas.addAll((ArrayList<ArrayList<CartaBlanca>>) getArgument());
                        mAdapterCartas.notifyDataSetChanged();
                        break;
                    case GameController.TZAR_YA_HA_ESCOGIDO_GANADOR:
                        cartasEscogidas.clear();
                        mAdapterCartas.notifyDataSetChanged();
                        Object[] objects = (Object[]) getArgument();
                        String email = (String) objects[0];
                        int puntos = (int) objects[1];
                        for(Jugador j : jugadores){
                            if(j.getEmail().equals(email)){
                                j.setPuntos(puntos);
                                if(Connection.getEmail().equals(j.getEmail())){
                                    Toast.makeText(Partida.this, R.string.game_you_win_message, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Partida.this, getString(R.string.game_other_win_message) + j.getNombre(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }
                        break;
                    case GameController.YA_HAY_GANADOR:
                        String email2 = (String) getArgument();
                        String nombre = "null";
                        for(Jugador j : jugadores){
                            if(j.getEmail().equals(email2)){
                                if(Connection.getEmail().equals(email2)){
                                    nombre = getString(R.string.game_winner_you_final_message);
                                }
                                else{
                                    nombre = j.getNombre();
                                }
                                break;
                            }
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(Partida.this);
                        builder.setMessage(getString(R.string.game_winner_name)+nombre);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                                .show();
                        break;
                    case GameController.CERRAR_PARTIDA:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Partida.this);
                        builder1.setMessage(R.string.game_canceled);
                        builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                                .show();
                        break;
                    case Connection.SOCKET_DISCONNECTED:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(Partida.this);
                        builder2.setMessage(R.string.noConexion);
                        builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                                .show();
                        break;
                }
                synchronized (GameController.class){
                    GameController.class.notify();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        GameController.getInstance().close();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ENVIA_CARTAS){
            if(resultCode == RESULT_OK && data != null){
                int[] ids = data.getIntArrayExtra("ids");
                GameController.getInstance().enviarCartasEscogidas(ids);
            }
            else{
                GameController.getInstance().close();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    private class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder>{

        @NonNull
        @Override
        public RAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new RAdapter.ViewHolder(new TextView(getBaseContext()));
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
                editText = (TextView) itemView;
            }
        }
    }

    private class CartaAdapter extends RecyclerView.Adapter<CartaAdapter.ViewHolder>{

        @NonNull
        @Override
        public CartaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LinearLayout v = (LinearLayout) getLayoutInflater().inflate(R.layout.recicler_chosen_cards_partida, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final CartaAdapter.ViewHolder viewHolder, int i) {
            LinearLayout linearLayout = viewHolder.linearLayout;
            int cantidad_hijos = cartasEscogidas.get(i).size();
            linearLayout.removeAllViews();
            for(int j = 0; j < cantidad_hijos; j++){
                View v = getLayoutInflater().inflate(R.layout.recicler_cartas_blancas, linearLayout, false);
                TextView textView = v.findViewById(R.id.etTextoCartasBViewHolder);
                textView.setText(cartasEscogidas.get(i).get(j).getNombre());
                linearLayout.addView(v);
            }
            if(eresCzar) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(eresCzar){
                            eresCzar = false;
                            GameController.getInstance().enviarGanador(viewHolder.getAdapterPosition());
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return cartasEscogidas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            LinearLayout linearLayout;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                linearLayout = itemView.findViewById(R.id.ll_recicler_chosen_cards_partida);
            }
        }
    }
}
