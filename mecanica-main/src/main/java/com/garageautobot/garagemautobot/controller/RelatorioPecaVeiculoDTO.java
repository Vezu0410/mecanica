package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Veiculo;

import java.util.ArrayList;
import java.util.List;

/**
 * Estrutura auxiliar (DTO) que agrupa as peças usadas por um veículo,
 * reunindo os itens de TODAS as ordens de serviço daquele veículo.
 * Usado apenas para montar o relatório "Peças por Veículo".
 */
public class RelatorioPecaVeiculoDTO {

    private final Veiculo veiculo;
    private final List<LinhaPeca> linhas = new ArrayList<>();
    private double totalGeral = 0.0;

    public RelatorioPecaVeiculoDTO(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public void adicionarLinha(String numeroOS, String nomePeca, int quantidade,
                               double valorUnitario, java.time.LocalDateTime dataUso) {
        LinhaPeca linha = new LinhaPeca(numeroOS, nomePeca, quantidade, valorUnitario, dataUso);
        linhas.add(linha);
        totalGeral += linha.getValorTotal();
    }

    public Veiculo getVeiculo()      { return veiculo; }
    public List<LinhaPeca> getLinhas() { return linhas; }
    public double getTotalGeral()    { return totalGeral; }
    public boolean isVazio()         { return linhas.isEmpty(); }

    // ── Linha individual de peça no relatório ──────────────────────
    public static class LinhaPeca {
        private final String numeroOS;
        private final String nomePeca;
        private final int quantidade;
        private final double valorUnitario;
        private final java.time.LocalDateTime dataUso;

        public LinhaPeca(String numeroOS, String nomePeca, int quantidade,
                         double valorUnitario, java.time.LocalDateTime dataUso) {
            this.numeroOS      = numeroOS;
            this.nomePeca      = nomePeca;
            this.quantidade    = quantidade;
            this.valorUnitario = valorUnitario;
            this.dataUso       = dataUso;
        }

        public String getNumeroOS()  { return numeroOS; }
        public String getNomePeca()  { return nomePeca; }
        public int getQuantidade()   { return quantidade; }
        public double getValorUnitario() { return valorUnitario; }
        public java.time.LocalDateTime getDataUso() { return dataUso; }
        public double getValorTotal() { return quantidade * valorUnitario; }
    }
}