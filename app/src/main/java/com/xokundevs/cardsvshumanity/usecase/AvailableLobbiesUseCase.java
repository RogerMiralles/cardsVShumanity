package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceGetGameDataOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableUseCase;

import io.reactivex.rxjava3.core.Observable;

public class AvailableLobbiesUseCase extends BaseObservableUseCase<Void, ServiceGetGameDataOutput> {
    @Override
    protected Observable<ServiceGetGameDataOutput> buildObservableUseCase(Void param) {
        return CcaRepository.getInstance().getAvaliableLobbies();
    }
}
