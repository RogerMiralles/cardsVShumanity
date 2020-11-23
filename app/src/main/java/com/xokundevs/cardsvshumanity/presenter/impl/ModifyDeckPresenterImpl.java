package com.xokundevs.cardsvshumanity.presenter.impl;

import com.xokundevs.cardsvshumanity.presenter.ModifyDeckPresenter;
import com.xokundevs.cardsvshumanity.presenter.callback.GetCardDeckObservableCallback;
import com.xokundevs.cardsvshumanity.presenter.callback.SaveBarajaCallback;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceGetCardsDeckInput;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckInput;
import com.xokundevs.cardsvshumanity.usecase.ModifyDeckUseCase;
import com.xokundevs.cardsvshumanity.usecase.SaveDeckUseCase;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterImpl;

public class ModifyDeckPresenterImpl extends BasePresenterImpl<ModifyDeckPresenter.View> implements ModifyDeckPresenter {

    ModifyDeckUseCase modifyDeckUseCase;
    SaveDeckUseCase saveDeckUseCase;

    public ModifyDeckPresenterImpl(ModifyDeckPresenter.View view){
        this.view = view;
        modifyDeckUseCase = new ModifyDeckUseCase();
        saveDeckUseCase = new SaveDeckUseCase();
    }

    @Override
    public void getCardDeck(ServiceGetCardsDeckInput input) {
        modifyDeckUseCase.setParameters(input).execute(new GetCardDeckObservableCallback(view));
    }

    @Override
    public void saveCardDeck(ServiceSaveDeckInput input) {
        saveDeckUseCase.setParameters(input).execute(new SaveBarajaCallback(view));
    }

    @Override
    public void onDestroy() {
        modifyDeckUseCase.dispose();
        saveDeckUseCase.dispose();

        modifyDeckUseCase = null;
        saveDeckUseCase = null;
    }
}
