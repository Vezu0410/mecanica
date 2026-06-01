package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.FotoVeiculo;
import com.garageautobot.garagemautobot.entities.MomentoFoto;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.repositories.VeiculoRepository;
import com.garageautobot.garagemautobot.services.FotoVeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/fotos")
public class FotoVeiculoController {

    private final FotoVeiculoService fotoService;
    private final VeiculoRepository  veiculoRepository;

    @Autowired
    public FotoVeiculoController(FotoVeiculoService fotoService,
                                  VeiculoRepository veiculoRepository) {
        this.fotoService       = fotoService;
        this.veiculoRepository = veiculoRepository;
    }

    // ── GALERIA DO VEÍCULO ───────────────────────────────────────

    @GetMapping("/veiculo/{veiculoId}")
    public String galeria(@PathVariable Long veiculoId,
                          @RequestParam(required = false) Long osId,
                          Model model) {

        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado: " + veiculoId));

        List<FotoVeiculo> fotosEntrada =
                fotoService.listarPorVeiculoEMomento(veiculoId, MomentoFoto.ENTRADA);
        List<FotoVeiculo> fotosSaida =
                fotoService.listarPorVeiculoEMomento(veiculoId, MomentoFoto.SAIDA);

        model.addAttribute("veiculo",      veiculo);
        model.addAttribute("fotosEntrada", fotosEntrada);
        model.addAttribute("fotosSaida",   fotosSaida);
        model.addAttribute("osId",         osId);
        model.addAttribute("momentos",     MomentoFoto.values());
        return "fotos/galeria-veiculo";
    }

    // ── UPLOAD ───────────────────────────────────────────────────

    @PostMapping("/veiculo/{veiculoId}/upload")
    public String upload(@PathVariable Long veiculoId,
                         @RequestParam(required = false) Long osId,
                         @RequestParam String momento,
                         @RequestParam(required = false) String legenda,
                         @RequestParam("arquivos") List<MultipartFile> arquivos,
                         RedirectAttributes ra) {
        try {
            MomentoFoto momentoFoto = MomentoFoto.valueOf(momento);
            fotoService.salvarFotos(veiculoId, osId, momentoFoto, legenda, arquivos);
            ra.addFlashAttribute("sucesso",
                    arquivos.size() + " foto(s) salva(s) com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao salvar fotos: " + e.getMessage());
        }

        // Redireciona de volta para a galeria, mantendo o contexto da OS se houver
        String redirect = "redirect:/fotos/veiculo/" + veiculoId;
        if (osId != null) redirect += "?osId=" + osId;
        return redirect;
    }

    // ── EXCLUIR FOTO ─────────────────────────────────────────────

    @PostMapping("/{fotoId}/excluir")
    public String excluir(@PathVariable Long fotoId,
                          @RequestParam Long veiculoId,
                          @RequestParam(required = false) Long osId,
                          RedirectAttributes ra) {
        try {
            fotoService.excluirFoto(fotoId);
            ra.addFlashAttribute("sucesso", "Foto removida com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao remover foto: " + e.getMessage());
        }

        String redirect = "redirect:/fotos/veiculo/" + veiculoId;
        if (osId != null) redirect += "?osId=" + osId;
        return redirect;
    }

    // ── SERVIR IMAGEM (src das <img>) ────────────────────────────

    @GetMapping("/imagem/{nomeArquivo:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> servirImagem(@PathVariable String nomeArquivo) {
        try {
            byte[] dados = fotoService.carregarArquivo(nomeArquivo);
            MediaType tipo = detectarMediaType(nomeArquivo);
            return ResponseEntity.ok().contentType(tipo).body(dados);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── HELPER ───────────────────────────────────────────────────

    private MediaType detectarMediaType(String nome) {
        String lower = nome.toLowerCase();
        if (lower.endsWith(".png"))  return MediaType.IMAGE_PNG;
        if (lower.endsWith(".gif"))  return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp")) return MediaType.parseMediaType("image/webp");
        return MediaType.IMAGE_JPEG;
    }
}