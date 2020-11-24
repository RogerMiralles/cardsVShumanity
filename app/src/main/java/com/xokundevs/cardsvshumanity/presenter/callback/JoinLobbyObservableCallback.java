package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.javaConCod.Connection;
import com.xokundevs.cardsvshumanity.presenter.JoinLobbyPresenter;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceJoinGameOutput;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class JoinLobbyObservableCallback extends BaseObservableCallback<ServiceJoinGameOutput, JoinLobbyPresenter.View> {

    public JoinLobbyObservableCallback(@androidx.annotation.NonNull JoinLobbyPresenter.View view) {
        super(view);
    }

    @Override
    public void onNext(@NonNull ServiceJoinGameOutput output) {
        view.hideProgress();
        view.onJoinLobbySuccess(output);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        view.hideProgress();
        if(e instanceof ServiceError){
            view.onJoinLobbyError(((ServiceError) e).getErrorCode());
        }
        else {
            view.onJoinLobbyError(Connection.UNKNOWN_ERROR);
        }
    }
}
