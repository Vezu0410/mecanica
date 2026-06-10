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

    // ── Versões que respeitam o soft delete (apenas ativos) ──────────

    List<Veiculo> findByAtivoTrue();
    List<Veiculo> findByAtivoFalse();

    List<Veiculo> findByClienteIdAndAtivoTrue(Long clienteId);
    List<Veiculo> findByStatusAndAtivoTrue(StatusVeiculo status);
    List<Veiculo> findByStatusInAndAtivoTrue(List<StatusVeiculo> status);

    List<Veiculo> findByMarcaContainingIgnoreCaseAndAtivoTrueOrModeloContainingIgnoreCaseAndAtivoTrueOrClienteNomeContainingIgnoreCaseAndAtivoTrue(
            String marca, String modelo, String clienteNome
    );

    // Conta veículos ativos de um cliente (para a trava de inativar cliente)
    long countByClienteIdAndAtivoTrue(Long clienteId);
}