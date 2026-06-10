package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.ItemOS;
import com.garageautobot.garagemautobot.entities.OrdemServico;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.repositories.OrdemServicoRepository;
import com.garageautobot.garagemautobot.services.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RelatorioPecaController {

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @GetMapping("/relatorios/pecas-por-veiculo")
    public String relatorioPecasPorVeiculo(
            @RequestParam(required = false) String search,
            Model model) {

        List<Veiculo> veiculos = veiculoService.findAll();
        List<RelatorioPecaVeiculoDTO> relatorio = new ArrayList<>();

        double totalGeralOficina = 0.0;
        int totalPecasUsadas = 0;

        for (Veiculo veiculo : veiculos) {

            // Aplica o filtro de busca (nome, modelo, marca, placa ou cliente)
            if (search != null && !search.isBlank()) {
                String termo = search.toLowerCase().trim();
                String alvo = (
                        (veiculo.getMarca()  != null ? veiculo.getMarca()  : "") + " " +
                        (veiculo.getModelo() != null ? veiculo.getModelo() : "") + " " +
                        (veiculo.getPlaca()  != null ? veiculo.getPlaca()  : "") + " " +
                        (veiculo.getCliente() != null && veiculo.getCliente().getNome() != null
                                ? veiculo.getCliente().getNome() : "")
                ).toLowerCase();

                if (!alvo.contains(termo)) {
                    continue; // não casa com a busca, pula
                }
            }

            RelatorioPecaVeiculoDTO dto = new RelatorioPecaVeiculoDTO(veiculo);

            // Busca todas as OS do veículo e reúne os itens (peças)
            List<OrdemServico> ordens = ordemServicoRepository.findByVeiculoId(veiculo.getId());
            for (OrdemServico os : ordens) {
                if (os.getItens() != null) {
                    for (ItemOS item : os.getItens()) {
                        if (item.getPeca() != null) {
                            dto.adicionarLinha(
                                    os.getNumeroOS(),
                                    item.getPeca().getNome(),
                                    item.getQuantidade(),
                                    item.getPrecoUnitarioAplicado(),
                                    os.getDataAbertura()
                            );
                            totalPecasUsadas += item.getQuantidade();
                        }
                    }
                }
            }

            totalGeralOficina += dto.getTotalGeral();
            relatorio.add(dto);
        }

        model.addAttribute("relatorio", relatorio);
        model.addAttribute("search", search);
        model.addAttribute("totalGeralOficina", totalGeralOficina);
        model.addAttribute("totalPecasUsadas", totalPecasUsadas);
        return "relatorio-pecas-veiculo";
    }
}