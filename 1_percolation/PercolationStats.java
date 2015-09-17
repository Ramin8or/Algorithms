/*
 * Course: Princeton Algorithms Part 1
 * Project: Week 1 programming assignment: PercolationStats
 * Information: http://coursera.cs.princeton.edu/algs4/assignments/percolation.html 
 * compile: javac-algs4 Percolation.java PercolationVisualizer.java PercolationStats.java 
 * Test with PercolationVisualizer and data file: java-algs4  PercolationVisualizer input20.txt 
 * Test with PercolationStats: java-algs4 PercolationStats 200 10
 */
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdOut;

public class PercolationStats {
    private double[] thresholdData;
    private int numT;
    public PercolationStats(int N, int T) {     
    // perform T independent experiments on an N-by-N grid
        if (N <= 0 || T <= 0)
            throw new java.lang.IllegalArgumentException(
                "PercolationStats cannot take negative numbers.");
        numT = T;
        thresholdData = new double[numT];
        for (int i = 0; i < numT; i++) {
            thresholdData[i] = runExperiment(N);
        }
    }

    private double runExperiment(int N) {
        int openedSites = 0;
        Percolation perc = new Percolation(N);

        while (!perc.percolates()) {
            int row = StdRandom.uniform(N) + 1;
            int col = StdRandom.uniform(N) + 1;
            if (!perc.isOpen(row, col)) {
                perc.open(row, col);
                openedSites++;
            }
        }
        return (double) openedSites / (double) (N*N);
    }

    public double mean() {
    // sample mean of percolation threshold
        return StdStats.mean(thresholdData);
    }

    public double stddev() {
    // sample standard deviation of percolation threshold 
        return StdStats.stddev(thresholdData);
    }

    public double confidenceLo() {
    // low  endpoint of 95% confidence interval             
        return mean() - ( (1.96*stddev()) / Math.sqrt(numT) );
    }

    public double confidenceHi() {   
    // high endpoint of 95% confidence interval          
        return mean() + ( (1.96*stddev()) / Math.sqrt(numT) );
    }

    public static void main(String[] args) 
    {
        PercolationStats percStats; 
       
        percStats = new PercolationStats(
            Integer.parseInt(args[0]), Integer.parseInt(args[1]));  
        
        StdOut.println("mean\t\t\t = " + percStats.mean());
        StdOut.println("stddev\t\t\t = " + percStats.stddev());
        StdOut.println("95% confidence interval\t = " + percStats.confidenceLo() + ", " + percStats.confidenceHi());
    }
}