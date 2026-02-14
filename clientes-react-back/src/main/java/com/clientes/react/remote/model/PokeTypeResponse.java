package com.clientes.react.remote.model;

import java.util.List;

public class PokeTypeResponse implements java.io.Serializable{

    private List<PokemonEntry> pokemon; // La lista que regresa la API

    public static class PokemonEntry {
        private PokemonSummary pokemon; // Objeto anidado que trae name y url
        // Getters y Setters
    }


}
