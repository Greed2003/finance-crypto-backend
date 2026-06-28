package com.finance_crypto.service;

import com.finance_crypto.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.finance_crypto.entity.Transaction;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public boolean registrarCompra(String simboloCripto, double quantidade, double precoNaHoraDaCompra) {
        try {
            // 1. Cria a nova transação
            Transaction novaTransacao = new Transaction();
            
            // Por enquanto estamos fixando o usuário 1. No futuro, isso virá do Token JWT de quem estiver logado!
            novaTransacao.setUsuarioId(1L); 
            
            novaTransacao.setSimbolo(simboloCripto);
            novaTransacao.setQuantidade(quantidade);
            novaTransacao.setPrecoUnitario(precoNaHoraDaCompra);
            novaTransacao.setTipo("BUY"); // Registra que a operação foi uma compra

            // 2. Manda o repositório salvar no banco de dados (PostgreSQL)
            transactionRepository.save(novaTransacao);
            
            return true; // Transação salva com sucesso!
            
        } catch (Exception e) {
            // Se o banco de dados cair ou der erro, retorna falso para o frontend não cobrar o usuário à toa
            System.err.println("Erro ao salvar transação de compra: " + e.getMessage());
            return false; 
        }
    }
}