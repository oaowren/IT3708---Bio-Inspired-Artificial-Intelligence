package Code;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Random;

public class Individual {

    // TODO:
    // If a graphnode at an edge of the image plane points in an outwards direction, it is treatedas having the value none. This means that all possible chromosome permutationsare valid.
    public final List<Gene> genotype;
    private Pixel[][] pixels;
    private int noOfSegments;

    public Individual(Pixel[][] pixels, int noOfSegments){
        this.noOfSegments = noOfSegments;
        this.pixels = pixels;
        this.genotype = new ArrayList<>();
        primMST();
    }

    public void primMST() {
        Random rand = new Random();
        int randX = rand.nextInt(this.pixels[0].length);
        int randY = rand.nextInt(this.pixels.length);
        int totalNodes = this.pixels.length * this.pixels[0].length;

        for (int i=0; i<totalNodes ; i++) {
            this.genotype.add(Gene.NONE);
        }

        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>();
        List<Edge> createdEdges = new ArrayList<>();
        List<Pixel> visitedNodes = new ArrayList<>();

        Pixel current = this.pixels[randY][randX];

        while (visitedNodes.size() < totalNodes) {
            if (!visitedNodes.contains(current)){
                visitedNodes.add(current);
                priorityQueue.addAll(this.createEdges(current));
            }
            Edge e = priorityQueue.poll();
            if (!visitedNodes.contains(e.to)){
                updateGenotype(e);
                createdEdges.add(e);
            }
            current = e.to;
        }
        Collections.sort(createdEdges);
        Collections.reverse(createdEdges);

        for (int i=0; i<this.noOfSegments; i++){
            Edge removedEdge = createdEdges.remove(i);
            updateGenoType(removedEdge.from, removedEdge.from);
        }
    }

    private List<Edge> createEdges(Pixel pixel){
        // Adds all neighbours as a potential edge
        List<Edge> edges = new ArrayList<>();
        for (int i=0; i<5; i++){
            Pixel neighbour = pixel.getNeighbours().get(i);
            if (neighbour != null) edges.add(new Edge(pixel, neighbour));
        }
        return edges;
    }

    private void updateGenotype(Edge e){
        updateGenoType(e.from, e.to);
    }

    private void updateGenoType(Pixel from, Pixel to){
        if (Objects.equals(from, to)){
            this.genotype.set(this.pixelToGenotype(from.x, from.y), Gene.NONE);
            return;
        }
        // Sets the gene at e.from to point towards e.to
        Map<Integer, Pixel> neighbours = from.getNeighbours();
        for (Integer key : neighbours.keySet()){
            if (Objects.equals(neighbours.get(key), to)){
                this.genotype.set(this.pixelToGenotype(from.x, from.y), Gene.values()[key-1]);
            }
        }
    }

    private int pixelToGenotype(int x, int y){
        return pixels[y].length * y + x;
    }

    private Tuple<Integer, Integer> genotypeToPixel(int i){
        int x = i % pixels[0].length;
        int y = Math.floorDiv(i, pixels[0].length);
        return new Tuple<>(x,y);
    }

}
