package com.example.mirella.seismocardiograph;

/**
 * Klasa definiująca wygląd pojedynczego elementu w menu aplikacji.
 *
 * @author Mirella
 * @version 1.0
 */
class Menu {
    private final int text;
    private final int imageResource;

    /**
     * Konstruktor klasy.
     *
     * @param text
     * @param imageResource the image resource
     */
    public Menu(int text, int imageResource) {
        this.text = text;
        this.imageResource = imageResource;
    }

    /**
     * Konstruktor parametru text.
     *
     * @return Obiekt typu text.
     */
    public int getText() {
        return text;
    }

    /**
     * Konstruktor parametru imageResource.
     *
     * @return Obiekt typu imageResource.
     */
    public int getImageResource() {
        return imageResource;
    }
}

