package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Cliente;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.repositories.ClienteRepository;
import com.garageautobot.garagemautobot.services.PecaService;
import com.garageautobot.garagemautobot.services.VeiculoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;
    private final ClienteRepository clienteRepository;


    @Autowired
    private PecaService pecaService; // 🔹 Adicionado
    
    @Autowired
    public VeiculoController(VeiculoService veiculoService, ClienteRepository clienteRepository) {
        this.veiculoService = veiculoService;
        this.clienteRepository = clienteRepository;
    }

    // Exibir formulário de cadastro (novo veículo)
    @GetMapping("/cadastro")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("veiculo", new Veiculo()); // Veículo novo
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("pecas", pecaService.findAll());
        return "cadastro-veiculo";
    }

    // Exibir formulário de edição (veículo existente)
    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Veiculo veiculo = veiculoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
        model.addAttribute("veiculo", veiculo);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("pecas", pecaService.findAll()); // 🔹 Aqui também
        return "cadastro-veiculo"; // mesma página do cadastro
    }

    // Salvar veículo (novo ou editado)
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Veiculo veiculo,
                         @RequestParam("foto") MultipartFile foto) throws IOException {

        // Se o usuário enviou uma foto
        if (!foto.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + foto.getOriginalFilename();
            Path caminho = Paths.get("uploads/veiculos/" + filename);
            Files.createDirectories(caminho.getParent());
            Files.write(caminho, foto.getBytes());
            veiculo.setCaminhoFoto(filename); // <-- garante que Veiculo tenha este atributo
        }

        veiculoService.save(veiculo);
        return "redirect:/veiculos/lista";
    }

    // Listagem dos veículos
    @GetMapping("/lista")
    public String listarTodos(Model model) {
        model.addAttribute("veiculos", veiculoService.findAll());
        return "lista-veiculos";
    }
    
 // Exibir foto do veículo
    @GetMapping("/foto/{filename:.+}")
    @ResponseBody
    public byte[] exibirFoto(@PathVariable String filename) throws IOException {
        Path caminho = Paths.get("uploads/veiculos/" + filename);
        return Files.readAllBytes(caminho);
    }

}
