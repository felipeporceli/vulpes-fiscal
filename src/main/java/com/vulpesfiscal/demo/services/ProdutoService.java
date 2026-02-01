package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoDTO;
import com.vulpesfiscal.demo.controllers.mappers.ProdutoMapper;
import com.vulpesfiscal.demo.controllers.specs.ProdutoSpecs;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.ProdutoRepository;
import com.vulpesfiscal.demo.validator.ProdutoValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final ProdutoValidator validator;
    private final EmpresaService empresaService;
    private final ProdutoMapper mapper;
    private final EstabelecimentoService estabelecimentoService;


    // Metodo para salvar a nivel de serviço. Utilizando o DTO para salvar apenas com o ID da empresa.
    public Produto salvar(CadastroProdutoDTO dto,
                          Integer empresaId,
                          Integer estabelecimentoid) {
        Empresa empresa = empresaService.buscarPorId(empresaId);
        Estabelecimento estabelecimento = estabelecimentoService.buscarPorId(estabelecimentoid);
        Produto produto = mapper.toEntity(dto);
        produto.setEmpresa(empresa);
        produto.setEstabelecimento(estabelecimento);
        validator.validar(produto, empresa);
        return repository.save(produto);
    }

    // Metodo para pesquisar com filtro a nível de serviço.
    public Page<Produto> pesquisar(Integer empresaId,
                                   Integer idProduto,
                                   String descricao,
                                   String codigoBarras,
                                   Integer ncm,
                                   BigDecimal precoMin,
                                   BigDecimal precoMax,
                                   Boolean ativo,
                                   Integer pagina,
                                   Integer tamanhoPagina) {
        // SELECT * FROM Produto WHERE 0 = 0
        Specification<Produto> specification =
                Specification.where(ProdutoSpecs.empresaIdIgual(empresaId));

        if (idProduto != null) {
            specification = specification.and(ProdutoSpecs.idProdutoIgual(idProduto));
        }

        if (descricao != null) {
            specification = specification.and(ProdutoSpecs.descricaoLike(descricao));
        }

        if (codigoBarras != null) {
            specification = specification.and(ProdutoSpecs.codigoBarrasLike(codigoBarras));
        }

        if (ncm != null) {
            specification = specification.and(ProdutoSpecs.ncmIgual(ncm));
        }

        if (precoMin != null || precoMax != null) {
            specification = specification.and(ProdutoSpecs.precoEntre(precoMin, precoMax));
        }

        if (ativo != null) {
            specification = specification.and(ProdutoSpecs.ativoIgual(ativo));
        }

        validator.validarPesquisa(empresaId);
        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specification, pageRequest);
    }
    public void deletar(Integer empresaId, Integer idProduto) {
        Produto produto = repository
                .findByEmpresaIdAndIdProduto(empresaId, idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto ou empresa não encontrado."
                ));

        repository.delete(produto);
    }



    @Transactional
    public void atualizar(Integer empresaId, Integer idProduto, AtualizacaoProdutoDTO dto) {
        Produto produto = repository.findByEmpresaIdAndIdProduto(empresaId, idProduto)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto ou empresa não encontrado."
                ));
        mapper.toEntityUpdate(dto, produto);
        validator.validarAtualizacao(produto);
        repository.save(produto);
    }



    public Produto buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                "Produto não encontrado para o ID informado"
                        )
                );
    }




}