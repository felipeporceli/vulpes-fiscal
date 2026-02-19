package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.CadastroPagamentoDTO;
import com.vulpesfiscal.demo.controllers.mappers.PagamentoMapper;
import com.vulpesfiscal.demo.controllers.specs.EstabelecimentoSpecs;
import com.vulpesfiscal.demo.controllers.specs.PagamentoSpecs;
import com.vulpesfiscal.demo.controllers.specs.ProdutoSpecs;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.exceptions.ValorRecebidoMenorException;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import com.vulpesfiscal.demo.repositories.PagamentoRepository;
import com.vulpesfiscal.demo.validator.PagamentoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PagamentoService {

    private final PagamentoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final PagamentoMapper pagamentoMapper;
    private final PagamentoValidator validator;

    // Metodo para salvar a nivel de serviço.
    public Pagamento salvar(Integer empresaId,
                            Integer estabelecimentoId,
                            CadastroPagamentoDTO dto) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada"));

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findByIdAndEmpresaId(estabelecimentoId, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estabelecimento não pertence à empresa informada"
                ));

        Pagamento pagamento = pagamentoMapper.toEntity(dto);
        pagamento.setEmpresa(empresa);
        pagamento.setEstabelecimento(estabelecimento);
        return repository.save(pagamento);
    }

    // Metodo para pesquisar com filtro a nível de serviço.
    public Page<Pagamento> pesquisar(Integer id,
                                     MetodoPagamento metodoPagamento,
                                     BigDecimal valorMin,
                                     BigDecimal valorMax,
                                     StatusPagamento statusPagamento,
                                     Integer empresaId,
                                     Integer estabelecimentoId,
                                     Integer pagina,
                                     Integer tamanhoPagina) {

        validator.validarPesquisar(empresaId, estabelecimentoId);

        // SELECT * FROM Estabelecimento WHERE 0 = 0
        Specification<Pagamento> specification = Specification.where
                ((root, query, cb) -> cb.conjunction());

        if (id != null) {
            specification = specification.and(PagamentoSpecs.idIgual(id));

        }
        if (metodoPagamento != null) {
            specification = specification.and(PagamentoSpecs.metodoPagamentoLike(metodoPagamento));
        }

        if (statusPagamento != null) {
            specification = specification.and(PagamentoSpecs.statusPagamentoIgual(statusPagamento));
        }

        if (empresaId != null) {
            specification = specification.and(PagamentoSpecs.empresaIdIgual(empresaId));
        }

        if (estabelecimentoId != null) {
            specification = specification.and(PagamentoSpecs.estabelecimentoIdIgual(estabelecimentoId));
        }

        if (valorMin != null || valorMax != null) {
            specification = specification.and(PagamentoSpecs.valorEntre(valorMin, valorMax));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specification, pageRequest);
    }


    public void processarPagamento(Pagamento pagamento) {

        // 1. Validação do valor bruto
        if (pagamento.getValor() == null || pagamento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da compra deve ser maior que zero.");
        }

        // 2. Tratamento do desconto
        if (pagamento.getDesconto() == null) {
            pagamento.setDesconto(BigDecimal.ZERO);
        }

        if (pagamento.getDesconto().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O desconto não pode ser negativo.");
        }

        if (pagamento.getDesconto().compareTo(pagamento.getValor()) > 0) {
            throw new IllegalArgumentException("O desconto não pode ser maior que o valor da compra.");
        }

        // 3. Cálculo do valor final
        pagamento.setValorFinal(pagamento.getValor().subtract(pagamento.getDesconto()));

        // 4. Validação do valor recebido (quando aplicável)
        if (pagamento.getValorRecebido() != null) {

            if (pagamento.getValorRecebido().compareTo(pagamento.getValorFinal()) < 0) {
                throw new IllegalArgumentException(
                        "O valor recebido é menor que o valor final da compra."
                );
            }

            // 5. Cálculo do troco
            pagamento.setTroco(pagamento.getValorRecebido().subtract(pagamento.getValorFinal()));

        } else {
            // Se não houve valor recebido, não há troco
            pagamento.setTroco(BigDecimal.ZERO);
        }
    }

}

