package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Cliente;
import com.garageautobot.garagemautobot.repositories.ClienteRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/cadastrar")
    public String exibirFormulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cadastro-cliente";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Cliente cliente, Model model) {
        try {
            if (cliente.getId() != null) {
                // Edição
                Cliente clienteExistente = clienteRepository.findById(cliente.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + cliente.getId()));

                clienteExistente.setNome(cliente.getNome());
                clienteExistente.setEmail(cliente.getEmail());
                clienteExistente.setTelefone(limparMascara(cliente.getTelefone()));
                clienteExistente.setCep(limparMascara(cliente.getCep()));
                clienteExistente.setEndereco(cliente.getEndereco());
                clienteExistente.setCidade(cliente.getCidade());
                clienteExistente.setEstado(cliente.getEstado());

                // NÃO ALTERE CPF durante edição
                clienteRepository.save(clienteExistente);

                model.addAttribute("message", "Cliente atualizado com sucesso!");
                model.addAttribute("cliente", clienteExistente);
            } else {
                // Novo cliente
                cliente.setCpf(limparMascara(cliente.getCpf()));
                cliente.setTelefone(limparMascara(cliente.getTelefone()));
                cliente.setCep(limparMascara(cliente.getCep()));

                // Verifica se CPF já existe
                if (clienteRepository.existsByCpf(cliente.getCpf())) {
                    model.addAttribute("error", "CPF já cadastrado no sistema!");
                    return "cadastro-cliente";
                }

                clienteRepository.save(cliente);
                model.addAttribute("message", "Cliente cadastrado com sucesso!");
                model.addAttribute("cliente", new Cliente());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao salvar cliente: " + e.getMessage());
        }
        return "redirect:/clientes/listar";
    }
    @GetMapping("/listar")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "clientes-lista";
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        model.addAttribute("cliente", cliente);
        return "cadastro-cliente";
    }

    // Método utilitário para remover máscara
    private String limparMascara(String valor) {
        if (valor != null) {
            return valor.replaceAll("\\D", "");
        }
        return null;
    }
}

    

