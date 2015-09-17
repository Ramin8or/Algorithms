
/*
 * Course: Princeton Algorithms Part 1
 * Project: Week 1 programming assignment: Percolation
 * Information: http://coursera.cs.princeton.edu/algs4/assignments/percolation.html 
 * compile: javac-algs4 Percolation.java PercolationVisualizer.java PercolationStats.java 
 * Test with PercolationVisualizer and data file: java-algs4  PercolationVisualizer input20.txt 
 * Test with PercolationStats: java-algs4 PercolationStats 200 10
 */
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[] siteStatus;      
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF uf4Full;
    private int sizeN;
    private int vTop;
    private int vBottom;

    public Percolation(int N) {
        // Validate argument
        if (N <= 0)
            throw new java.lang.IllegalArgumentException(
                "Percolation size cannot be a negative number.");
        // Initialize sites to zero
        sizeN = N;
        vTop = 0;
        vBottom = sizeN*sizeN + 1;
        siteStatus = new boolean[sizeN*sizeN + 2];
        for (int i = 0; i < sizeN*sizeN + 2; i++)
            siteStatus[i] = false;
        // Initialize the weighted-quick-union-find instances
        uf = new WeightedQuickUnionUF(sizeN*sizeN + 2);
        uf4Full = new WeightedQuickUnionUF(sizeN*sizeN + 2);
    }

    public void open(int i, int j) {
        // Validate and convert to site
        checkBounds(i, j);
        int site = xyTo1D(i, j);
        // Check if site is already open
        if (siteStatus[ site ])
            return;
        // Set site status to open
        siteStatus[ site ] = true;
        // Connect with neighbors as appropriate
        if (i == 1) {
            // Site is at top row, connect to top
            uf.union(site, vTop);
            uf4Full.union(site, vTop);
        } 
        else if (i == sizeN) {
            // Site is at bottom row, do not use uf4Full to prevent "backwash"
            uf.union(site, vBottom);
        } 
        else { // i > 1 or i < sizeN 
            if (isOpen(i-1, j)) {
                // Connect to neighbor above
                int siteAbove = xyTo1D(i-1, j);
                uf.union(site, siteAbove);
                uf4Full.union(site, siteAbove);
            }
            if (isOpen(i+1, j)) {
                // Connect to neighbor below 
                int siteBelow = xyTo1D(i+1, j);
                uf.union(site, siteBelow);
                uf4Full.union(site, siteBelow);
            }
        }
        if (j > 1 && isOpen(i, j-1)) {
            // Connect to neighbor to the left
            uf.union(site, site - 1);
            uf4Full.union(site, site - 1);
        }
        if (j < sizeN && isOpen(i, j+1)) {
            // Connect to neighbor to the right
            uf.union(site, site + 1);
            uf4Full.union(site, site + 1);
        }
     }

    public boolean isFull(int i, int j) {
         checkBounds(i, j);
         if (!siteStatus[ xyTo1D(i, j) ]) 
            return false;
        return uf4Full.connected(vTop, xyTo1D(i, j));
    }

    public boolean percolates() {
        return uf.connected(vTop, vBottom);
    }

    private void checkBounds(int i, int j) {
        if (i <= 0 || i > sizeN || j <= 0 || j > sizeN)
            throw new IndexOutOfBoundsException("checkBounds: index out of bounds");
    }

    private int xyTo1D(int i, int j) {
        // Map from row column to a 1-dimensional index used by uf
        return (i - 1) * sizeN + j; 
    }

    public boolean isOpen(int i, int j) {
        checkBounds(i, j);
        return siteStatus[xyTo1D(i, j)];
    }
}