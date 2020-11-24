package com.xokundevs.cardsvshumanity.actiPartida;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.adapter.AdaptadorMazos;
import com.xokundevs.cardsvshumanity.adapter.items.AdaptadorMazosItem;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.javaConCod.GameController;
import com.xokundevs.cardsvshumanity.presenter.CreateGamePresenter;
import com.xokundevs.cardsvshumanity.presenter.impl.CreateGamePresenterImpl;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateGameDeckDataInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateGameInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCreateGameOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoListOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterActivity;

import java.util.ArrayList;

public class CrearPartida extends BasePresenterActivity<CreateGamePresenter> implements CreateGamePresenter.View {

    private ArrayList<AdaptadorMazosItem> listDeckCards;
    private LinearLayout linearLayout_listaBarajas;
    private EditText mNombrePartida, mContraPartida;
    private TextView seekBarText;
    private SeekBar limiteJugadores;
    private AdaptadorMazos adaptadorMazos;
    private int maxPlayers;
    private RecyclerView reciclerMazos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_partida);
        setTitle(getString(R.string.crear_partida));
        setPresenter(new CreateGamePresenterImpl(this));

        //linearLayout_listaBarajas = findViewById(R.id.scr_layout_escogerBaraja_crearPartida);
        mNombrePartida = findViewById(R.id.et_nombrePartida_crearPartida);
        mContraPartida = findViewById(R.id.et_contraPartida_crearPartida);
        limiteJugadores = findViewById(R.id.skBar_maximumPlayer_crearPartida);
        seekBarText = findViewById(R.id.tv_seekbarCount_crearPartida);

        String nGame = Connection.getName().concat("'s Game");
        mNombrePartida.setText(nGame);

        maxPlayers = 3;
        seekBarText.setText(Integer.toString(maxPlayers));
        limiteJugadores.setMax(5);
        limiteJugadores.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    maxPlayers = 3 + progress;
                    seekBarText.setText(Integer.toString(maxPlayers));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        listDeckCards = new ArrayList<>();

        reciclerMazos = findViewById(R.id.reciclerMazo);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(CrearPartida.this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        reciclerMazos.setLayoutManager(layoutManager);
        adaptadorMazos = new AdaptadorMazos(this, listDeckCards);
        reciclerMazos.setAdapter(adaptadorMazos);

        getPresenter().getSimpleDeck();

    }

    public void creaPartida(View v) {
        final String nomPartida = mNombrePartida.getText().toString();
        String cGame = mContraPartida.getText().toString();
        boolean noValid = false;
        if (nomPartida.trim().isEmpty() || nomPartida.length() > 20) {
            mNombrePartida.setText("");
            noValid = true;
        }
        if (mContraPartida.length() > 20) {
            mContraPartida.setText("");
            noValid = true;
        }


        if (!noValid) {
            ArrayList<ServiceCreateGameDeckDataInput> deckDataInputArrayList = new ArrayList<>();
            for(AdaptadorMazosItem item : listDeckCards){
                if(item.isChecked()){
                    deckDataInputArrayList.add(new ServiceCreateGameDeckDataInput(item.getDeckName(),item.getDeckEmail()));
                }
            }

            if (!deckDataInputArrayList.isEmpty()) {
                ServiceCreateGameInput serviceCreateGameInput = new ServiceCreateGameInput();
                serviceCreateGameInput.setGameName(nomPartida);
                serviceCreateGameInput.setGamePass(cGame);
                serviceCreateGameInput.setMaxPlayers(maxPlayers);
                serviceCreateGameInput.setDeckData(deckDataInputArrayList);

                getPresenter().createGame(serviceCreateGameInput);
            } else {
                Toast.makeText(this, R.string.camposVacios, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "NO VALID", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetSimpleDeckSuccess(ServiceSimpleDeckInfoListOutput receivedData) {
        listDeckCards.clear();
        for(ServiceSimpleDeckInfoOutput deckInfo : receivedData.getListBarajas()){
            listDeckCards.add(new AdaptadorMazosItem(deckInfo.getDeckEmail(), deckInfo.getDeckName(), false));
        }

        adaptadorMazos.notifyDataSetChanged();
    }

    @Override
    public void onGetSimpleDeckError(int error) {
        switch (error){
            case Connection.SOCKET_DISCONNECTED:
                break;
            case Connection.USER_NOT_LOGINED:
                break;
            case Connection.UNKNOWN_ERROR:
                break;
            case Connection.USER_ERROR_NON_EXISTANT_USER:
                break;
            case Connection.INVALID_CREDENTIALS_ERROR:
                break;
        }
    }

    @Override
    public void onCreateGameSuccess(ServiceCreateGameOutput output) {
        Intent intent = new Intent(CrearPartida.this, PrePartidaActivity.class);
        GameController.GenerateGameController(output.getSocketHandler(), output.getSecretKey());
        intent.putExtra(PrePartidaActivity.LOBBY_NAME_ARG, mNombrePartida.getText().toString());
        intent.putExtra(PrePartidaActivity.CREATOR_CHECK_ARG, true);
        startActivity(intent);
    }

    @Override
    public void onCreateGameError(int error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);
        builder.setMessage(Integer.toString(error));
        builder.show();
    }
}