package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceinput.ServiceJoinGameInput;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceJoinGameOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableUseCase;

import io.reactivex.rxjava3.core.Observable;

public class JoinGameUseCase extends BaseObservableUseCase<ServiceJoinGameInput, ServiceJoinGameOutput> {

    @Override
    protected Observable<ServiceJoinGameOutput> buildObservableUseCase(ServiceJoinGameInput param) {
        return CcaRepository.getInstance().joinGame(param);
    }

}
