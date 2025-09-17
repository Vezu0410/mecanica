package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.Peca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {
    // Aqui você pode criar queries personalizadas no futuro
    Peca findByCodigo(String codigo);
}
