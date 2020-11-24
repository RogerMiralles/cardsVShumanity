package com.xokundevs.cardsvshumanity.presenter;

import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateUserInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenter;

public interface CreateUserPresenter extends BasePresenter<CreateUserPresenter.View> {

    void onCreateUser(ServiceCreateUserInput param);

    interface View extends BasePresenter.View {
        void onCreateUserSuccess();
        void onCreateUserError(int error);
    }
}
