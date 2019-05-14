package com.example.cardsvshumanity.jugarPerfil;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cardsvshumanity.MainActivity;
import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.logReg.login;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ajustesFragment extends Fragment {


    public ajustesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ajustes, container, false);
    }
    private void noUsuari(){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("No tienes usuario, quieres iniciar tu sesion?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent=new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), login.class);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent=new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), MainActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
