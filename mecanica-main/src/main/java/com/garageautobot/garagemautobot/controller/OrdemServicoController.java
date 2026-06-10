package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.*;
import com.garageautobot.garagemautobot.repositories.ClienteRepository;
import com.garageautobot.garagemautobot.repositories.PecaRepository;
import com.garageautobot.garagemautobot.repositories.VeiculoRepository;
import com.garageautobot.garagemautobot.services.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/os")
public class OrdemServicoController {

    private final OrdemServicoService osService;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final PecaRepository pecaRepository;

    @Autowired
    public OrdemServicoController(OrdemServicoService osService,
                                  ClienteRepository clienteRepository,
                                  VeiculoRepository veiculoRepository,
                                  PecaRepository pecaRepository) {
        this.osService          = osService;
        this.clienteRepository  = clienteRepository;
        this.veiculoRepository  = veiculoRepository;
        this.pecaRepository     = pecaRepository;
    }

    // ── LISTAGEM ─────────────────────────────────────────────────

    @GetMapping
    public String listar(@RequestParam(required = false) String search,
                         @RequestParam(required = false) String status,
                         Model model) {

        List<OrdemServico> lista;

        if (search != null && !search.isBlank()) {
            lista = osService.buscarPorTermo(search);
        } else if (status != null && !status.equals("TODOS")) {
            try {
                lista = osService.findByStatus(StatusOS.valueOf(status));
            } catch (IllegalArgumentException e) {
                lista = osService.findAll();
            }
        } else {
            lista = osService.findAll();
        }

        model.addAttribute("ordens", lista);
        model.addAttribute("search", search);
        model.addAttribute("statusFiltro", status);
        model.addAttribute("statusOpcoes", StatusOS.values());
        return "os/lista-os";
    }

    // ── NOVA OS — ESCOLHA DO TIPO ────────────────────────────────

    @GetMapping("/nova")
    public String escolherTipo(Model model) {
        var clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);
        model.addAttribute("veiculos", veiculoRepository.findAll());

        // JSON do combo de cliente com busca
        model.addAttribute("clientesJson", ComboJson.gerar(
                clientes,
                Cliente::getId,
                Cliente::getNome
        ));
        return "os/nova-os";
    }

    // ── NOVA OS DIRETO DE UM VEÍCULO (vindo do card do painel) ───
    // Tela enxuta: cliente e veículo já definidos, só pede a descrição.

    @GetMapping("/nova/{veiculoId}")
    public String novaOSdoVeiculo(@PathVariable Long veiculoId,
                                  Model model,
                                  RedirectAttributes ra) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId).orElse(null);

        if (veiculo == null) {
            ra.addFlashAttribute("erro", "Veículo não encontrado.");
            return "redirect:/menu";
        }

        model.addAttribute("veiculo", veiculo);
        return "os/nova-os-veiculo";
    }

    // ── ABRIR OS COM VEÍCULO EXISTENTE ───────────────────────────

    @PostMapping("/abrir/veiculo-existente")
    public String abrirComVeiculoExistente(
            @RequestParam Long veiculoId,
            @RequestParam String descricaoProblema,
            @RequestParam(required = false) String observacoes,
            RedirectAttributes ra) {
        try {
            OrdemServico os = osService.abrirOS(veiculoId, descricaoProblema, observacoes);
            ra.addFlashAttribute("sucesso", "OS " + os.getNumeroOS() + " aberta com sucesso!");
            return "redirect:/os/" + os.getId();
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/os/nova";
        }
    }

    // ── ABRIR OS COM NOVO VEÍCULO ────────────────────────────────

    @PostMapping("/abrir/novo-veiculo")
    public String abrirComNovoVeiculo(
            @RequestParam Long clienteId,
            @RequestParam String marca,
            @RequestParam String modelo,
            @RequestParam String ano,
            @RequestParam String placa,
            @RequestParam String descricaoProblema,
            @RequestParam(required = false) String observacoes,
            RedirectAttributes ra) {
        try {
            OrdemServico os = osService.abrirOSComNovoVeiculo(
                    clienteId, marca, modelo, ano, placa, descricaoProblema, observacoes);
            ra.addFlashAttribute("sucesso", "OS " + os.getNumeroOS() + " aberta com sucesso!");
            return "redirect:/os/" + os.getId();
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/os/nova";
        }
    }

    // ── DETALHE / EDIÇÃO DA OS ───────────────────────────────────

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        OrdemServico os = osService.findById(id)
                .orElseThrow(() -> new RuntimeException("OS não encontrada: " + id));

        var pecas = pecaRepository.findAll();
        model.addAttribute("os", os);
        model.addAttribute("pecas", pecas);
        model.addAttribute("statusOpcoes", StatusOS.values());

        // JSON do combo de peça com busca: "Nome (Cód: X — Estq: Y)"
        model.addAttribute("pecasJson", ComboJson.gerar(
                pecas,
                p -> p.getId(),
                p -> p.getNome() + " (Cód: " + p.getCodigo() + " — Estq: " + p.getQuantidade() + ")"
        ));
        return "os/detalhe-os";
    }

    // ── ADICIONAR PEÇA ───────────────────────────────────────────

    @PostMapping("/{id}/adicionar-peca")
    public String adicionarPeca(@PathVariable Long id,
                                @RequestParam Long pecaId,
                                @RequestParam int quantidade,
                                RedirectAttributes ra) {
        try {
            osService.adicionarItem(id, pecaId, quantidade);
            ra.addFlashAttribute("sucesso", "Peça adicionada com sucesso!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/os/" + id;
    }

    // ── REMOVER PEÇA ─────────────────────────────────────────────

    @PostMapping("/{id}/remover-item/{itemId}")
    public String removerItem(@PathVariable Long id,
                              @PathVariable Long itemId,
                              RedirectAttributes ra) {
        try {
            osService.removerItem(itemId);
            ra.addFlashAttribute("sucesso", "Item removido. Estoque devolvido.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/os/" + id;
    }

    // ── SALVAR MÃO DE OBRA ───────────────────────────────────────

    @PostMapping("/{id}/mao-obra")
    public String salvarMaoObra(@PathVariable Long id,
                                @RequestParam(required = false) Boolean usarMaoObra,
                                @RequestParam(required = false) Boolean porPercentual,
                                @RequestParam(required = false) Double valorFixo,
                                @RequestParam(required = false) Double percentual,
                                RedirectAttributes ra) {
        try {
            osService.salvarMaoObra(id, usarMaoObra, porPercentual, valorFixo, percentual);
            ra.addFlashAttribute("sucesso", "Mão de obra atualizada.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/os/" + id;
    }

    // ── ATUALIZAR STATUS ─────────────────────────────────────────

    @PostMapping("/{id}/status")
    public String atualizarStatus(@PathVariable Long id,
                                  @RequestParam StatusOS novoStatus,
                                  RedirectAttributes ra) {
        try {
            osService.atualizarStatus(id, novoStatus);
            ra.addFlashAttribute("sucesso", "Status atualizado para: " + novoStatus.getDescricao());
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/os/" + id;
    }

    // ── AJAX: veículos por cliente (para carregar select dinamicamente) ──

    @GetMapping("/veiculos-por-cliente/{clienteId}")
    @ResponseBody
    public List<Veiculo> veiculosPorCliente(@PathVariable Long clienteId) {
        return veiculoRepository.findByClienteId(clienteId);
    }
}