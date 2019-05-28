package com.example.cardsvshumanity.actiPartida;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.actiBarajas.EditarBaraja;
import com.example.cardsvshumanity.cosasRecicler.Baraja;
import com.example.cardsvshumanity.javaConCod.Connection;

import java.util.ArrayList;

public class Barajas extends AppCompatActivity {

    private RecyclerView recicler;

    private ArrayList<Baraja> baraja=new ArrayList<>();
    private Adaptador adaptador1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barajas);
        recicler=findViewById(R.id.reciclerBaraja);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.internet_dialog_cargando))
                .setCancelable(false)
                .create();

        Connection.ConnectionThread thread = Connection.getBarajasUser(this);


        thread.setRunBegin(new Runnable() {
            @Override
            public void run() {
                builder.show();
            }
        });

        Connection.ConnectionThread.SuccessRunnable successRunnable = new Connection.ConnectionThread.SuccessRunnable() {
            @Override
            public void run() {
                String nombreBaraja = null, email = null, username = null, idioma = null;
                Integer cantidadCartas = null;
                try {
                    ArrayList<Object[]> objects = (ArrayList<Object[]>) getArguments();
                    for (Object[] obj : objects) {
                        nombreBaraja = (String) obj[0];
                        email = (String) obj[1];
                        username = (String) obj[2];
                        cantidadCartas = (int) obj[3];
                        idioma = (String) obj[4];
                        baraja.add(new Baraja(nombreBaraja, email, username, cantidadCartas, idioma));
                    }
                    adaptador1.notifyDataSetChanged();
                    builder.dismiss();
                }catch(ClassCastException | NullPointerException ex){
                    AlertDialog alertDialog = new AlertDialog.Builder(Barajas.this)
                            .setMessage(R.string.error_unknown_error)
                            .setPositiveButton(R.string.ok, null)
                            .setCancelable(false)
                            .create();
                    alertDialog.show();
                    if(builder.isShowing())
                        builder.dismiss();
                    ex.printStackTrace();
                }
            }
        };

        Connection.ConnectionThread.ErrorRunable errorRunable = new Connection.ConnectionThread.ErrorRunable() {
            @Override
            public void run() {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Barajas.this);
                builder1.setPositiveButton(R.string.ok, null)
                        .setCancelable(false);
                switch (getError()){
                    case Connection.USER_ERROR_INVALID_PASSWORD:
                        builder1.setMessage(R.string.emailContraMal);
                        break;
                    case Connection.USER_ERROR_NON_EXISTANT_USER:
                        builder1.setMessage(R.string.emailContraMal);
                        break;
                    case Connection.UNKOWN_ERROR:
                        builder1.setMessage(R.string.error_unknown_error);
                        break;
                    case Connection.SOCKET_DISCONNECTED:
                        builder1.setMessage(R.string.noConexion);
                        break;
                }

                Log.d(Barajas.class.getSimpleName(), String.valueOf(getError()));
                baraja.add(new Baraja("pepe"));
                baraja.add(new Baraja("antonio el grande"));
                baraja.add(new Baraja("raul ha hecho la web"));
                baraja.add(new Baraja("test"));

                builder1.show();
                builder.dismiss();
            }
        };

        thread.setRunNo(errorRunable);
        thread.setRunOk(successRunnable);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(Barajas.this);
        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recicler.setLayoutManager(layoutManager);
        adaptador1=new Adaptador(this, baraja);
        recicler.setAdapter(adaptador1);


        thread.start();
    }

    public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder> {

        private  ArrayList<Baraja> baraja;
        private LayoutInflater mInflater;


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(
                    R.layout.adap, viewGroup, false);
            return new ViewHolder(mItemView,this);
        }

        @Override
        public int getItemCount() {
            return baraja.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            String mCurrent = baraja.get(i).getNombre();
            viewHolder.texto.setText(mCurrent);
        }



        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView texto;
            private Button editarBaraja;
            private Button verBaraja;
            final Adaptador adaptador;
            public ViewHolder(@NonNull View itemView,Adaptador adaptador) {
                super(itemView);
                texto=itemView.findViewById(R.id.etTextoViewHolder);
                editarBaraja=itemView.findViewById(R.id.btnEditarBaraja);
                editarBaraja.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in=new Intent(getApplicationContext(), EditarBaraja.class);
                        in.putExtra("baraja",baraja.get(getAdapterPosition()));
                        in.putExtra("editOread",true);
                        startActivity(in);
                    }
                });
                verBaraja=itemView.findViewById(R.id.btnVerBaraja);
                verBaraja.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in=new Intent(getApplicationContext(), EditarBaraja.class);
                        in.putExtra("baraja",baraja.get(getAdapterPosition()));
                        in.putExtra("editOread",false);
                        startActivity(in);
                    }
                });
                this.adaptador=adaptador;
            }
        }
        public Adaptador(Context context,ArrayList<Baraja> bar){
            mInflater = LayoutInflater.from(context);
            this.baraja=bar;
        }
    }

    public void onClickNuevaBaraja(View view){
        baraja.add(new Baraja("1"));
        adaptador1.notifyItemInserted(baraja.size());
    }
}
