package com.xokundevs.cardsvshumanity.presenter.callback;

import com.xokundevs.cardsvshumanity.presenter.LoginPresenter;
import com.xokundevs.cardsvshumanity.utils.ServiceError;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableCallback;

import io.reactivex.rxjava3.annotations.NonNull;

public class LoginCompletableCallback extends BaseCompletableCallback<LoginPresenter.View> {

    public LoginCompletableCallback(LoginPresenter.View v) {
        super(v);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if(e instanceof ServiceError) {
            view.onLoginFailure(((ServiceError) e).getErrorCode());
            view.hideProgress();
        }
    }

    @Override
    public void onComplete() {
        view.onLoginSuccess();
        view.hideProgress();
    }
}
