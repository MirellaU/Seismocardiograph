package com.example.mirella.seismocardiograph;

public class Menu {
    private final int text;
    private final int imageResource;

    public Menu(int text, int imageResource) {
        this.text = text;
        this.imageResource = imageResource;
    }

    public int getText() {
        return text;
    }

    public int getImageResource() {
        return imageResource;
    }
}

