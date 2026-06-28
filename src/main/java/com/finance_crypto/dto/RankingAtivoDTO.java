package com.finance_crypto.dto;

public class RankingAtivoDTO {
    
    private String simbolo;
    private double precoMedioCompra;
    private double precoBrl;
    private double percentualLucro;

    // Construtor vazio (necessário para alguns frameworks)
    public RankingAtivoDTO() {
    }

    // Construtor com todos os argumentos
    public RankingAtivoDTO(String simbolo, double precoMedioCompra, double precoBrl, double percentualLucro) {
        this.simbolo = simbolo;
        this.precoMedioCompra = precoMedioCompra;
        this.precoBrl = precoBrl;
        this.percentualLucro = percentualLucro;
    }

    // Getters
    public String getSimbolo() { return simbolo; }
    public double getPrecoMedioCompra() { return precoMedioCompra; }
    public double getPrecoBrl() { return precoBrl; }
    public double getPercentualLucro() { return percentualLucro; }

    // Setters
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }
    public void setPrecoMedioCompra(double precoMedioCompra) { this.precoMedioCompra = precoMedioCompra; }
    public void setPrecoBrl(double precoBrl) { this.precoBrl = precoBrl; }
    public void setPercentualLucro(double percentualLucro) { this.percentualLucro = percentualLucro; }
}