package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.entities.StatusVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByClienteId(Long clienteId);
    List<Veiculo> findByPlacaContainingIgnoreCase(String placa);
    List<Veiculo> findByStatus(StatusVeiculo status);
    List<Veiculo> findByStatusIn(List<StatusVeiculo> status);
    
    
    
    List<Veiculo> findByMarcaContainingIgnoreCaseOrModeloContainingIgnoreCaseOrClienteNomeContainingIgnoreCase(
            String marca, String modelo, String clienteNome
    );

}
