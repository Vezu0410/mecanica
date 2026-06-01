package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.PecaUsada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PecaUsadaRepository extends JpaRepository<PecaUsada, Long> {
    List<PecaUsada> findByVeiculoId(Long veiculoId);
}
