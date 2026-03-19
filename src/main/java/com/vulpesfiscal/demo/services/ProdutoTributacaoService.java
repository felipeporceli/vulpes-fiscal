package com.vulpesfiscal.demo.services;

import java.util.List;
import java.util.Objects;

import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoTributacaoDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.entities.ProdutoTributacao;
import com.vulpesfiscal.demo.exceptions.*;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.repositories.ProdutoRepository;
import com.vulpesfiscal.demo.repositories.ProdutoTributacaoRepository;
import com.vulpesfiscal.demo.validator.ProdutoTributacaoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProdutoTributacaoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoTributacaoRepository produtoTributacaoRepository;
    private final EmpresaRepository empresaRepository;
    private final ProdutoTributacaoValidator validator;

    public ProdutoTributacao cadastrar(CadastroProdutoTributacaoDTO dto, Integer empresaId) {
        Objects.requireNonNull(dto, "DTO de tributação não pode ser nulo");
        validator.validarCamposObrigatorios(dto);

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + empresaId));

        Produto produto = buscarProdutoPorReferencia(empresaId, dto.getIdProduto());

        boolean jaExiste = produtoTributacaoRepository.existsByEmpresaIdAndProdutoIdTecnicoAndUf(
                empresaId,
                produto.getIdTecnico(),
                dto.getUf()
        );

        if (jaExiste) {
            throw new RegistroDuplicadoException(
                    "Já existe tributação cadastrada para este produto nesta UF."
            );
        }

        ProdutoTributacao tributacao = new ProdutoTributacao();
        tributacao.setEmpresa(empresa);
        tributacao.setProduto(produto);

        preencherCampos(tributacao, dto);

        return produtoTributacaoRepository.save(tributacao);
    }

    public ProdutoTributacao atualizar(Long id, CadastroProdutoTributacaoDTO dto, Integer empresaId) {
        Objects.requireNonNull(id, "Id da tributação não pode ser nulo");
        Objects.requireNonNull(dto, "DTO de tributação não pode ser nulo");
        validator.validarCamposObrigatorios(dto);

        ProdutoTributacao tributacao = produtoTributacaoRepository.findById(id)
                .orElseThrow(() -> new TributacaoNaoEncontradaException(
                        "Tributação não encontrada: " + id
                ));

        if (!tributacao.getEmpresa().getId().equals(empresaId)) {
            throw new TributacaoNaoPertenceAEmpresaException(
                    "Tributação não pertence à empresa informada."
            );
        }

        Produto produto = buscarProdutoPorReferencia(empresaId, dto.getIdProduto());

        ProdutoTributacao tributacaoExistenteMesmaUf =
                produtoTributacaoRepository.findByEmpresaIdAndProdutoIdTecnicoAndUf(
                        empresaId,
                        produto.getIdTecnico(),
                        dto.getUf()
                ).orElse(null);

        if (tributacaoExistenteMesmaUf != null && !tributacaoExistenteMesmaUf.getId().equals(id)) {
            throw new RegistroDuplicadoException (
                    "Já existe outra tributação para este produto nesta UF."
            );
        }

        tributacao.setProduto(produto);
        preencherCampos(tributacao, dto);

        return produtoTributacaoRepository.save(tributacao);
    }

    public void deletar(Long id, Integer empresaId) {

        ProdutoTributacao tributacao = produtoTributacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tributação não encontrada."));

        if (!tributacao.getEmpresa().getId().equals(empresaId)) {
            throw new RuntimeException("Tributação não pertence à empresa informada.");
        }

        produtoTributacaoRepository.delete(tributacao);
    }

    private Produto buscarProdutoPorReferencia(Integer empresaId, Integer idProdutoReferencia) {
        if (idProdutoReferencia == null) {
            throw new CampoInvalidoException("idProduto",
                    "O idProduto referencial é obrigatório.");
        }

        return produtoRepository.findByEmpresaIdAndIdProduto(empresaId, idProdutoReferencia)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(
                        "Produto não encontrado para a referência informada: " + idProdutoReferencia
                ));
    }

    private void preencherCampos(ProdutoTributacao tributacao,
                                 CadastroProdutoTributacaoDTO dto) {
        tributacao.setUf(normalizarUf(dto.getUf()));
        tributacao.setCfop(dto.getCfop());
        tributacao.setCstIcms(dto.getCstIcms());
        tributacao.setCsosnIcms(dto.getCsosnIcms());
        tributacao.setAliquotaIcms(dto.getAliquotaIcms());
        tributacao.setPFcp(dto.getPFcp());
        tributacao.setPRedBc(dto.pRedBc);
        tributacao.setTemStAnterior(Boolean.TRUE.equals(dto.getTemStAnterior()));
        tributacao.setCstPis(dto.getCstPis());
        tributacao.setAliquotaPis(dto.getAliquotaPis());
        tributacao.setCstCofins(dto.getCstCofins());
        tributacao.setAliquotaCofins(dto.getAliquotaCofins());
        tributacao.setRegimeTributarioEmpresa(dto.getRegimeTributarioEmpresa());
    }

    @Transactional(readOnly = true)
    public ProdutoTributacao buscarPorProdutoEUf(Integer empresaId, Integer idProduto, String uf) {

        Produto produto = produtoRepository
                .findByEmpresaIdAndIdProduto(empresaId, idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado para a referência informada."));

        return produtoTributacaoRepository
                .findByEmpresaIdAndProdutoIdTecnicoAndUf(
                        empresaId,
                        produto.getIdTecnico(),
                        uf.toUpperCase()
                )
                .orElseThrow(() -> new RuntimeException("Tributação não encontrada para o produto e UF."));
    }

    private String normalizarUf(String uf) {
        return uf == null ? null : uf.trim().toUpperCase();
    }
}
