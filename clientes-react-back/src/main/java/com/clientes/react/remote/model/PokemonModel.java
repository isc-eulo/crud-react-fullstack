package com.clientes.react.remote.model;

import java.io.Serializable;

public class PokemonModel implements Serializable{
    private int id;
    private String name;
    private int weight;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
}
