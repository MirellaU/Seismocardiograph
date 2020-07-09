package com.example.mirella.seismocardiograph;

/**
 * Implementacja filtru IIR.
 *
 * @author Mirella
 * @version 1.0
 */
public class IIRFilterImplementation {
    /**
     * Tablica o długości równej rzędowi filtru.
     */
    private final double[] z;

    /**
     * Konstruktor klasy.
     *
     * @param order Rząd filtru.
     */
    public IIRFilterImplementation(int order) {
        this.z = new double[order];
    }

    /**
     * Równanie różnicowe filtru w postaci: y(t) = x(t) + a1*y(t-1) + a2*y(t-2) + ... + an*y(t-n).
     * Transformata Z w postaci: H(z) = 1 / (1 - sum(1 do n) * [an * y(t-n)]).
     * Założono, że a0 jest równe 1.
     *
     * @param input Próbka sygnału
     * @param a     Współczynniki filtru IIR.
     * @return      Sumę wyników mnożenia próbek sygnału przez współczynniki filtru.
     */
    public double compute(double input, double[] a) {
        // computes y(t) = x(t) + a1*y(t-1) + a2*y(t-2) + ... an*y(t-n)
        // z-transform: H(z) = 1 / (1 - sum(1 to n) [an * y(t-n)])
        // a0 is assumed to be 1
        // y(t) is not stored, so y(t-1) is stored at z[0],
        // and a1 is stored as coefficient[0]
        double result = input;
        for (int t = 0; t < a.length; t++) {
            result += a[t] * this.z[t];
        }
        for (int t = a.length - 1; t >= 0; t--) {
            if (t > 0) {
                this.z[t] = this.z[t - 1];
            } else {
                this.z[t] = result;
            }
        }
        return result;
    }
}
