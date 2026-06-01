package com.garageautobot.garagemautobot.repositories;

import com.garageautobot.garagemautobot.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    
	
	 Optional<Funcionario> findByCpfAndSenha(String cpf, String senha);

    Optional<Funcionario> findByEmail(String email);
}
