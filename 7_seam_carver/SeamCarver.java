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
    private Picture picture;
    private int H;
    private int W;

    /**
     * create a seam carver object based on the given picture.
     * @param picture the picture
     */
    public SeamCarver(Picture picture) {
        this.picture = picture;
        W = this.picture.width();
        H = this.picture.height();
    }

     /**
     * Return a Picture instance given the internal 2d array of colors
     */
    public Picture picture() {
        return this.picture;
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
        Color pixelBefore;
        Color pixelAfter;
        if (forX) {
            assert( x > 0 && x < width() );
            pixelBefore = picture.get(x - 1, y);
            pixelAfter  = picture.get(x + 1, y);
        }
        else {
            assert( y > 0 && y < height() );
            pixelBefore = picture.get(x, y - 1);
            pixelAfter  = picture.get(x, y + 1);
        }
        int redDiff = pixelAfter.getRed() - pixelBefore.getRed();
        int greenDiff = pixelAfter.getGreen() - pixelBefore.getGreen();
        int blueDiff = pixelAfter.getBlue() - pixelBefore.getBlue();
        return (redDiff*redDiff + greenDiff*greenDiff + blueDiff*blueDiff);
    }
/*
    // sequence of indices for horizontal seam
    public   int[] findHorizontalSeam() {

    }

    // sequence of indices for vertical seam
    public   int[] findVerticalSeam() {

    }   
*/
    // remove horizontal seam from current picture
    public    void removeHorizontalSeam(int[] seam) {

    }  

    // remove vertical seam from current picture
    public    void removeVerticalSeam(int[] seam) {

    } 

    // Unit tests
    public static void main(String[] args) {
    double[][] energyData = {
        {1000.00, 1000.00, 1000.00, 1000.00, 1000.00, 1000.00}, 
        {1000.00, 237.35, 151.02, 234.09, 107.89, 1000.00}, 
        {1000.00, 138.69, 228.10, 133.07, 211.51, 1000.00}, 
        {1000.00, 153.88, 174.01, 284.01, 194.50, 1000.00}, 
        {1000.00, 1000.00, 1000.00, 1000.00, 1000.00, 1000.00},
        }; 
        Picture picture = new Picture("test_data/6x5.png");
        SeamCarver sc = new SeamCarver(picture);
        
        StdOut.printf("Validating energy calculated for each pixel.\n");        
        for (int j = 0; j < sc.height(); j++) {
            for (int i = 0; i < sc.width(); i++) {
                // Round off energy values to 2 decimal points
                double energy = Math.round(sc.energy(i, j) * 100);
                energy = energy / 100;
                if (energy != energyData[j][i]) {
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
}