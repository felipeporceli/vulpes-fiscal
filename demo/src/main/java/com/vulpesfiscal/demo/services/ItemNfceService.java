package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoDTO;
import com.vulpesfiscal.demo.controllers.mappers.ItemNfceMapper;
import com.vulpesfiscal.demo.controllers.mappers.ProdutoMapper;
import com.vulpesfiscal.demo.controllers.specs.ProdutoSpecs;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.ItemNfceRepository;
import com.vulpesfiscal.demo.repositories.ProdutoRepository;
import com.vulpesfiscal.demo.validator.ItemNfceValidator;
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
public class ItemNfceService {

    private final ItemNfceRepository repository;
    private final ItemNfceValidator validator;
    private final ItemNfceMapper mapper;


    /*// Metodo para salvar a nivel de serviço. Utilizando o DTO para salvar apenas com o ID da empresa.
    public Produto salvar(CadastroProdutoDTO dto, Integer empresaId) {
        Empresa empresa = empresaService.buscarPorId(empresaId);
        Produto produto = mapper.toEntity(dto);
        produto.setEmpresa(empresa);
        validator.validar(produto, empresa);
        return repository.save(produto);
    }*/
}