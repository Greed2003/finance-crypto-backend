package com.finance_crypto.service;

import com.finance_crypto.dto.AssetHoldingDTO;
import com.finance_crypto.dto.RankingAtivoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final RestTemplate restTemplate;
    private final PortfolioService portfolioService; // 1. Injetamos o novo serviço

    // 2. Atualize o construtor para receber ambos os serviços
    public RankingService(RestTemplate restTemplate, PortfolioService portfolioService) {
        this.restTemplate = restTemplate;
        this.portfolioService = portfolioService;
    }

    public List<RankingAtivoDTO> obterRankingAtivos(Long usuarioId) {

        List<RankingAtivoDTO> ativosDoMercado = buscarDadosCriptoNoMercado(); 

        List<AssetHoldingDTO> portfolioUsuario = portfolioService.calcularPortfolio(usuarioId);

        Map<String, Double> mapaPrecosMedios = portfolioUsuario.stream()
                .collect(Collectors.toMap(
                        ativo -> ativo.getSimbolo().toUpperCase(),
                        AssetHoldingDTO::getPrecoMedio,
                        (antigo, novo) -> antigo // Evita erros se houver duplicatas
                ));

        for (RankingAtivoDTO ativo : ativosDoMercado) {
            String simbolo = ativo.getSimbolo().toUpperCase();
            
            if (mapaPrecosMedios.containsKey(simbolo)) {
                double precoMedioReal = mapaPrecosMedios.get(simbolo);
                
                // Atualiza o DTO com os valores reais calculados
                ativo.setPrecoMedioCompra(precoMedioReal);
                
                // Recalcula a porcentagem de lucro/prejuízo com base no preço real vs preço atual de mercado
                double novaPorcentagem = calcularPorcentagemLucro(precoMedioReal, ativo.getPrecoBrl());
                ativo.setPercentualLucro(novaPorcentagem);
            }
        }

        return ativosDoMercado;
    }

    public double calcularPorcentagemLucro(double precoCompra, double precoAtual) {
        if (precoCompra == 0) return 0.0;
        return ((precoAtual - precoCompra) / precoCompra) * 100.0;
    }

    private List<RankingAtivoDTO> buscarDadosCriptoNoMercado() {
        // URL da CoinGecko buscando as top 10 moedas em Reais (BRL)
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=brl&order=market_cap_desc&per_page=10&page=1&sparkline=false";
        
        try {
            // Faz a chamada HTTP e recebe um array de objetos JSON (mapeados como Map)
            Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);
            
            if (response == null) {
                return new ArrayList<>();
            }

            List<RankingAtivoDTO> ativosDoMercado = new ArrayList<>();
            
            for (Map<String, Object> coin : response) {
                String simbolo = (String) coin.get("symbol");
                
                // Tratamento de segurança: converte para String primeiro para evitar erro de cast direto (Integer para Double)
                double precoAtual = Double.parseDouble(coin.get("current_price").toString());
                
                // Tratamento para evitar NullPointerException caso a moeda não tenha variação nas últimas 24h
                double percentual24h = 0.0;
                if (coin.get("price_change_percentage_24h") != null) {
                    percentual24h = Double.parseDouble(coin.get("price_change_percentage_24h").toString());
                }
                
                // Instancia o DTO. O precoMedioCompra vai como 0.0 aqui, 
                // pois será substituído pela função obterRankingAtivos logo em seguida!
                ativosDoMercado.add(new RankingAtivoDTO(simbolo.toUpperCase(), 0.0, precoAtual, percentual24h));
            }
            
            return ativosDoMercado;
            
        } catch (Exception e) {
            // Resiliência: se a CoinGecko cair ou bloquear o IP (Rate Limit), o servidor não "explode"
            System.err.println("Aviso: Falha ao buscar dados na CoinGecko. Motivo: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}