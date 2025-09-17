package com.garageautobot.garagemautobot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garageautobot.garagemautobot.entities.Cliente;
import com.garageautobot.garagemautobot.repositories.ClienteRepository;

@RestController
	@RequestMapping("/clientes")
	public class ClienteRestController {

	    private final ClienteRepository clienteRepository;

	    @Autowired
	    public ClienteRestController(ClienteRepository clienteRepository) {
	        this.clienteRepository = clienteRepository;
	    }

	    // Endpoint que retorna todos os clientes em JSON
	    @GetMapping
	    public List<Cliente> listarTodos() {
	        return clienteRepository.findAll();
	    }
	}


