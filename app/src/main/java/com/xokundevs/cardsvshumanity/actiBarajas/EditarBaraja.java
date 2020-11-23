package com.xokundevs.cardsvshumanity.actiBarajas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.MainActivity;
import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.adapter.editdeckcardadapter.EditBlackDeckCardAdapter;
import com.xokundevs.cardsvshumanity.adapter.editdeckcardadapter.EditWhiteDeckCardAdapter;
import com.xokundevs.cardsvshumanity.cosasRecicler.DeckInfo;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.ModifyDeckPresenter;
import com.xokundevs.cardsvshumanity.presenter.impl.ModifyDeckPresenterImpl;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceGetCardsDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckBlackCardInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckWhiteCardInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCardBlackInfoOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCardWhiteInfoOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetCardsDeckOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterActivity;

import java.util.ArrayList;


public class EditarBaraja extends BasePresenterActivity<ModifyDeckPresenter> implements ModifyDeckPresenter.View,
        EditBlackDeckCardAdapter.OnClickCardListener,
        EditWhiteDeckCardAdapter.OnClickCardListener {

    public static final String ARG_DECK_INFO = "ARG_DECK_EMAIL";
    public static final String ARG_DECK_POSITION = "ARG_DECK_POSITION";
    public static final String ARG_NEW_DECK = "ARG_NEW_DECK";
    public static final String ARG_MODIFICABLE = "ARG_MODIFICABLE";
    private EditText textoNombreBaraja;
    private EditText textoNombreCreador;
    private EditText textoIdioma;
    private EditText textoNumCartas;
    private RecyclerView reciclerCBlancas;
    private RecyclerView reciclerCNegras;
    private AlertDialog alertDialogCartaBlanca;
    private AlertDialog alertDialogCartaNegra;
    private DeckInfo deckInfo;
    private boolean modificable;
    private boolean crear = false;
    private Button btnGuardarCanvios;
    private Button nuevaBlanca;
    private Button nuevaNegra;
    private AlertDialog alertDialogCrearCarta;
    private int posicion;
    private ServiceGetCardsDeckOutput cardsData;
    private EditBlackDeckCardAdapter blackCardAdapter;
    private EditWhiteDeckCardAdapter whiteCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_baraja);
        setPresenter(new ModifyDeckPresenterImpl(this));
        Intent in = getIntent();
        deckInfo = (DeckInfo) in.getParcelableExtra(ARG_DECK_INFO);
        modificable = in.getBooleanExtra(ARG_MODIFICABLE, false);
        posicion = in.getIntExtra(ARG_DECK_POSITION, 0);
        crear = in.getBooleanExtra(ARG_NEW_DECK, crear);

        bindViews();
        setupViews();

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.internet_dialog_cargando)
                .setCancelable(false)
                .create();
        if (!crear) {
            getPresenter().getCardDeck(new ServiceGetCardsDeckInput(deckInfo.getDeckEmailOwner(), deckInfo.getDeckName()));
        }
    }

    private void bindViews() {
        btnGuardarCanvios = findViewById(R.id.btnGuardarCanvios);
        nuevaBlanca = findViewById(R.id.btnNewCardBlanca);
        nuevaNegra = findViewById(R.id.btnNewCardNegra);

        textoNombreBaraja = findViewById(R.id.txtNombreBarajaEdit);
        textoNombreCreador = findViewById(R.id.txtNombreCreadorBaraja);
        textoIdioma = findViewById(R.id.txtIdioma);
        textoNumCartas = findViewById(R.id.txtNumCartas);
    }

    private void setupViews() {
        textoNombreBaraja.setText(deckInfo.getDeckName());
        textoNombreCreador.setText(deckInfo.getDeckOwnerUsername());
        textoIdioma.setText(deckInfo.getDeckLanguage());
        textoNumCartas.setText(Integer.toString(deckInfo.getDeckSize()));

        textoNombreBaraja.setEnabled(false);
        textoNombreCreador.setEnabled(false);
        textoIdioma.setEnabled(false);
        textoNumCartas.setEnabled(false);

        if (modificable) { //edit
            if (!deckInfo.getDeckEmailOwner().equals(Connection.getEmail())) {
                Toast.makeText(this, getString(R.string.no_editable), Toast.LENGTH_SHORT).show();
                btnGuardarCanvios.setEnabled(false);
                btnGuardarCanvios.setVisibility(View.GONE);
                nuevaBlanca.setEnabled(false);
                nuevaBlanca.setVisibility(View.GONE);
                nuevaNegra.setEnabled(false);
                nuevaNegra.setVisibility(View.GONE);
                modificable = false;
            } else {
                btnGuardarCanvios.setEnabled(true);
                btnGuardarCanvios.setVisibility(View.VISIBLE);
                nuevaBlanca.setEnabled(true);
                nuevaBlanca.setVisibility(View.VISIBLE);
                nuevaNegra.setEnabled(true);
                nuevaNegra.setVisibility(View.VISIBLE);
            }
        } else { //read
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
        whiteCardAdapter = new EditWhiteDeckCardAdapter(this, new ArrayList<>());
        reciclerCBlancas.setAdapter(whiteCardAdapter);

        reciclerCNegras.setLayoutManager(layoutManager1);
        blackCardAdapter = new EditBlackDeckCardAdapter(this, new ArrayList<>());
        reciclerCNegras.setAdapter(blackCardAdapter);
    }

    @Override
    public void onGetCardDeckSuccess(ServiceGetCardsDeckOutput output) {
        cardsData = output;
        whiteCardAdapter.setCardData(output.getWhiteCardList());
        whiteCardAdapter.notifyDataSetChanged();

        blackCardAdapter.setCardData(output.getBlackCardList());
        blackCardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetCardDeckFailure(int error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarBaraja.this);
        builder.setPositiveButton(R.string.ok, null)
                .setCancelable(false);
        switch (error) {
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
        Log.d(EditarBaraja.class.getSimpleName(), String.valueOf(error));

        builder.create().show();
    }

    @Override
    public void onSaveDeckSuccess() {
        Intent in = new Intent();
        in.putExtra("numCartas", deckInfo.getDeckSize());
        in.putExtra("posicion", posicion);
        setResult(RESULT_OK, in);
        finish();
    }

    @Override
    public void onSaveDeckError(int error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarBaraja.this);
        builder.setPositiveButton(R.string.ok, null)
                .setCancelable(false);
        switch (error) {
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
        Log.d(EditarBaraja.class.getSimpleName(), String.valueOf(error));

        builder.create().show();
    }

    private void mostrarCartaBlancaSeleccionada(final ServiceCardWhiteInfoOutput carta, final int pos) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.texto) + " " + carta.getNombre());
        builder1.setCancelable(false);

        if (deckInfo.getDeckEmailOwner().equals(Connection.getEmail()) && modificable) {
            builder1.setPositiveButton(
                    getString(R.string.editarCarta),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            View viewCartaBlanca = getLayoutInflater().inflate(R.layout.editar_cartas_blancas, null);
                            builder1.setView(viewCartaBlanca);
                            final EditText aa = viewCartaBlanca.findViewById(R.id.eTxtNomCBlanca);

                            builder1.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    final String a = aa.getText().toString();
                                    if (!a.isEmpty()) {
                                        carta.setNombre(a);
                                    }
                                    whiteCardAdapter.notifyItemChanged(pos);
                                }
                            });
                            builder1.setNeutralButton(null, null);
                            builder1.show();
                        }
                    });
            builder1.setNeutralButton(getString(R.string.borrar_carta), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(EditarBaraja.this);
                    builder.setMessage(getString(R.string.confirmar));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            whiteCardAdapter.getCardData().remove(pos);
                            whiteCardAdapter.notifyItemRemoved(pos);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alertDialogConfirmacion = builder.create();
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

    private void mostrarCartaNegraSeleccionada(final ServiceCardBlackInfoOutput carta, final int pos) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.texto) + " " + carta.getNombre() + "\n" + getString(R.string.numEspacios) + " " + carta.getNumEspacios());
        builder1.setCancelable(false);

        if (deckInfo.getDeckEmailOwner().equals(Connection.getEmail()) && modificable) {
            builder1.setPositiveButton(
                    getString(R.string.editarCarta),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            View viewCartaNegra = getLayoutInflater().inflate(R.layout.editar_cartas_negras, null);
                            builder1.setView(viewCartaNegra);
                            final EditText aa = viewCartaNegra.findViewById(R.id.eTxtNomCNegra);
                            final EditText aa1 = viewCartaNegra.findViewById(R.id.eTxtNumEspacios);
                            builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    final String a = aa.getText().toString();
                                    final int a1 = Integer.parseInt(aa1.getText().toString());
                                    boolean aux;
                                    if (!a.isEmpty())
                                        carta.setNombre(a);
                                    switch (a1) {
                                        case 1:
                                            aux = true;
                                            break;
                                        case 2:
                                            aux = true;
                                            break;
                                        case 3:
                                            aux = true;
                                            break;
                                        default:
                                            aux = false;
                                            break;
                                    }
                                    if (!aa1.getText().toString().isEmpty()) {
                                        if (aux)
                                            carta.setNumEspacios(a1);
                                        else
                                            Toast.makeText(EditarBaraja.this, getString(R.string.num_espacios_invalido), Toast.LENGTH_SHORT).show();
                                    }
                                    blackCardAdapter.notifyItemChanged(pos);
                                }
                            });
                            builder1.setNeutralButton(null, null);
                            builder1.show();
                        }
                    });
            builder1.setNeutralButton(getString(R.string.borrar_carta), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(EditarBaraja.this);
                    builder.setMessage(getString(R.string.confirmar));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            blackCardAdapter.getCardData().remove(pos);
                            blackCardAdapter.notifyItemRemoved(pos);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alertDialogConfirmacion = builder.create();
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

    public void onClickNewCartaBlanca(View view) {
        crearCartaBlanca();
    }

    public void onClickNewCartaNegra(View view) {
        crearCartaNegra();
    }

    public void onClickGuardar(View view) {
        guardar();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (btnGuardarCanvios.getVisibility() == View.VISIBLE) {
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
        } else
            finish();
    }

    private void crearCartaBlanca() {
        final android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.crear_carta));
        builder1.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.crear_carta_blanca, null);
        builder1.setView(view);
        final EditText txtContenido = view.findViewById(R.id.eTxtContenido);
        builder1.setPositiveButton(getString(R.string.crear_carta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String contenidos = txtContenido.getText().toString();
                if (!contenidos.isEmpty()) {
                    deckInfo.setDeckSize(whiteCardAdapter.getCardData().size());
                    whiteCardAdapter.getCardData().add(new ServiceCardWhiteInfoOutput(contenidos));

                    //update Strings
                    whiteCardAdapter.notifyItemInserted(whiteCardAdapter.getCardData().size());
                    textoNumCartas.setText(String.valueOf(deckInfo.getDeckSize()));
                } else
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
        View view = getLayoutInflater().inflate(R.layout.crear_carta_negra, null);
        builder1.setView(view);
        final EditText txtContenido = view.findViewById(R.id.eTxtContenidoN);
        final EditText txtCantEsp = view.findViewById(R.id.eTxtCantEsp);
        builder1.setPositiveButton(getString(R.string.crear_carta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean aux;
                int cantEsp = 0;
                if (!TextUtils.isEmpty(txtCantEsp.getText())) {
                    cantEsp = Integer.parseInt(txtCantEsp.getText().toString());
                }
                switch (cantEsp) {
                    case 1:
                    case 2:
                    case 3:
                        aux = true;
                        break;
                    default:
                        aux = false;
                        break;
                }
                if (aux) {
                    if (!TextUtils.isEmpty(txtContenido.getText())) {
                        String contenido = txtContenido.getText().toString();

                        deckInfo.setDeckSize(deckInfo.getDeckSize() + 1);
                        blackCardAdapter.getCardData().add(new ServiceCardBlackInfoOutput(contenido, blackCardAdapter.getItemCount()));

                        //update texts
                        blackCardAdapter.notifyItemInserted(blackCardAdapter.getCardData().size());
                        textoNumCartas.setText(String.valueOf(deckInfo.getDeckSize()));
                    } else {
                        Toast.makeText(EditarBaraja.this, getString(R.string.contenido_vacio), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditarBaraja.this, getString(R.string.num_espacios_invalido), Toast.LENGTH_SHORT).show();
                }
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

    public void guardar() {
        int total = 0;
        total = whiteCardAdapter.getCardData().size() + blackCardAdapter.getCardData().size();
        Log.d("cartaB", whiteCardAdapter.getCardData().size() + "");
        Log.d("cartaN", blackCardAdapter.getCardData().size() + "");
        deckInfo.setDeckSize(total);
        if (whiteCardAdapter.getCardData().size() >= 15 && whiteCardAdapter.getCardData().size() >= 5) {
            ServiceSaveDeckInput serviceSaveDeckInput = new ServiceSaveDeckInput();
            ArrayList<ServiceSaveDeckBlackCardInput> blackCardList = new ArrayList<>();
            ArrayList<ServiceSaveDeckWhiteCardInput> whiteCardList = new ArrayList<>();

            for(ServiceCardBlackInfoOutput blackInfoOutput : blackCardAdapter.getCardData()){
                blackCardList.add(new ServiceSaveDeckBlackCardInput(blackInfoOutput.getNombre(), blackInfoOutput.getNumEspacios()));
            }

            for (ServiceCardWhiteInfoOutput whiteCard: whiteCardAdapter.getCardData()) {
                whiteCardList.add(new ServiceSaveDeckWhiteCardInput(whiteCard.getNombre()));
            }

            serviceSaveDeckInput.setDeckEmail(deckInfo.getDeckEmailOwner());
            serviceSaveDeckInput.setDeckLanguage(deckInfo.getDeckLanguage());
            serviceSaveDeckInput.setDeckName(deckInfo.getDeckName());
            serviceSaveDeckInput.setDeckUsername(deckInfo.getDeckOwnerUsername());
            serviceSaveDeckInput.setBlackCards(blackCardList);
            serviceSaveDeckInput.setWhiteCards(whiteCardList);

            getPresenter().saveCardDeck(serviceSaveDeckInput);
        } else {
            Toast.makeText(this, getString(R.string.minimo_cartas), Toast.LENGTH_SHORT).show();
        }
    }

    public void decide() {
        final AlertDialog.Builder bild = new AlertDialog.Builder(EditarBaraja.this);
        bild.setMessage(getString(R.string.selecciona));
        bild.setCancelable(false);
        bild.setPositiveButton(getString(R.string.opcion_uno), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                setResult(RESULT_CANCELED);
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

    @Override
    public void onBlackCardClicked(int pos) {
        mostrarCartaNegraSeleccionada(blackCardAdapter.getCardData().get(pos), pos);
    }

    @Override
    public void onWhiteCardClicked(int pos) {
        mostrarCartaBlancaSeleccionada(whiteCardAdapter.getCardData().get(pos), pos);
    }
}