package com.garageautobot.garagemautobot.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de backup automático do banco MySQL.
 *
 * - Backup automático diário às 3h da madrugada
 * - Botão manual (método gerarBackup) disponível para o admin
 * - Retenção: mantém os últimos 7 backups, apaga os mais antigos
 *
 * Lê as credenciais direto da configuração do Spring (não duplica senha).
 * Usa o utilitário mysqldump (vem junto com a instalação do MySQL).
 */
@Service
public class BackupService {

    // Lidos do application.properties que o Spring já carrega
    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // Pasta onde os backups são salvos (configurável; default abaixo)
    @Value("${app.backup.dir:backups}")
    private String backupDir;

    // Caminho do mysqldump. Por padrão assume que está no PATH do sistema.
    // Se não estiver, configure app.backup.mysqldump-path no application.properties
    // apontando para o executável (ex: C:/Program Files/MySQL/MySQL Server 9.4/bin/mysqldump.exe)
    @Value("${app.backup.mysqldump-path:mysqldump}")
    private String mysqldumpPath;

    private static final int RETENCAO_DIAS = 7;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    // Garante que a pasta de backup existe ao iniciar
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(backupDir).toAbsolutePath().normalize());
        } catch (IOException e) {
            System.err.println("[BACKUP] Não foi possível criar a pasta de backup: " + e.getMessage());
        }
    }

    // ── BACKUP AUTOMÁTICO DIÁRIO (3h da manhã) ────────────────────
    // cron: segundo minuto hora dia mês diaDaSemana
    @Scheduled(cron = "0 0 3 * * *")
    public void backupAutomatico() {
        try {
            String arquivo = gerarBackup();
            System.out.println("[BACKUP] Backup automático criado: " + arquivo);
            limparBackupsAntigos();
        } catch (Exception e) {
            System.err.println("[BACKUP] Falha no backup automático: " + e.getMessage());
        }
    }

    // ── GERAR BACKUP (usado pelo automático e pelo botão manual) ──
    public String gerarBackup() throws IOException, InterruptedException {
        String nomeBanco = extrairNomeBanco(datasourceUrl);

        Path pasta = Paths.get(backupDir).toAbsolutePath().normalize();
        Files.createDirectories(pasta);

        String nomeArquivo = "mecanikas_" + nomeBanco + "_" +
                LocalDateTime.now().format(FMT) + ".sql";
        Path destino = pasta.resolve(nomeArquivo);

        // Monta o comando mysqldump
        List<String> comando = new ArrayList<>();
        comando.add(mysqldumpPath);
        comando.add("--user=" + dbUser);
        // Senha vai por variável de ambiente para não aparecer em logs de processo
        comando.add("--password=" + (dbPassword == null ? "" : dbPassword));
        comando.add("--databases");
        comando.add(nomeBanco);
        comando.add("--result-file=" + destino.toString());
        comando.add("--default-character-set=utf8mb4");

        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectErrorStream(true);
        Process processo = pb.start();

        // Lê a saída (para não travar) e espera terminar
        String saida = new String(processo.getInputStream().readAllBytes());
        int codigo = processo.waitFor();

        if (codigo != 0) {
            // Remove arquivo incompleto, se gerou
            Files.deleteIfExists(destino);
            throw new IOException("mysqldump falhou (código " + codigo + "): " + saida);
        }

        limparBackupsAntigos();
        return destino.toString();
    }

    // ── RETENÇÃO: mantém só os últimos N backups ──────────────────
    public void limparBackupsAntigos() {
        try {
            Path pasta = Paths.get(backupDir).toAbsolutePath().normalize();
            if (!Files.exists(pasta)) return;

            List<Path> backups = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(pasta, "mecanikas_*.sql")) {
                for (Path p : stream) backups.add(p);
            }

            // Ordena do mais novo para o mais antigo
            backups.sort((a, b) -> {
                try {
                    BasicFileAttributes attA = Files.readAttributes(a, BasicFileAttributes.class);
                    BasicFileAttributes attB = Files.readAttributes(b, BasicFileAttributes.class);
                    return attB.creationTime().compareTo(attA.creationTime());
                } catch (IOException e) {
                    return 0;
                }
            });

            // Apaga os que excedem a retenção
            for (int i = RETENCAO_DIAS; i < backups.size(); i++) {
                Files.deleteIfExists(backups.get(i));
            }
        } catch (IOException e) {
            System.err.println("[BACKUP] Erro ao limpar backups antigos: " + e.getMessage());
        }
    }

    // ── INFO: lista os backups existentes (para a tela) ───────────
    public List<String> listarBackups() {
        List<String> nomes = new ArrayList<>();
        try {
            Path pasta = Paths.get(backupDir).toAbsolutePath().normalize();
            if (!Files.exists(pasta)) return nomes;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(pasta, "mecanikas_*.sql")) {
                for (Path p : stream) {
                    nomes.add(p.getFileName().toString());
                }
            }
            nomes.sort((a, b) -> b.compareTo(a)); // mais recente primeiro (nome tem data)
        } catch (IOException e) {
            System.err.println("[BACKUP] Erro ao listar backups: " + e.getMessage());
        }
        return nomes;
    }

    public String getPastaBackup() {
        return Paths.get(backupDir).toAbsolutePath().normalize().toString();
    }

    // Extrai o nome do banco da URL jdbc (ex: jdbc:mysql://localhost:3306/mecanikas?... -> mecanikas)
    private String extrairNomeBanco(String url) {
        String semParametros = url.split("\\?")[0];
        int ultimaBarra = semParametros.lastIndexOf('/');
        return semParametros.substring(ultimaBarra + 1);
    }
}