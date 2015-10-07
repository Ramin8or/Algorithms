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

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.LinkedQueue;

/**
 * 
 */
public class KdTree {
    private Node root;             // root of BST
    private int  size;

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        public Node(Point2D point, RectHV rect) {
            this.p = point;
            this.rect = rect;
            this.lb = null;
            this.rt = null;
        }
        public void draw(boolean vertical) {
            if (rect == null || p == null)
                return;

            if (vertical) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(p.x(), rect.ymin(), p.x(), rect.ymax());
            }
            else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(rect.xmin(), p.y(), rect.xmax(), p.y());
            }
        }
    }
    /**
     * Initializes an empty KdTree
     */
    public KdTree() {
    }

    /**
     * Returns true if this KdTree is empty.
     * @return <tt>true</tt> if this KdTree is empty; <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Returns the number of points in this KdTree.
     * @return the number of points in this KdTree
     */
    public int size() {
        return this.size;
    }

    /**
     * Does this KdTree contain the given point?
     *
     * @param  point
     * @return <tt>true</tt> if this KdTree contains <tt>point</tt> and
     *         <tt>false</tt> otherwise
     * @throws NullPointerException if <tt>point</tt> is <tt>null</tt>
     */
    public boolean contains(Point2D point) {
        if (point == null)
            throw new NullPointerException(
                "Point2D cannot be null!");
        return (get(root, point, true) != null);
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param  key the key
     * @return the value associated with the given key if the key is in the symbol table
     *         and <tt>null</tt> if the key is not in the symbol table
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    private Node get(Node node, Point2D point, boolean vertical) {
        if (node == null) 
            return null;

        double cmp = 0.0;
        if (vertical) 
            cmp = point.x() - node.p.x();
        else 
            cmp = point.y() - node.p.y();

        if (cmp < 0) 
            return get(node.lb, point, !vertical);
        else if (cmp > 0) 
            return get(node.rt, point, !vertical);
        else {
            if (point.equals(node.p))
                return node;
            else
                return get(node.rt, point, !vertical);
        }      
    }

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value
     * with the new value if the key is already in the symbol table.
     * If the value is <tt>null</tt>, this effectively deletes the key from the symbol table.
     *
     * @param  key the key
     * @param  val the value
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public void insert(Point2D point) {
        if (point == null)
            throw new NullPointerException(
                "Point2D cannot be null!");

        this.size++; 
        root = put(root, point, true);
        if (root != null && root.rect == null) 
            root.rect = new RectHV(0.0, 0.0, 1.0, 1.0);
    }
/*
Search and insert. The algorithms for search and insert are similar to those for BSTs, 
but at the root we use the x-coordinate 
(if the point to be inserted has a smaller x-coordinate than the point at the root, go left; otherwise go right); 
then at the next level, we use the y-coordinate 
(if the point to be inserted has a smaller y-coordinate than the point in the node, go left; otherwise go right); 
then at the next level the x-coordinate, and so forth.
*/
    private Node put(Node node, Point2D point, boolean vertical) {
        if (node == null) {
            return new Node(point, null);
        }
        double cmp = 0.0;
        if (vertical) 
            cmp = point.x() - node.p.x();
        else  
            cmp = point.y() - node.p.y();

        if (cmp < 0.0)        
            node.lb  = put(node.lb, point, !vertical);
        else if (cmp > 0.0)   
            node.rt = put(node.rt, point, !vertical);
        else {
            if (point.equals(node.p)) {
                this.size--; // We did not add a node in this case
                return node;
            }
            else
                node.rt = put(node.rt, point, !vertical);
        }
        // Set the rectangle
        if (cmp < 0.0) {
            if (node.lb != null && node.lb.rect == null) 
                node.lb.rect = setNodeRect(node, vertical, true);
        }
        else {
            if (node.rt != null && node.rt.rect == null) 
                node.rt.rect = setNodeRect(node, vertical, false);            
        }

        return node;
    }
    // TODO this is all wrong, need to know right, left, top bottom
    private RectHV setNodeRect(Node parentNode, boolean vertical, boolean lb) {
        // TODO clean this up
        RectHV newRect = null;
        RectHV parentRect = parentNode.rect;
        if (parentRect == null) {
            parentRect = new RectHV(0.0, 0.0, 1.0, 1.0);
        }
        if (vertical) {
            if (lb)
                newRect = new RectHV(
                    parentRect.xmin(), parentRect.ymin(), 
                    parentNode.p.x(), parentRect.ymax());
            else
                newRect = new RectHV(
                    parentNode.p.x(), parentRect.ymin(), 
                    parentRect.xmax(), parentRect.ymax());
        }
        else {
            if (lb)
                newRect = new RectHV(
                    parentRect.xmin(), parentRect.ymin(),
                    parentRect.xmax(), parentNode.p.y());
            else
                newRect = new RectHV(
                    parentRect.xmin(), parentNode.p.y(),
                    parentRect.xmax(), parentRect.ymax());
        }
        return newRect;
    }

    /**
     * 
     */
    // draw all points to standard draw 
    public void draw() {
        draw(root, true);
    }

    private void draw(Node node, boolean vertical) {
        if (node == null)
            return;
        // Draw left or bottom
        draw(node.lb, !vertical);

        // Draw this node
        node.draw(vertical);
        // Draw the point
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        node.p.draw();
        StdDraw.setPenRadius();

        // Draw right or top
        draw(node.rt, !vertical);
    }
    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new NullPointerException(
                "RectHV cannot be null!");
        // Create a queue and fill it with points that are inside rect
        LinkedQueue<Point2D> q = new LinkedQueue<Point2D>();
        range(q, root, rect, true);
        return q;
    }
    private void range(LinkedQueue<Point2D> q, Node node, RectHV rect, boolean vertical) {
        // Does the query rect contain the node?
        if (node == null)
            return;
        if (node.rect == null)
            return;
        if (!rect.intersects(node.rect)) 
            return;

        if (rect.contains(node.p))
            q.enqueue(node.p);

        range(q, node.lb, rect, !vertical);
        range(q, node.rt, rect, !vertical);
    }            
    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D point) {
        if (point == null)
            throw new NullPointerException(
                "Point2D cannot be null!");
        if (root == null)
            return null;
        return nearest(root, point, true, Double.MAX_VALUE);
    }
    private Point2D nearest(Node node, Point2D point, boolean vertical, double minDist) {
        if (node == null) 
            return null;
        if (node.rect == null)
            return null;
        if (node.rect.distanceSquaredTo(point) > minDist)
            return null;

        Point2D minPoint = null;        
        Node firstSearch = null;
        Node nextSearch  = null;
        Point2D searchPoint = null;

        double cmp = 0.0;
        if (vertical) 
            cmp = point.x() - node.p.x();
        else 
            cmp = point.y() - node.p.y();

        if (cmp < 0) { 
            firstSearch = node.lb;
            nextSearch  = node.rt;
        }
        else if (cmp >= 0) {
            firstSearch = node.rt;
            nextSearch  = node.lb;
            if (point.equals(node.p))
                return node.p; // TODO is this right?
        } 
        // Check this node
        double distance = node.p.distanceSquaredTo(point);
        if (distance < minDist) {
            minDist = distance;
            minPoint = node.p; 
        }               
        // Now do the first search
        searchPoint = nearest(firstSearch, point, !vertical, minDist);
        if (searchPoint != null) {
            distance = searchPoint.distanceSquaredTo(point);
            if (distance < minDist) {
                minDist = distance;
                minPoint = searchPoint; 
            }               
        }
        searchPoint = nearest(nextSearch, point, !vertical, minDist);
        if (searchPoint != null) {
            distance = searchPoint.distanceSquaredTo(point);
            if (distance < minDist) {
                minDist = distance; // This is not being used TODO
                minPoint = searchPoint; 
            }               
        }
        return minPoint;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {

        String filename = args[0];
        In in = new In(filename);

        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        Point2D p = new Point2D(0.975528, 0.345492);
        if (kdtree.contains(p))
            StdOut.println("Success!");
        else 
            StdOut.println("Failure!");

        p = new Point2D(0.975527, 0.345492);
        if (!kdtree.contains(p))
            StdOut.println("Success!");
        else 
            StdOut.println("Failure!");

        StdOut.println("Size: " + kdtree.size());

        //StdDraw.show(0);
        // draw the points
        StdDraw.clear();
        StdDraw.setPenRadius();
        kdtree.draw();
        StdDraw.show();

        RectHV r = new RectHV(0.08, 0.08, 0.38, 0.9);
        for (Point2D p1 : kdtree.range(r)) 
            StdOut.println("In range: " + p1);
    }
}
