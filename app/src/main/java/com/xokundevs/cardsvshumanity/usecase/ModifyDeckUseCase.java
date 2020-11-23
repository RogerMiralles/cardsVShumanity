package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceGetCardsDeckInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetCardsDeckOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableUseCase;

import io.reactivex.rxjava3.core.Observable;

public class ModifyDeckUseCase extends BaseObservableUseCase<ServiceGetCardsDeckInput, ServiceGetCardsDeckOutput> {
    @Override
    protected Observable<ServiceGetCardsDeckOutput> buildObservableUseCase(ServiceGetCardsDeckInput param) {
        return CcaRepository.getInstance().getCardsFromDeck(param);
    }
}
