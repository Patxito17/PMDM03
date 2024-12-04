package com.gortmol.tupokedex.model;

import java.util.ArrayList;

public class Pokemon {

    private final String IMAGE_BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/";

    private String name;
    private String url;
    private int id;
    private String imageUrl;
    private ArrayList<String> imageTypes; // A url list with type images
    private double weight; // The weight of this Pokémon in hectograms
    private double height; // The height of this Pokémon in decimetres
    private boolean isCaptured; // Whether the Pokémon is captured or not

    public Pokemon() {
        this.isCaptured = false;
    }

    public Pokemon(String name, int id, String url, ArrayList<String> imageTypes, double weight, double height) {
        this.name = name;
        this.id = id;
        this.url = url;
        this.imageTypes = imageTypes;
        this.weight = weight;
        this.height = height;
        setImageUrl();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId() {
        String[] urlParts = url.split("/");
        id = Integer.parseInt(urlParts[urlParts.length - 1]);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl() {
        if (imageUrl == null) {
            imageUrl = IMAGE_BASE_URL + id + ".png";
        }
    }

    public ArrayList<String> getImageTypes() {
        return imageTypes;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured(boolean isCaptured) {
        this.isCaptured = isCaptured;
    }

}
