package com.xokundevs.cardsvshumanity.presenter;

import com.xokundevs.cardsvshumanity.serviceinput.ServiceGetCardsDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetCardsDeckOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenter;

public interface ModifyDeckPresenter extends BasePresenter<ModifyDeckPresenter.View> {

    void getCardDeck(ServiceGetCardsDeckInput input);
    void saveCardDeck(ServiceSaveDeckInput input);

    interface View extends BasePresenter.View{
        void onGetCardDeckSuccess(ServiceGetCardsDeckOutput output);
        void onGetCardDeckFailure(int error);
        void onSaveDeckSuccess();
        void onSaveDeckError(int error);
    }
}
