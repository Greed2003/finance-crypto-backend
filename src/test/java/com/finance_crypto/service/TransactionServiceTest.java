package com.finance_crypto.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.finance_crypto.repository.TransactionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    // Mock do repositório (simula o banco de dados)
    @Mock
    private TransactionRepository transactionRepository;

    // A classe que estamos testando (e que vamos criar!)
    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Deve registrar uma transação de compra com sucesso")
    void deveRegistrarCompraComSucesso() {
        // Arrange (Preparar)
        String simboloCripto = "BTC";
        double quantidade = 0.5;
        double precoNaHoraDaCompra = 350000.00;

        // Simulando que, ao tentar salvar no banco, não haverá erros.
        // when(transactionRepository.save(any())).thenReturn(mockRetorno);

        // Act (Agir)
        // Aqui estamos forçando o design do método. Queremos que ele receba
        // esses 3 parâmetros e retorne um boolean confirmando o sucesso.
        boolean transacaoAprovada = transactionService.registrarCompra(simboloCripto, quantidade, precoNaHoraDaCompra);

        // Assert (Verificar)
        assertTrue(transacaoAprovada, "A transação de compra deveria ser aprovada e retornar true");
        
        // No futuro, podemos verificar se o repositório foi chamado corretamente:
        // verify(transactionRepository).save(any());
    }
}