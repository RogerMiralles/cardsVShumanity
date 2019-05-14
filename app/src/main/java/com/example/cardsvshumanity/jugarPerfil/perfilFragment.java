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


import com.example.cardsvshumanity.R;
import com.example.cardsvshumanity.logReg.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView=inflater.inflate(R.layout.fragment_perfil, container, false);
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.tituloPerfil));
        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        if(mUser==null){
            noUsuari();
        }
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
                        Intent intent=new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), login.class);
                        startActivity(intent);
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

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
