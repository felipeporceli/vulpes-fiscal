package com.vulpesfiscal.demo.services.nfce.det.imposto;

import com.vulpesfiscal.demo.entities.ItemVenda;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObterRegimeService {

    public RegimeTributarioEmpresa obterRegime(ItemVenda item) {
        return item.getVenda()
                .getEmpresa()
                .getRegimeTributario();
    }

}
