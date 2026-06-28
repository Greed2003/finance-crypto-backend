package com.finance_crypto.repository;

import com.finance_crypto.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Transaction, Long> {
    // Esse é o método mágico que o Spring cria sozinho para buscar as transações do usuário
    List<Transaction> findByUsuarioId(Long usuarioId);
}