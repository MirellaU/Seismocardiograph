package com.example.mirella.seismocardiograph;

/**
 * Implementacja filtru górnoprzepustowego Butterwortha.
 *
 * @author Mirella
 * @version 1.0
 */
public class HighpassFilterButterworthImplementation {
    /**
     * Obiekt klasy HighpassFilterButterworthSection;
     */
    private final HighpassFilterButterworthSection[] section;

    /**
     * Implementacja filtru górnoprzepustowego Butterwortha poprzez
     * kaskadowe połączenie sekcji filtrów górnoprzepustowych.
     *
     * @param cutoffFrequencyHz Częstotliwość odcięcia w Hz.
     * @param numSections       Ilość sekcji filtru.
     * @param Fs                Częstotliwość próbkowania.
     */
    public HighpassFilterButterworthImplementation
            (double cutoffFrequencyHz, int numSections, double Fs) {
        this.section = new HighpassFilterButterworthSection[numSections];
        for (int i = 0; i < numSections; i++) {
            this.section[i] = new HighpassFilterButterworthSection
                    (cutoffFrequencyHz, i + 1, numSections * 2, Fs);
        }
    }

    /**
     * Oblicza współczynnik filtru dla pojedyńczej próbki sygnału.
     *
     * @param input Próbka sygnału.
     * @return      Próbka sygnału po filtracji.
     */
    public double compute(double input) {
        double output = input;
        for (HighpassFilterButterworthSection highpassFilterButterworthSection : this.section) {
            output = highpassFilterButterworthSection.compute(output);
        }
        return output;
    }
}
