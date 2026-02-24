package com.vulpesfiscal.demo.services.nfce.det;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.impostodevol.ImpostoDevolDTO;
import org.springframework.stereotype.Service;

@Service
public class ImpostoDevolService {

    public ImpostoDevolDTO gerarImpostoDevol() {
        ImpostoDevolDTO impostoDevol = new ImpostoDevolDTO();

        impostoDevol.setIpi(null);
        impostoDevol.setPDevol(null);

        return impostoDevol;

    }


}
