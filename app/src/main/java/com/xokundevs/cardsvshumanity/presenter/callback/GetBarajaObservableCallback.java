package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.presenter.BarajasPresenter;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoListOutput;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class GetBarajaObservableCallback extends BaseObservableCallback<ServiceSimpleDeckInfoListOutput,BarajasPresenter.View> {


    public GetBarajaObservableCallback(BarajasPresenter.View v) {
        super(v);
    }

    @Override
    public void onNext(@NonNull ServiceSimpleDeckInfoListOutput serviceSimpleDeckInfoListOutput) {
        view.onGetBarajasSuccess(serviceSimpleDeckInfoListOutput);
        view.hideProgress();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if(e instanceof ServiceError) {
            view.onBorrarBarajaFailure(((ServiceError) e).getErrorCode());
        }
        view.hideProgress();
    }
}
