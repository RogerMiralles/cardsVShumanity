package com.example.cardsvshumanity.actiBarajas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.cosasRecicler.Baraja;
import com.example.cardsvshumanity.cosasRecicler.CartaBlanca;
import com.example.cardsvshumanity.cosasRecicler.CartaNegra;
import com.example.cardsvshumanity.javaConCod.Connection;

import java.util.ArrayList;

public class EditarBaraja extends AppCompatActivity {

    private TextView textoNombreBaraja;
    private RecyclerView reciclerCBlancas;
    private RecyclerView reciclerCNegras;
    private ArrayList<CartaBlanca> blanca=new ArrayList<>();
    private ArrayList<CartaNegra> negra=new ArrayList<>();
    private AdaptadorCartasBlancas adapBlancas;
    private AdaptadorCartasNegras adapNegras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_baraja);
        Intent in=getIntent();
        textoNombreBaraja =findViewById(R.id.txtNombreBarajaEdit);
        textoNombreBaraja.setText(in.getStringExtra("nombre"));

        reciclerCBlancas=findViewById(R.id.reciclerCartasBlancas);
        reciclerCNegras=findViewById(R.id.reciclerCartasNegras);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(EditarBaraja.this);
        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(EditarBaraja.this);

        reciclerCBlancas.setLayoutManager(layoutManager);
        adapBlancas=new AdaptadorCartasBlancas(this,blanca);
        reciclerCBlancas.setAdapter(adapBlancas);

        reciclerCNegras.setLayoutManager(layoutManager1);
        adapNegras=new AdaptadorCartasNegras(this,negra);
        reciclerCNegras.setAdapter(adapNegras);

        Baraja b = new Baraja("Baraja default es", "default", "default", 0, "es");
        Connection.ConnectionThread thread = Connection.getCartasUser(this, b);
        thread.setRunOk(
                new Connection.ConnectionThread.SuccessRunnable() {
                    @Override
                    public void run() {
                        Object[] lista = (Object[]) getArguments();
                        ArrayList<CartaBlanca> cartaBlancas = (ArrayList<CartaBlanca>) lista[0];
                        ArrayList<CartaNegra> cartaNegras = (ArrayList<CartaNegra>) lista[1];
                        adapBlancas.carta = cartaBlancas;
                        adapNegras.carta = cartaNegras;
                        adapBlancas.notifyDataSetChanged();
                        adapNegras.notifyDataSetChanged();
                    }
                }
        );

        thread.setRunNo(
                new Connection.ConnectionThread.ErrorRunable() {
                    @Override
                    public void run() {
                        blanca.add(new CartaBlanca("HOLA"));
                        negra.add(new CartaNegra("ADIOS"));
                        adapBlancas.notifyDataSetChanged();
                        adapNegras.notifyDataSetChanged();
                        Log.d(EditarBaraja.class.getSimpleName(), String.valueOf(getError()));
                    }
                }
        );

        thread.start();
    }

    public class AdaptadorCartasBlancas extends RecyclerView.Adapter<AdaptadorCartasBlancas.ViewHolder>{
        private  ArrayList<CartaBlanca> carta;
        private LayoutInflater mInflater;



        public AdaptadorCartasBlancas(Context context, ArrayList<CartaBlanca> bar){
            mInflater = LayoutInflater.from(context);
            this.carta=bar;
        }

        @NonNull
        @Override
        public AdaptadorCartasBlancas.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(
                    R.layout.recicler_cartas_blancas, viewGroup, false);
            return new AdaptadorCartasBlancas.ViewHolder(mItemView,this);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorCartasBlancas.ViewHolder viewHolder, int i) {
            String mCurrent = carta.get(i).getNombre();
            viewHolder.texto.setText(mCurrent);
        }

        @Override
        public int getItemCount() {
            return carta.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView texto;
            final AdaptadorCartasBlancas adaptador;
            public ViewHolder(@NonNull View itemView,AdaptadorCartasBlancas adaptador) {
                super(itemView);
                texto=itemView.findViewById(R.id.etTextoCartasBViewHolder);
                this.adaptador=adaptador;
            }
        }
    }

    public class AdaptadorCartasNegras extends RecyclerView.Adapter<AdaptadorCartasNegras.ViewHolder>{
        private  ArrayList<CartaNegra> carta;
        private LayoutInflater mInflater;

        public AdaptadorCartasNegras(Context context, ArrayList<CartaNegra> bar){
            mInflater = LayoutInflater.from(context);
            this.carta=bar;
        }

        @NonNull
        @Override
        public AdaptadorCartasNegras.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(
                    R.layout.recicler_cartas_negras, viewGroup, false);
            return new AdaptadorCartasNegras.ViewHolder(mItemView,this);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorCartasNegras.ViewHolder viewHolder, int i) {
            String mCurrent = carta.get(i).getNombre();
            viewHolder.texto.setText(mCurrent);
        }

        @Override
        public int getItemCount() {
            return carta.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView texto;
            final AdaptadorCartasNegras adaptador;
            public ViewHolder(@NonNull View itemView,AdaptadorCartasNegras adaptador) {
                super(itemView);
                texto=itemView.findViewById(R.id.etTextoCartasNViewHolder);
                this.adaptador=adaptador;
            }
        }
    }
}
