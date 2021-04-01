package Code;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Individual {

    // TODO:
    // If a graphnode at an edge of the image plane points in an outwards direction, it is treatedas having the value none. This means that all possible chromosome permutationsare valid.
    public final List<Gene> genotype = new ArrayList<>();
    private Pixel[][] pixels;

    public Individual(Pixel[][] pixels){
        this.pixels = pixels;
    }

    public void primMST(Pixel[][] pixels) {
        Random rand = new Random();
        int randY = rand.nextInt(pixels.length);
        int randX = rand.nextInt(pixels[0].length);

        int totalNodes = pixels.length * pixels[0].length;
        int mstWeight = 0;
        Pixel w = null;
        double minimumWeight;

        // TODO: Denne Prim-algoritmen for å finne MST fungerer garra ikke.
        List<Pixel> visitedNodes = new ArrayList<>();
        visitedNodes.add(pixels[randY][randX]);
        while (visitedNodes.size() < totalNodes) {
            minimumWeight = Integer.MAX_VALUE;
            for (Pixel currentNode : visitedNodes) {
                for (int y = 0; y < pixels.length; ++y){
                    for (int x = 0; x < pixels[y].length; ++x){
                        if (minimumWeight > Fitness.distance(currentNode.color, pixels[y][x].color) && !visitedNodes.contains(pixels[y][x])){
                            minimumWeight = Fitness.distance(currentNode.color, pixels[y][x].color);
                            w = pixels[y][x];
                        }
                    }
                }
            }
            visitedNodes.add(w);
            mstWeight += minimumWeight;
            
        }
    }

    private int pixelToGenotype(int x, int y){
        return pixels[x].length * y + x;
    }

    private Tuple<Integer, Integer> genotypeToPixel(int i){
        int x = i % pixels[0].length;
        int y = Math.floorDiv(i, pixels[0].length);
        return new Tuple<>(x,y);
    }

}
