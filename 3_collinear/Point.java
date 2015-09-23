/******************************************************************************
 *  Compilation:  javac-algs4 Point.java
 *  Execution:    java-algs4  Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/collinear.html
 *
 ******************************************************************************/

import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

public class Point implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertcal;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        if (this.y == that.y) {
            // Handle horizontal line
            if (this.x == that.x)   return Double.NEGATIVE_INFINITY;
            else                    return +0.0;
        }
        else if (this.x == that.x) {
            // Vertical line
            return  Double.POSITIVE_INFINITY;
        }
        else {
            // Calculate slope
            return (double) (that.y - this.y) / (double) (that.x - this.x);
        }
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that) {
        if (this.y < that.y)            return -1;
        else if (this.y > that.y)       return +1;
        else {
            // Use x coordinate as tie breaker
            if (this.x < that.x)        return -1;
            else if (this.x > that.x)   return +1;
            else                        return  0;
        }
    }

    private class SlopeOrder implements Comparator<Point>
    {
        public int compare(Point q1, Point q2)
        {
            double q1Slope = Point.this.slopeTo(q1);
            double q2Slope = Point.this.slopeTo(q2);
            if (q1Slope < q2Slope)      return -1;
            else if (q1Slope > q2Slope) return +1;
            else                        return  0;
        }
    }
    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return new SlopeOrder();
    }


    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }
    /**
     * Verification helper
     */
    private static void verify(boolean b, String message) {
        if (b) {
            StdOut.println("PASSED: " + message);
        }
        else {
            throw new java.lang.UnsupportedOperationException(
                "FAILED: " + message);
        }
    }
    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        int N = 3;
        // points are in random order
        Point[] points = {
            new Point(4, 12),
            new Point(2, 2),
            new Point(1, 2),
            new Point(0, 0),
            new Point(1, 1),
            new Point(7, 22),
            new Point(1, 2),
        };
        // Expected results assume sorted order in points array
        double[] expectedSlopes = { 
            1.0, 
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            +0.0, 
            5.0,
            3.3333333333333335
        };
        // Sort the array first
        Arrays.sort(points);
        // Check expected results for slope for consecutive points
        for (int i = 0; i < points.length; i++) {
            if (i > 0) {
                double slope = points[i-1].slopeTo(points[i]);
                StdOut.println("Slope of " + points[i-1] + " - " + points[i] +
                    "\tis: " + slope);
                verify((slope == expectedSlopes[i-1]), "Slope verification");
                slope = points[0].slopeTo(points[i]);
            }
        }
    }
}