package com.xokundevs.cardsvshumanity.actiPartida;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.adapter.RAdapter;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.javaConCod.GameController;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseActivity;

import java.util.ArrayList;

public class PrePartidaActivity extends BaseActivity {

    public static final String PLAYERS_INTENT_ARG = "jugadores_arraylist";
    public static final String LOBBY_NAME_ARG = "lobby_name";
    public static final String CREATOR_CHECK_ARG = "creator_check";


    private ArrayList<Jugador> jugadores;
    private String lobbyName;
    private boolean isCreator;

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
        mAdapter = new RAdapter(this, jugadores);

        Intent intent = getIntent();

        getArguments();

        mReciclerView.setLayoutManager(new LinearLayoutManager(this));
        mReciclerView.setAdapter(mAdapter);

        if(lobbyName != null){
            mSalaNombre.setText(lobbyName);
        }

        if(!isCreator){
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

    private void getArguments(){
        jugadores = getIntent().getParcelableArrayListExtra(PLAYERS_INTENT_ARG);
        lobbyName = getIntent().getStringExtra(LOBBY_NAME_ARG);
        isCreator = getIntent().getBooleanExtra(CREATOR_CHECK_ARG, false);
    }

    public void empezarPartida(View v){
        GameController.getInstance().enviarComenzarPartida();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GameController.getInstance().close();
    }
}
