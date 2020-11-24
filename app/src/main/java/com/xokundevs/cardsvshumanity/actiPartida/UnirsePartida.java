package com.xokundevs.cardsvshumanity.actiPartida;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.adapter.AdapterRec;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.javaConCod.GameController;
import com.xokundevs.cardsvshumanity.presenter.JoinLobbyPresenter;
import com.xokundevs.cardsvshumanity.presenter.impl.JoinLobbyPresenterImpl;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceJoinGameInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetGameDataItemOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetGameDataOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceJoinGameOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterActivity;

import java.util.ArrayList;

public class UnirsePartida extends BasePresenterActivity<JoinLobbyPresenter> implements JoinLobbyPresenter.View, AdapterRec.OnAdapterRecItemClick {

    private ArrayList<ServiceGetGameDataItemOutput> infoPartidas;
    private RecyclerView mRecyclerView;
    private AdapterRec mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unirse_partida);
        setTitle(getString(R.string.unirse_partida));
        setPresenter(new JoinLobbyPresenterImpl(this));

        infoPartidas = new ArrayList<>();

        bindViews();
        setUpViews();

        getPresenter().getAvailableLobbies();
    }

    private void bindViews(){
        mRecyclerView = findViewById(R.id.rv_unirse_partida);
    }

    private void setUpViews(){
        mAdapter = new AdapterRec(this, infoPartidas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onGetAvailableLobbiesSuccess(ServiceGetGameDataOutput output) {
        infoPartidas.clear();
        infoPartidas.addAll(output.getGameDataList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetAvailableLobbiesError(int error) {
        switch (error){
            case Connection.UNKNOWN_ERROR:
                break;
            case Connection.SOCKET_DISCONNECTED:
                break;
        }
    }

    @Override
    public void onJoinLobbySuccess(ServiceJoinGameOutput output) {
        Intent intent = new Intent(getBaseContext(), PrePartidaActivity.class);
        ArrayList<String> emails = new ArrayList<>()
                , noms = new ArrayList<>();

        for(Jugador j : output.getJugadors()){
            emails.add(j.getEmail());
            noms.add(j.getNombre());
        }

        intent.putParcelableArrayListExtra(PrePartidaActivity.PLAYERS_INTENT_ARG,output.getJugadors());
        intent.putExtra(PrePartidaActivity.CREATOR_CHECK_ARG, false);
        intent.putExtra(PrePartidaActivity.LOBBY_NAME_ARG, output.getLobbyName());
        GameController.GenerateGameController(output.getSocketHandler(), output.getSecretKey());
        startActivity(intent);
    }

    @Override
    public void onJoinLobbyError(int error) {
        AlertDialog.Builder erroresbuild = new AlertDialog.Builder(UnirsePartida.this);
        erroresbuild.setPositiveButton(R.string.ok,null);
        switch (error){
            case Connection.INVALID_CREDENTIALS_ERROR:
                erroresbuild.setMessage(R.string.invalidPassword);
                break;
            case Connection.USER_ERROR_NON_EXISTANT_USER:
                erroresbuild.setMessage(R.string.error_usuario_no_existe);
                break;
            case Connection.PARTIDA_ERROR_NO_ENTRAR_DENIED:
                erroresbuild.setMessage(R.string.error_partida_no_existe);
                break;
            case Connection.PARTIDA_ERROR_NON_EXISTANT_PARTIDA:
                erroresbuild.setMessage(R.string.error_partida_no_existe);
                break;
        }
        erroresbuild.show();
    }


    private void CreaAlertDialog(final String nombreCreador, final String nombreSala){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(getBaseContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        editText.setHint(R.string.contrase_a);
        builder.setView(editText);
        DialogInterface.OnClickListener onCListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == Dialog.BUTTON_POSITIVE) {
                    getPresenter().joinLobby(new ServiceJoinGameInput(nombreSala, nombreCreador));
                }else if(which == Dialog.BUTTON_NEGATIVE){
                    dialog.dismiss();
                }
            }
        };
        builder.setPositiveButton(R.string.ok, onCListener);
        builder.setNegativeButton(R.string.cancel, onCListener);
        builder.create().show();
    }

    @Override
    public void onItemClick(ServiceGetGameDataItemOutput info) {
        CreaAlertDialog(info.getOwnerUsername(), info.getGameName());
    }
}
