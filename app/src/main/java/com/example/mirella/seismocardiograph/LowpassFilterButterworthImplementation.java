package com.example.mirella.seismocardiograph;

/**
 * Implementacja filtru dolnoprzepustowego Butterwortha.
 *
 * @author Mirella
 * @version 1.0
 */
public class LowpassFilterButterworthImplementation {

    /**
     * Obiekt klasy LowpassFilterButterworthSection;
     */
    private final LowpassFilterButterworthSection[] section;

    /**
     * Implementacja filtru dolnoprzepustowego Butterwortha poprzez
     * kaskadowe połączenie sekcji filtrów dolnoprzepustowych.
     *
     * @param cutoffFrequencyHz Częstotliwość odcięcia w Hz.
     * @param numSections       Ilość sekcji filtru.
     * @param Fs                Częstotliwość próbkowania.
     */
    public LowpassFilterButterworthImplementation
            (double cutoffFrequencyHz, int numSections, double Fs) {
        this.section = new LowpassFilterButterworthSection[numSections];
        for (int i = 0; i < numSections; i++) {
            this.section[i] = new LowpassFilterButterworthSection
                    (cutoffFrequencyHz, i + 1, numSections * 2, Fs);
        }
    }

    /**
     * Oblicza współczynnik filtru dla pojedyńczej próbki sygnału.
     *
     * @param input  Próbka sygnału.
     * @return       Próbka sygnału po filtracji.
     */
    public double compute(double input) {
        double output = input;
        for (LowpassFilterButterworthSection lowpassFilterButterworthSection : this.section) {
            output = lowpassFilterButterworthSection.compute(output);
        }
        return output;
    }
}
