package com.example.cardsvshumanity.actiPartida;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.javaConCod.Connection;
import com.example.cardsvshumanity.javaConCod.GameController;

import java.util.ArrayList;

public class PrePartidaActivity extends AppCompatActivity {

    private ArrayList<Jugador> jugadores;
    private TextView mSalaNombre;
    private RecyclerView mReciclerView;
    private Button mEmpezar;
    private RAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_partida);

        mSalaNombre = findViewById(R.id.txtSala_prepartida);
        mReciclerView = findViewById(R.id.recView_prepartida);
        mEmpezar = findViewById(R.id.btnComenzar_prepartida);

        jugadores = new ArrayList<>();
        Intent intent = getIntent();
        ArrayList<String> emails = intent.getStringArrayListExtra("peopleEmail");
        ArrayList<String> nombres = intent.getStringArrayListExtra("peopleName");

        if(emails != null && nombres != null) {
            for (int i = 0; i < emails.size(); i++) {
                jugadores.add(new Jugador(emails.get(i), nombres.get(i)));
            }
        }
        jugadores.add(new Jugador(Connection.getEmail(), Connection.getName()));


        mAdapter = new RAdapter();

        mReciclerView.setLayoutManager(new LinearLayoutManager(this));
        mReciclerView.setAdapter(mAdapter);

        Log.d(PrePartidaActivity.class.getSimpleName(), Integer.toString(jugadores.size()));
        String nombreSala = intent.getStringExtra("salaName");
        if(nombreSala != null){
            mSalaNombre.setText(nombreSala);
        }

        boolean creator = intent.getBooleanExtra("creator", false);
        if(!creator){
            mEmpezar.setVisibility(View.GONE);
        }
        else{
            mEmpezar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    empezarPartida(v);
                }
            });
        }

        GameController.getInstance().waitUntilGameStarts(this, new GameController.ArgumentableRunnable() {
            @Override
            public void run() {
                switch (getOrder()){
                    case GameController.NEW_PLAYER:
                        Jugador j = (Jugador) getArgument();
                        jugadores.add(j);
                        mAdapter.notifyItemInserted(jugadores.size()-1);
                        break;
                    case GameController.EMPEZAR_PARTIDA:
                        Intent intent1 = new Intent(PrePartidaActivity.this, Partida.class);
                        ArrayList<String> emails = new ArrayList<>()
                                , noms = new ArrayList<>();

                        for(Jugador jugador : jugadores){
                            emails.add(jugador.getEmail());
                            noms.add(jugador.getNombre());
                        }
                        intent1.putStringArrayListExtra("peopleEmail", emails);
                        intent1.putStringArrayListExtra("peopleName", noms);
                        startActivity(intent1);
                        break;
                    case GameController.ERROR_NO_SUFFICIENT_PLAYERS:
                        Toast.makeText(PrePartidaActivity.this, R.string.not_enough_players, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public void empezarPartida(View v){
        GameController.getInstance().enviarComenzarPartida();
    }

    private class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder>{



        @NonNull
        @Override
        public RAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.viewholder_prepartida_rv_jugadores_layout, viewGroup, false);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GameController.getInstance().close();
    }
}
