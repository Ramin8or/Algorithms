/******************************************************************************
 *  Compilation:  javac-algs4 KdTree.java
 *  Execution:    java-algs4  KdTree
 *  Dependencies: Point2D, RectHV
 *  
 *  2d-tree implementation. 
 *  Write a mutable data type KdTree.java that uses a 2d-tree to implement the same API 
 *  as PointSET. A 2d-tree is a generalization of a BST to two-dimensional keys. 
 *  The idea is to build a BST with points in the nodes, using the x- and y-coordinates 
 *  of the points as keys in strictly alternating sequence.
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
 * KdTree implementation
 */
public class KdTree {
    private Node root;          // root of BST
    private int  size;          // Size of nodes in KdTree

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
     * Inserts the point in the KdTree
     *
     * @param  Point the point
     * @throws NullPointerException if <tt>point</tt> is <tt>null</tt>
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

    /** 
    * Recursive helper function for insert
    * 
    * The algorithms for search and insert are similar to those for BSTs, 
    * but at the root we use the x-coordinate (if the point to be inserted 
    * has a smaller x-coordinate than the point at the root, go left; 
    * otherwise go right); then at the next level, we use the y-coordinate 
    * (if the point to be inserted has a smaller y-coordinate than the point 
    * in the node, go left; otherwise go right); then at the next level the 
    * x-coordinate, and so forth.
    * @param  Node denotes the parent level node
    * @param  Point is the point that will be inserted
    * @param  Vertical denotes the orrientation of the node. 
    *         Nodes at levels that are even are vertical, odd leve nodes are horizontal.
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
        // Set up the rectangle
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
    private RectHV setNodeRect(Node parentNode, boolean vertical, boolean lb) {
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
     * draw all points to standard draw 
     */
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
                return node.p; 
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
        // Last do the next search which most likely will be pruned
        searchPoint = nearest(nextSearch, point, !vertical, minDist);
        if (searchPoint != null) {
            distance = searchPoint.distanceSquaredTo(point);
            if (distance < minDist) {
                minPoint = searchPoint; 
            }               
        }
        return minPoint;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }
}
