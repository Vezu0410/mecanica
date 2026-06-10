package com.garageautobot.garagemautobot.services;

import com.garageautobot.garagemautobot.entities.Peca;
import com.garageautobot.garagemautobot.repositories.PecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PecaService {

    private final PecaRepository pecaRepository;

    @Autowired
    public PecaService(PecaRepository pecaRepository) {
        this.pecaRepository = pecaRepository;
    }

    public Peca save(Peca peca) {
        return pecaRepository.save(peca);
    }

    // findAll agora retorna SÓ as peças ativas (uso normal em listas e combos)
    public List<Peca> findAll() {
        return pecaRepository.findByAtivoTrue();
    }

    // Lista as peças inativas (para o admin gerenciar/reativar)
    public List<Peca> findInativas() {
        return pecaRepository.findByAtivoFalse();
    }

    // Inclui ativas + inativas (caso precise em algum relatório)
    public List<Peca> findTodas() {
        return pecaRepository.findAll();
    }

    public Optional<Peca> findById(Long id) {
        return pecaRepository.findById(id);
    }

    // "delete" agora é INATIVAÇÃO (soft delete): preserva o histórico
    public void inativar(Long id) {
        Peca peca = pecaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada: " + id));

        // Regra: não permite inativar peça que ainda tem estoque físico.
        // O usuário precisa zerar o estoque antes (vender, usar ou ajustar).
        if (peca.getQuantidade() > 0) {
            throw new IllegalStateException(
                "Não é possível inativar \"" + peca.getNome() + "\": ainda há " +
                peca.getQuantidade() + " unidade(s) em estoque. " +
                "Zere o estoque antes de inativar.");
        }

        peca.setAtivo(false);
        pecaRepository.save(peca);
    }

    // Reativa uma peça (só admin)
    public void reativar(Long id) {
        Peca peca = pecaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada: " + id));
        peca.setAtivo(true);
        pecaRepository.save(peca);
    }

    public void entradaEstoque(Long id, int quantidade) {
        Peca peca = pecaRepository.findById(id).orElseThrow();
        peca.setQuantidade(peca.getQuantidade() + quantidade);
        pecaRepository.save(peca);
    }

    public void saidaEstoque(Long id, int quantidade) {
        Peca peca = pecaRepository.findById(id).orElseThrow();
        if (peca.getQuantidade() < quantidade) {
            throw new IllegalArgumentException("Quantidade insuficiente em estoque!");
        }
        peca.setQuantidade(peca.getQuantidade() - quantidade);
        pecaRepository.save(peca);
    }
}