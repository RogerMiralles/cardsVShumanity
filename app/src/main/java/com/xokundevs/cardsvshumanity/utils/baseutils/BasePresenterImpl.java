package com.xokundevs.cardsvshumanity.utils.baseutils;

public abstract class BasePresenterImpl<V extends BasePresenter.View> implements BasePresenter<V> {

    protected V view;

    @Override
    public V getView() {
        return view;
    }

    @Override
    public void setView(View view) {
        this.view = (V) view;
    }
}
