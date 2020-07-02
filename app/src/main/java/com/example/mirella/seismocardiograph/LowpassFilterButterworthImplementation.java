package com.example.mirella.seismocardiograph;

public class LowpassFilterButterworthImplementation
{
    private final LowpassFilterButterworthSection[] section;

    public LowpassFilterButterworthImplementation
            (double cutoffFrequencyHz, int numSections, double Fs)
    {
        this.section = new LowpassFilterButterworthSection[numSections];
        for (int i = 0; i < numSections; i++)
        {
            this.section[i] = new LowpassFilterButterworthSection
                    (cutoffFrequencyHz, i + 1, numSections * 2, Fs);
        }
    }
    public double compute(double input)
    {
        double output = input;
        for (LowpassFilterButterworthSection lowpassFilterButterworthSection : this.section) {
            output = lowpassFilterButterworthSection.compute(output);
        }
        return output;
    }
}
