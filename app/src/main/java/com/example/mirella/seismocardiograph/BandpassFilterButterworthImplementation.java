package com.example.mirella.seismocardiograph;

class BandpassFilterButterworthImplementation
{
    private final LowpassFilterButterworthImplementation lowpassFilter;
    private final HighpassFilterButterworthImplementation highpassFilter;

    public BandpassFilterButterworthImplementation
            (double bottomFrequencyHz, double topFrequencyHz, int numSections, double Fs)
    {
        this.lowpassFilter = new LowpassFilterButterworthImplementation
                (topFrequencyHz, numSections, Fs);
        this.highpassFilter = new HighpassFilterButterworthImplementation
                (bottomFrequencyHz, numSections, Fs);
    }

    public void compute(double input)
    {
        // compute the result as the cascade of the highpass and lowpass filters
        this.highpassFilter.compute(this.lowpassFilter.compute(input));
    }
}
