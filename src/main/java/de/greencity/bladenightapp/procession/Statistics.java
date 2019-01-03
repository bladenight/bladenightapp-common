package de.greencity.bladenightapp.procession;

public class Statistics {
    static public class Segment {
        /** Number of participants currently on this segment
         *
         */
        public int nParticipants;

        /** Speed on this segment in km/h
         * Can be equal to NaN if no statistic is available
         *
         */
        public double speed;
    };

    Statistics() {
        this.segments = new Segment[0];
    }

    public Segment[] segments;
    public double averageSpeed;
}
