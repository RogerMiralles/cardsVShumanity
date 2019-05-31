package com.example.cardsvshumanity.actiBarajas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.cosasRecicler.Baraja;
import com.example.cardsvshumanity.javaConCod.Connection;

import java.util.ArrayList;

public class Barajas extends AppCompatActivity {

    private RecyclerView recicler;
    private static final int YA_PUEDE_ACTUALIZAR=0;
    private static final int YA_PUEDE_MODICIFAR=1;
    private ArrayList<Baraja> baraja=new ArrayList<>();
    private Adaptador adaptador1;
    private AlertDialog alertDialogCrearMazo;

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
                    case Connection.USER_ERROR_NON_EXISTANT_USER:
                        builder1.setMessage(R.string.emailContraMal);
                        break;
                    case Connection.UNKOWN_ERROR:
                        builder1.setMessage(R.string.error_unknown_error);
                        break;
                    case Connection.SOCKET_DISCONNECTED:
                        builder1.setMessage(R.string.noConexion);
                        break;
                    case Connection.USER_NOT_LOGINED:
                        //TODO anadir que hace si el usuario no esta logeado
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
            private Button borrarBaraja;
            final Adaptador adaptador;
            public ViewHolder(@NonNull final View itemView, Adaptador adaptador) {
                super(itemView);
                texto=itemView.findViewById(R.id.etTextoViewHolder);
                editarBaraja=itemView.findViewById(R.id.btnEditarBaraja);
                editarBaraja.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in=new Intent(getApplicationContext(), EditarBaraja.class);
                        in.putExtra("baraja",baraja.get(getAdapterPosition()));
                        in.putExtra("editOread",true);
                        in.putExtra("posicion",getAdapterPosition());
                        startActivityForResult(in,YA_PUEDE_MODICIFAR);
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
                borrarBaraja=itemView.findViewById(R.id.btnBorrarBaraja);
                borrarBaraja.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!baraja.get(getAdapterPosition()).getEmail().equals("default")) {
                            final AlertDialog.Builder builder=new AlertDialog.Builder(Barajas.this);
                            builder.setMessage(getString(R.string.confirmar));
                            builder.setCancelable(false);
                            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    borrar(getAdapterPosition());
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
                        }else
                            Toast.makeText(Barajas.this, getString(R.string.no_borrable), Toast.LENGTH_SHORT).show();
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
    public void borrar(final int pos){
        final AlertDialog.Builder builder1=new AlertDialog.Builder(Barajas.this);
        builder1.setMessage(R.string.internet_dialog_cargando);
        builder1.setCancelable(false);
        final AlertDialog alertDialogBorrandoBarajas = builder1.create();
        Connection.ConnectionThread borrandoBarajas = Connection.borraBaraja(Barajas.this, baraja.get(pos).getNombre());
        borrandoBarajas.setRunBegin(new Runnable() {
            @Override
            public void run() {
                alertDialogBorrandoBarajas.show();
            }
        });
        borrandoBarajas.setRunOk(new Connection.ConnectionThread.SuccessRunnable() {
            @Override
            public void run() {
                alertDialogBorrandoBarajas.dismiss();
                builder1.setMessage(getString(R.string.mazo_borrado_correctamente));
                builder1.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        adaptador1.notifyItemRemoved(pos);
                    }
                });
                builder1.show();
            }
        });
        borrandoBarajas.setRunNo(new Connection.ConnectionThread.ErrorRunable() {
            @Override
            public void run() {
                alertDialogBorrandoBarajas.dismiss();
                builder1.setMessage(getString(R.string.error_unknown_error));
                builder1.setPositiveButton(getString(R.string.ok), null);
                builder1.show();
            }
        });
        borrandoBarajas.start();

    }

    public void onClickNuevaBaraja(View view){
        creandoBaraja(baraja);
    }

    private void creandoBaraja(final ArrayList<Baraja> baraja) {
        final android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.crear_mazo));
        builder1.setCancelable(false);
        View view=getLayoutInflater().inflate(R.layout.crear_mazo,null);
        builder1.setView(view);
        final EditText txtNombreMazo=view.findViewById(R.id.eTxtNombreMazo);
        final EditText txtIdiomaMazo=view.findViewById(R.id.eTxtIdiomaMazo);
            builder1.setPositiveButton(
                    getString(R.string.crear_mazo),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            boolean idiomaB;
                            String nombreMazo=txtNombreMazo.getText().toString();
                            String idiomaMazo=txtIdiomaMazo.getText().toString();
                            switch (idiomaMazo){
                                case "es":
                                    idiomaB=true;
                                    break;
                                case "ca":
                                    idiomaB=true;
                                    break;
                                case "en":
                                    idiomaB=true;
                                    break;
                                default:
                                    idiomaB=false;
                                    break;
                            }if(!nombreMazo.isEmpty()) {
                                if (idiomaB) {
                                    String email = Connection.getEmail();
                                    String nombre = Connection.getName();
                                    baraja.add(new Baraja(nombreMazo, email, nombre, idiomaMazo));
                                    Intent in = new Intent(getApplicationContext(), EditarBaraja.class);
                                    in.putExtra("baraja", baraja.get(baraja.size() - 1));
                                    in.putExtra("editOread",true);
                                    in.putExtra("crearMazo",true);
                                    startActivityForResult(in, YA_PUEDE_ACTUALIZAR);
                                } else {
                                    Toast.makeText(Barajas.this, getString(R.string.idioma_no_valido), Toast.LENGTH_SHORT).show();
                                }
                            }else
                                Toast.makeText(Barajas.this, getString(R.string.nombre_baraja_vacio), Toast.LENGTH_SHORT).show();
                        }
                    });


        builder1.setNegativeButton(
                getString(R.string.salida),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogCrearMazo = builder1.create();
        alertDialogCrearMazo.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==YA_PUEDE_ACTUALIZAR){
            if (data != null) {
                baraja.get(baraja.size()-1).setNumCartas(data.getIntExtra("numCartas",0));
            }
           // Log.d("contenido baraja 2",baraja.get(baraja.size()-1).toString());
            adaptador1.notifyItemInserted(baraja.size());
        }else if(requestCode==YA_PUEDE_MODICIFAR){
            if (data != null) {
                baraja.get(data.getIntExtra("posicion",0)).setNumCartas(data.getIntExtra("numCartas",0));
            }
        }
    }
}
