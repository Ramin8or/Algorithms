/******************************************************************************
 *  Compilation:  javac-algs4 PointSET.java
 *  Execution:    java-algs4  PointSET
 *  Dependencies: Point2D, RectHV
 *  
 *  Brute-force implementation. Write a mutable data type PointSET.java that 
 *  represents a set of points in the unit square. Implement the API by using a 
 *  red-black BST (using either SET from algs4.jar or java.util.TreeSet).
 * 
 *  For use on Coursera, Algorithms Part I programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/kdtree.html
 *  Written by Ramin Halviatti
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.LinkedQueue;

public class PointSET {
    // Internal SET which uses red-black BST
    SET<Point2D> set;
    // construct an empty set of points 
    public PointSET() {
        set = new SET<Point2D>();
    }
    // is the set empty?          
    public boolean isEmpty() {
        return set.isEmpty();
    }                       
    // number of points in the set
    public int size() {
        return set.size();
    }                     
    // add the point to the set (if it is not already in the set)     
    public void insert(Point2D p) {
        if (p == null)
            throw new NullPointerException(
                "Point2D cannot be null!");
        if (!set.contains(p))
            set.add(p);
    }              
    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new NullPointerException(
                "Point2D cannot be null!");
        return set.contains(p);
    }
    // draw all points to standard draw 
    public void draw() {
        for (Point2D point : set) {
            point.draw();
        }
    }              
    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new NullPointerException(
                "RectHV cannot be null!");
        // Create a queue and fill it with points that are inside rect
        LinkedQueue<Point2D> q = new LinkedQueue<Point2D>();
        for (Point2D point : set) {
            if (rect.contains(point))
                q.enqueue(point);
        }
        return q;
    }            
    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new NullPointerException(
                "Point2D cannot be null!");
        if (set.isEmpty())
            return null;
        double minDistance = Double.MAX_VALUE;
        Point2D minPoint = null;
        for (Point2D point : set) {
            double distance = p.distanceSquaredTo(point);
            if (distance < minDistance) {
                minDistance = distance;
                minPoint = point; 
            }               
        }
        return minPoint;
    }         
    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }                
}
