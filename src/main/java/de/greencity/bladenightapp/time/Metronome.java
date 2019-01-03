package de.greencity.bladenightapp.time;

public class Metronome {

    public Metronome(long period) {
        this.period = period;
        reset();
    }

    public void reset() {
        this.start = System.currentTimeMillis();
        this.iteration = 1;
    }

    public void waitNext() throws InterruptedException {
        long next = start + iteration * period;
        iteration++;
        Sleep.sleep(next - now());
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private long period;
    private long start;
    private long iteration;
}
