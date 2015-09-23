/******************************************************************************
 *  Compilation:  javac-algs4 FastCollinearPoints.java
 *  Execution:    java-algs4  FastCollinearPoints
 *  Dependencies: Point.java, LineSegment.java
 *  
 * A faster, sorting-based solution to BruteCollinearPoints.java. 
 * Given a point p, think of p as the origin.
 * For each other point q, determine the slope it makes with p.
 * Sort the points according to the slopes they makes with p.
 * Check if any 3 (or more) adjacent points in the sorted order have equal slopes 
 * with respect to p. If so, these points, together with p, are collinear.
 * Applying this method for each of the N points in turn yields an efficient 
 * algorithm to the problem. The algorithm solves the problem because points 
 * that have equal slopes with respect to p are collinear, and sorting brings 
 * such points together. The algorithm is fast because the bottleneck operation 
 * is sorting.
 *
 *  For use on Coursera, Algorithms Part I programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/collinear.html
 *
 ******************************************************************************/

import java.util.Arrays;
import java.util.ArrayList;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

public class FastCollinearPoints {
    // List of segments of collinear points
    private ArrayList<LineSegment> segmentList;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points)    
    {
        if (points == null)
            throw new java.lang.NullPointerException(
                "Null argument passed to constructor!");
        // Create the list of segments
        segmentList = new ArrayList<LineSegment>();
        // Copy the points array to preserve it
        Point[] pArray = new Point[points.length];
        System.arraycopy(points, 0, pArray, 0, points.length);
        // Loop to Find points above origin that have same slope with origin
        for (int i = 1; i < pArray.length; i++) {
            // Origin is the i - 1th point
            Point origin = pArray[i - 1];
            // Sort points above origin according to slope order
            Arrays.sort(pArray, i, pArray.length, origin.slopeOrder());
            // Find consecutive points that have same slope with origin
            int index = i;
            while (index < pArray.length) {
                int startIndex = index;
                double slope = getSlope(origin, pArray[index++]);
                while (index < pArray.length) {
                    double newSlope = getSlope(origin, pArray[index]);
                    if (newSlope != slope)
                        break;
                    index++;
                }   
                int count = index - startIndex;
                if (count >= 3) {
                    addSegment(pArray, origin, startIndex, index);
                }
                else {
                    index = startIndex + 1;
                }
            }
        }
    }
    private double getSlope(Point origin, Point point)
    {
        checkPoint(point);
        double newSlope = origin.slopeTo(point);
        checkSlope(newSlope);
        return newSlope;
    }
    private void checkPoint(Point p)
    {
        if (p == null)
            throw new java.lang.NullPointerException(
                "Point cannot be null!");
    }
    private void checkSlope(double slope)
    {
        if (slope == Double.NEGATIVE_INFINITY)
            throw new java.lang.IllegalArgumentException(
                "Slope indicates repeated point was passed in!");
    }
    private void addSegment(Point[] points, Point origin, int from, int to)
    {
        // Sort points and pick first and last points to form a segment
        int size = to - from + 1; // Add room for origin point too
        Point[] segPoints = new Point[size]; 
        segPoints[0] = origin;
        int start = from;
        for (int i = 1; i < size; i++) {
            segPoints[i] = points[start++];
        }
        Arrays.sort(segPoints);
        LineSegment segment = new LineSegment(segPoints[0], segPoints[segPoints.length - 1]); 
        segmentList.add(segment);
    }
    // the number of line segments
    public           int numberOfSegments()        
    {
        return segmentList.size();
    }
    // the line segments
    public LineSegment[] segments()                
    {
        LineSegment[] lsArray = segmentList.toArray(new LineSegment[segmentList.size()]);
        return lsArray;
    }

    public static void main(String[] args) {

        // read the N points from a file
        In in = new In(args[0]);
        int N = in.readInt();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.show(0);
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
    }
}
