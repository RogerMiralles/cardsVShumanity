package com.example.cardsvshumanity.actiBarajas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cardsvshumanity.R;

public class CrearCarta extends AppCompatActivity {

    private EditText contenido;
    private EditText numEsp;
    private TextView numEspT;
    private String tipoCarta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_carta);
        Intent in=getIntent();
        tipoCarta=in.getStringExtra("CartaTipo");

        contenido=findViewById(R.id.eTxtContenido);
        numEsp=findViewById(R.id.eTxtNumEsp);
        numEspT=findViewById(R.id.txtNumEsp);


        if(tipoCarta.equals("blanca")){
            numEspT.setVisibility(View.INVISIBLE);
            numEsp.setVisibility(View.INVISIBLE);
            numEsp.setEnabled(false);
            numEspT.setEnabled(false);
        }else if(tipoCarta.equals("negra")){
            numEspT.setVisibility(View.VISIBLE);
            numEsp.setVisibility(View.VISIBLE);
            numEsp.setEnabled(true);
            numEspT.setEnabled(true);
        }
    }

    public void onClickCrearCarta(View v){
        if(!contenido.getText().toString().isEmpty()){
            Intent in=new Intent();
            if(tipoCarta.equals("blanca")){
                in.putExtra("contenido",contenido.getText());
                setResult(RESULT_OK,in);
                finish();
            }else if(tipoCarta.equals("negra")&&!numEsp.getText().toString().isEmpty()){
                in.putExtra("contenido",contenido.getText());
                in.putExtra("cantEsp",Integer.parseInt(numEsp.getText().toString()));
                setResult(RESULT_OK,in);
                finish();
            }
        }
    }
}
