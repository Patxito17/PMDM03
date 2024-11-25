package com.gortmol.tupokedex.io.response;


import java.util.ArrayList;

public class PokemonDetailsResponse {

    private final String BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/types/generation-viii/sword-shield/";

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
        return name.substring(0, 1).toUpperCase() + name.substring(1);
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
        setTypeImages();
        return typeImages;
    }

    public void setTypeImages() {
        typeImages = new ArrayList<>();
        for (TypeSlot typeSlot : types) {
            String url = typeSlot.getType().getUrl();
            typeImages.add(BASE_URL + getTypeId(url) + ".png");
        }
    }

    public int getTypeId(String url) {
        String[] urlParts = url.split("/");
        try {
            return Integer.parseInt(urlParts[urlParts.length - 1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The URL does not contain a valid numeric index: " + url, e);
        }
    }


    public static class TypeSlot {
        private Type type;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public static class Type {
            private String name;
            private String url;

            public String getName() {
                return name;
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
        }
    }
}
