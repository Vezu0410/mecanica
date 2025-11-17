package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Cliente;
import com.garageautobot.garagemautobot.repositories.ClienteRepository;

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

            // ------------ EDITAR CLIENTE ------------
            if (cliente.getId() != null) {

                Cliente clienteExistente = clienteRepository.findById(cliente.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + cliente.getId()));

                clienteExistente.setNome(cliente.getNome());
                clienteExistente.setEmail(cliente.getEmail());
                clienteExistente.setTelefone(limparMascara(cliente.getTelefone()));
                clienteExistente.setCep(limparMascara(cliente.getCep()));
                clienteExistente.setEndereco(cliente.getEndereco());
                clienteExistente.setCidade(cliente.getCidade());
                clienteExistente.setEstado(cliente.getEstado());

                // Agora permite alterar o status
                clienteExistente.setAtivo(cliente.getAtivo());

                clienteRepository.save(clienteExistente);

                model.addAttribute("message", "Cliente atualizado com sucesso!");

                return "redirect:/clientes/listar";
            }

            // ------------ NOVO CLIENTE ------------
            cliente.setCpf(limparMascara(cliente.getCpf()));
            cliente.setTelefone(limparMascara(cliente.getTelefone()));
            cliente.setCep(limparMascara(cliente.getCep()));

            // verifica cpf duplicado
            if (clienteRepository.existsByCpf(cliente.getCpf())) {
                model.addAttribute("error", "CPF já cadastrado no sistema!");
                return "cadastro-cliente";
            }

            // novo cliente começa ativo
            cliente.setAtivo(true);

            clienteRepository.save(cliente);

            model.addAttribute("message", "Cliente cadastrado com sucesso!");

        } catch (Exception e) {
            model.addAttribute("error", "Erro ao salvar cliente: " + e.getMessage());
        }

        return "redirect:/clientes/listar";
    }


    // NÃO PRECISA MAIS DO MÉTODO DE ALTERAR STATUS
    // FOI REMOVIDO ✔
    

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


    // remove máscara
    private String limparMascara(String valor) {
        if (valor != null) {
            return valor.replaceAll("\\D", "");
        }
        return null;
    }
}
