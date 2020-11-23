package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceSaveDeckInput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseCompletableUseCase;

import io.reactivex.rxjava3.core.Completable;

public class SaveDeckUseCase extends BaseCompletableUseCase<ServiceSaveDeckInput> {

    @Override
    protected Completable buildObservableUseCase(ServiceSaveDeckInput param) {
        return CcaRepository.getInstance().saveUserDeck(param);
    }
}
