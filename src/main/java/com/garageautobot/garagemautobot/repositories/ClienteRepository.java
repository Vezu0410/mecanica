package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar cliente por CPF
    Optional<Cliente> findByCpf(String cpf);

    // Buscar cliente por Email
    Optional<Cliente> findByEmail(String email);
    List<Cliente> findByAtivoTrue();
    List<Cliente> findByAtivoFalse();

    boolean existsByCpf(String cpf);
}
