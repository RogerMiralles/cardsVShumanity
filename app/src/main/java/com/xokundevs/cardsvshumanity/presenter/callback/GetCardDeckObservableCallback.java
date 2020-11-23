package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.presenter.ModifyDeckPresenter;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetCardsDeckOutput;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class GetCardDeckObservableCallback extends BaseObservableCallback<ServiceGetCardsDeckOutput, ModifyDeckPresenter.View> {

    public GetCardDeckObservableCallback(ModifyDeckPresenter.View v) {
        super(v);
    }

    @Override
    public void onNext(@NonNull ServiceGetCardsDeckOutput serviceGetCardsDeckOutput) {
        view.onGetCardDeckSuccess(serviceGetCardsDeckOutput);
        view.hideProgress();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if(e instanceof ServiceError){
            view.onGetCardDeckFailure(((ServiceError) e).getErrorCode());
            view.hideProgress();
        }
    }
}
