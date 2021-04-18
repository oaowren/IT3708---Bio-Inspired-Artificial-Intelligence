package Code;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageSegmentationIO {
    
    private static int printedDebugImageCount = 0;
    private int imageWidth;
    private int imageHeight;
    private Pixel[][] pixels;

    public ImageSegmentationIO(String fileName) {
        try (InputStream input = new FileInputStream(new File("project3/training_images/" + fileName+ "/Test image.jpg"))) {
            BufferedImage image = ImageIO.read(input);
            this.imageWidth = image.getWidth();
            this.imageHeight = image.getHeight();

            this.pixels = new Pixel[image.getHeight()][image.getWidth()];

            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    final Color color = new Color(image.getRGB(x, y));
                    final Pixel pixel = new Pixel(
                        new RGB(
                            (color.getRed()), 
                            (color.getGreen()), 
                            color.getBlue()
                        ), x, y
                    );
                    pixels[y][x] = pixel;
                }
            }

            // NOTE: pixels are indexed by x = width and y = height, inverse of typical 2D-arrays
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    pixels[y][x].setNeighbours(getPixelNeighbours(x, y));
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public int getImageWidth() {
        return this.imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight(){
        return this.imageHeight;
    }

    public void setImageHeight(int imageHeight){
        this.imageHeight = imageHeight;
    }

    public Pixel[][] getPixels() {
        return this.pixels;
    }

    public void setPixels(Pixel[][] pixels) {
        this.pixels = pixels;
    }
    
    private Map<Integer, Pixel> getPixelNeighbours(int x, int y) {
        Map<Integer, Pixel> neighbours = new HashMap<>();
        if (x+1 < imageWidth)                      neighbours.put(1, pixels[y][x+1]);
        if (x-1 >= 0)                              neighbours.put(2, pixels[y][x-1]);
        if (y-1 >= 0)                              neighbours.put(3, pixels[y-1][x]);
        if (y+1 < imageHeight)                     neighbours.put(4, pixels[y+1][x]);
        if (y-1 >= 0 && x+1 < imageWidth)          neighbours.put(5, pixels[y-1][x+1]);
        if (y+1 < imageHeight && x+1 < imageWidth) neighbours.put(6, pixels[y+1][x+1]);
        if (y-1 >= 0 && x-1 >= 0)                  neighbours.put(7, pixels[y-1][x-1]);
        if (y+1 < imageHeight && x-1 >= 0)         neighbours.put(8, pixels[y+1][x-1]);
        return neighbours;
    }


    public void save(String path, Individual solution, String color) {
        save(path, solution, color, false);
    }

    public void save(String path, Individual solution, String color, boolean debug){
        if (color !="b" && color != "g"){
            throw new IllegalArgumentException("Color must be either 'b'(black) or 'g'(green).");
        }
        int segmentColor = color == "b" ? RGB.black.toRgbInt() : RGB.green.toRgbInt();
        String fileSuffix = color == "b" ? "black" : "green";
        String folder = color == "b" ? "" : "_Green";
        int numberOfSegments = solution.getNoOfSegments();
        String fullPath = "project3/Evaluator/Student_Segmentation_Files" + folder + "/" + path + "/" + fileSuffix + (debug ? printedDebugImageCount++ : numberOfSegments) + ".jpg";
        System.out.println("Writing file to " + fullPath);
        try {
            File output = new File(fullPath);
            BufferedImage image = new BufferedImage(this.getImageWidth(), this.getImageHeight(), BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < this.getImageHeight(); y++) {
                for (int x = 0; x < this.getImageWidth(); x++) {
                    if (solution.isEdge(pixels[y][x])){
                        image.setRGB(x,y, segmentColor);
                    } else {
                        image.setRGB(x, y, getBackground(pixels[y][x], color));
                    }
                }
            }

            // Edge around the image
            for (int j = 0; j < this.getImageWidth(); j++) {
                image.setRGB(j, 0, segmentColor);
                image.setRGB(j, this.getImageHeight() - 1, segmentColor);
            }
            for (int i = 0; i < this.getImageHeight(); i++) {
                image.setRGB(0, i, segmentColor);
                image.setRGB(this.getImageWidth() - 1, i, segmentColor);
            }
            ImageIO.write(image, "jpg", output);
        } catch (IOException e){
            System.out.println(e);
        }
    }

    private int getBackground(Pixel pixel, String color){
        return color== "b" ? RGB.white.toRgbInt() : pixel.color.toRgbInt();
    }

    public void deletePrevious(Path path){
        for (File file : path.toFile().listFiles())
            if (!file.isDirectory())
                file.delete();
    }
}
