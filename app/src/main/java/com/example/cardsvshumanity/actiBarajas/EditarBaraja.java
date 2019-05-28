package com.example.cardsvshumanity.actiBarajas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.cosasRecicler.Baraja;
import com.example.cardsvshumanity.cosasRecicler.CartaBlanca;
import com.example.cardsvshumanity.cosasRecicler.CartaNegra;
import com.example.cardsvshumanity.javaConCod.Connection;

import java.util.ArrayList;


public class EditarBaraja extends AppCompatActivity {

    private EditText textoNombreBaraja;
    private EditText textoNombreCreador;
    private EditText textoIdioma;
    private EditText textoNumCartas;
    private RecyclerView reciclerCBlancas;
    private RecyclerView reciclerCNegras;
    private ArrayList<CartaBlanca> blanca = new ArrayList<>();
    private ArrayList<CartaNegra> negra = new ArrayList<>();
    private AdaptadorCartasBlancas adapBlancas;
    private AdaptadorCartasNegras adapNegras;
    private AlertDialog alertDialogCartaBlanca;
    private AlertDialog alertDialogCartaNegra;
    private Baraja barajaDeBarajas;
    private boolean editOread;
    private Button btnGuardarCanvios;
    private Button nuevaBlanca;
    private Button nuevaNegra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_baraja);
        Intent in = getIntent();
        barajaDeBarajas=(Baraja)in.getSerializableExtra("baraja");
        editOread=in.getBooleanExtra("editOread",editOread);
        btnGuardarCanvios=findViewById(R.id.btnGuardarCanvios);
        nuevaBlanca=findViewById(R.id.btnNewCardBlanca);
        nuevaNegra=findViewById(R.id.btnNewCardNegra);

        textoNombreBaraja = findViewById(R.id.txtNombreBarajaEdit);
        textoNombreCreador=findViewById(R.id.txtNombreCreadorBaraja);
        textoIdioma=findViewById(R.id.txtIdioma);
        textoNumCartas=findViewById(R.id.txtNumCartas);

        textoNombreBaraja.setText(barajaDeBarajas.getNombre());
        textoNombreCreador.setText(barajaDeBarajas.getUsername());
        textoIdioma.setText(barajaDeBarajas.getIdioma());
        textoNumCartas.setText(""+barajaDeBarajas.getNumCartas());

        if(editOread){ //edit
            if(barajaDeBarajas.getEmail().equals("default")){
                Toast.makeText(this, "esta baraja no se puede editar", Toast.LENGTH_SHORT).show();
                textoNombreBaraja.setEnabled(false);
                textoNombreCreador.setEnabled(false);
                textoIdioma.setEnabled(false);
                textoNumCartas.setEnabled(false);

                btnGuardarCanvios.setEnabled(false);
                btnGuardarCanvios.setVisibility(View.GONE);
                nuevaBlanca.setEnabled(false);
                nuevaBlanca.setVisibility(View.GONE);
                nuevaNegra.setEnabled(false);
                nuevaNegra.setVisibility(View.GONE);
            }else{
                textoNombreBaraja.setEnabled(true);
                textoNombreCreador.setEnabled(false);
                textoIdioma.setEnabled(false);
                textoNumCartas.setEnabled(false);

                btnGuardarCanvios.setEnabled(true);
                btnGuardarCanvios.setVisibility(View.VISIBLE);
                nuevaBlanca.setEnabled(true);
                nuevaBlanca.setVisibility(View.VISIBLE);
                nuevaNegra.setEnabled(true);
                nuevaNegra.setVisibility(View.VISIBLE);
            }
        }else{ //read
            textoNombreBaraja.setEnabled(false);
            textoNombreCreador.setEnabled(false);
            textoIdioma.setEnabled(false);
            textoNumCartas.setEnabled(false);

            btnGuardarCanvios.setEnabled(false);
            btnGuardarCanvios.setVisibility(View.GONE);
            nuevaBlanca.setEnabled(false);
            nuevaBlanca.setVisibility(View.GONE);
            nuevaNegra.setEnabled(false);
            nuevaNegra.setVisibility(View.GONE);
        }

        reciclerCBlancas = findViewById(R.id.reciclerCartasBlancas);
        reciclerCNegras = findViewById(R.id.reciclerCartasNegras);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(EditarBaraja.this);
        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(EditarBaraja.this);

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);

        reciclerCBlancas.setLayoutManager(layoutManager);
        adapBlancas = new AdaptadorCartasBlancas(this, blanca);
        reciclerCBlancas.setAdapter(adapBlancas);

        reciclerCNegras.setLayoutManager(layoutManager1);
        adapNegras = new AdaptadorCartasNegras(this, negra);
        reciclerCNegras.setAdapter(adapNegras);


        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.internet_dialog_cargando)
                .setCancelable(false)
                .create();
        Connection.ConnectionThread thread = Connection.getCartasUser(this, barajaDeBarajas);
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

                        if(alertDialog.isShowing())
                            alertDialog.dismiss();
                    }
                }
        );

        thread.setRunNo(
                new Connection.ConnectionThread.ErrorRunable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditarBaraja.this);
                        builder.setPositiveButton(R.string.ok, null)
                                .setCancelable(false);
                        switch (getError()){
                            case Connection.UNKOWN_ERROR:
                                builder.setMessage(R.string.error_unknown_error);
                                break;
                            case Connection.SOCKET_DISCONNECTED:
                                builder.setMessage(R.string.noConexion);
                                break;
                            case Connection.BARAJA_ERROR_NON_EXISTANT_BARAJA:
                                builder.setMessage(R.string.error_no_existe_baraja);
                                break;
                            case Connection.USER_ERROR_NON_EXISTANT_USER:
                                builder.setMessage(R.string.error_usuario_no_existe);
                                break;

                        }
                        blanca.add(new CartaBlanca("HOLA"));
                        negra.add(new CartaNegra("ADIOS"));
                        adapBlancas.notifyDataSetChanged();
                        adapNegras.notifyDataSetChanged();
                        Log.d(EditarBaraja.class.getSimpleName(), String.valueOf(getError()));

                        builder.create().show();
                        if(alertDialog.isShowing())
                            alertDialog.dismiss();
                    }
                }
        );

        thread.start();
    }


    public class AdaptadorCartasBlancas extends RecyclerView.Adapter<AdaptadorCartasBlancas.ViewHolder> {
        private ArrayList<CartaBlanca> carta;
        private LayoutInflater mInflater;

        public AdaptadorCartasBlancas(Context context, ArrayList<CartaBlanca> bar) {
            mInflater = LayoutInflater.from(context);
            this.carta = bar;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(
                    R.layout.recicler_cartas_blancas, viewGroup, false);
            return new ViewHolder(mItemView, this);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            String mCurrent = carta.get(i).getNombre();
            viewHolder.texto.setText(mCurrent);
        }

        @Override
        public int getItemCount() {
            return carta.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView texto;
            private ScrollView scroll;
            private ConstraintLayout cons;
            final AdaptadorCartasBlancas adaptador;

            public ViewHolder(@NonNull View itemView, AdaptadorCartasBlancas adaptador) {
                super(itemView);
                texto = itemView.findViewById(R.id.etTextoCartasBViewHolder);
                texto.setOnClickListener(this);
                scroll=itemView.findViewById(R.id.srcollViewCartasBlancas);
                scroll.setOnClickListener(this);
                cons=itemView.findViewById(R.id.consCartasBlancas);
                cons.setOnClickListener(this);
                this.adaptador = adaptador;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                mostrarCartaBlancaSeleccionada(carta, getAdapterPosition());
            }
        }

    }

    public class AdaptadorCartasNegras extends RecyclerView.Adapter<AdaptadorCartasNegras.ViewHolder> {
        private ArrayList<CartaNegra> carta;
        private LayoutInflater mInflater;

        public AdaptadorCartasNegras(Context context, ArrayList<CartaNegra> bar) {
            mInflater = LayoutInflater.from(context);
            this.carta = bar;
        }

        @NonNull
        @Override
        public AdaptadorCartasNegras.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(
                    R.layout.recicler_cartas_negras, viewGroup, false);
            return new AdaptadorCartasNegras.ViewHolder(mItemView, this);
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

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView texto;
            private ScrollView scroll;
            private ConstraintLayout cons;
            final AdaptadorCartasNegras adaptador;

            public ViewHolder(@NonNull View itemView, AdaptadorCartasNegras adaptador) {
                super(itemView);
                texto = itemView.findViewById(R.id.etTextoCartasNViewHolder);
                texto.setOnClickListener(this);
                scroll=itemView.findViewById(R.id.scrollViewCartasNegras);
                scroll.setOnClickListener(this);
                cons=itemView.findViewById(R.id.consCartasNegras);cons.setOnClickListener(this);
                this.adaptador = adaptador;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                mostrarCartaNegraSeleccionada(carta, getAdapterPosition());
            }
        }
    }

    private void mostrarCartaBlancaSeleccionada(final ArrayList<CartaBlanca> carta, final int pos) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.texto)+" "+carta.get(pos).getNombre());
        builder1.setCancelable(false);

        if (!carta.get(pos).getEmail().equals("default")&&editOread) {
            builder1.setPositiveButton(
                    getString(R.string.editarCarta),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Toast.makeText(EditarBaraja.this, "hola", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        builder1.setNegativeButton(
                getString(R.string.salida),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogCartaBlanca = builder1.create();
        alertDialogCartaBlanca.show();
    }

    private void mostrarCartaNegraSeleccionada(ArrayList<CartaNegra> carta, final int pos) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.texto)+" "+carta.get(pos).getNombre()+"\n"+getString(R.string.numEspacios)+" "+carta.get(pos).getNumEspacios());
        builder1.setCancelable(false);

        if (!carta.get(pos).getEmail().equals("default")&&editOread) {
        builder1.setPositiveButton(
                getString(R.string.editarCarta),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(EditarBaraja.this, "hola", Toast.LENGTH_SHORT).show();
                    }
                });
        }

        builder1.setNegativeButton(
                getString(R.string.salida),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogCartaNegra = builder1.create();
        alertDialogCartaNegra.show();
    }

    public void onClickNewCartaBlanca(View view){

    }
    public void onClickNewCartaNegra(View view){

    }
    public void onClickGuardar(View view){

    }

}
