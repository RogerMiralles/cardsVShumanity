package com.xokundevs.cardsvshumanity.usecase;

import com.xokundevs.cardsvshumanity.repository.CcaRepository;
import com.xokundevs.cardsvshumanity.serviceoutput.ServiceSimpleDeckInfoListOutput;
import com.xokundevs.cardsvshumanity.utils.baseutils.BaseObservableUseCase;

import io.reactivex.rxjava3.core.Observable;

public class BarajaUseCase extends BaseObservableUseCase<Void, ServiceSimpleDeckInfoListOutput> {

    @Override
    protected Observable<ServiceSimpleDeckInfoListOutput> buildObservableUseCase(Void param) {
        return CcaRepository.getInstance().getBarajaBasic();
    }

}
