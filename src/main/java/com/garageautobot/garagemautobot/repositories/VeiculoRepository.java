package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByClienteId(Long clienteId);
    List<Veiculo> findByPlacaContainingIgnoreCase(String placa);
}
