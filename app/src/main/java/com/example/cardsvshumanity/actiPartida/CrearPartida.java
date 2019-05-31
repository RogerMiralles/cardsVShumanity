package com.example.cardsvshumanity.actiPartida;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.cosasRecicler.Baraja;
import com.example.cardsvshumanity.javaConCod.Connection;

import java.util.ArrayList;

public class CrearPartida extends AppCompatActivity {

    private ArrayList<Baraja> listaBarajas;
    private LinearLayout linearLayout_listaBarajas;
    private EditText mNombrePartida, mContraPartida;
    private TextView seekBarText;
    private SeekBar limiteJugadores;

    private int maxPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_partida);
        setTitle(getString(R.string.crear_partida));

        linearLayout_listaBarajas = findViewById(R.id.scr_layout_escogerBaraja_crearPartida);
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
                if(fromUser){
                    maxPlayers = 3+progress;
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

        listaBarajas = new ArrayList<>();

        Connection.ConnectionThread hilo = Connection.getBarajasUser(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.internet_dialog_cargando);
        builder.setCancelable(false);
        final AlertDialog primerDialog = builder.create();
        hilo.setRunBegin(new Runnable() {
            @Override
            public void run() {
                primerDialog.show();
            }
        });
        hilo.setRunOk(new Connection.ConnectionThread.SuccessRunnable() {
            @Override
            public void run() {
                ArrayList<Object[]> list = (ArrayList<Object[]>) getArguments();
                for(Object[] obj : list){
                    String nombreBaraja, nombreEmail, userName, idioma;
                    int cantidad;
                    nombreBaraja = (String) obj[0];
                    nombreEmail = (String) obj[1];
                    userName = (String) obj[2];
                    cantidad = (int) obj[3];
                    idioma = (String) obj[4];

                    listaBarajas.add(new Baraja(nombreBaraja,nombreEmail, userName, cantidad, idioma));
                }

                linearLayout_listaBarajas.removeAllViews();
                for(Baraja b : listaBarajas){
                    System.out.println(b.getNombre());
                    CheckBox checkBox = new CheckBox(CrearPartida.this);
                    String strCb = b.getNombre() + " -- " + b.getIdioma();
                    checkBox.setText(strCb);
                    linearLayout_listaBarajas.addView(checkBox);
                }
                primerDialog.dismiss();
            }
        });

        hilo.setRunNo(new Connection.ConnectionThread.ErrorRunable() {
            @Override
            public void run() {
                switch (getError()){
                    case Connection.SOCKET_DISCONNECTED:
                        break;
                    case Connection.USER_NOT_LOGINED:
                        break;
                    case Connection.UNKOWN_ERROR:
                        break;
                    case Connection.USER_ERROR_NON_EXISTANT_USER:
                        break;
                    case Connection.USER_ERROR_INVALID_PASSWORD:
                        break;
                }
                primerDialog.dismiss();
            }
        });
        hilo.start();
    }

    public void creaPartida(View v){
        String nomPartida = mNombrePartida.getText().toString();
        String cGame = mContraPartida.getText().toString();
        boolean noValid = false;
        if(nomPartida.trim().isEmpty() || nomPartida.length() > 20){
            mNombrePartida.setText("");
            noValid = true;
        }
        if(mContraPartida.length() > 20){
            mContraPartida.setText("");
            noValid = true;
        }

        if(!noValid){
            ArrayList<Baraja> listaAceptada = new ArrayList<>();
            for(int i = 0; i<linearLayout_listaBarajas.getChildCount(); i++ ){
                CheckBox c = (CheckBox) linearLayout_listaBarajas.getChildAt(i);
                if(c.isChecked()){
                    listaAceptada.add(listaBarajas.get(i));
                }
            }
            if(!listaAceptada.isEmpty()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(R.string.internet_dialog_cargando);
                final AlertDialog alertDialog1 = builder.create();
                Connection.ConnectionThread hilo = Connection.crearPartida(this, nomPartida, cGame, maxPlayers, listaAceptada);
                hilo.setRunOk(new Connection.ConnectionThread.SuccessRunnable() {
                    @Override
                    public void run() {
                        alertDialog1.dismiss();
                        Intent intent = new Intent(CrearPartida.this, PrePartidaActivity.class);
                        intent.putExtra("autoEnter", true);
                        startActivity(intent);
                    }
                });
                hilo.setRunBegin(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog1.show();
                    }
                });
                hilo.setRunNo(new Connection.ConnectionThread.ErrorRunable() {
                    @Override
                    public void run() {
                        alertDialog1.dismiss();
                        builder.setPositiveButton(R.string.ok, null);
                        builder.setMessage(R.string.error_unknown_error);
                        builder.show();
                        Log.d(CrearPartida.class.getSimpleName(), "Error: "+getError());
                    }
                });

                hilo.start();
            }
            else{
                Toast.makeText(this, R.string.camposVacios, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "NO VALID", Toast.LENGTH_SHORT).show();
        }
    }
}
