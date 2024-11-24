package com.gortmol.tupokedex.io.response;


import java.util.ArrayList;

public class PokemonDetailsResponse {

    private int id;
    private String name;
    private int height;
    private int weight;
    private ArrayList<TypeSlot> types;
    private ArrayList<String> typeImages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ArrayList<TypeSlot> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<TypeSlot> types) {
        this.types = types;
    }

    public ArrayList<String> getTypeImages() {
        return typeImages;
    }

    public void setTypeImages(ArrayList<String> typeImages) {
        this.typeImages = typeImages;
    }

    private static class TypeSlot {
        private Type type;

        public Type getType() {
            return type;
        }

        public static class Type {
            private final String BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/types/generation-viii/sword-shield/";

            private String url;

            public String getUrl() {
                if (url == null) {
                    url = BASE_URL + getTypeIndex() + ".png";
                }
                return url;
            }

            public int getTypeIndex() {
                String[] urlParts = url.split("/");
                try {
                    return Integer.parseInt(urlParts[urlParts.length - 1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("The URL does not contain a valid numeric index: " + url, e);
                }
            }

        }

    }
}
