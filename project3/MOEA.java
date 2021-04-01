import Code.ImageSegmentationIO;
import Code.Parameters;

class MOEA {
    public static void main(String[] args) {
        ImageSegmentationIO imageIO = new ImageSegmentationIO(Parameters.filename);
        imageIO.save(Parameters.filename, Parameters.segmentColor);
    }
}
