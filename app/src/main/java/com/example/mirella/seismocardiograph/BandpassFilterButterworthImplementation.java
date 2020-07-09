package com.example.mirella.seismocardiograph;

/**
 * Implementacja filtru pasmowo-przepustowego Butterwortha.
 *
 * @author Mirella
 * @version 1.0
 */
class BandpassFilterButterworthImplementation {
    /**
     * Obiekt klasy LowpassFilterButterworthImplementation;
     */
    private final LowpassFilterButterworthImplementation lowpassFilter;

    /**
     * Obiekt klasy HighpassFilterButterworthImplementation;
     */
    private final HighpassFilterButterworthImplementation highpassFilter;

    /**
     * Implementacja filtru pasmowo-przepustowego Butterwortha poprzez
     * kaskadowe połączenie sekcji filtrów górnoprzepustowych i dolnoprzepustowych.
     *
     * @param bottomFrequencyHz Dolna częstotliwość odcięcia w Hz.
     * @param topFrequencyHz    Górna częstotliwość odcięcia w Hz.
     * @param numSections       Ilość sekcji filtru.
     * @param Fs                Częstotliwość próbkowania.
     */
    public BandpassFilterButterworthImplementation
            (double bottomFrequencyHz, double topFrequencyHz, int numSections, double Fs) {
        this.lowpassFilter = new LowpassFilterButterworthImplementation
                (topFrequencyHz, numSections, Fs);
        this.highpassFilter = new HighpassFilterButterworthImplementation
                (bottomFrequencyHz, numSections, Fs);
    }

    /**
     * Zwraca próbkę sygnału po filtracji kaskadą filtrów dolnoprzepustowych i górnoprzepustowych.
     *
     * @param input  Próbka sygnału.
     * @return       Próbka sygnału po filtracji.
     */
    public void compute(double input) {
        // compute the result as the cascade of the highpass and lowpass filters
        this.highpassFilter.compute(this.lowpassFilter.compute(input));
    }
}
