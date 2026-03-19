package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoTributacaoDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProdutoTributacaoValidator {


    public void validarCamposObrigatorios(CadastroProdutoTributacaoDTO dto) {
        if (dto.getIdProduto() == null) {
            throw new CampoInvalidoException("idProduto",
                    "O idProduto é obrigatório.");
        }

        if (dto.getUf() == null || dto.getUf().isBlank()) {
            throw new CampoInvalidoException("uf", "Uf é obrigatória.");
        }

        if (dto.getCfop() == null || dto.getCfop().isBlank()) {
            throw new CampoInvalidoException("cfop",
                    "O CFOP é obrigatório.");
        }

        boolean temCstNormal = dto.getCstCofins() != null && !dto.getCstCofins().isBlank();
        boolean temCsosn = dto.getCsosnIcms() != null && !dto.getCsosnIcms().isBlank();

        if (!temCstNormal && !temCsosn) {
            throw new CampoInvalidoException("CST ICMS/CSOSN ICMS",
                    "Informe CST ICMS ou CSOSN ICMS.");
        }

        if (temCstNormal && temCsosn) {
            throw new CampoInvalidoException("CST Normal/CSOSN",
                    "Informe apenas CST ICMS ou CSOSN ICMS, não os dois.");
        }
    }

}

