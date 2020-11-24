package com.xokundevs.cardsvshumanity.presenter.impl;

import com.xokundevs.cardsvshumanity.presenter.EraseUserPresenter;
import com.xokundevs.cardsvshumanity.presenter.callback.EraseUserCallback;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseUserInput;
import com.xokundevs.cardsvshumanity.usecase.EraseUserUseCase;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterImpl;

public class EraseUserPresenterImpl extends BasePresenterImpl<EraseUserPresenter.View> implements EraseUserPresenter {

    EraseUserUseCase eraseUserUseCase;

    public EraseUserPresenterImpl(EraseUserPresenter.View view){
        setView(view);
        eraseUserUseCase = new EraseUserUseCase();
    }

    @Override
    public void onEraseUser(ServiceEraseUserInput input) {
        view.showProgress();
        eraseUserUseCase.setParameters(input).execute(new EraseUserCallback(view));
    }

    @Override
    public void onDestroy() {
        eraseUserUseCase.dispose();

        eraseUserUseCase = null;
    }
}
