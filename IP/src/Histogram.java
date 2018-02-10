import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Class that contains the method for histogram equalization.
 */
class Histogram {

    /**
     * Performs histogram equalization.
     * @param inputImage the image to be equalized
     * @return the histogram equalized image
     */
    public static BufferedImage equalize(BufferedImage inputImage) {
        BufferedImage outputImage = new BufferedImage(
                inputImage.getWidth(),
                inputImage.getHeight(),
//                BufferedImage.TYPE_BYTE_GRAY
                inputImage.getType()
        );

        WritableRaster inputRaster = inputImage.getRaster();
        WritableRaster outputRaster = outputImage.getRaster();
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        int numComponents = inputImage.getColorModel().getNumComponents();

        int noOfIntensities = (int) (Math.pow(2, inputImage.getColorModel().getPixelSize()));
        int[][] pdf = new int[numComponents][noOfIntensities];

        for(int i=0; i<width; ++i)
            for(int j=0; j<height; ++j) {
                int[] intensity = inputRaster.getPixel(i, j, new int[numComponents]);
                for(int k=0;k<numComponents;++k)
                    (pdf[k][intensity[k]])++;
            }

        int[][] transformation = new int[numComponents][noOfIntensities];

        float s=0;
        for(int c=0;c<numComponents;++c) {
            s = ((noOfIntensities - 1) * pdf[c][0]) / (width * height);
            transformation[c][0] = Math.round(s);
        }
        for(int c=0;c<numComponents;++c) {
            for (int k = 1; k < noOfIntensities; ++k) {
                s += ((noOfIntensities - 1) * pdf[c][k]) / (width * height);
                transformation[c][k] = Math.round(s);
            }
        }

        for(int i=0; i<width; ++i)
            for(int j=0; j<height; ++j){
                int[] pixel = new int[inputImage.getColorModel().getNumComponents()];
                for(int c=0;c<numComponents;++c)
                    pixel[c] = transformation[c][(inputRaster.getPixel(i, j, new int[numComponents]))[c]];
                outputRaster.setPixel(i, j, pixel);
            }

        outputImage.setData(outputRaster);

        Data.writeImageFile(outputImage);

        return outputImage;
    }
}