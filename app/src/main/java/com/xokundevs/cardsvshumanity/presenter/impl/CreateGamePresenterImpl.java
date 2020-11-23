package com.xokundevs.cardsvshumanity.presenter.impl;

import com.xokundevs.cardsvshumanity.presenter.CreateGamePresenter;
import com.xokundevs.cardsvshumanity.presenter.callback.CreateGameObservableCallback;
import com.xokundevs.cardsvshumanity.presenter.callback.CreateGameGetDeckObservableCallback;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateGameInput;
import com.xokundevs.cardsvshumanity.usecase.BarajaUseCase;
import com.xokundevs.cardsvshumanity.usecase.CreateGameUseCase;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterImpl;

public class CreateGamePresenterImpl extends BasePresenterImpl<CreateGamePresenter.View> implements CreateGamePresenter{

    BarajaUseCase barajaUseCase;
    CreateGameUseCase createGameUseCase;

    public CreateGamePresenterImpl(CreateGamePresenter.View view){
        this.view = view;
        barajaUseCase = new BarajaUseCase();
        createGameUseCase = new CreateGameUseCase();
    }

    @Override
    public void getSimpleDeck() {
        barajaUseCase.setParameters(null).execute(new CreateGameGetDeckObservableCallback(view));
    }

    @Override
    public void createGame(ServiceCreateGameInput param) {
        createGameUseCase.setParameters(param).execute(new CreateGameObservableCallback(view));
    }

    @Override
    public void onDestroy() {
        barajaUseCase.dispose();
    }
}
