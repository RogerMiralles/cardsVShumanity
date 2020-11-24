package com.xokundevs.cardsvshumanity.presenter;

import com.xokundevs.cardsvshumanity.serviceinput.ServiceJoinGameInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetGameDataOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceJoinGameOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenter;

public interface JoinLobbyPresenter extends BasePresenter<JoinLobbyPresenter.View> {

    void getAvailableLobbies();
    void joinLobby(ServiceJoinGameInput param);

    interface View extends BasePresenter.View{
        void onGetAvailableLobbiesSuccess(ServiceGetGameDataOutput output);
        void onGetAvailableLobbiesError(int error);
        void onJoinLobbySuccess(ServiceJoinGameOutput output);
        void onJoinLobbyError(int error);
    }
}
