package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.JoinLobbyPresenter;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetGameDataOutput;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class AvailableLobbiesCallback extends BaseObservableCallback<ServiceGetGameDataOutput, JoinLobbyPresenter.View> {

    public AvailableLobbiesCallback(@androidx.annotation.NonNull JoinLobbyPresenter.View view) {
        super(view);
    }

    @Override
    public void onNext(@NonNull ServiceGetGameDataOutput serviceGetGameDataOutput) {
        view.hideProgress();
        view.onGetAvailableLobbiesSuccess(serviceGetGameDataOutput);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        view.hideProgress();
        if(e instanceof ServiceError){
            view.onGetAvailableLobbiesError(((ServiceError) e).getErrorCode());
        }
        else{
            view.onGetAvailableLobbiesError(Connection.UNKNOWN_ERROR);
        }
    }
}
