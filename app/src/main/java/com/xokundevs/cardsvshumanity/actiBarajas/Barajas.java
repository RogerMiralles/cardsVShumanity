package com.xokundevs.cardsvshumanity.actiBarajas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xokundevs.cardsvshumanity.MainActivity;
import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.adapter.DeckAdapter;
import com.xokundevs.cardsvshumanity.cosasRecicler.DeckInfo;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.BarajasPresenter;
import com.xokundevs.cardsvshumanity.presenter.impl.BarajasPresenterImpl;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseDeckInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoListOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Barajas extends BasePresenterActivity<BarajasPresenter> implements BarajasPresenter.View, DeckAdapter.OnDeckClickListener {

    private RecyclerView recicler;
    private static final int YA_PUEDE_ACTUALIZAR = 0;
    private static final int YA_PUEDE_MODIFICAR = 1;
    private ArrayList<DeckInfo> deckInfoList;
    private DeckAdapter adaptador1;
    private AlertDialog alertDialogCrearMazo;
    DialogFragment dialogFragment;
    private int eraseDeck = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barajas);
        bindViews();
        setupViews();
        setPresenter(new BarajasPresenterImpl(this));

        getPresenter().getBaraja();
    }

    private void bindViews() {
        recicler = findViewById(R.id.reciclerBaraja);
        deckInfoList = new ArrayList<>();
        adaptador1 = new DeckAdapter(this,deckInfoList);
    }

    private void setupViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recicler.setLayoutManager(layoutManager);
        recicler.setAdapter(adaptador1);
    }

    @Override
    public void onGetBarajasSuccess(ServiceSimpleDeckInfoListOutput serviceSimpleDeckInfoListOutput) {
        deckInfoList.clear();
        for (ServiceSimpleDeckInfoOutput baraja : serviceSimpleDeckInfoListOutput.getListBarajas()) {
            deckInfoList.add(new DeckInfo(baraja.getDeckEmail(), baraja.getDeckName(), baraja.getDeckLanguage(), baraja.getDeckUsername(), baraja.getDeckSize(), baraja.isEditable()));
        }
        adaptador1.notifyDataSetChanged();
    }

    @Override
    public void onGetBarajaFailure(int error) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Barajas.this);
        builder1.setPositiveButton(R.string.ok, null)
                .setCancelable(false);
        switch (error) {
            case Connection.INVALID_CREDENTIALS_ERROR:
                builder1.setMessage(R.string.emailContraMal);
                break;
            case Connection.UNKNOWN_ERROR:
                builder1.setMessage(R.string.error_unknown_error);
                break;
            case Connection.SOCKET_DISCONNECTED:
                builder1.setMessage(R.string.noConexion);
                break;
            case Connection.USER_NOT_LOGINED:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default:
                builder1.setMessage(String.format(Locale.getDefault(), "%s %d", getString(R.string.noConexion), error));
                break;
        }

        Log.d(Barajas.class.getSimpleName(), String.valueOf(error));

        builder1.show();
    }

    @Override
    public void onBorrarBarajaSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.internet_dialog_cargando);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.mazo_borrado_correctamente));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                deckInfoList.remove(eraseDeck);
                adaptador1.notifyItemRemoved(eraseDeck);
                eraseDeck = -1;
            }
        });
        builder.show();
    }

    @Override
    public void onBorrarBarajaFailure(int error) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(R.string.internet_dialog_cargando);
        builder1.setCancelable(false);
        builder1.setMessage(getString(R.string.error_unknown_error));
        builder1.setPositiveButton(getString(R.string.ok), null);
        builder1.show();
    }

    private void borrar(int pos) {
        eraseDeck = pos;
        getPresenter().borrarBaraja(new ServiceEraseDeckInput(Connection.getEmail(), Connection.getPassword(), deckInfoList.get(pos).getDeckName()));
    }

    public void onClickNuevaBaraja(View view) {
        creandoBaraja(deckInfoList);
    }

    private void creandoBaraja(final ArrayList<DeckInfo> serviceGetCardsDeckOutput) {
        final android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.crear_mazo));
        builder1.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.crear_mazo, null);
        builder1.setView(view);
        final EditText txtNombreMazo = view.findViewById(R.id.eTxtNombreMazo);
        final EditText txtIdiomaMazo = view.findViewById(R.id.eTxtIdiomaMazo);
        builder1.setPositiveButton(
                getString(R.string.crear_mazo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        boolean idiomaB;
                        String nombreMazo = txtNombreMazo.getText().toString();
                        String idiomaMazo = txtIdiomaMazo.getText().toString();
                        switch (idiomaMazo) {
                            case "es":
                            case "ca":
                            case "en":
                                idiomaB = true;
                                break;
                            default:
                                idiomaB = false;
                                break;
                        }
                        if (!nombreMazo.isEmpty()) {
                            if (idiomaB) {
                                String email = Connection.getEmail();
                                String nombre = Connection.getName();
                                DeckInfo deckInfo = new DeckInfo(email, nombreMazo, idiomaMazo, nombre, 0, true);
                                deckInfoList.add(deckInfo);
                                Intent in = new Intent(getApplicationContext(), EditarBaraja.class);
                                in.putExtra(EditarBaraja.ARG_DECK_INFO, deckInfo);
                                in.putExtra(EditarBaraja.ARG_MODIFICABLE, true);
                                in.putExtra(EditarBaraja.ARG_NEW_DECK, true);
                                startActivityForResult(in, YA_PUEDE_ACTUALIZAR);
                            } else {
                                Toast.makeText(Barajas.this, getString(R.string.idioma_no_valido), Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(Barajas.this, getString(R.string.nombre_baraja_vacio), Toast.LENGTH_SHORT).show();
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.salida),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );

        alertDialogCrearMazo = builder1.create();
        alertDialogCrearMazo.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YA_PUEDE_ACTUALIZAR) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    deckInfoList.get(deckInfoList.size() - 1).setDeckSize(data.getIntExtra("numCartas", 0));
                }
            } else {
                deckInfoList.remove(deckInfoList.size() - 1);
            }
            // Log.d("contenido baraja 2",baraja.get(baraja.size()-1).toString());
            adaptador1.notifyItemInserted(deckInfoList.size());
        } else if (requestCode == YA_PUEDE_MODIFICAR) {
            if (data != null) {
                deckInfoList.get(data.getIntExtra("posicion", 0)).setDeckSize(data.getIntExtra("numCartas", 0));
            }
        }
    }

    @Override
    public void onEditDeckClicked(DeckInfo deckInfo, int adapterPos) {
        Intent in = new Intent(this, EditarBaraja.class);
        in.putExtra(EditarBaraja.ARG_DECK_INFO, deckInfo);
        in.putExtra(EditarBaraja.ARG_MODIFICABLE, true);
        in.putExtra(EditarBaraja.ARG_DECK_POSITION, adapterPos);
        startActivityForResult(in, YA_PUEDE_MODIFICAR);
    }

    @Override
    public void onReadDeckClicked(DeckInfo deckInfo) {
        Intent in = new Intent(getApplicationContext(), EditarBaraja.class);
        in.putExtra(EditarBaraja.ARG_DECK_INFO, deckInfo);
        in.putExtra(EditarBaraja.ARG_MODIFICABLE, false);
        startActivity(in);
    }

    @Override
    public void onEraseDeckClicked(DeckInfo deckInfo, int pos) {
        if (deckInfo.getDeckEmailOwner().equals(Connection.getEmail())) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Barajas.this);
            builder.setMessage(getString(R.string.confirmar));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    borrar(pos);
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
        } else {
            Toast.makeText(Barajas.this, getString(R.string.no_borrable), Toast.LENGTH_SHORT).show();
        }
    }
}
