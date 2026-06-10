package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.Funcionario;
import com.garageautobot.garagemautobot.entities.PapelFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    // Busca por CPF (usado no login com hash e na validação de duplicidade)
    Optional<Funcionario> findByCpf(String cpf);

    Optional<Funcionario> findByEmail(String email);

    // Soft delete: ativos e inativos
    List<Funcionario> findByAtivoTrue();
    List<Funcionario> findByAtivoFalse();

    // Conta administradores ATIVOS (para a trava do "último admin")
    long countByPapelAndAtivoTrue(PapelFuncionario papel);
}