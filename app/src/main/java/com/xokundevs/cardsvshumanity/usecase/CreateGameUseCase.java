package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceCreateGameInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceCreateGameOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableUseCase;

import io.reactivex.rxjava3.core.Observable;

public class CreateGameUseCase extends BaseObservableUseCase<ServiceCreateGameInput, ServiceCreateGameOutput> {
    @Override
    protected Observable<ServiceCreateGameOutput> buildObservableUseCase(ServiceCreateGameInput param) {
        return CcaRepository.getInstance().getCreateGame(param);
    }
}
