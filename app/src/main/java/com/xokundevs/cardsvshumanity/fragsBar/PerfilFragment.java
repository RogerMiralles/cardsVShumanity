package com.xokundevs.cardsvshumanity.fragsBar;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


import com.xokundevs.cardsvshumanity.R;
import com.xokundevs.cardsvshumanity.actiLogReg.Login;
import com.xokundevs.cardsvshumanity.javaConCod.Connection;

import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {


    private static final int INICIAR_SESION = 101;

    private EditText eTxtNom, eTxtCorreu, eTxtPass, eTxtPartGan;
    private ImageView imageView;
    public PerfilFragment() {
        // Required empty public constructor
    }
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView=inflater.inflate(R.layout.fragment_perfil, container, false);
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.tituloPerfil));
        imageView = rootView.findViewById(R.id.imgUsuario3);
        eTxtNom=rootView.findViewById(R.id.eTxtNombreU3);
        eTxtCorreu=rootView.findViewById(R.id.eTxtCorreo4);
        eTxtPass=rootView.findViewById(R.id.eTxtPass4);
        eTxtPartGan=rootView.findViewById(R.id.eTxtPartGana2);
        UpdateUI();
        return rootView;
    }

    private void noUsuari(){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(getString(R.string.noUsuari));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getString(R.string.si),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent=new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), Login.class);
                        startActivityForResult(intent, INICIAR_SESION);
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent= Objects.requireNonNull(getActivity()).getIntent();
                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_NO_ANIMATION);
                        getActivity().overridePendingTransition(0,0);
                        getActivity().finish();

                        getActivity().overridePendingTransition(0,0);
                        startActivity(intent);
                    }
                });

        alertDialog = builder1.create();
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        if(alertDialog != null)
            alertDialog.dismiss();
        super.onDestroy();
    }

    private void UpdateUI(){
        if(Connection.isLogined()){

            if(Connection.getImage() != null) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(Connection.getImage().getAbsolutePath()));
            }
            else{
                imageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_black_24dp));
            }

            if(Connection.getName()!=null){
                eTxtNom.setText(Connection.getName());
            }

            if(Connection.getEmail()!=null){
                eTxtCorreu.setText(Connection.getEmail());
            }

            eTxtPass.setText("****");

            if(Connection.getWins() != null){
                eTxtPartGan.setText(Integer.toString(Connection.getWins()));
            }
        }
        else{
            imageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_person_black_24dp));
            noUsuari();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == INICIAR_SESION){
                UpdateUI();
        }
    }
}
