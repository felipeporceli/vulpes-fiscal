package com.vulpesfiscal.demo.services.nfce.total;

import com.vulpesfiscal.demo.controllers.dtos.nfce.total.TotalDTO;
import org.springframework.stereotype.Service;

@Service
public class TotalService {

    public TotalDTO gerarTotal() {

        TotalDTO total = new TotalDTO();
        total.setIbscbsTot(null);
        total.setIssqNtot(null);
        total.setIsTot(null);
        total.setIcmsTot(null);
        total.setVNFTot(null);
        total.setRetTrib(null);

        return total;

    }

}
