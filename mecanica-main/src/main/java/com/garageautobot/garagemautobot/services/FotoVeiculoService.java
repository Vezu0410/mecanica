package com.garageautobot.garagemautobot.services;

import com.garageautobot.garagemautobot.entities.*;
import com.garageautobot.garagemautobot.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FotoVeiculoService {

    private final FotoVeiculoRepository fotoRepository;
    private final VeiculoRepository     veiculoRepository;
    private final OrdemServicoRepository osRepository;

    // Pasta base de uploads — configurável no application.properties
    // Padrão: uploads/fotos-veiculo (relativo à raiz do projeto)
    @Value("${app.upload.fotos-dir:uploads/fotos-veiculo}")
    private String uploadDir;

    @Autowired
    public FotoVeiculoService(FotoVeiculoRepository fotoRepository,
                               VeiculoRepository veiculoRepository,
                               OrdemServicoRepository osRepository) {
        this.fotoRepository    = fotoRepository;
        this.veiculoRepository = veiculoRepository;
        this.osRepository      = osRepository;
    }

    // ── UPLOAD MÚLTIPLO ───────────────────────────────────────────

    /**
     * Salva uma lista de fotos vinculadas ao veículo.
     * osId é opcional — se informado, vincula também à OS.
     */
    @Transactional
    public void salvarFotos(Long veiculoId,
                             Long osId,
                             MomentoFoto momento,
                             String legenda,
                             List<MultipartFile> arquivos) throws IOException {

        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado: " + veiculoId));

        OrdemServico os = null;
        if (osId != null) {
            os = osRepository.findById(osId).orElse(null);
        }

        // Garante que o diretório existe (caminho absoluto, mesmo usado na leitura)
        Path pasta = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(pasta);

        for (MultipartFile arquivo : arquivos) {
            if (arquivo == null || arquivo.isEmpty()) continue;

            // Valida tipo de arquivo
            String contentType = arquivo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Apenas imagens são aceitas. Arquivo inválido: "
                        + arquivo.getOriginalFilename());
            }

            // Gera nome único para evitar colisão
            String extensao    = obterExtensao(arquivo.getOriginalFilename());
            String nomeArquivo = UUID.randomUUID().toString() + extensao;

            // Salva o arquivo em disco
            Path destino = pasta.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // Persiste metadados no banco
            FotoVeiculo foto = new FotoVeiculo(nomeArquivo, veiculo, momento);
            foto.setLegenda(legenda);
            foto.setOrdemServico(os);
            fotoRepository.save(foto);
        }
    }

    // ── LISTAGENS ─────────────────────────────────────────────────

    public List<FotoVeiculo> listarPorVeiculo(Long veiculoId) {
        return fotoRepository.findByVeiculoIdOrderByDataRegistroAsc(veiculoId);
    }

    public List<FotoVeiculo> listarPorVeiculoEMomento(Long veiculoId, MomentoFoto momento) {
        return fotoRepository.findByVeiculoIdAndMomentoOrderByDataRegistroAsc(veiculoId, momento);
    }

    public List<FotoVeiculo> listarPorOS(Long osId) {
        return fotoRepository.findByOrdemServicoIdOrderByDataRegistroAsc(osId);
    }

    // ── EXCLUSÃO ──────────────────────────────────────────────────

    @Transactional
    public void excluirFoto(Long fotoId) throws IOException {
        FotoVeiculo foto = fotoRepository.findById(fotoId)
                .orElseThrow(() -> new RuntimeException("Foto não encontrada: " + fotoId));

        // Remove arquivo do disco (mesmo caminho absoluto usado para salvar/ler)
        Path arquivo = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(foto.getNomeArquivo());
        Files.deleteIfExists(arquivo);

        // Remove do banco
        fotoRepository.delete(foto);
    }

    // ── SERVIR ARQUIVO ────────────────────────────────────────────

    public byte[] carregarArquivo(String nomeArquivo) throws IOException {
        // Base absoluta da pasta de uploads
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path arquivo = base.resolve(nomeArquivo).normalize();

        // Segurança: impede path traversal (../) — compara absoluto com absoluto
        if (!arquivo.startsWith(base)) {
            throw new SecurityException("Acesso negado.");
        }
        return Files.readAllBytes(arquivo);
    }

    // ── HELPER ────────────────────────────────────────────────────

    private String obterExtensao(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) return ".jpg";
        return nomeOriginal.substring(nomeOriginal.lastIndexOf('.'));
    }
}