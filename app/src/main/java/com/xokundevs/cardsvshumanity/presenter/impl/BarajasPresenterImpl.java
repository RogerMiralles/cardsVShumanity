package com.xokundevs.cardsvshumanity.presenter.impl;

import com.xokundevs.cardsvshumanity.presenter.BarajasPresenter;
import com.xokundevs.cardsvshumanity.presenter.callback.EraseDeckCallback;
import com.xokundevs.cardsvshumanity.presenter.callback.GetBarajaObservableCallback;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseDeckInput;
import com.xokundevs.cardsvshumanity.usecase.BarajaUseCase;
import com.xokundevs.cardsvshumanity.usecase.EraseDeckUseCase;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenterImpl;

public class BarajasPresenterImpl extends BasePresenterImpl<BarajasPresenter.View> implements BarajasPresenter {

    private BarajaUseCase barajaUseCase;
    private EraseDeckUseCase eraseDeckUseCase;


    public BarajasPresenterImpl(BarajasPresenter.View view){
        barajaUseCase = new BarajaUseCase();
        eraseDeckUseCase = new EraseDeckUseCase();
        this.view = view;
    }

    @Override
    public void getBaraja() {
        barajaUseCase.setParameters(null).execute(new GetBarajaObservableCallback(view));
    }

    @Override
    public void borrarBaraja(ServiceEraseDeckInput param) {
        eraseDeckUseCase.setParameters(param).execute(new EraseDeckCallback(view));
    }

    @Override
    public void onDestroy() {
        barajaUseCase.dispose();
        eraseDeckUseCase.dispose();
        barajaUseCase = null;
        eraseDeckUseCase = null;
    }
}
