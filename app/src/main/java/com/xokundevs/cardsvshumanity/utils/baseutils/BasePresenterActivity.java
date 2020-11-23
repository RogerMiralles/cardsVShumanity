package com.xokundevs.cardsvshumanity.utils.baseutils;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.xokundevs.cardsvshumanity.utils.CustomLoadingDialog;

public abstract class BasePresenterActivity<P extends BasePresenter<? extends BasePresenter.View>> extends BaseActivity implements BasePresenter.View {

    private P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected P getPresenter(){
        return presenter;
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showProgress() {
        CustomLoadingDialog.showDialog(getSupportFragmentManager());
    }

    @Override
    public void hideProgress() {
        CustomLoadingDialog.hideDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
