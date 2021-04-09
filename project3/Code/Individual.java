package Code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

public class Individual {

    private final List<Gene> genotype;
    private Pixel[][] pixels;
    private int noOfSegments;
    private List<Segment> segments = new ArrayList<>();
    private int rank;
    private int prevMerge = Integer.MAX_VALUE;
    public final double deviation, edgeValue, connectivity;

    public Individual(Pixel[][] pixels, int noOfSegments){
        this.noOfSegments = noOfSegments;
        this.pixels = pixels;
        this.genotype = new ArrayList<>();
        primMST();
        createSegments();
        this.deviation = Fitness.overallDeviation(this);
        this.edgeValue = Fitness.overallEdgeValue(this);
        this.connectivity = Fitness.overallConnectivity(this);
    }

    public Individual(List<Gene> genotype, Pixel[][] pixels){
        this.genotype = genotype;
        this.pixels = pixels;
        createSegments();
        this.deviation = Fitness.overallDeviation(this);
        this.edgeValue = Fitness.overallEdgeValue(this);
        this.connectivity = Fitness.overallConnectivity(this);
    }

    public void setRank(int rank){
        this.rank = rank;
    }

    public int getRank(){
        return this.rank;
    }

    public List<Gene> getGenotype(){
        return new ArrayList<>(this.genotype);
    }

    public Gene getGenotypeFromPixel(Pixel p){
        return this.genotype.get(this.pixelToGenotype(p.x, p.y));
    }

    public void primMST() {
        Random rand = new Random();
        int randX = rand.nextInt(this.pixels[0].length);
        int randY = rand.nextInt(this.pixels.length);
        int totalNodes = this.pixels.length * this.pixels[0].length;

        // Initialize genotype to only point at itself
        for (int i=0; i<totalNodes ; i++) {
            this.genotype.add(Gene.NONE);
        }
        //Initialize priorityqueue of Edges and list of visitedNodes
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>();
        List<Edge> createdEdges = new ArrayList<>();
        Set<Pixel> visitedNodes = new HashSet<>();
        // Initial choice of pixel is randomized
        Pixel current = this.pixels[randY][randX];
        // Make sure that all nodes are visited once
        while (visitedNodes.size() < totalNodes) {
            if (!visitedNodes.contains(current)){
                // Add to priorityqueue if not already there
                visitedNodes.add(current);
                priorityQueue.addAll(this.createEdges(current));
            }
            // Get current best edge (measured in RGB-distance between pixels)
            Edge e = priorityQueue.poll();
            if (!visitedNodes.contains(e.to)){
                // Set genotype to the corresponding gene to get from pixel to pixel
                updateGenotype(e);
                createdEdges.add(e);
            }
            current = e.to;
        }
        Collections.sort(createdEdges);
        Collections.reverse(createdEdges);
        // Remove the worst edges, as to create noOfSegments initial segments
        for (int i=0; i<this.noOfSegments - 1; i++){
            Edge removedEdge = createdEdges.get(Utils.randomInt(createdEdges.size()));
            updateGenotype(removedEdge.from, removedEdge.from);
        }
    }

    public void mergeSmallSegments(int tries){
        List<Segment> mergeableSegments = new ArrayList<>();
        // Find segments with fewer pixels than threshold
        for (Segment s: this.segments){
            if (s.getPixels().size() < Parameters.minimumSegmentSize){
                mergeableSegments.add(s);
            }
        }
        // If no merge was made previous run, increment tries counter
        if (mergeableSegments.size() == this.prevMerge) tries++;
        // If no merge is needed or tries exceeded; exit condition
        if (mergeableSegments.size() == 0 || tries > Parameters.mergeTries) return;
        // Find the best edge from each segment to merge
        for (Segment s: mergeableSegments){
            Edge merge = getBestSegmentEdge(s);
            if (merge != null){
                updateGenotype(merge.to, merge.from);
            }
        }
        System.out.println(mergeableSegments.size());
        // Update prevMerge and create segments based on new genotype
        this.prevMerge = mergeableSegments.size();
        this.createSegments();
        // Recursively run until exit condition is reached
        mergeSmallSegments(tries);
    }

    private Edge getBestSegmentEdge(Segment segment){
        Edge bestEdge = null;
        double bestDistance = Integer.MAX_VALUE;
        // Iterate through pixels in segment, find neighbours
        for (Pixel p: segment.getPixels()){
            for (Pixel n: p.getCardinalNeighbours().values()){
                // Assign neighbours who are not in the same segment to a new Edge candidate
                if (!segment.contains(n)){
                    Edge temp = new Edge(p, n);
                    if (temp.distance < bestDistance){
                        // Update bestEdge to keep the edge with the lowest distance in RGB-space
                        bestDistance = temp.distance;
                        bestEdge = temp;
                    }
                }
            }
        }
        return bestEdge;
    }

    private void createSegments(){
        Pixel current;
        int currentIndex;
        boolean[] visitedNodes = new boolean[genotype.size()];
        Arrays.fill(visitedNodes, false);
        Set<Pixel> segment;
        for (int i=0; i<genotype.size();i++){
            // If already visited, skip
            if (visitedNodes[i]){
                continue;
            }
            // Select pixel at index, add to segment and visitedNodes
            segment = new HashSet<>();
            Tuple<Integer, Integer> pixelIndex = genotypeToPixel(i);
            current = this.pixels[pixelIndex.y][pixelIndex.x];
            segment.add(current);
            visitedNodes[i] = true;
            // Move on to neighbour as defined by genotype
            current = current.getCardinalNeighbour(genotype.get(i));
            currentIndex = pixelToGenotype(current.x, current.y);
            // While the neighbour has not been visited previously, keep moving
            while (!visitedNodes[currentIndex]){
                segment.add(current);
                visitedNodes[currentIndex] = true;
                current = current.getCardinalNeighbour(genotype.get(currentIndex));
                currentIndex = pixelToGenotype(current.x, current.y);
            }
            // If the node that is last pointed to is contained in another segment, merge
            boolean flag = false;
            for (Segment s: this.segments){
                if (s.contains(current)){
                    s.addPixels(segment);
                    flag = true;
                    break;
                }
            }
            // If flag is not set, this means we can create a new segment
            if (!flag){
                this.segments.add(new Segment(segment));
            }
        }
        // Update number of segments
        this.noOfSegments = this.segments.size();
    }

    private List<Edge> createEdges(Pixel pixel){
        // Adds all neighbours as a potential edge
        List<Edge> edges = new ArrayList<>();
        for (int i = 1; i<5; i++){
            Pixel neighbour = pixel.getCardinalNeighbour(i);
            if (neighbour != null) edges.add(new Edge(pixel, neighbour));
        }
        return edges;
    }

    private void updateGenotype(Edge e){
        updateGenotype(e.from, e.to);
    }

    private void updateGenotype(Pixel from, Pixel to){
        if (Objects.equals(from, to)){
            this.genotype.set(this.pixelToGenotype(from.x, from.y), Gene.NONE);
            return;
        }
        // Sets the gene at e.to to point towards e.from as an MST can only have one parent but multiple children
        this.genotype.set(this.pixelToGenotype(to.x, to.y), 
                          Utils.mapNeighbourToGene.get(new Tuple<>(from.x-to.x, from.y-to.y)));
    }

    private int pixelToGenotype(int x, int y){
        return pixels[y].length * y + x;
    }

    private Tuple<Integer, Integer> genotypeToPixel(int i){
        int x = i % pixels[0].length;
        int y = Math.floorDiv(i, pixels[0].length);
        return new Tuple<>(x,y);
    }

    public int getNoOfSegments(){
        return this.noOfSegments;
    }

    public List<Segment> getSegments(){
        return this.segments;
    }

    public boolean isEdge(Pixel pixel){
        Segment pixelSegment = this.segments.get(0);
        for (Segment s: this.segments){
            if (s.contains(pixel)){
                pixelSegment = s;
                break;
            }
        }
        // If both neighbours are in the same segment as the pixel, it is not an edge
        return !pixelSegment.contains(pixel.getCardinalNeighbour(Gene.LEFT)) || !pixelSegment.contains(pixel.getCardinalNeighbour(Gene.DOWN));
    }

}
