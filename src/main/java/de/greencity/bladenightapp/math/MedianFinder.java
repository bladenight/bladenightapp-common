package de.greencity.bladenightapp.math;

import java.util.Collections;
import java.util.Vector;

public class MedianFinder {

    public static class WeightedValue implements Comparable<WeightedValue> {
        public WeightedValue(double value, double weight) {
            this.value = value;
            this.weight = weight;
        }

        @Override
        public int compareTo(WeightedValue o) {
            if ( o.value > value )
                return -1;
            if ( o.value < value )
                return 1;
            return 0;
        }
        public double value;
        public double weight;
    }

    public MedianFinder() {
        this.weightedValues = new Vector<WeightedValue>();
    }

    public void addValue(double value) {
        weightedValues.add(new WeightedValue(value, 1.0));
    }

    public void addWeightedValue(WeightedValue weightedValue) {
        weightedValues.add(new WeightedValue(weightedValue.value, weightedValue.weight));
    }

    public void addWeightedValue(double value, double weight) {
        if ( weight <= 0.0 )
            throw new IllegalArgumentException("Invalid weight : " + weight);
        weightedValues.add(new WeightedValue(value, weight));
    }

    public double findMedian() {
        return findMedian(0.5);
    }

    public double findMedian(double quantil) {
        double weightCumul = 0;
        double weightTotal = getTotalWeight();
        double weightTarget = weightTotal * quantil;

        if ( weightedValues.size() == 0.0 )
            throw new IllegalStateException("No data has been provided");

        Collections.sort(weightedValues);

        for ( int i = 0 ; i < weightedValues.size() ; i ++) {
            weightCumul += weightedValues.get(i).weight;
            if ( weightCumul > weightTarget ) {
                double sumValue = 0;
                double sumWeights = 0;
                for (int incr = -1 ; incr <= +1 ; incr++) {
                    int neighbor = i + incr;
                    if ( neighbor >= 0 && neighbor < weightedValues.size() ) {
                        sumValue += weightedValues.get(neighbor).value * weightedValues.get(neighbor).weight;
                        sumWeights += weightedValues.get(neighbor).weight;
                    }
                }
                return sumValue / sumWeights;
            }
        }

        return weightedValues.get(weightedValues.size() - 1 ).value;
    }

    public int sampleCount() {
        return weightedValues.size();
    }

    public double getTotalWeight() {
        double sum = 0.0;
        for ( WeightedValue v : weightedValues) {
            sum += v.weight;
        }
        return sum;
    }

    private Vector<WeightedValue> weightedValues;

}
