
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
        if (i == sizeN) {
            // Site is at bottom row, do not use uf4Full
            uf.union(site, vBottom);
        }
        if (i > 1 && isOpen(i-1, j)) {
            // Connect to neighbor above
            uf.union(site, site - sizeN);
            uf4Full.union(site, site - sizeN);
        }
        if (i < sizeN && isOpen(i+1, j)) {
            // Connect to neighbor below 
            uf.union(site, site + sizeN);
            uf4Full.union(site, site + sizeN);
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