package com.vulpesfiscal.demo.services.nfce.transporte;

import com.vulpesfiscal.demo.controllers.dtos.nfce.transporte.TransporteDTO;
import org.springframework.stereotype.Service;

@Service
public class TransporteService {

    public TransporteDTO gerarTransporte() {
        TransporteDTO transporte = new TransporteDTO();
        transporte.setModFrete(9);
        return transporte;
    }

}
