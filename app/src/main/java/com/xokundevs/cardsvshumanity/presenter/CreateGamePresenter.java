package com.xokundevs.cardsvshumanity.presenter;

import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateGameInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCreateGameOutput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoListOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenter;

public interface CreateGamePresenter extends BasePresenter<CreateGamePresenter.View> {

    void getSimpleDeck();
    void createGame(ServiceCreateGameInput param);

    interface View extends BasePresenter.View{
        void onGetSimpleDeckSuccess(ServiceSimpleDeckInfoListOutput receivedData);
        void onGetSimpleDeckError(int error);
        void onCreateGameSuccess(ServiceCreateGameOutput output);
        void onCreateGameError(int error);
    }
}
