package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseUserInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableUseCase;

import io.reactivex.rxjava3.core.Completable;

public class EraseUserUseCase extends BaseCompletableUseCase<ServiceEraseUserInput> {
    @Override
    protected Completable buildObservableUseCase(ServiceEraseUserInput param) {
        return CcaRepository.getInstance().eraseUser(param);
    }
}
