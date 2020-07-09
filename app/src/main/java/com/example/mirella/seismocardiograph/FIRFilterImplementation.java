package com.example.mirella.seismocardiograph;

/**
 * Implementacja filtru FIR.
 *
 * @author Mirella
 * @version 1.0
 */
public class FIRFilterImplementation {
    /**
     * Tablica o długości równej rzędowi filtru.
     */
    private final double[] z;

    /**
     * Konstruktor klasy.
     *
     * @param order Rząd filtru.
     */
    public FIRFilterImplementation(int order)
    {
        this.z = new double[order];
    }

    /**
     * Oblicza sumę wyników mnożenia próbek sygnału przez współczynniki filtru.
     * Równanie różnicowe w postaci: y(t) = a0*x(t) + a1*x(t-1) + a2*x(t-2) + ... + an*x(t-n).
     *
     * @param input Próbka sygnału.
     * @param a     Współczynniki filtru.
     * @return      Sumę wyników mnożenia próbek sygnału przez współczynniki filtru.
     */
    public double compute(double input, double[] a) {
        // computes y(t) = a0*x(t) + a1*x(t-1) + a2*x(t-2) + ... + an*x(t-n)
        double result = 0;
        for (int t = a.length - 1; t >= 0; t--)
        {
            if (t > 0)
            {
                this.z[t] = this.z[t - 1];
            }
            else
            {
                this.z[t] = input;
            }
            result += a[t] * this.z[t];
        }
        return result;
    }
}

