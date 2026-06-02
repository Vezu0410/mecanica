package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.*;
import com.garageautobot.garagemautobot.repositories.ClienteRepository;
import com.garageautobot.garagemautobot.repositories.VeiculoRepository;
import com.garageautobot.garagemautobot.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/agenda")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final ClienteRepository  clienteRepository;
    private final VeiculoRepository  veiculoRepository;

    @Autowired
    public AgendamentoController(AgendamentoService agendamentoService,
                                  ClienteRepository clienteRepository,
                                  VeiculoRepository veiculoRepository) {
        this.agendamentoService = agendamentoService;
        this.clienteRepository  = clienteRepository;
        this.veiculoRepository  = veiculoRepository;
    }

    // ── AGENDA DO DIA (tela principal) ───────────────────────────

    @GetMapping
    public String agenda(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model) {

        // Se não informou data, usa hoje
        LocalDate dataAlvo = (data != null) ? data : LocalDate.now();

        List<Agendamento> agendamentos = agendamentoService.listarPorData(dataAlvo);

        // Separa por período para exibir em duas colunas
        List<Agendamento> manha = agendamentos.stream()
                .filter(a -> a.getPeriodo() == PeriodoAgendamento.MANHA)
                .toList();
        List<Agendamento> tarde = agendamentos.stream()
                .filter(a -> a.getPeriodo() == PeriodoAgendamento.TARDE)
                .toList();

        model.addAttribute("dataAlvo", dataAlvo);
        model.addAttribute("dataAnterior", dataAlvo.minusDays(1));
        model.addAttribute("dataProxima", dataAlvo.plusDays(1));
        model.addAttribute("hoje", LocalDate.now());
        model.addAttribute("manha", manha);
        model.addAttribute("tarde", tarde);
        return "agenda/agenda";
    }

    // ── FORMULÁRIO DE NOVO AGENDAMENTO ───────────────────────────

    @GetMapping("/novo")
    public String novoForm(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model) {

        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("veiculos", veiculoRepository.findAll());
        model.addAttribute("periodos", PeriodoAgendamento.values());
        model.addAttribute("dataPreenchida", data != null ? data : LocalDate.now());
        return "agenda/novo-agendamento";
    }

    // ── SALVAR NOVO AGENDAMENTO ──────────────────────────────────

    @PostMapping("/salvar")
    public String salvar(
            @RequestParam Long clienteId,
            @RequestParam(required = false) Long veiculoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAgendada,
            @RequestParam PeriodoAgendamento periodo,
            @RequestParam String servicoSolicitado,
            @RequestParam(required = false) String observacoes,
            @RequestParam(required = false) String telefoneContato,
            RedirectAttributes ra) {
        try {
            agendamentoService.criar(clienteId, veiculoId, dataAgendada, periodo,
                    servicoSolicitado, observacoes, telefoneContato);
            ra.addFlashAttribute("sucesso", "Agendamento criado com sucesso!");
            return "redirect:/agenda?data=" + dataAgendada;
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/agenda/novo";
        }
    }

    // ── CARRO CHEGOU → ABRE OS ───────────────────────────────────

    @PostMapping("/{id}/carro-chegou")
    public String carroChegou(@PathVariable Long id,
                              @RequestParam(required = false) Long veiculoId,
                              RedirectAttributes ra) {
        try {
            OrdemServico os = agendamentoService.converterEmOS(id, veiculoId);
            ra.addFlashAttribute("sucesso",
                    "OS " + os.getNumeroOS() + " aberta a partir do agendamento!");
            return "redirect:/os/" + os.getId();
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/agenda";
        }
    }

    // ── MARCAR NÃO COMPARECEU ────────────────────────────────────

    @PostMapping("/{id}/nao-compareceu")
    public String naoCompareceu(@PathVariable Long id,
                                @RequestParam(required = false) String data,
                                RedirectAttributes ra) {
        try {
            agendamentoService.marcarNaoCompareceu(id);
            ra.addFlashAttribute("sucesso", "Agendamento marcado como não compareceu.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/agenda" + (data != null ? "?data=" + data : "");
    }

    // ── CANCELAR ─────────────────────────────────────────────────

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id,
                           @RequestParam(required = false) String data,
                           RedirectAttributes ra) {
        try {
            agendamentoService.cancelar(id);
            ra.addFlashAttribute("sucesso", "Agendamento cancelado.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/agenda" + (data != null ? "?data=" + data : "");
    }

    // ── AJAX: veículos de um cliente ─────────────────────────────

    @GetMapping("/veiculos-por-cliente/{clienteId}")
    @ResponseBody
    public List<Veiculo> veiculosPorCliente(@PathVariable Long clienteId) {
        return veiculoRepository.findByClienteId(clienteId);
    }
}