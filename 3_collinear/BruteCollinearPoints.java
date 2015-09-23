/******************************************************************************
 *  Compilation:  javac-algs4 BruteCollinearPoints.java
 *  Execution:    java-algs4  BruteCollinearPoints
 *  Dependencies: Point.java, LineSegment.java
 *  
 *  Brute force. Write a program BruteCollinearPoints.java that examines 4 points 
 *  at a time and checks whether they all lie on the same line segment, returning 
 *  all such line segments. To check whether the 4 points p, q, r, and s are 
 *  collinear, check whether the three slopes between p and q, between p and r, 
 *  and between p and s are all equal.
 * 
 *  The method segments() should include each line segment containing 4 points 
 *  exactly once. If 4 points appear on a line segment in the order p→q→r→s, 
 *  then you should include either the line segment p→s or s→p (but not both) 
 *  and you should not include subsegments such as p→r or q→r. For simplicity, 
 *  we will not supply any input to BruteCollinearPoints that has 5 or more 
 *  collinear points.
 * 
 *  Corner cases. Throw a java.lang.NullPointerException either the argument to 
 *  the constructor is null or if any point in the array is null. 
 *  Throw a java.lang.IllegalArgumentException if the argument to the constructor 
 *  contains a repeated point.
 * 
 *  Performance requirement. The order of growth of the running time of your program 
 *  should be N4 in the worst case and it should use space proportional to N plus the 
 *  number of line segments returned.
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

public class BruteCollinearPoints {
    private ArrayList<LineSegment> segmentList;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points)    
    {
        if (points == null)
            throw new java.lang.NullPointerException(
                "Null argument passed to constructor!");
        // Create the list of segments
        segmentList = new ArrayList<LineSegment>();

        double pqSlope = 0.0;
        double prSlope = 0.0;
        double psSlope = 0.0;
        for (int p = 0; p < points.length; p++) {
            checkPoint(points[p]);
            for (int q = p+1; q < points.length; q++) { 
                pqSlope = getSlope(points[p], points[q]);
                for (int r = q+1; r < points.length; r++) {
                    prSlope = getSlope(points[p], points[r]);  
                    if (pqSlope != prSlope)
                        continue;                      
                    for (int s = r+1; s < points.length; s++) { 
                        psSlope = getSlope(points[p], points[s]);
                        if (prSlope != psSlope)
                            continue;
                        addSegment(points, p, q, r, s);
                    }
                }
            }
        }
    }
    private double getSlope(Point reference, Point point)
    {
        checkPoint(point);
        double newSlope = reference.slopeTo(point);
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
    private void addSegment(Point[] points, int p, int q, int r, int s)
    {
        // Sort points and pick first and last points to form a segment
        Point[] segPoints = {points[p], points[q], points[r], points[s]};
        Arrays.sort(segPoints);
        LineSegment segment = new LineSegment(segPoints[0], segPoints[3]); 
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
    }
}