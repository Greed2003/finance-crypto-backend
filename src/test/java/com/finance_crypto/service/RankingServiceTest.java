package com.finance_crypto.service;

import com.finance_crypto.dto.RankingAtivoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    private static final String COINGECKO_RESPONSE_JSON = """
            [
              {
                "id": "bitcoin",
                "symbol": "btc",
                "name": "Bitcoin",
                "current_price": 352841.00,
                "market_cap_rank": 1,
                "price_change_percentage_24h": 3.12502
              },
              {
                "id": "ethereum",
                "symbol": "eth",
                "name": "Ethereum",
                "current_price": 18500.00,
                "market_cap_rank": 2,
                "price_change_percentage_24h": -1.07
              }
            ]
            """;

    @Mock
    private RestTemplate restTemplate;
    private PortfolioService portfolioService;

    @InjectMocks
    private RankingService rankingService;

    @Test
    @DisplayName("Deve retornar dados formatados a partir da resposta da API CoinGecko")
    void deveRetornarDadosDaApiCoinGecko() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(COINGECKO_RESPONSE_JSON);

        // Act
        List<RankingAtivoDTO> resultado = rankingService.obterRankingAtivos(1L);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals("BTC", resultado.get(0).getSimbolo().toUpperCase());
        //assertEquals(352841.00, resultado.get(0).getPrecoBrl());
        assertEquals(3.12502, resultado.get(0).getPercentualLucro(), 0.001); 
        
        // Verifica se a chamada HTTP falsa foi realmente disparada
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void deveCalcularLucroCorretamenteQuandoPrecoAtualForMaior() {
        double precoCompra = 100.0;
        double precoAtual = 150.0;
        double resultado = rankingService.calcularPorcentagemLucro(precoCompra, precoAtual);
        assertEquals(50.0, resultado, "O lucro deveria ser de 50%");
    }

    @Test
    void deveCalcularPrejuizoCorretamenteQuandoPrecoAtualForMenor() {
        double precoCompra = 100.0;
        double precoAtual = 80.0;
        double resultado = rankingService.calcularPorcentagemLucro(precoCompra, precoAtual);
        assertEquals(-20.0, resultado, "O prejuízo deveria ser de -20%");
    }

    @Test
    void deveRetornarZeroQuandoPrecoCompraForInvalido() {
        double precoCompra = 0.0;
        double precoAtual = 150.0;
        double resultado = rankingService.calcularPorcentagemLucro(precoCompra, precoAtual);
        assertEquals(0.0, resultado, "Deve retornar 0 para evitar divisão por zero");
    }

    
}