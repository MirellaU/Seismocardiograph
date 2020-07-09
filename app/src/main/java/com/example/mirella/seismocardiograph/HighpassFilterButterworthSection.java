package com.example.mirella.seismocardiograph;

/**
 * Tworzy filtr górnoprzepustwiy Butterwortha.
 *
 * @author Mirella
 * @version 1.0
 */
public class HighpassFilterButterworthSection {
    /**
     * Tworzy obiekt klasy FIRFilterImplementation o zadanym rzędzie filtru.
     */
    private final FIRFilterImplementation firFilter = new FIRFilterImplementation(3);
    /**
     * Tworzy obiekt klasy IIRFilterImplementation o zadanym rzędzie filtru.
     */
    private final IIRFilterImplementation iirFilter = new IIRFilterImplementation(2);

    /**
     * Współczynniki filtru FIR.
     */
    private final double[] a = new double[3];

    /**
     * Współczynniki filtru IIR.
     */
    private final double[] b = new double[2];

    /**
     * Wzmocnienie filtru.
     */
    private final double gain;

    /**
     * Tworzy filtr górnoprzepustowy typu FIR i IIR.
     *
     * @param cutoffFrequencyHz Częstotliwość odcięcia w Hz.
     * @param k                 Współczynnik tłumienia.
     * @param n                 Długość filtru.
     * @param Fs                Częstotliwość próbkowania.
     */
    public HighpassFilterButterworthSection(double cutoffFrequencyHz, double k, double n, double Fs) {
        // pre-warp omegac and invert it
        double omegac = 1.0 /(2.0 * Fs * Math.tan(Math.PI * cutoffFrequencyHz / Fs));

        // compute zeta
        double zeta = -Math.cos(Math.PI * (2.0 * k + n - 1.0) / (2.0 * n));

        // fir section
        this.a[0] = 4.0 * Fs * Fs;
        this.a[1] = -8.0 * Fs * Fs;
        this.a[2] = 4.0 * Fs * Fs;

        //iir section
        //normalize coefficients so that b0 = 1
        //and higher-order coefficients are scaled and negated
        double b0 = (4.0 * Fs * Fs) + (4.0 * Fs * zeta / omegac) + (1.0 / (omegac * omegac));
        this.b[0] = ((2.0 / (omegac * omegac)) - (8.0 * Fs * Fs)) / (-b0);
        this.b[1] = ((4.0 * Fs * Fs)
                - (4.0 * Fs * zeta / omegac) + (1.0 / (omegac * omegac))) / (-b0);
        this.gain = 1.0 / b0;
    }

     /**
     * Oblicza współczynnik filtru dla pojedyńczej próbki sygnału
     * w postaci kaskady filtrów FIR i IIR.
     *
     * @param input Próbka sygnału.
     * @return      Próbka sygnału po filtracji.
     */
    public double compute(double input) {
        // compute the result as the cascade of the fir and iir filters
        return this.iirFilter.compute
                (this.firFilter.compute(this.gain * input, this.a), this.b);
    }
}
