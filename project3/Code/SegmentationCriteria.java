package Code;

import java.util.Comparator;

public enum SegmentationCriteria {
    EdgeValue,
    Connectivity,
    Deviation;

    public static Comparator<Individual> getIndividualComparator(SegmentationCriteria segmentationCriteria) {
        return switch(segmentationCriteria) {
            case EdgeValue    -> (a, b) -> Double.compare(a.getEdgeValue(), b.getEdgeValue());
            case Connectivity -> (a, b) -> Double.compare(a.getConnectivity(), b.getConnectivity());
            case Deviation    -> (a, b) -> Double.compare(a.getDeviation(), b.getDeviation());
        };
    }

    public static double measure(SegmentationCriteria segmentationCriteria, Segment segment) {
        return switch(segmentationCriteria) {
            case EdgeValue    -> Fitness.edgeValue(segment);
            case Connectivity -> Fitness.connectivity(segment);
            case Deviation    -> Fitness.deviation(segment);
        };
    }
}
