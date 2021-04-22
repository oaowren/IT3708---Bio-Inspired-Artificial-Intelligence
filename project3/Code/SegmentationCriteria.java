package Code;

import java.util.Comparator;

public enum SegmentationCriteria {
    EdgeValue,
    Connectivity,
    Deviation;

    public static Comparator<Individual> getIndividualComparator(SegmentationCriteria segmentationCriteria) {
        return switch(segmentationCriteria) {
            case EdgeValue    -> (a, b) -> Double.compare(b.edgeValue, a.edgeValue);
            case Connectivity -> (a, b) -> Double.compare(b.connectivity, a.connectivity);
            case Deviation    -> (a, b) -> Double.compare(b.deviation, a.deviation);
        };
    }
}
