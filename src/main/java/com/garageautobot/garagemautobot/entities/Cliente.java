package com.garageautobot.garagemautobot.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefone;

    private String cep;
    private String endereco;
    private String cidade;
    private String estado;

    private LocalDate dataCadastro = LocalDate.now();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Veiculo> veiculos = new ArrayList<>();

    public List<Veiculo> getVeiculos() { return veiculos; }
    public void setVeiculos(List<Veiculo> veiculos) { this.veiculos = veiculos; }

    public Cliente() {}

    public Cliente(String nome, String cpf, String email, String telefone,
                   String cep, String endereco, String cidade, String estado) {
        this.nome = nome;
        setCpf(cpf); 
        this.email = email;
        setTelefone(telefone); 
        setCep(cep);
        this.endereco = endereco;
        this.cidade = cidade;
        this.estado = estado;
        this.dataCadastro = LocalDate.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) {
        if (cpf != null) {
            this.cpf = cpf.replaceAll("\\D", "");
        } else {
            this.cpf = null;
        }
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) {
        if (telefone != null) {
            this.telefone = telefone.replaceAll("\\D", ""); 
        } else {
            this.telefone = null;
        }
    }

    public String getCep() { return cep; }
    public void setCep(String cep) {
        if (cep != null) {
            this.cep = cep.replaceAll("\\D", "");
        } else {
            this.cep = null;
        }
    }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
}
