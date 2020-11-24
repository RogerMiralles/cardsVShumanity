package com.xokundevs.cardsvshumanity.presenter;

import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseUserInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BasePresenter;

public interface EraseUserPresenter extends BasePresenter<EraseUserPresenter.View> {

    void onEraseUser(ServiceEraseUserInput input);

    interface View extends BasePresenter.View{
        void onEraseUserSuccess();
        void onEraseUserError(int error);
    }
}
