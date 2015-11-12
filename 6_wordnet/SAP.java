/******************************************************************************
 *  Compilation:  javac-algs4 SAP.java
 *  Execution:    java-algs4  SAP  (if digraph file is not specified uses
                                    ./test_data/digraph1.txt file)
 *  Dependencies: DeluxeBFS, algs4.In, algs4.Digraph, algs4.StdIn
 *  
 *  Shortest ancestral path (SAP) 
 *  An ancestral path between two vertices v and w in a digraph is a directed path 
 *  from v to a common ancestor x, together with a directed path from w to the same 
 *  ancestor x. A shortest ancestral path is an ancestral path of minimum total length. 
 * 
 *  For use on Coursera, Algorithms Part II programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/wordnet.html
 *  Written by Ramin Halviatti
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;

public class SAP {

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new NullPointerException(
                "Null argument specified.");
        this.G = new Digraph(G);
        bfs1 = new DeluxeBFS(G);
        bfs2 = new DeluxeBFS(G);
        vCached = wCached = lengthCached = ancestorCached = -1;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v == vCached && w == wCached) {
            return lengthCached;
        }
        refreshCache(v, w);
        return lengthCached;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v == vCached && w == wCached) {
            return ancestorCached;
        }
        refreshCache(v, w);
        return ancestorCached;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new NullPointerException(
                "Null argument specified.");

        bfs1.setSource(v);
        bfs2.setSource(w);

        int minSAP = Integer.MAX_VALUE;
        int idSAP = -1;
        for (int i = 0; i < G.V(); i++) {
            if (bfs1.hasPathTo(i) && bfs2.hasPathTo(i)) {
                int distance = bfs1.distTo(i) + bfs2.distTo(i);
                if (distance < minSAP) {
                    minSAP = distance;
                    idSAP = i;
                }
            }
        }
        return minSAP;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new NullPointerException(
                "Null argument specified.");

        // TODO this is duplicate code 
        bfs1.setSource(v);
        bfs2.setSource(w);

        int minSAP = Integer.MAX_VALUE;
        int idSAP = -1;
        for (int i = 0; i < G.V(); i++) {
            if (bfs1.hasPathTo(i) && bfs2.hasPathTo(i)) {
                int distance = bfs1.distTo(i) + bfs2.distTo(i);
                if (distance < minSAP) {
                    minSAP = distance;
                    idSAP = i;
                }
            }
        }
        return idSAP;
    }

    private Digraph G;
    private int vCached, wCached, lengthCached, ancestorCached;
    private DeluxeBFS bfs1, bfs2;

    private void refreshCache(int v, int w)
    {
        vCached = v;
        wCached = w;

        bfs1.setSource(v);
        bfs2.setSource(w);

        lengthCached = -1;
        ancestorCached = -1;

        int minSAP = Integer.MAX_VALUE;
        int idSAP = -1;
        for (int i = 0; i < G.V(); i++) {
            if (bfs1.hasPathTo(i) && bfs2.hasPathTo(i)) {
                int distance = bfs1.distTo(i) + bfs2.distTo(i);
                if (distance < minSAP) {
                    minSAP = distance;
                    idSAP = i;
                }
            }
        }
        if (idSAP != -1 && minSAP != Integer.MAX_VALUE) { 
            lengthCached = minSAP;
            ancestorCached = idSAP;
        }
    }

    // Verification helper
    private static void verify(boolean b, String message) {
        if (b) {
            StdOut.println("PASSED: " + message);
        }
        else {
            throw new java.lang.UnsupportedOperationException(
                "FAILED: " + message);
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in;
        boolean interactive = false;
        if (args.length == 1) {
            in = new In(args[0]);
            interactive = true;
        }
        else 
            in = new In("test_data/digraph1.txt");
 
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        if (interactive) {
            while (!StdIn.isEmpty()) {
                int v = StdIn.readInt();
                int w = StdIn.readInt();
                int length   = sap.length(v, w);
                int ancestor = sap.ancestor(v, w);
                StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
            }
        }
        else {
                int v = 3;
                int w = 11;
                int length   = sap.length(v, w);
                int ancestor = sap.ancestor(v, w);
                verify ( (length == 4), "Correct ancestoral length.");
                verify ( (ancestor == 1), "Correct ancestor.");
        }
    }
}