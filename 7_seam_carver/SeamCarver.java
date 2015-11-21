/******************************************************************************
 *  Compilation:  javac SeamCarver.java
 *  Execution:    java SeamCarver (expects file test_data/6x5.png to exist for
                                   validating unit tests)
 *  Dependencies: 
 *  For use on Coursera, Algorithms Part II programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/seamCarving.html
 *  Written by Ramin Halviatti
 ******************************************************************************/

/**
 *  The <tt>SeamCarver</tt> class represents a data type for ...
 *  <em>s</em>
 *  ...
 *  <p>
 *  This implementation uses ...
 *  The constructor takes time proportional to <em>V</em> ... <em>E</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  It uses extra space proportional to <em>V</em>.
 *  <p>
 *  For additional documentation, 
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of 
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Ramin Halviatti
 */
import java.awt.Color;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
    private int[][] pixelArray; // Internal representation of a picture 
                                // where Alpha, Red, Green and Blue color
                                // values are packed in an integer for each pixel
    private int H;              // Height of picture
    private int W;              // Width of picture
    private double[][] energyArray; // Calculated energy values for each pixel in pixelArray

    /**
     * create a seam carver object based on the given picture.
     * @param picture the picture
     */
    public SeamCarver(Picture picture) {
        // Set width and height
        W = picture.width();
        H = picture.height();
        // Create a pixel array based on the picture ARGB values
        pixelArray = new int[W][H];
        for (int col = 0; col < W; col++)
            for (int row = 0; row < H; row++) 
                pixelArray[col][row] = picture.get(col, row).getRGB();
        // Calculate energy values for each pixel
        energyArray = new double[W][H];
        for (int col = 0; col < W; col++)
            for (int row = 0; row < H; row++) 
                energyArray[col][row] = calculateEnergy(col, row);
    }

     /**
     * Return a Picture instance given the internal 2d array of colors
     */
    public Picture picture() {
        Picture picture = new Picture(W, H);
        for (int col = 0; col < W; col++)
            for (int row = 0; row < H; row++) {
                Color color = new Color(pixelArray[col][row], true);
                picture.set(col, row, color);
            }
        return picture;
    }
     /**
     * Return width of internal picture.
     */
    public     int width() {
        return W;
    }

     /**
     * Return height of internal picture.
     */
    public     int height() {
        return H;
    }

     /**
     * Return energy of pixel at column x and row y
     * @param x is the column
     * @param y is the row
     */
    public  double energy(int x, int y)  {
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IndexOutOfBoundsException(
                "x or y is outside its prescribed range.");

        return energyArray[x][y];
    }

     /**
     * Calculate energy for a passed in (x,y)
     * @param int x is the column
     * @param int y is the row
     */
    private double calculateEnergy(int x, int y) {
        if (x == 0 || y == 0 || x == (width()-1) || y == (height()-1)) {
            // Border pixels get energy of 1000 by default
            return 1000.0;
        } 
        // Energy is square root of gradiant squared for x and y
        return Math.sqrt(
            gradiantSquare(x, y, true) + gradiantSquare(x, y, false));        
    }

     /**
     * Helper function that calculates gradient square values for a passed in (x,y)
     * @param int x is the column
     * @param int y is the row
     * @param boolean forX denotes if the calcution is for x (or y if false)
     */
    private int gradiantSquare(int x, int y, boolean forX) {
        int pixelBefore;
        int pixelAfter;
        if (forX) {
            assert( x > 0 && x < width() );
            pixelBefore = pixelArray[x - 1][y];
            pixelAfter  = pixelArray[x + 1][y];
        }
        else {
            assert( y > 0 && y < height() );
            pixelBefore = pixelArray[x][y - 1];
            pixelAfter  = pixelArray[x][y + 1];
        }
        int redDiff = ((pixelAfter  & 0x00FF0000) >> 16) - 
                      ((pixelBefore & 0x00FF0000) >> 16);
        int greenDiff = ((pixelAfter  & 0x0000FF00) >> 8) -
                        ((pixelBefore & 0x0000FF00) >> 8);
        int blueDiff = (pixelAfter & 0x000000FF) - 
                       (pixelBefore & 0x000000FF);
        return (redDiff*redDiff + greenDiff*greenDiff + blueDiff*blueDiff);
    }

/*
    // sequence of indices for horizontal seam
    public   int[] findHorizontalSeam() {

    }
*/
    // sequence of indices for vertical seam
    public   int[] findVerticalSeam() {
        int[] seam = new int[height()];
        // Find minimum
        double min = 1001.0;
        int minCol = -1;
        for (int col = 1; col < width() - 1; col++) {
            if (energyArray[col][1] < min) {
                min = energyArray[col][1];
                minCol = col;
            }
        }
        StdOut.printf("Min is 1000.0, index: (%d, 0)\n", minCol);
        StdOut.printf("Min is %f, index: (%d, 1)\n", min, minCol);
        seam[0] = seam[1] = minCol;

        for (int row = 2; row < height() - 1; row++) { 
            min = 1001.0;
            int minIndex = -1;
            for (int i = 1; i >= -1; i--) {
                if (energyArray[minCol + i][row] < min) {
                    minIndex = minCol + i;
                    min = energyArray[minIndex][row];
                }
            }
            minCol = minIndex;
            StdOut.printf("Min is %f, index: (%d, %d)\n", min, minCol, row);
            seam[row] = minCol;
        }
        StdOut.printf("Min is 1000.0, index: (%d, %d)\n", minCol, height()-1);
        seam[height()-1] = minCol;
        return seam;
    }   

    // remove horizontal seam from current picture
    public    void removeHorizontalSeam(int[] seam) {

    }  

    // remove vertical seam from current picture
    public    void removeVerticalSeam(int[] seam) {

    } 

    private static void validateEnergy(SeamCarver sc) {
        // Expected energy results for test_data/6x5.png
        double[][] energyData = {
            {1000.00, 1000.00, 1000.00, 1000.00, 1000.00, 1000.00}, 
            {1000.00, 237.35, 151.02, 234.09, 107.89, 1000.00}, 
            {1000.00, 138.69, 228.10, 133.07, 211.51, 1000.00}, 
            {1000.00, 153.88, 174.01, 284.01, 194.50, 1000.00}, 
            {1000.00, 1000.00, 1000.00, 1000.00, 1000.00, 1000.00},
        };

        StdOut.printf(
            "Validating pixel energy calculated for test_data/6x5.png.\n");        
        for (int j = 0; j < sc.height(); j++) {
            for (int i = 0; i < sc.width(); i++) {
                // Round off energy values to 2 decimal points
                double energy = Math.round(sc.energy(i, j) * 100);
                energy = energy / 100;
                if (energy != energyData[j][i]) {
                    // Print out info to track down the problem
                    StdOut.printf("For pixel (%d, %d) ", i, j);
                    StdOut.printf("expected energy value %f got %f\n", 
                        energyData[j][i], energy);
                    throw new java.lang.UnsupportedOperationException(
                        "Wrong energy value.");
                }
                StdOut.printf("%9.2f ", energy);
            }
            StdOut.println();
        }
    }

    // Unit tests
    public static void main(String[] args) {
        Picture picture = new Picture("test_data/6x5.png");
        SeamCarver sc = new SeamCarver(picture);
        validateEnergy(sc);   
        
        // Save picture
        Picture newPicture = sc.picture();

        // Now use the new picture to validate energy calculation again
        sc = new SeamCarver(newPicture);
        validateEnergy(sc);   
        int[] vSeam = sc.findVerticalSeam();
        for (int i : vSeam)
            StdOut.printf("%d, ", i);
        StdOut.println();

    }
}