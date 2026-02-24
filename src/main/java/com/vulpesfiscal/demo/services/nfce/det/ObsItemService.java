package com.vulpesfiscal.demo.services.nfce.det;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.obsitem.ObsItemDTO;
import org.springframework.stereotype.Service;

@Service
public class ObsItemService {

    public ObsItemDTO gerarObsItem() {
        ObsItemDTO obsItem = new ObsItemDTO();
        obsItem.setObsCont(null);
        obsItem.setObsFisco(null);

        return obsItem;
    }

}
