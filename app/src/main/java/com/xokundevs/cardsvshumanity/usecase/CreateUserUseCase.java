package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateUserInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableUseCase;

import io.reactivex.rxjava3.core.Completable;

public class CreateUserUseCase extends BaseCompletableUseCase<ServiceCreateUserInput> {

    @Override
    protected Completable buildObservableUseCase(ServiceCreateUserInput param) {
        return CcaRepository.getInstance().createUser(param);
    }
}
