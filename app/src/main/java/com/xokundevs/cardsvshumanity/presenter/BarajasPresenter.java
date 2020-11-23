package com.xokundevs.cardsvshumanity.presenter;

import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseDeckInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoListOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenter;

public interface BarajasPresenter extends BasePresenter<BarajasPresenter.View> {

    void getBaraja();
    void borrarBaraja(ServiceEraseDeckInput param);

    interface View extends BasePresenter.View{
        void onBorrarBarajaSuccess();
        void onGetBarajasSuccess(ServiceSimpleDeckInfoListOutput receivedData);
        void onBorrarBarajaFailure(int error);
        void onGetBarajaFailure(int error);
    }
}
