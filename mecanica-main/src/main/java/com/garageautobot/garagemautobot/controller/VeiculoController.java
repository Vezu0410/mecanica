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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;
    private final ClienteRepository clienteRepository;

    @Autowired
    private PecaService pecaService;

    @Autowired
    public VeiculoController(VeiculoService veiculoService, ClienteRepository clienteRepository) {
        this.veiculoService = veiculoService;
        this.clienteRepository = clienteRepository;
    }

    // Exibir formulário de cadastro (novo veículo)
    @GetMapping("/cadastro")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("veiculo", new Veiculo());
        carregarCombos(model);
        return "cadastro-veiculo";
    }

    // Exibir formulário de edição (veículo existente)
    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Veiculo veiculo = veiculoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
        model.addAttribute("veiculo", veiculo);
        carregarCombos(model);
        return "cadastro-veiculo";
    }

    // Salvar veículo (novo ou editado)
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Veiculo veiculo,
                         @RequestParam("foto") MultipartFile foto) throws IOException {

        if (!foto.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + foto.getOriginalFilename();
            Path caminho = Paths.get("uploads/veiculos/" + filename);
            Files.createDirectories(caminho.getParent());
            Files.write(caminho, foto.getBytes());
            veiculo.setCaminhoFoto(filename);
        }

        veiculoService.save(veiculo);
        return "redirect:/veiculos/lista";
    }

    // Listagem dos veículos
    @GetMapping("/lista")
    public String listarTodos(Model model) {
        model.addAttribute("veiculos", veiculoService.findAll());
        model.addAttribute("veiculosInativos", veiculoService.findInativos());
        return "lista-veiculos";
    }

    // INATIVAR veículo (soft delete, com trava de OS em aberto)
    @PostMapping("/inativar/{id}")
    public String inativar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            veiculoService.inativar(id);
            ra.addFlashAttribute("sucesso", "Veículo inativado. O histórico de OS e fotos foi preservado.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/veiculos/lista";
    }

    // REATIVAR veículo (somente admin - protegido no WebConfig)
    @PostMapping("/reativar/{id}")
    public String reativar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            veiculoService.reativar(id);
            ra.addFlashAttribute("sucesso", "Veículo reativado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/veiculos/lista";
    }

    // Exibir foto do veículo
    @GetMapping("/foto/{filename:.+}")
    @ResponseBody
    public byte[] exibirFoto(@PathVariable String filename) throws IOException {
        Path caminho = Paths.get("uploads/veiculos/" + filename);
        return Files.readAllBytes(caminho);
    }

    // ── Helper: carrega clientes/peças e gera o JSON do combo com busca ──
    private void carregarCombos(Model model) {
        List<Cliente> clientes = clienteRepository.findAll();
        var pecas = pecaService.findAll();

        model.addAttribute("clientes", clientes);
        model.addAttribute("pecas", pecas);

        // JSON consumido pelo componente searchable-select (combo de cliente)
        String clientesJson = ComboJson.gerar(
                clientes,
                Cliente::getId,
                Cliente::getNome
        );
        model.addAttribute("clientesJson", clientesJson);

        // JSON do combo de peças: "Nome (Cód: X — Estq: Y)"
        String pecasJson = ComboJson.gerar(
                pecas,
                p -> p.getId(),
                p -> p.getNome() + " (Cód: " + p.getCodigo() + " — Estq: " + p.getQuantidade() + ")"
        );
        model.addAttribute("pecasJson", pecasJson);
    }
}