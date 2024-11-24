package com.gortmol.tupokedex.data;

public class PokemonPokedexData {
    private String name;
    private String index;
    private boolean isCaptured;

    public PokemonPokedexData(String name, String index, boolean isCaptured) {
        this.name = name;
        this.index = index;
        this.isCaptured = isCaptured;
    }

    public String getName() {
        return name;
    }

    public String getIndex() {
        return index;
    }

    public boolean isCaptured() {
        return isCaptured;
    }
}
