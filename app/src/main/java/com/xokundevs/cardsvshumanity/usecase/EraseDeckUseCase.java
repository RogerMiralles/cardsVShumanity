package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceEraseDeckInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableUseCase;

import io.reactivex.rxjava3.core.Completable;

public class EraseDeckUseCase extends BaseCompletableUseCase<ServiceEraseDeckInput> {

    @Override
    protected Completable buildObservableUseCase(ServiceEraseDeckInput param) {
        return CcaRepository.getInstance().eraseUserDeck(param);
    }
}
