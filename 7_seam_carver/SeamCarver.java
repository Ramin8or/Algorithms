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
    private static final double MAX_ENERGY  = 1000.00;
    private static final boolean HORIZONTAL = true;
    private static final boolean VERTICAL   = false;
    private int[][] pixels; // Alpha and RGB values packed in integers
    private int H;          // Height of picture
    private int W;          // Width of picture

    /**
     * create a seam carver object based on the given picture.
     * @param picture the picture
     */
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new NullPointerException(
                "Invalid null argument was passed.");

        W = picture.width();
        H = picture.height();
        // Create a pixel array based on the picture ARGB values
        pixels = new int[W][H];
        for (int col = 0; col < W; col++)
            for (int row = 0; row < H; row++) 
                pixels[col][row] = picture.get(col, row).getRGB();
    }

     /**
     * Return a Picture instance given the internal 2d array of colors
     */
    public Picture picture() {
        Picture picture = new Picture(W, H);
        for (int col = 0; col < W; col++)
            for (int row = 0; row < H; row++) {
                Color color = new Color(pixels[col][row], true);
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
        // Validate params
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IndexOutOfBoundsException(
                "x or y is outside its prescribed range.");

        // Border pixels get energy of MAX_ENERGY by default
        if (x == 0 || y == 0 || x == (width()-1) || y == (height()-1)) 
            return MAX_ENERGY;

        // Energy is square root of gradiant squared for x and y
        return Math.sqrt(
            gradiantSquare(x, y, HORIZONTAL) + gradiantSquare(x, y, VERTICAL));        
    }

     /**
     * Return the sequence of indices for horizontal seam
     */
    public int[] findHorizontalSeam() {
        double[][] energyTable = createEnergyTable(H, W, true);
        return findSeam(energyTable);
    }

     /**
     * Return the sequence of indices for vertical seam
     */
    public int[] findVerticalSeam() {
        double[][] energyTable = createEnergyTable(W, H, false);
        return findSeam(energyTable);
    }

     /**
     * Helper function that finds a seam depending on the passed in energyArray
     */
    private int[] findSeam(double[][] energyArray) {
        int width  = energyArray.length;
        int height = energyArray[0].length;
        double[][] distTo = new double[width][height];
        int[] seam = new int[height];
        int[][] edgeTo = new int[width][height];

        // Initialize distTo
        for (int x = 0; x < width; x++) {           
            for (int y = 0; y < height; y++) { 
                if (y == 0) 
                    distTo[x][y] = MAX_ENERGY;
                else 
                    distTo[x][y] = Double.POSITIVE_INFINITY;
            }        
        }
                
        // Populate distTo while relaxing edges
        // TODO describe the algorithm here
        for (int y = 0; y < height - 1; y++) 
            for (int x = 0; x < width; x++) 
                for (int k = x-1; k <= x+1; k++) 
                    if (k >= 0 && k < width &&
                        (distTo[k][y+1] > distTo[x][y] + energyArray[k][y+1])){
                            // relax edge
                            distTo[k][y+1] = distTo[x][y] + energyArray[k][y+1];
                            edgeTo[k][y+1] = y*energyArray.length + x;
                    }

        // find minimum in last row
        double lastMin = Double.POSITIVE_INFINITY;
        int lastIndex = -1;
        for (int x = 0; x < width; x++) {
            if (lastMin > distTo[x][height-1]) {
                lastMin = distTo[x][height-1];
                lastIndex = x;
            }        
        }
        seam[height-1] = lastIndex;
        
        // reconstruct the path from bottom up
        for (int y = height-1; y > 0; y--)
            seam[y-1] = edgeTo[seam[y]][y] % width;        
        return seam;
    }

     /**
     * Remove horizontal seam from current picture
     * @param seam is the array of indices to be removed
     */
    public    void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new NullPointerException(
                "Invalid null argument was passed.");
        
        if (seam.length != W || H <= 1) 
            throw new IllegalArgumentException(
                "Invalid seam or picture size.");        

        validateSeam(seam, HORIZONTAL);
        
        int[][] pixelsCopy = new int[W][H-1];
        
        for (int col = 0; col < W; col++) {
            System.arraycopy(pixels[col], 0, 
                             pixelsCopy[col], 0, 
                             seam[col]);
            System.arraycopy(pixels[col], seam[col]+1, 
                             pixelsCopy[col], seam[col], 
                             H-seam[col]-1);
        }
        --H;
        pixels = pixelsCopy;
    }  

     /**
     * Remove vertical seam from current picture
     * @param seam is the array of indices to be removed
     */
    public    void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new NullPointerException(
                "Invalid null argument was passed.");

        if (seam.length != H || W <= 1) 
            throw new IllegalArgumentException(
                "Invalid seam or picture size.");  

        validateSeam(seam, VERTICAL);

        int[][] pixelsCopy = new int[W-1][H];

        for (int x = 0; x < W; x++)
            for (int y = 0; y < H; y++) {
                if (x < seam[y]) 
                    pixelsCopy[x][y] = pixels[x][y];
                else if (x > seam[y]) 
                    pixelsCopy[x-1][y] = pixels[x][y];
            }

        --W;
        pixels = pixelsCopy;      

    } 

    /**
     * Helper function that calculates gradient square values for a passed in (x,y)
     * @param int x is the column
     * @param int y is the row
     * @param boolean forX denotes if the calcution is for x (or y if false)
     */
    private int gradiantSquare(int x, int y, boolean horizontal) {
        int pixelBefore;
        int pixelAfter;
        if (horizontal) {
            assert (x > 0 && x < width());
            pixelBefore = pixels[x - 1][y];
            pixelAfter  = pixels[x + 1][y];
        }
        else {
            assert (y > 0 && y < height());
            pixelBefore = pixels[x][y - 1];
            pixelAfter  = pixels[x][y + 1];
        }
        int redDiff = Math.abs(((pixelAfter  & 0x00FF0000) >> 16) - 
                               ((pixelBefore & 0x00FF0000) >> 16));
        int greenDiff = Math.abs(((pixelAfter  & 0x0000FF00) >> 8) -
                                 ((pixelBefore & 0x0000FF00) >> 8));
        int blueDiff = Math.abs((pixelAfter & 0x000000FF) - 
                                (pixelBefore & 0x000000FF));
        return (redDiff*redDiff + greenDiff*greenDiff + blueDiff*blueDiff);
    }

    private double[][] createEnergyTable(int width, int height, boolean isTranspose)
    {
        double[][] table = new double[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                if (isTranspose) 
                    table[x][y] = energy(y, x);
                else 
                    table[x][y] = energy(x, y);
            }
        return table;        
    }

    private void validateSeam(int[] seam, boolean horizontal) {
        int limit;
        if (horizontal)
            limit = H;
        else
            limit = W;

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= limit || 
                (i < seam.length - 1 && Math.abs(seam[i] - seam[i+1]) > 1))
                    throw new IllegalArgumentException(
                            "Invalid value in seam.");
        }
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