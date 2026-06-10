package com.garageautobot.garagemautobot.controller;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilitário para gerar o JSON consumido pelo componente searchable-select.
 * Produz uma string no formato: [{"id":1,"texto":"João Silva"},{...}]
 *
 * Escapa aspas e barras para não quebrar o JSON nem o HTML assim da pra fazer a consulta.
 */
public final class ComboJson {

    private ComboJson() {}

    /**
     * @param itens   lista de objetos (ex: clientes, veículos)
     * @param getId   função que extrai o id (ex: Cliente::getId)
     * @param getTexto função que extrai o texto a exibir (ex: Cliente::getNome)
     */
    public static <T> String gerar(List<T> itens,
                                   Function<T, Object> getId,
                                   Function<T, String> getTexto) {
        return itens.stream()
                .map(item -> {
                    String id    = escapar(String.valueOf(getId.apply(item)));
                    String texto = escapar(getTexto.apply(item));
                    return "{\"id\":\"" + id + "\",\"texto\":\"" + texto + "\"}";
                })
                .collect(Collectors.joining(",", "[", "]"));
    }

    private static String escapar(String valor) {
        if (valor == null) return "";
        return valor
                .replace("\\", "\\\\")  // barra invertida
                .replace("\"", "\\\"")  // aspas duplas
                .replace("\n", " ")      // quebras de linha
                .replace("\r", " ");
    }
}