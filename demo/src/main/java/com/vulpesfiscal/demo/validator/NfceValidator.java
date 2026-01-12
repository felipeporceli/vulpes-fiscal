package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.controllers.dtos.CadastroNfceDTO;
import com.vulpesfiscal.demo.controllers.mappers.ProdutoMapper;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.exceptions.RegistroDuplicadoException;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.repositories.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NfceValidator {

    private final ProdutoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final ProdutoMapper mapper;

    public void validar(Integer empresaId,
                        Integer estabelecimentoId,
                        Integer usuarioId,
                        CadastroNfceDTO dto) {

    }

}

