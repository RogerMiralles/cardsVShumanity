package com.xokundevs.cardsvshumanity.presenter;

import com.xokundevs.cardsvshumanity.serviceinput.ServiceLoginInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenter;

public interface LoginPresenter extends BasePresenter<LoginPresenter.View> {

    void logIn(ServiceLoginInput input);

    interface View extends BasePresenter.View{
        void onLoginSuccess();
        void onLoginFailure(int error);
    }
}
