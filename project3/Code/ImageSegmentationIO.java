package Code;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ImageSegmentationIO {
    
    private int imageWidth;
    private int imageHeight;
    private Pixel[][] pixels;

    public ImageSegmentationIO(String fileName) {
        try (InputStream input = new FileInputStream(new File("training_images/" + fileName+ "/Test image.jpg"))) {
            BufferedImage image = ImageIO.read(input);
            this.imageWidth = image.getWidth();
            this.imageHeight = image.getHeight();

            this.pixels = new Pixel[image.getHeight()][image.getWidth()];

            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    final Color color = new Color(image.getRGB(x, y));
                    final Pixel pixel = new Pixel(
                        new RGB(
                            (color.getRed() << 16), 
                            (color.getGreen() << 8), 
                            color.getBlue()
                        ), x, y
                    );
                    pixels[x][y] = pixel;
                }
            }

            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    pixels[x][y].setNeighbours(getPixelNeighbours(x, y));
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
        return Map.of(
            1, x+1 >= imageWidth ? null : pixels[x+1][y],
            2, x-1 < 0 ? null : pixels[x-1][y],
            3, y-1 < 0 ? null : pixels[x][y-1],
            4, y+1 >= imageHeight ? null : pixels[x][y+1],
            5, y-1 < imageHeight || x+1 >= imageWidth ? null : pixels[x+1][y-1],
            6, y+1 >= imageHeight || x+1 >= imageWidth ? null : pixels[x+1][y+1],
            7, y-1 < imageHeight || x-1 < imageWidth ? null : pixels[x-1][y-1],
            8, y+1 >= imageHeight || x-1 < imageWidth ? null : pixels[x-1][y+1]
        );
    }

    public void save(String path, String color){
        if (color !="b" && color != "g"){
            throw new IllegalArgumentException("Color must be either 'b'(black) or 'g'(green).");
        }
        int segmentColor = color == "b" ? RGB.black.toRgbInt() : RGB.green.toRgbInt();
        String fileSuffix = color == "b" ? "black" : "green";
        try {
            File output = new File("solution_images/" + path + fileSuffix + ".jpg");
            BufferedImage image = new BufferedImage(this.getImageWidth(), this.getImageHeight(), BufferedImage.TYPE_INT_RGB);
            //TODO: Draw the solution

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
}
