package com.vulpesfiscal.demo.validator;

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
public class ProdutoValidator {

    private final ProdutoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final ProdutoMapper mapper;

    public void validar(Produto produto, Empresa empresa) {

        if (produto.getCodigoBarras() == null && repository.existsByEmpresaIdAndIdProduto(empresa.getId(), produto.getIdProduto() )) {
            throw new RegistroDuplicadoException(
                    "Já existe produto com esse ID para essa empresa"
            );
        }

        if (repository.existsByEmpresaIdAndCodigoBarras(empresa.getId(), produto.getCodigoBarras())) {
            throw new CampoInvalidoException(
                    "codigoBarras",
                    "Já existe um produto cadastrado com esse Código de Barras para esta empresa"
            );
        }

        if (repository.existsByEmpresaIdAndIdProduto(
                empresa.getId(),
                produto.getIdProduto()
        )) {
            throw new RegistroDuplicadoException(
                    "Já existe produto com esse ID para essa empresa"
            );
        }
    }


    public Produto pesquisarPorEmpresaEIdProduto(Integer empresaId, Integer idProduto) {
        return repository
                .findByEmpresaIdAndIdProduto(empresaId, idProduto)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto ou empresa não encontrado."
                        )
                );
    }

    public void validarAtualizacao(Produto produto) {

        if (repository.existsByEmpresaIdAndIdProdutoAndIdTecnicoNot(
                produto.getEmpresa().getId(),
                produto.getIdProduto(),
                produto.getIdTecnico()
        )) {
            throw new RegistroDuplicadoException(
                    "Já existe outro produto com esse ID nesta empresa"
            );
        }
    }


    public void validarPesquisa(Integer empresaId) {
        if (empresaRepository.existsById(empresaId)) {
        }

        else {
            throw new RecursoNaoEncontradoException("Empresa não encontrada.");
        }
    }




}

