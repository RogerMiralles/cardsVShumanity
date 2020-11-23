package com.xokundevs.cardsvshumanity.utils.baseutils;

public interface BasePresenter<View extends BasePresenter.View> {

    View getView();
    void onDestroy();
    void setView(View view);

    interface View {
        void showProgress();
        void hideProgress();
    }
}
