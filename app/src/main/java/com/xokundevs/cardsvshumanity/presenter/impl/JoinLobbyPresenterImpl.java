package com.xokundevs.cardsvshumanity.presenter.impl;

import com.xokundevs.cardsvshumanity.presenter.JoinLobbyPresenter;
import com.xokundevs.cardsvshumanity.presenter.callback.AvailableLobbiesCallback;
import com.xokundevs.cardsvshumanity.presenter.callback.JoinLobbyObservableCallback;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceJoinGameInput;
import com.xokundevs.cardsvshumanity.usecase.AvailableLobbiesUseCase;
import com.xokundevs.cardsvshumanity.usecase.JoinGameUseCase;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterImpl;

public class JoinLobbyPresenterImpl extends BasePresenterImpl<JoinLobbyPresenter.View> implements JoinLobbyPresenter {
    private AvailableLobbiesUseCase availableLobbiesUseCase;
    private JoinGameUseCase joinGameUseCase;

    public JoinLobbyPresenterImpl(JoinLobbyPresenter.View view){
        setView(view);
        availableLobbiesUseCase = new AvailableLobbiesUseCase();
        joinGameUseCase = new JoinGameUseCase();
    }

    @Override
    public void getAvailableLobbies() {
        view.showProgress();
        availableLobbiesUseCase.setParameters(null).execute(new AvailableLobbiesCallback(view));
    }

    @Override
    public void joinLobby(ServiceJoinGameInput param) {
        view.showProgress();
        joinGameUseCase.setParameters(param).execute(new JoinLobbyObservableCallback(view));
    }

    @Override
    public void onDestroy() {
        availableLobbiesUseCase.dispose();
        joinGameUseCase.dispose();

        joinGameUseCase = null;
        availableLobbiesUseCase = null;
    }
}
