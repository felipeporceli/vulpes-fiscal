package com.vulpesfiscal.demo.services.nfce.det;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.dfereferenciado.DfeReferenciadoDTO;
import org.springframework.stereotype.Service;

@Service
public class DfeReferenciadoService {

    public DfeReferenciadoDTO gerarDfeReferenciado() {

        DfeReferenciadoDTO dfeReferenciado = new DfeReferenciadoDTO();
        dfeReferenciado.setChaveAcesso(null);
        dfeReferenciado.setNItem(null);

        return dfeReferenciado;

    }

}
