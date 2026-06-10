package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.Peca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {

    Peca findByCodigo(String codigo);

    // Apenas peças ativas (uso normal do dia a dia)
    List<Peca> findByAtivoTrue();

    // Apenas peças inativas (para o admin ver/reativar)
    List<Peca> findByAtivoFalse();
}