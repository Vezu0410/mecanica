package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.services.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BackupController {

    @Autowired
    private BackupService backupService;

    // Tela de backup (lista os existentes)
    @GetMapping("/backup")
    public String telaBackup(Model model) {
        model.addAttribute("backups", backupService.listarBackups());
        model.addAttribute("pasta", backupService.getPastaBackup());
        return "backup";
    }

    // Botao manual: gera backup agora
    @PostMapping("/backup/gerar")
    public String gerarAgora(RedirectAttributes ra) {
        try {
            String arquivo = backupService.gerarBackup();
            ra.addFlashAttribute("sucesso", "Backup gerado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Falha ao gerar backup: " + e.getMessage());
        }
        return "redirect:/backup";
    }
}