/*
One approach is to include the point, a link to the left/bottom subtree, a link to the right/top subtree, and an axis-aligned rectangle 
corresponding to the node.
Unlike the Node class for BST, this Node class is static because it does not refer to a generic Key or Value type that depends on the object associated with the outer class. This saves the 8-byte inner class object overhead. (Making the Node class static in BST is also possible if you make the Node type itself generic as well). Also, since we don't need to implement the rank and select operations, there is no need to store the subtree size.
Writing KdTree. Start by writing isEmpty() and size(). These should be very easy. From there, write a simplified version of insert() which does everything except set up the RectHV for each node. Write the contains() method, and use this to test that insert() was implemented properly. Note that insert() and contains() are best implemented by using private helper methods analogous to those found on page 399 of the book or by looking at BST.java. We recommend using orientation as an argument to these helper methods.
Now add the code to insert() which sets up the RectHV for each Node. Next, write draw(), and use this to test these rectangles. Finish up KdTree with the nearest and range methods. Finally, test your implementation using our interactive client programs as well as any other tests you'd like to conduct.
*/
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
            if (parentNode == root)
                parentNode.rect = parentRect;
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

    public void draw(Node node, boolean vertical) {
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
    }
}
