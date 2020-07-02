package com.example.mirella.seismocardiograph;

public class HighpassFilterButterworthImplementation
{
    private final HighpassFilterButterworthSection[] section;

    public HighpassFilterButterworthImplementation
            (double cutoffFrequencyHz, int numSections, double Fs)
    {
        this.section = new HighpassFilterButterworthSection[numSections];
        for (int i = 0; i < numSections; i++)
        {
            this.section[i] = new HighpassFilterButterworthSection
                    (cutoffFrequencyHz, i + 1, numSections * 2, Fs);
        }
    }
    public double compute(double input)
    {
        double output = input;
        for (HighpassFilterButterworthSection highpassFilterButterworthSection : this.section) {
            output = highpassFilterButterworthSection.compute(output);
        }
        return output;
    }
}
