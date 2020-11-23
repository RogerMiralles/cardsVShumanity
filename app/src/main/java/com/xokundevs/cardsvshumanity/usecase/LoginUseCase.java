package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceLoginInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableUseCase;

import io.reactivex.rxjava3.core.Completable;

public class LoginUseCase extends BaseCompletableUseCase<ServiceLoginInput> {
    @Override
    protected Completable buildObservableUseCase(ServiceLoginInput param) {
        return CcaRepository.getInstance().logInUser(param);
    }
}
