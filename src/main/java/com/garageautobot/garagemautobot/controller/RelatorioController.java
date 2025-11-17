package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Peca;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.services.PecaService;
import com.garageautobot.garagemautobot.services.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Controller
public class RelatorioController {

    private final PecaService pecaService;
    private final VeiculoService veiculoService;

    @Autowired
    public RelatorioController(PecaService pecaService, VeiculoService veiculoService) {
        this.pecaService = pecaService;
        this.veiculoService = veiculoService;
    }

    
    @GetMapping("/relatorios/pecas/pdf")
    public ResponseEntity<byte[]> gerarRelatorioPecasPDF() {
        try {
            List<Peca> pecas = pecaService.findAll();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, baos);
            document.open();

            com.itextpdf.text.Font tituloFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font textoFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);

            document.add(new com.itextpdf.text.Paragraph("Relatório de Peças Cadastradas", tituloFont));
            document.add(new com.itextpdf.text.Paragraph("Gerado em: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new com.itextpdf.text.Paragraph(" ")); // espaço

            com.itextpdf.text.pdf.PdfPTable tabela = new com.itextpdf.text.pdf.PdfPTable(5);
            tabela.setWidthPercentage(100);
            tabela.addCell("Nome");
            tabela.addCell("Código");
            tabela.addCell("Quantidade");
            tabela.addCell("Preço Unitário (R$)");
            tabela.addCell("Valor Total (R$)");

            for (Peca p : pecas) {
                tabela.addCell(p.getNome());
                tabela.addCell(p.getCodigo());
                tabela.addCell(String.valueOf(p.getQuantidade()));
                tabela.addCell(String.format(Locale.forLanguageTag("pt-BR"), "%.2f", p.getPrecoUnitario()));
                tabela.addCell(String.format(Locale.forLanguageTag("pt-BR"), "%.2f", p.getValorTotalEstoque()));
            }

            document.add(tabela);
            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio_pecas.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    
    @GetMapping("/relatorios")
    public String relatorios(
            @RequestParam(name = "searchPeca", required = false) String searchPeca,
            @RequestParam(name = "searchVeiculo", required = false) String searchVeiculo,
            Model model) {

        DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Lista de peças filtradas
        List<Map<String, Object>> pecas = pecaService.findAll().stream()
                .filter(p -> searchPeca == null || searchPeca.isEmpty() ||
                        p.getNome().toLowerCase().contains(searchPeca.toLowerCase()))
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nome", p.getNome());
                    map.put("codigo", p.getCodigo());
                    map.put("quantidade", p.getQuantidade());
                    map.put("precoUnitario", String.format(Locale.forLanguageTag("pt-BR"), "%.2f", p.getPrecoUnitario()));
                    map.put("valorTotal", String.format(Locale.forLanguageTag("pt-BR"), "%.2f", p.getValorTotalEstoque()));
                    map.put("dataCadastro", p.getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    return map;
                }).collect(Collectors.toList());
        List<Map<String, Object>> veiculos = veiculoService.findAll().stream()
                .filter(v -> searchVeiculo == null || searchVeiculo.isEmpty() ||
                        v.getMarca().toLowerCase().contains(searchVeiculo.toLowerCase()) ||
                        v.getModelo().toLowerCase().contains(searchVeiculo.toLowerCase()) ||
                        v.getPlaca().toLowerCase().contains(searchVeiculo.toLowerCase()))
                .map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("marca", v.getMarca());
                    map.put("modelo", v.getModelo());
                    map.put("ano", v.getAno());
                    map.put("placa", v.getPlaca());
                    map.put("status", v.getStatus());
                    map.put("cliente", v.getCliente().getNome());
                    map.put("dataCadastro", v.getDataCadastro().format(dataFormatter));
                    return map;
                }).collect(Collectors.toList());

        model.addAttribute("pecas", pecas);
        model.addAttribute("veiculos", veiculos);
        model.addAttribute("searchPeca", searchPeca);
        model.addAttribute("searchVeiculo", searchVeiculo);
        return "relatorios";
    }
    
    
    @GetMapping("/relatorios/veiculos/pdf")
    public ResponseEntity<byte[]> gerarRelatorioVeiculosPDF() {
        try {
            List<Veiculo> veiculos = veiculoService.findAll();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, baos);
            document.open();

            com.itextpdf.text.Font tituloFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font textoFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 12);

            document.add(new com.itextpdf.text.Paragraph("Relatório de Veículos Cadastrados", tituloFont));
            document.add(new com.itextpdf.text.Paragraph("Gerado em: " +
                    java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    )));
            document.add(new com.itextpdf.text.Paragraph(" "));

            com.itextpdf.text.pdf.PdfPTable tabela = new com.itextpdf.text.pdf.PdfPTable(7);
            tabela.setWidthPercentage(100);

            tabela.addCell("Marca");
            tabela.addCell("Modelo");
            tabela.addCell("Ano");
            tabela.addCell("Placa");
            tabela.addCell("Status");
            tabela.addCell("Cliente");
            tabela.addCell("Data Cadastro");

            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Veiculo v : veiculos) {
                // sempre converter para String e tratar nulls
                tabela.addCell(String.valueOf(v.getMarca() == null ? "" : v.getMarca()));
                tabela.addCell(String.valueOf(v.getModelo() == null ? "" : v.getModelo()));
                tabela.addCell(String.valueOf(v.getAno())); // ano é provavelmente int
                tabela.addCell(String.valueOf(v.getPlaca() == null ? "" : v.getPlaca()));

                // Status pode ser um enum — converta para String
                String statusStr = v.getStatus() == null ? "" : v.getStatus().toString();
                // se seu enum tiver um método getDescricao(), use: v.getStatus().getDescricao()
                tabela.addCell(statusStr);

                // cliente pode ser nulo; proteja-se
                String clienteNome = (v.getCliente() != null && v.getCliente().getNome() != null) ?
                        v.getCliente().getNome() : "";
                tabela.addCell(clienteNome);

                // data (pode ser LocalDate/LocalDateTime)
                String dataStr = "";
                if (v.getDataCadastro() != null) {
                    try {
                        dataStr = v.getDataCadastro().format(df);
                    } catch (Exception ex) {
                        dataStr = v.getDataCadastro().toString();
                    }
                }
                tabela.addCell(dataStr);
            }

            document.add(tabela);
            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio_veiculos.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    
    
}
