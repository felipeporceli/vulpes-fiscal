package com.vulpesfiscal.demo.validator;


import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import com.vulpesfiscal.demo.repositories.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoValidator {

    private final EstabelecimentoRepository estabelecimentoRepository;


    public void validarPesquisar (Integer empresaId,
                                  Integer estabelecimentoId) {

        if (empresaId != null && estabelecimentoId != null) {
            Estabelecimento estabelecimento = estabelecimentoRepository
                    .findByIdAndEmpresaId(estabelecimentoId, empresaId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Estabelecimento ou Empresa nao encontrados."
                    ));
        }


    }

}
