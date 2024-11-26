package com.gortmol.tupokedex.model;

public class Pokemon {

    private final String BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/";

    private String name;
    private String url;
    private String spriteUrl;
    private boolean isCaptured;

    public Pokemon() {
        this.isCaptured = false;
    }

    public String getName() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        String[] urlParts = url.split("/");
        try {
            return Integer.parseInt(urlParts[urlParts.length - 1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The URL does not contain a valid numeric index: " + url, e);
        }
    }

    public String getSpriteUrl() {
        if (spriteUrl == null) {
            spriteUrl = BASE_URL + getId() + ".png";
        }
        return spriteUrl;
    }


    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured(boolean captured) {
        isCaptured = captured;
    }

}
