package com.garageautobot.garagemautobot.services;

import com.garageautobot.garagemautobot.entities.Funcionario;
import com.garageautobot.garagemautobot.repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    // Codificador BCrypt — transforma a senha em hash irreversível
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    /**
     * Salva ou atualiza um funcionário.
     * A senha é sempre convertida em hash BCrypt antes de gravar.
     */
    public Funcionario salvarFuncionario(Funcionario funcionario) {

        // Valida CPF duplicado (apenas para novos funcionários)
        if (funcionario.getId() == null) {
            Optional<Funcionario> existentePorCpf =
                    funcionarioRepository.findByCpf(funcionario.getCpf());
            if (existentePorCpf.isPresent()) {
                throw new RuntimeException("Já existe um funcionário com este CPF.");
            }

            Optional<Funcionario> existentePorEmail =
                    funcionarioRepository.findByEmail(funcionario.getEmail());
            if (existentePorEmail.isPresent()) {
                throw new RuntimeException("Já existe um funcionário com este e-mail.");
            }
        }

        // Aplica o hash na senha.
        // Detecta se a senha já é um hash (começa com $2a$, $2b$ ou $2y$)
        // para não "re-hashear" ao editar um funcionário sem trocar a senha.
        String senha = funcionario.getSenha();
        if (senha != null && !senha.isBlank() && !senhaJaCriptografada(senha)) {
            funcionario.setSenha(passwordEncoder.encode(senha));
        }

        return funcionarioRepository.save(funcionario);
    }

    /**
     * Verifica se a senha digitada bate com o hash salvo no banco.
     * Usado pelo login.
     */
    public Optional<Funcionario> autenticar(String cpf, String senhaDigitada) {
        if (cpf == null || senhaDigitada == null) return Optional.empty();

        // Remove máscara do CPF para bater com o que está no banco
        String cpfLimpo = cpf.replaceAll("\\D", "");

        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findByCpf(cpfLimpo);

        if (funcionarioOpt.isPresent()) {
            Funcionario f = funcionarioOpt.get();

            // Funcionário inativo não pode logar
            if (!f.isAtivo()) {
                return Optional.empty();
            }

            // Compara a senha digitada com o hash salvo
            if (passwordEncoder.matches(senhaDigitada, f.getSenha())) {
                return funcionarioOpt;
            }
        }
        return Optional.empty();
    }

    private boolean senhaJaCriptografada(String senha) {
        return senha.startsWith("$2a$") || senha.startsWith("$2b$") || senha.startsWith("$2y$");
    }

    // Lista apenas funcionários ATIVOS
    public List<Funcionario> listarAtivos() {
        return funcionarioRepository.findByAtivoTrue();
    }

    // Lista apenas funcionários INATIVOS (para reativar)
    public List<Funcionario> listarInativos() {
        return funcionarioRepository.findByAtivoFalse();
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Optional<Funcionario> buscarPorId(Long id) {
        return funcionarioRepository.findById(id);
    }

    /**
     * INATIVAÇÃO (soft delete) de funcionário, com travas de segurança:
     * - Não pode inativar a si mesmo (evita se trancar para fora)
     * - Não pode inativar o último admin ativo (sistema sem admin)
     *
     * @param id          funcionário a inativar
     * @param idLogado    id do funcionário que está fazendo a ação
     */
    public void inativar(Long id, Long idLogado) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado: " + id));

        // Trava 1: não pode inativar a si mesmo
        if (idLogado != null && idLogado.equals(id)) {
            throw new IllegalStateException("Você não pode inativar o seu próprio usuário.");
        }

        // Trava 2: não pode inativar o último admin ativo
        if (funcionario.isAdmin()) {
            long adminsAtivos = funcionarioRepository.countByPapelAndAtivoTrue(
                    com.garageautobot.garagemautobot.entities.PapelFuncionario.ADMIN);
            if (adminsAtivos <= 1) {
                throw new IllegalStateException(
                    "Não é possível inativar o único administrador ativo do sistema. " +
                    "Promova outro funcionário a administrador antes.");
            }
        }

        funcionario.setAtivo(false);
        funcionarioRepository.save(funcionario);
    }

    public void reativar(Long id) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado: " + id));
        funcionario.setAtivo(true);
        funcionarioRepository.save(funcionario);
    }
}