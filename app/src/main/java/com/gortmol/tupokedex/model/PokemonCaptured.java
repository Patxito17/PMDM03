package com.gortmol.tupokedex.model;

import java.util.ArrayList;

public class PokemonCaptured {

    private final String BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/";

    private String name;
    private int id;
    private String imageUrl;
    private ArrayList<String> imageTypes; // A url list with type images
    private double weight; // The weight of this Pokémon in hectograms
    private double height; // The height of this Pokémon in decimetres

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpriteUrl() {
        return imageUrl;
    }

    public void setImageUrl() {
        if (imageUrl == null) {
            imageUrl = BASE_URL + id + ".png";
        }
    }

    public ArrayList<String> getImageTypes() {
        return imageTypes;
    }

    public void setImageTypes(ArrayList<String> imageTypes) {
        this.imageTypes = imageTypes;
    }

    public double getWeight() {
        return weight / 10.0;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height / 10.0;
    }

    public void setHeight(double height) {
        this.height = height;
    }

}