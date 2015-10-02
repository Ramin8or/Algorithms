/******************************************************************************
 *  Compilation:  javac-algs4 Solver.java
 *  Execution:    java-algs4  Solver
 *  Dependencies: none
 *  
 *  For use on Coursera, Algorithms Part I programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html
 *  Written by Ramin Halviatti
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import java.util.Iterator;

public class Solver {
    private Node solutionNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new java.lang.NullPointerException(
                "Null array passed to Solver constructor!");
        // Initialize class members
        MinPQ<Node> pq = new MinPQ<Node>();
        MinPQ<Node> pqTwin = new MinPQ<Node>(); 
        solutionNode = null;
        // First, insert the initial search node into a priority queue. 
        pq.insert(new Node(initial, null));
        pqTwin.insert(new Node(initial.twin(), null));

        while (!pq.isEmpty()) {
            // Delete from priority q the search node with the min priority 
            Node dquedNode = pq.delMin();
            Node dquedTwinNode = pqTwin.delMin();
            // Did we reach the goal?
            if (dquedNode.board.isGoal()) {
                // Stop the search node dequeued is a goal, set solutionNode
                solutionNode = dquedNode;
                return;
            }
            if (dquedTwinNode.board.isGoal()) {
                // Stop the search there is no solution
                return;
            }

            // Go through neighbors of dequeued node and insert in MinPQ
            insertNeighborsInPQ(pq, dquedNode);
            insertNeighborsInPQ(pqTwin, dquedTwinNode);
        }
    }

    // Go through neighbors of node and inserts them in MinPQ
    private void insertNeighborsInPQ(MinPQ<Node> pq, Node node) {
        // Set previous Node to avoid inserting duplicate boards
        Board prevBoard = null;
        if (node.previous != null) 
            prevBoard = node.previous.board;

        Iterator<Board> iter = node.board.neighbors().iterator();
        while (iter.hasNext()) {
            Board b = (Board) iter.next();
            if (!b.equals(prevBoard)) // equals handle null prevBoard
                pq.insert(new Node(b, node));
        }
    }
    // is the initial board solvable?
    public boolean isSolvable() {
        return (solutionNode != null);
    }   
    // min number of moves to solve initial board; -1 if unsolvable   
    public int moves() {
        if (solutionNode == null)
            return -1;
        return solutionNode.moves;
    }
    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() { 
        // Create a Stack from algs4 and fill it with neighboring boards
        if (solutionNode == null)
            return null;
        Stack<Board> stack = new Stack<Board>();
        Node solution = solutionNode;
        while (solution != null) {
            Board b = solution.board;
            if (b != null)
                stack.push(b);
            solution = solution.previous;
        }
        return stack;
    }

    private class Node implements Comparable<Node> {
        private final Board board;
        private final Node previous;
        private final int moves;
        private final int priority;

        public Node(Board b, Node prev) {
            board = b;
            previous = prev;
            if (previous == null)
                moves = 0;
            else 
                moves = previous.moves + 1;
            priority = moves + board.manhattan();
        }
        public int compareTo(Node that) {
            return this.priority - that.priority;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("priority\t= " + priority + "\n");
            sb.append("moves\t\t= "    +  moves + "\n");
            sb.append(this.board.toString());
            return sb.toString();
        }
    }

    // solve a slider puzzle 
    public static void main(String[] args) 
    {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
