package Code;

import java.util.Objects;
import java.awt.Color;

public class RGB {

    public final int r, g, b;
    
    public RGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static RGB green = new RGB((0), (255), 0);
    public static RGB black = new RGB((0), (0), 0);
    public static RGB white = new RGB((255), (255), 255);

    public int toRgbInt(){
        return new Color(this.r, this.g, this.b).getRGB();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RGB)) {
            return false;
        }
        RGB rGB = (RGB) o;
        return r == rGB.r && g == rGB.g && b == rGB.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}
