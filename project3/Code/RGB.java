package Code;

import java.util.Objects;

public class RGB {

    public final int r, g, b;
    
    public RGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
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
