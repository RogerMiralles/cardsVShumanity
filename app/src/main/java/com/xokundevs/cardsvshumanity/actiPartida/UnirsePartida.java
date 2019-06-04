package com.xokundevs.cardsvshumanity.actiPartida;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.javaConCod.GameController;
import com.xokundevs.cardsvshumanity.javaConCod.SocketHandler;

import java.util.ArrayList;

import javax.crypto.SecretKey;

public class UnirsePartida extends AppCompatActivity {

    private ArrayList<String[]> infoPartidas;
    private RecyclerView mRecyclerView;
    private AdapterRec mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unirse_partida);
        setTitle(getString(R.string.unirse_partida));

        mRecyclerView = findViewById(R.id.rv_unirse_partida);
        mAdapter = new AdapterRec();

        infoPartidas = new ArrayList<>();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.internet_dialog_cargando);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();

        Connection.ConnectionThread thread = Connection.CogerPartida(this);
        thread.setRunBegin(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
        thread.setRunOk(new Connection.ConnectionThread.SuccessRunnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                infoPartidas.clear();
                infoPartidas.addAll((ArrayList<String[]>)getArguments());
                mAdapter.notifyDataSetChanged();
                Toast.makeText(UnirsePartida.this, Integer.toString(infoPartidas.size()), Toast.LENGTH_SHORT).show();
            }
        });

        thread.setRunNo(new Connection.ConnectionThread.ErrorRunable() {
            @Override
            public void run() {
                builder.setMessage(R.string.error_unknown_error);
                builder.setPositiveButton(R.string.ok, null);
                switch (getError()){
                    case Connection.UNKOWN_ERROR:
                        break;
                    case Connection.SOCKET_DISCONNECTED:
                        break;
                }
                builder.show();
                alertDialog.dismiss();

            }
        });
        thread.start();
    }

    public class AdapterRec extends RecyclerView.Adapter<AdapterRec.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.rv_unirsepartida_viewholder_layout, mRecyclerView, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            String[] info = infoPartidas.get(i);
            viewHolder.nombreSala.setText(info[0]);
            viewHolder.nombreCreador.setText(info[1]);
        }

        @Override
        public int getItemCount() {
            return infoPartidas.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private TextView nombreSala, nombreCreador;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nombreCreador = itemView.findViewById(R.id.tv__vhcreador_partida);
                nombreSala = itemView.findViewById(R.id.tv_namePartida_vh_unirsePartida);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                CreaAlertDialog(nombreCreador.getText().toString(), nombreSala.getText().toString());
            }
        }
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
                    Connection.ConnectionThread connectionThread = Connection.unirsePartida(UnirsePartida.this, nombreSala, editText.getText().toString());
                    connectionThread.setRunOk(new Connection.ConnectionThread.SuccessRunnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getBaseContext(), PrePartidaActivity.class);
                            Object[] objects = (Object[]) getArguments();
                            ArrayList<Jugador> strings = (ArrayList<Jugador>) objects[0];
                            ArrayList<String> emails = new ArrayList<>()
                                    , noms = new ArrayList<>();

                            for(Jugador j : strings){
                                emails.add(j.getEmail());
                                noms.add(j.getNombre());
                            }
                            intent.putStringArrayListExtra("peopleEmail", emails);
                            intent.putStringArrayListExtra("peopleName", noms);
                            intent.putExtra("creator", false);
                            intent.putExtra("salaName", nombreSala);
                            GameController.GenerateGameController((SocketHandler) objects[1], (SecretKey) objects[2]);
                            startActivity(intent);
                        }
                    });

                    connectionThread.setRunNo(new Connection.ConnectionThread.ErrorRunable() {
                        @Override
                        public void run() {
                            switch (getError()){
                                case Connection.USER_ERROR_INVALID_PASSWORD:
                                case Connection.USER_ERROR_NON_EXISTANT_USER:
                                    break;
                                case Connection.PARTIDA_ERROR_NO_ENTRAR_DENIED:
                                    break;
                                case Connection.PARTIDA_ERROR_NON_EXISTANT_PARTIDA:
                                    break;
                            }
                            Toast.makeText(getBaseContext(), Integer.toString(getError()), Toast.LENGTH_SHORT).show();
                        }
                    });

                    connectionThread.start();
                }else if(which == Dialog.BUTTON_NEGATIVE){
                    dialog.dismiss();
                }
            }
        };
        builder.setPositiveButton(R.string.ok, onCListener);
        builder.setNegativeButton(R.string.cancel, onCListener);
        builder.create().show();
    }
}
