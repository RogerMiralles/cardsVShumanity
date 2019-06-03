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


import com.example.cardsvshumanity.MainActivity;
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
    private boolean crear=false;
    private Button btnGuardarCanvios;
    private Button nuevaBlanca;
    private Button nuevaNegra;
    private AlertDialog alertDialogCrearCarta;
    private int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_baraja);
        Intent in = getIntent();
        barajaDeBarajas=(Baraja)in.getSerializableExtra("baraja");
        editOread=in.getBooleanExtra("editOread",editOread);
        posicion=in.getIntExtra("posicion",0);
        crear=in.getBooleanExtra("crearMazo",crear);
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

        textoNombreBaraja.setEnabled(false);
        textoNombreCreador.setEnabled(false);
        textoIdioma.setEnabled(false);
        textoNumCartas.setEnabled(false);

        if(editOread){ //edit
            if(barajaDeBarajas.getEmail().equals("default")){
                Toast.makeText(this, getString(R.string.no_editable), Toast.LENGTH_SHORT).show();
                btnGuardarCanvios.setEnabled(false);
                btnGuardarCanvios.setVisibility(View.GONE);
                nuevaBlanca.setEnabled(false);
                nuevaBlanca.setVisibility(View.GONE);
                nuevaNegra.setEnabled(false);
                nuevaNegra.setVisibility(View.GONE);
            }else{
                btnGuardarCanvios.setEnabled(true);
                btnGuardarCanvios.setVisibility(View.VISIBLE);
                nuevaBlanca.setEnabled(true);
                nuevaBlanca.setVisibility(View.VISIBLE);
                nuevaNegra.setEnabled(true);
                nuevaNegra.setVisibility(View.VISIBLE);
            }
        }else{ //read
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
        if(!crear){
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

                            if (alertDialog.isShowing())
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
                            switch (getError()) {
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
                                case Connection.USER_NOT_LOGINED:
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;
                            }
                            blanca.add(new CartaBlanca("HOLA"));
                            negra.add(new CartaNegra("ADIOS"));
                            adapBlancas.notifyDataSetChanged();
                            adapNegras.notifyDataSetChanged();
                            Log.d(EditarBaraja.class.getSimpleName(), String.valueOf(getError()));

                            builder.create().show();
                            if (alertDialog.isShowing())
                                alertDialog.dismiss();
                        }
                    }
            );

            thread.start();
        }
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
                cons=itemView.findViewById(R.id.consCartasNegras);
                cons.setOnClickListener(this);
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
                            View viewCartaBlanca=getLayoutInflater().inflate(R.layout.editar_cartas_blancas,null);
                            builder1.setView(viewCartaBlanca);
                            final EditText aa=viewCartaBlanca.findViewById(R.id.eTxtNomCBlanca);

                            builder1.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    final String a=aa.getText().toString();
                                    if(!a.isEmpty())
                                        carta.get(pos).setNombre(a);
                                    adapBlancas.notifyItemChanged(pos);
                                }
                            });
                            builder1.setNeutralButton(null,null);
                            builder1.show();
                        }
                    });
            builder1.setNeutralButton(getString(R.string.borrar_carta), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    final AlertDialog.Builder builder=new AlertDialog.Builder(EditarBaraja.this);
                    builder.setMessage(getString(R.string.confirmar));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            adapBlancas.carta.remove(pos);
                            adapBlancas.notifyItemRemoved(pos);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alertDialogConfirmacion=builder.create();
                    alertDialogConfirmacion.show();

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

    private void mostrarCartaNegraSeleccionada(final ArrayList<CartaNegra> carta, final int pos) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.texto)+" "+carta.get(pos).getNombre()+"\n"+getString(R.string.numEspacios)+" "+carta.get(pos).getNumEspacios());
        builder1.setCancelable(false);

        if (!carta.get(pos).getEmail().equals("default")&&editOread) {
            builder1.setPositiveButton(
                getString(R.string.editarCarta),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        View viewCartaNegra=getLayoutInflater().inflate(R.layout.editar_cartas_negras,null);
                        builder1.setView(viewCartaNegra);
                        final EditText aa=viewCartaNegra.findViewById(R.id.eTxtNomCNegra);
                        final EditText aa1=viewCartaNegra.findViewById(R.id.eTxtNumEspacios);
                        builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                final String a=aa.getText().toString();
                                final int a1= Integer.parseInt(aa1.getText().toString());
                                boolean aux;
                                if(!a.isEmpty())
                                    carta.get(pos).setNombre(a);
                                switch (a1){
                                    case 1:
                                        aux=true;
                                        break;
                                    case 2:
                                        aux=true;
                                        break;
                                    case 3:
                                        aux=true;
                                        break;
                                    default:
                                        aux=false;
                                        break;
                                }
                                if(!aa1.getText().toString().isEmpty()) {
                                    if(aux)
                                        carta.get(pos).setNumEspacios(a1);
                                    else
                                        Toast.makeText(EditarBaraja.this, getString(R.string.num_espacios_invalido), Toast.LENGTH_SHORT).show();
                                }
                                adapNegras.notifyItemChanged(pos);
                            }
                        });
                        builder1.setNeutralButton(null,null);
                        builder1.show();
                    }
                });
            builder1.setNeutralButton(getString(R.string.borrar_carta), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    final AlertDialog.Builder builder=new AlertDialog.Builder(EditarBaraja.this);
                    builder.setMessage(getString(R.string.confirmar));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            adapNegras.carta.remove(pos);
                            adapNegras.notifyItemRemoved(pos);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alertDialogConfirmacion=builder.create();
                    alertDialogConfirmacion.show();

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
       crearCartaBlanca();
    }
    public void onClickNewCartaNegra(View view){
        crearCartaNegra();
    }
    public void onClickGuardar(View view){
        guardar();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(btnGuardarCanvios.getVisibility()==View.VISIBLE) {
            final AlertDialog.Builder bild = new AlertDialog.Builder(EditarBaraja.this);
            bild.setMessage(getString(R.string.guardar_canvios));
            bild.setCancelable(false);
            bild.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    guardar();
                }
            });
            bild.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    decide();
                }
            });
            final AlertDialog alDi = bild.create();
            alDi.show();
        }else
            finish();
    }

    private void crearCartaBlanca() {
        final android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.crear_carta));
        builder1.setCancelable(false);
        View view =getLayoutInflater().inflate(R.layout.crear_carta_blanca,null);
        builder1.setView(view);
        final EditText txtContenido=view.findViewById(R.id.eTxtContenido);
        builder1.setPositiveButton(getString(R.string.crear_carta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String contenidos= txtContenido.getText().toString();
                String correo= Connection.getEmail();
                int idC=barajaDeBarajas.getNumCartas()+1;
                barajaDeBarajas.setNumCartas(barajaDeBarajas.getNumCartas()+1);
                if(!contenidos.isEmpty()) {
                    adapBlancas.carta.add(new CartaBlanca(correo, contenidos, idC));
                    adapBlancas.notifyItemInserted(adapBlancas.carta.size());
                }else
                    Toast.makeText(EditarBaraja.this, getString(R.string.contenido_vacio), Toast.LENGTH_SHORT).show();
            }
        });

        builder1.setNegativeButton(
                getString(R.string.salida),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogCrearCarta = builder1.create();
        alertDialogCrearCarta.show();
    }

    private void crearCartaNegra() {
        final android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.crear_carta));
        builder1.setCancelable(false);
        View view =getLayoutInflater().inflate(R.layout.crear_carta_negra,null);
        builder1.setView(view);
        final EditText txtContenido=view.findViewById(R.id.eTxtContenidoN);
        final EditText txtCantEsp=view.findViewById(R.id.eTxtCantEsp);
        builder1.setPositiveButton(getString(R.string.crear_carta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String contenido=txtContenido.getText().toString();
                String correo= Connection.getEmail();
                int idC=barajaDeBarajas.getNumCartas()+1;
                barajaDeBarajas.setNumCartas(barajaDeBarajas.getNumCartas()+1);
                boolean aux;
                int cantEsp=Integer.parseInt(txtCantEsp.getText().toString());
                switch ( cantEsp){
                    case 1:
                        aux=true;
                        break;
                    case 2:
                        aux=true;
                        break;
                    case 3:
                        aux=true;
                        break;
                    default:
                        aux=false;
                        break;
                }
                if(aux) {
                    if(!contenido.isEmpty()) {
                        adapNegras.carta.add(new CartaNegra(correo, contenido, idC, cantEsp));
                        adapNegras.notifyItemInserted(adapNegras.carta.size());
                    }else
                        Toast.makeText(EditarBaraja.this, getString(R.string.contenido_vacio), Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(EditarBaraja.this, getString(R.string.num_espacios_invalido), Toast.LENGTH_SHORT).show();
            }
        });

        builder1.setNegativeButton(
                getString(R.string.salida),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogCrearCarta = builder1.create();
        alertDialogCrearCarta.show();
    }

    public void guardar(){
        int total=0;
        total=adapBlancas.carta.size()+adapNegras.carta.size();
        Log.d("cartaB",adapBlancas.carta.size()+"");
        Log.d("cartaN",adapNegras.carta.size()+"");
        barajaDeBarajas.setNumCartas(total);
        final Intent in=new Intent();
        in.putExtra("numCartas",total);
        in.putExtra("posicion",posicion);
        if(adapBlancas.carta.size()>=15 &&adapNegras.carta.size()>=5) {
            final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(R.string.internet_dialog_cargando);
            final AlertDialog alertDialogClickGuardar = builder1.create();
            Connection.ConnectionThread guardarMazoCartas = Connection.saveBaraja(this, barajaDeBarajas, adapBlancas.carta, adapNegras.carta);
            guardarMazoCartas.setRunOk(new Connection.ConnectionThread.SuccessRunnable() {
                @Override
                public void run() {
                    alertDialogClickGuardar.dismiss();
                    builder1.setMessage(getString(R.string.mazo_creado_correctamente));
                    builder1.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setResult(RESULT_OK, in);
                            finish();
                        }
                    });
                    builder1.show();
                }
            });
            guardarMazoCartas.setRunBegin(new Runnable() {
                @Override
                public void run() {
                    alertDialogClickGuardar.show();
                }
            });
            guardarMazoCartas.setRunNo(new Connection.ConnectionThread.ErrorRunable() {
                @Override
                public void run() {
                    alertDialogClickGuardar.dismiss();
                    switch (getError()) {
                        case Connection.UNKOWN_ERROR:
                            builder1.setMessage(getString(R.string.error_unknown_error));
                            break;
                        case Connection.SOCKET_DISCONNECTED:
                            builder1.setMessage(getString(R.string.noConexion));
                            break;
                        case Connection.USER_ERROR_INVALID_PASSWORD:
                            builder1.setMessage(getString(R.string.invalidPassword));
                            break;
                        case Connection.USER_ERROR_NON_EXISTANT_USER:
                            builder1.setMessage(getString(R.string.error_usuario_no_existe));
                            break;
                        case Connection.USER_NOT_LOGINED:
                            builder1.setMessage(getString(R.string.notLogin));
                            break;
                    }
                    builder1.setPositiveButton(getString(R.string.ok), null);
                    builder1.show();
                }
            });
            guardarMazoCartas.start();
        }else
            Toast.makeText(this, getString(R.string.minimo_cartas), Toast.LENGTH_SHORT).show();
    }

    public void decide(){
        final AlertDialog.Builder bild = new AlertDialog.Builder(EditarBaraja.this);
        bild.setMessage(getString(R.string.selecciona));
        bild.setCancelable(false);
        bild.setPositiveButton(getString(R.string.opcion_uno), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        bild.setNegativeButton(getString(R.string.opcion_dos), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alDi = bild.create();
        alDi.show();
    }
}