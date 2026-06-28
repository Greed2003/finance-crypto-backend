package com.finance_crypto.dto;

public class AssetHoldingDTO {
    private String simbolo;
    private double holding;
    private double precoMedio;

    public AssetHoldingDTO(String simbolo, double holding, double precoMedio) {
        this.simbolo = simbolo;
        this.holding = holding;
        this.precoMedio = precoMedio;
    }

    public String getSimbolo() { return simbolo; }
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }
    
    public double getHolding() { return holding; }
    public void setHolding(double holding) { this.holding = holding; }
    
    public double getPrecoMedio() { return precoMedio; }
    public void setPrecoMedio(double precoMedio) { this.precoMedio = precoMedio; }
}