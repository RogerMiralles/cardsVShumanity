package com.xokundevs.cardsvshumanity.presenter.impl;

import com.xokundevs.cardsvshumanity.presenter.LoginPresenter;
import com.xokundevs.cardsvshumanity.presenter.callback.LoginCompletableCallback;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceLoginInput;
import com.xokundevs.cardsvshumanity.usecase.LoginUseCase;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterImpl;

public class LoginPresenterImpl extends BasePresenterImpl<LoginPresenter.View> implements LoginPresenter {

    LoginUseCase loginUseCase;

    public LoginPresenterImpl(LoginPresenter.View v){
        loginUseCase = new LoginUseCase();
        view = v;
    }

    @Override
    public void logIn(ServiceLoginInput input) {
        view.showProgress();
        loginUseCase.setParameters(input).execute(new LoginCompletableCallback(view));
    }

    @Override
    public void onDestroy() {
        loginUseCase.dispose();
        loginUseCase = null;
    }
}
