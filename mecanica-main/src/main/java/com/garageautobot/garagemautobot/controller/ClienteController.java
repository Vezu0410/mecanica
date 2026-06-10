package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Cliente;
import com.garageautobot.garagemautobot.repositories.ClienteRepository;
import com.garageautobot.garagemautobot.repositories.VeiculoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @GetMapping("/cadastrar")
    public String exibirFormulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cadastro-cliente";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Cliente cliente,
                                Model model,
                                RedirectAttributes ra) {
        try {

            String cpfLimpo   = limparMascara(cliente.getCpf());
            String emailLimpo = cliente.getEmail() != null ? cliente.getEmail().trim() : null;

            // ───────────── EDITAR CLIENTE ─────────────
            if (cliente.getId() != null) {

                Cliente existente = clienteRepository.findById(cliente.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + cliente.getId()));

                // Valida e-mail duplicado em OUTRO cliente
                Optional<Cliente> donoDoEmail = clienteRepository.findByEmail(emailLimpo);
                if (donoDoEmail.isPresent() && !donoDoEmail.get().getId().equals(cliente.getId())) {
                    model.addAttribute("error", "Este e-mail já está cadastrado para outro cliente.");
                    model.addAttribute("cliente", cliente);
                    return "cadastro-cliente";
                }

                existente.setNome(cliente.getNome());
                existente.setEmail(emailLimpo);
                existente.setTelefone(limparMascara(cliente.getTelefone()));
                existente.setCep(limparMascara(cliente.getCep()));
                existente.setEndereco(cliente.getEndereco());
                existente.setCidade(cliente.getCidade());
                existente.setEstado(cliente.getEstado());
                existente.setAtivo(cliente.getAtivo());

                clienteRepository.save(existente);

                ra.addFlashAttribute("message", "Cliente atualizado com sucesso!");
                return "redirect:/clientes/listar";
            }

            // ───────────── NOVO CLIENTE ─────────────
            cliente.setCpf(cpfLimpo);
            cliente.setEmail(emailLimpo);
            cliente.setTelefone(limparMascara(cliente.getTelefone()));
            cliente.setCep(limparMascara(cliente.getCep()));

            // Valida CPF duplicado
            if (clienteRepository.existsByCpf(cpfLimpo)) {
                model.addAttribute("error", "CPF já cadastrado no sistema!");
                model.addAttribute("cliente", cliente);
                return "cadastro-cliente";
            }

            // Valida e-mail duplicado
            if (emailLimpo != null && clienteRepository.findByEmail(emailLimpo).isPresent()) {
                model.addAttribute("error", "E-mail já cadastrado no sistema!");
                model.addAttribute("cliente", cliente);
                return "cadastro-cliente";
            }

            cliente.setAtivo(true);
            clienteRepository.save(cliente);

            ra.addFlashAttribute("message", "Cliente cadastrado com sucesso!");
            return "redirect:/clientes/listar";

        } catch (Exception e) {
            // Erro inesperado: volta ao form com mensagem em vez de tela branca
            model.addAttribute("error", "Erro ao salvar cliente: " + e.getMessage());
            model.addAttribute("cliente", cliente);
            return "cadastro-cliente";
        }
    }

    @GetMapping("/listar")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findByAtivoTrue());
        model.addAttribute("clientesInativos", clienteRepository.findByAtivoFalse());
        return "clientes-lista";
    }

    // INATIVAR cliente (soft delete) com trava: não inativa se tiver veículo ativo
    @PostMapping("/inativar/{id}")
    public String inativar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

            long veiculosAtivos = veiculoRepository.countByClienteIdAndAtivoTrue(id);
            if (veiculosAtivos > 0) {
                throw new IllegalStateException(
                    "Não é possível inativar este cliente: há " + veiculosAtivos +
                    " veículo(s) ativo(s) vinculado(s). Inative os veículos primeiro.");
            }

            cliente.setAtivo(false);
            clienteRepository.save(cliente);
            ra.addFlashAttribute("message", "Cliente inativado. O histórico foi preservado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes/listar";
    }

    // REATIVAR cliente (somente admin - protegido no WebConfig)
    @PostMapping("/reativar/{id}")
    public String reativar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
            cliente.setAtivo(true);
            clienteRepository.save(cliente);
            ra.addFlashAttribute("message", "Cliente reativado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes/listar";
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        model.addAttribute("cliente", cliente);
        return "cadastro-cliente";
    }

    private String limparMascara(String valor) {
        if (valor != null) {
            return valor.replaceAll("\\D", "");
        }
        return null;
    }
}