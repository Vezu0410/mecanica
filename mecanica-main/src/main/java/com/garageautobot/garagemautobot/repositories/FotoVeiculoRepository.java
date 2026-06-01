package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.FotoVeiculo;
import com.garageautobot.garagemautobot.entities.MomentoFoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoVeiculoRepository extends JpaRepository<FotoVeiculo, Long> {

    // Todas as fotos de um veículo
    List<FotoVeiculo> findByVeiculoIdOrderByDataRegistroAsc(Long veiculoId);

    // Fotos de um veículo filtradas por momento (ENTRADA ou SAIDA)
    List<FotoVeiculo> findByVeiculoIdAndMomentoOrderByDataRegistroAsc(
            Long veiculoId, MomentoFoto momento);

    // Fotos vinculadas a uma OS específica
    List<FotoVeiculo> findByOrdemServicoIdOrderByDataRegistroAsc(Long osId);

    // Fotos de um veículo em uma OS específica
    List<FotoVeiculo> findByVeiculoIdAndOrdemServicoIdOrderByDataRegistroAsc(
            Long veiculoId, Long osId);
}