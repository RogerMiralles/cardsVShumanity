package com.xokundevs.cardsvshumanity.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.xokundevs.cardsvshumanity.R;

public class CustomLoadingDialog extends DialogFragment {

    private static DialogFragment dialogFragment;

    public CustomLoadingDialog() {
        setStyle(STYLE_NO_FRAME, 0);
        setCancelable(false);
    }


    public static void showDialog(FragmentManager fragmentManager) {
        if (dialogFragment == null) {
            dialogFragment = new CustomLoadingDialog();
        }
        dialogFragment.show(fragmentManager, "DIALOG_FRAGMENT");
    }

    public static void hideDialog() {
        if (dialogFragment != null && dialogFragment.isAdded()) {
            dialogFragment.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.dialog_loading_screen, container);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return v;
    }
}
