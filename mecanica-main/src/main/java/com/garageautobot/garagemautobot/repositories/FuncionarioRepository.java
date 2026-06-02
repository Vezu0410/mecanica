package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    // Busca por CPF (usado no login com hash e na validação de duplicidade)
    Optional<Funcionario> findByCpf(String cpf);

    Optional<Funcionario> findByEmail(String email);

    // NOTA: o método findByCpfAndSenha foi removido porque a senha agora é hash.
    // A comparação de senha é feita pelo FuncionarioService.autenticar() com BCrypt.
}