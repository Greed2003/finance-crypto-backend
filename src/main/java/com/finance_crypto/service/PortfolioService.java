package com.finance_crypto.service;

import com.finance_crypto.dto.AssetHoldingDTO;
import com.finance_crypto.entity.Transaction;
import com.finance_crypto.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    // 1. Lógica real: Calcula o saldo total multiplicando as moedas pelo preço médio
    public double calcularSaldoTotal(Long usuarioId) {
        List<AssetHoldingDTO> ativos = calcularPortfolio(usuarioId);
        
        // Soma o valor total estimado ou retorna 0.0 se a carteira estiver vazia
        return ativos.stream().mapToDouble(a -> a.getHolding() * a.getPrecoMedio()).sum();
    }

    // 2. Lógica real: Adiciona um ativo diretamente na carteira (salvando no banco)
    public boolean adicionarAtivo(Long usuarioId, String simbolo, double quantidade) {
        try {
            Transaction novaTransacao = new Transaction();
            novaTransacao.setUsuarioId(usuarioId);
            novaTransacao.setSimbolo(simbolo);
            novaTransacao.setQuantidade(quantidade);
            novaTransacao.setPrecoUnitario(0.0); // O preço pode ser atualizado via TransactionService depois
            novaTransacao.setTipo("BUY");

            portfolioRepository.save(novaTransacao);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao adicionar ativo no portfólio: " + e.getMessage());
            return false;
        }
    }

    // 3. Método auxiliar que agrupa as moedas e calcula o holding e preço médio
    public List<AssetHoldingDTO> calcularPortfolio(Long usuarioId) {
        List<Transaction> transacoes = portfolioRepository.findByUsuarioId(usuarioId);
        
        if (transacoes.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<Transaction>> transacoesPorAtivo = transacoes.stream()
                .collect(Collectors.groupingBy(Transaction::getSimbolo));

        List<AssetHoldingDTO> portfolio = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : transacoesPorAtivo.entrySet()) {
            String simbolo = entry.getKey();
            List<Transaction> listaAtivo = entry.getValue();

            double qtdTotal = 0.0;
            double custoTotal = 0.0;

            for (Transaction t : listaAtivo) {
                if ("BUY".equalsIgnoreCase(t.getTipo()) || t.getTipo() == null) {
                    qtdTotal += t.getQuantidade();
                    custoTotal += (t.getQuantidade() * t.getPrecoUnitario());
                } 
            }

            double precoMedio = (qtdTotal > 0) ? (custoTotal / qtdTotal) : 0.0;
            portfolio.add(new AssetHoldingDTO(simbolo, qtdTotal, precoMedio));
        }

        return portfolio;
    }
}