package com.gortmol.tupokedex.model;

public class Pokemon {

    private String name;
    private String url;
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

    public int getIndex() {
        String[] urlParts = url.split("/");
        try {
            return Integer.parseInt(urlParts[urlParts.length - 1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The URL does not contain a valid numeric index: " + url, e);
        }
    }

    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured(boolean captured) {
        isCaptured = captured;
    }

}
