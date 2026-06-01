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

    public List<Peca> findAll() {
        return pecaRepository.findAll();
    }

    public Optional<Peca> findById(Long id) {
        return pecaRepository.findById(id);
    }

    public void delete(Long id) {
        pecaRepository.deleteById(id);
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
