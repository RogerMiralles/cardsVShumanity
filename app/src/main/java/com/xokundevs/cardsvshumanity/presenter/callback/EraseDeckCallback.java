package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.presenter.BarajasPresenter;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class EraseDeckCallback extends BaseCompletableCallback<BarajasPresenter.View> {

    public EraseDeckCallback(BarajasPresenter.View _view) {
        super(_view);
    }

    @Override
    public void onComplete() {
        view.hideProgress();
        view.onBorrarBarajaSuccess();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if(e instanceof ServiceError) {
            view.hideProgress();
            view.onBorrarBarajaFailure(((ServiceError) e).getErrorCode());
        }
    }
}
