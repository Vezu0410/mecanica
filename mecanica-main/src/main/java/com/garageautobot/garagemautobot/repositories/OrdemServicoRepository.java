package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.OrdemServico;
import com.garageautobot.garagemautobot.entities.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    Optional<OrdemServico> findByNumeroOS(String numeroOS);

    List<OrdemServico> findByStatus(StatusOS status);

    List<OrdemServico> findByVeiculoId(Long veiculoId);

    List<OrdemServico> findByVeiculoClienteId(Long clienteId);

    // Busca pelo número mais alto para gerar o próximo número sequencial
    @Query("SELECT COALESCE(MAX(o.id), 0) FROM OrdemServico o")
    Long findMaxId();

    // Busca por texto: número OS, placa ou nome do cliente
    @Query("""
        SELECT o FROM OrdemServico o
        WHERE LOWER(o.numeroOS) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR LOWER(o.veiculo.placa) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR LOWER(o.veiculo.cliente.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR LOWER(o.veiculo.marca) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR LOWER(o.veiculo.modelo) LIKE LOWER(CONCAT('%', :termo, '%'))
        ORDER BY o.dataAbertura DESC
        """)
    List<OrdemServico> buscarPorTermo(String termo);

    // Busca as OS ATIVAS de um veículo (não concluídas nem canceladas),
    // da mais recente para a mais antiga. Usado para sincronizar o status
    // quando o veículo é alterado pelo painel.
    @Query("""
        SELECT o FROM OrdemServico o
        WHERE o.veiculo.id = :veiculoId
          AND o.status NOT IN ('CONCLUIDA', 'CANCELADA')
        ORDER BY o.dataAbertura DESC
        """)
    List<OrdemServico> findOSAtivasDoVeiculo(@Param("veiculoId") Long veiculoId);
}