package com.example.cardsvshumanity.jugarPerfil;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cardsvshumanity.MainActivity;
import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.logReg.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class perfilFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public perfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //this.setTitle("Cartas Contra la Humanidad");
        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        if(mUser==null){
            noUsuari();
        }
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    private void noUsuari(){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("No tienes usuario, quieres iniciar tu sesion?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent=new Intent(getActivity().getApplicationContext(), login.class);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent=new Intent(getActivity().getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
