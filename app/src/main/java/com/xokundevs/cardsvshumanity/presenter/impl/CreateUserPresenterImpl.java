package com.xokundevs.cardsvshumanity.presenter.impl;

import com.xokundevs.cardsvshumanity.presenter.CreateUserPresenter;
import com.xokundevs.cardsvshumanity.presenter.callback.CreateUserCallback;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateUserInput;
import com.xokundevs.cardsvshumanity.usecase.CreateUserUseCase;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterImpl;

public class CreateUserPresenterImpl extends BasePresenterImpl<CreateUserPresenter.View> implements CreateUserPresenter {

    CreateUserUseCase createUserUseCase;

    public CreateUserPresenterImpl(CreateUserPresenter.View view){
        setView(view);
        createUserUseCase = new CreateUserUseCase();
    }

    @Override
    public void onCreateUser(ServiceCreateUserInput param) {
        view.showProgress();
        createUserUseCase.setParameters(param).execute(new CreateUserCallback(view));
    }

    @Override
    public void onDestroy() {
        createUserUseCase.dispose();

        createUserUseCase = null;
    }
}
