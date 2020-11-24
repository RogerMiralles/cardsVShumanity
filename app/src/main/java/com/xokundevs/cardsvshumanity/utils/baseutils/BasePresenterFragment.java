package com.xokundevs.cardsvshumanity.utils.baseutils;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.xokundevs.cardsvshumanity.utils.CustomLoadingDialog;

public abstract class BasePresenterFragment<P extends BasePresenter<? extends BasePresenter.View>> extends BaseFragment implements BasePresenter.View {
    private P presenter;

    protected P getPresenter(){
        return presenter;
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showProgress() {
        CustomLoadingDialog.showDialog(getFragmentManager());
    }

    @Override
    public void hideProgress() {
        CustomLoadingDialog.hideDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
