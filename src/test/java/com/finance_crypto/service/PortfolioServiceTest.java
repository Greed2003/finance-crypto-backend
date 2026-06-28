package com.finance_crypto.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.finance_crypto.repository.PortfolioRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    // Mock do banco de dados do portfólio
    @Mock
    private PortfolioRepository portfolioRepository;

    // O serviço que vamos criar
    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    @DisplayName("Deve retornar saldo zero para um portfólio recém-criado/vazio")
    void deveRetornarSaldoZeroParaCarteiraVazia() {
        // Arrange
        Long usuarioId = 1L;
        
        // Act
        // Forçamos a criação de um método que calcule o saldo total da carteira
        double saldoTotal = portfolioService.calcularSaldoTotal(usuarioId);

        // Assert
        assertEquals(0.0, saldoTotal, "O saldo de um portfólio novo deve ser 0.0");
    }
    
    @Test
    @DisplayName("Deve adicionar um ativo ao portfólio com sucesso")
    void deveAdicionarAtivo() {
        // Arrange
        Long usuarioId = 1L;
        String simbolo = "ETH";
        double quantidade = 2.5;

        // Act
        // Forçamos a criação de um método para inserir criptos na carteira
        boolean adicionado = portfolioService.adicionarAtivo(usuarioId, simbolo, quantidade);

        // Assert
        assertTrue(adicionado, "O ativo deve ser adicionado com sucesso ao portfólio");
    }
}