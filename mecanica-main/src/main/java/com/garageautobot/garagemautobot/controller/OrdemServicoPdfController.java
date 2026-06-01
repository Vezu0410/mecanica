package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.ItemOS;
import com.garageautobot.garagemautobot.entities.OrdemServico;
import com.garageautobot.garagemautobot.services.OrdemServicoService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
@RequestMapping("/os")
public class OrdemServicoPdfController {

    private final OrdemServicoService osService;

    @Autowired
    public OrdemServicoPdfController(OrdemServicoService osService) {
        this.osService = osService;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        try {
            OrdemServico os = osService.findById(id)
                    .orElseThrow(() -> new RuntimeException("OS não encontrada: " + id));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Locale ptBR = Locale.forLanguageTag("pt-BR");
            DateTimeFormatter dtFmt  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            Font fTitulo   = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD,   new BaseColor(15, 23, 42));
            Font fSubtitulo = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(90, 100, 120));
            Font fLabel    = new Font(Font.FontFamily.HELVETICA,  9, Font.BOLD,   new BaseColor(100, 110, 130));
            Font fValor    = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, new BaseColor(15, 23, 42));
            Font fBold     = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,   new BaseColor(15, 23, 42));
            Font fTh       = new Font(Font.FontFamily.HELVETICA,  9, Font.BOLD,   BaseColor.WHITE);
            Font fTd       = new Font(Font.FontFamily.HELVETICA,  9, Font.NORMAL, new BaseColor(30, 40, 60));
            Font fTotal    = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD,   new BaseColor(15, 23, 42));
            Font fDestaque = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD,   new BaseColor(240, 180, 41));

            // ── CABEÇALHO ────────────────────────────────────────
            Paragraph header = new Paragraph("GARAGEM AUTOBOT", fTitulo);
            header.setAlignment(Element.ALIGN_CENTER);
            doc.add(header);

            Paragraph subHeader = new Paragraph("Sistema de Gestão de Oficina Mecânica", fSubtitulo);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            subHeader.setSpacingAfter(4);
            doc.add(subHeader);

            // Linha separadora
            LineSeparator ls = new LineSeparator();
            ls.setLineColor(new BaseColor(240, 180, 41));
            doc.add(new Chunk(ls));

            // ── NÚMERO DA OS e STATUS ─────────────────────────────
            doc.add(Chunk.NEWLINE);
            PdfPTable headerOS = new PdfPTable(2);
            headerOS.setWidthPercentage(100);
            headerOS.setWidths(new float[]{1, 1});
            headerOS.setSpacingAfter(12);

            PdfPCell cNumOS = new PdfPCell();
            cNumOS.setBorder(Rectangle.NO_BORDER);
            Paragraph pNumOS = new Paragraph();
            pNumOS.add(new Chunk("ORDEM DE SERVIÇO\n", fLabel));
            pNumOS.add(new Chunk(os.getNumeroOS(), fDestaque));
            cNumOS.addElement(pNumOS);
            headerOS.addCell(cNumOS);

            PdfPCell cStatus = new PdfPCell();
            cStatus.setBorder(Rectangle.NO_BORDER);
            cStatus.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph pStatus = new Paragraph();
            pStatus.setAlignment(Element.ALIGN_RIGHT);
            pStatus.add(new Chunk("STATUS\n", fLabel));
            pStatus.add(new Chunk(os.getStatus().getDescricao().toUpperCase(), fBold));
            cStatus.addElement(pStatus);
            headerOS.addCell(cStatus);

            doc.add(headerOS);

            // ── DATAS ─────────────────────────────────────────────
            PdfPTable tDatas = new PdfPTable(2);
            tDatas.setWidthPercentage(100);
            tDatas.setWidths(new float[]{1, 1});
            tDatas.setSpacingAfter(12);

            addCampo(tDatas, "Data de Abertura",
                    os.getDataAbertura().format(dtFmt), fLabel, fValor);
            addCampo(tDatas, "Data de Conclusão",
                    os.getDataConclusao() != null ? os.getDataConclusao().format(dateFmt) : "—",
                    fLabel, fValor);
            doc.add(tDatas);

            // ── DADOS DO VEÍCULO ──────────────────────────────────
            addSecao(doc, "VEÍCULO", fLabel);

            PdfPTable tVeiculo = new PdfPTable(4);
            tVeiculo.setWidthPercentage(100);
            tVeiculo.setWidths(new float[]{2, 2, 1, 1});
            tVeiculo.setSpacingAfter(12);

            addCampo(tVeiculo, "Marca / Modelo",
                    os.getVeiculo().getMarca() + " " + os.getVeiculo().getModelo(), fLabel, fValor);
            addCampo(tVeiculo, "Placa", os.getVeiculo().getPlaca(), fLabel, fValor);
            addCampo(tVeiculo, "Ano",   os.getVeiculo().getAno(),   fLabel, fValor);
            addCampo(tVeiculo, "Cliente", os.getVeiculo().getCliente().getNome(), fLabel, fValor);
            doc.add(tVeiculo);

            // ── DESCRIÇÃO DO PROBLEMA ─────────────────────────────
            addSecao(doc, "DESCRIÇÃO DO PROBLEMA / SERVIÇO", fLabel);
            Paragraph pDesc = new Paragraph(os.getDescricaoProblema(), fValor);
            pDesc.setSpacingAfter(8);
            doc.add(pDesc);

            if (os.getObservacoes() != null && !os.getObservacoes().isBlank()) {
                addSecao(doc, "OBSERVAÇÕES", fLabel);
                Paragraph pObs = new Paragraph(os.getObservacoes(), fValor);
                pObs.setSpacingAfter(8);
                doc.add(pObs);
            }

            // ── TABELA DE PEÇAS ───────────────────────────────────
            addSecao(doc, "PEÇAS UTILIZADAS", fLabel);

            PdfPTable tPecas = new PdfPTable(5);
            tPecas.setWidthPercentage(100);
            tPecas.setWidths(new float[]{3, 1, 1, 1.5f, 1.5f});
            tPecas.setSpacingAfter(8);

            BaseColor corCabecalho = new BaseColor(15, 23, 42);
            addTh(tPecas, "Peça / Código",   fTh, corCabecalho);
            addTh(tPecas, "Qtd.",            fTh, corCabecalho);
            addTh(tPecas, "Preço Unit.",     fTh, corCabecalho);
            addTh(tPecas, "Total",           fTh, corCabecalho);
            addTh(tPecas, "",                fTh, corCabecalho); // coluna vazia para simetria

            if (os.getItens().isEmpty()) {
                PdfPCell cVazio = new PdfPCell(new Phrase("Nenhuma peça registrada.", fTd));
                cVazio.setColspan(5);
                cVazio.setBorder(Rectangle.NO_BORDER);
                cVazio.setPadding(8);
                tPecas.addCell(cVazio);
            } else {
                for (ItemOS item : os.getItens()) {
                    String nomePeca = item.getPeca().getNome() + "\n" + item.getPeca().getCodigo();
                    addTd(tPecas, nomePeca, fTd);
                    addTd(tPecas, String.valueOf(item.getQuantidade()), fTd);
                    addTd(tPecas, "R$ " + String.format(ptBR, "%.2f", item.getPrecoUnitarioAplicado()), fTd);
                    addTd(tPecas, "R$ " + String.format(ptBR, "%.2f", item.getValorTotal()), fTd);
                    addTd(tPecas, "", fTd);
                }
            }
            doc.add(tPecas);

            // ── TOTAIS ────────────────────────────────────────────
            PdfPTable tTotais = new PdfPTable(2);
            tTotais.setWidthPercentage(60);
            tTotais.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tTotais.setWidths(new float[]{2, 1.5f});
            tTotais.setSpacingBefore(4);
            tTotais.setSpacingAfter(20);

            addLinhaTotalSimples(tTotais,
                    "Subtotal Peças:",
                    "R$ " + String.format(ptBR, "%.2f", os.getTotalPecas()), fValor, fBold);

            String descMaoObra = "Mão de Obra:";
            if (Boolean.TRUE.equals(os.getMaoObraPorPercentual()) && os.getPercentualMaoObra() != null) {
                descMaoObra = "Mão de Obra (" + String.format(ptBR, "%.0f", os.getPercentualMaoObra()) + "%):";
            }
            addLinhaTotalSimples(tTotais, descMaoObra,
                    "R$ " + String.format(ptBR, "%.2f", os.getValorMaoObraCalculado()), fValor, fBold);

            // Linha de total geral com destaque
            PdfPCell cDescTotal = new PdfPCell(new Phrase("TOTAL GERAL:", fTotal));
            cDescTotal.setBorder(Rectangle.TOP);
            cDescTotal.setPaddingTop(6);
            cDescTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tTotais.addCell(cDescTotal);

            PdfPCell cValTotal = new PdfPCell(
                    new Phrase("R$ " + String.format(ptBR, "%.2f", os.getTotalGeral()), fDestaque));
            cValTotal.setBorder(Rectangle.TOP);
            cValTotal.setPaddingTop(6);
            cValTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tTotais.addCell(cValTotal);

            doc.add(tTotais);

            // ── RODAPÉ ────────────────────────────────────────────
            doc.add(new Chunk(ls));
            Paragraph rodape = new Paragraph(
                    "Documento gerado em: " +
                    java.time.LocalDateTime.now().format(dtFmt) +
                    "   •   Garagem AutoBot — Sistema de Gestão de Oficina", fSubtitulo);
            rodape.setAlignment(Element.ALIGN_CENTER);
            rodape.setSpacingBefore(4);
            doc.add(rodape);

            doc.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "OS-" + os.getNumeroOS().replace("/", "-") + ".pdf");

            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── HELPERS ──────────────────────────────────────────────────

    private void addSecao(Document doc, String texto, Font font) throws DocumentException {
        Paragraph p = new Paragraph(texto, font);
        p.setSpacingBefore(6);
        p.setSpacingAfter(4);
        doc.add(p);
    }

    private void addCampo(PdfPTable table, String label, String valor, Font fLabel, Font fValor) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new BaseColor(220, 225, 235));
        cell.setPadding(6);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", fLabel));
        p.add(new Chunk(valor, fValor));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void addTh(PdfPTable table, String texto, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(7);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addTd(PdfPTable table, String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(6);
        cell.setBorderColor(new BaseColor(220, 225, 235));
        cell.setBorder(Rectangle.BOTTOM);
        table.addCell(cell);
    }

    private void addLinhaTotalSimples(PdfPTable table, String desc, String valor,
                                      Font fDesc, Font fValor) {
        PdfPCell cD = new PdfPCell(new Phrase(desc, fDesc));
        cD.setBorder(Rectangle.NO_BORDER);
        cD.setPadding(4);
        cD.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cD);

        PdfPCell cV = new PdfPCell(new Phrase(valor, fValor));
        cV.setBorder(Rectangle.NO_BORDER);
        cV.setPadding(4);
        cV.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cV);
    }
}