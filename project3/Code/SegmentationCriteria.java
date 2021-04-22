package Code;

import java.util.Comparator;

public enum SegmentationCriteria {
    EdgeValue,
    Connectivity,
    Deviation;

    public static Comparator<Individual> getIndividualComparator(SegmentationCriteria segmentationCriteria) {
        return switch(segmentationCriteria) {
            case EdgeValue    -> (a, b) -> Double.compare(a.edgeValue, b.edgeValue);
            case Connectivity -> (a, b) -> Double.compare(a.connectivity, b.connectivity);
            case Deviation    -> (a, b) -> Double.compare(a.deviation, b.deviation);
        };
    }
}
