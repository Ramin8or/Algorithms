/******************************************************************************
 *  Compilation:  javac-algs4 Solver.java
 *  Execution:    java-algs4  Solver
 *  Dependencies: none
 *  
 *  For use on Coursera, Algorithms Part I programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html
 *  Corner cases.  The constructor should throw a java.lang.NullPointerException if passed a null argument.
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import java.util.Iterator;

public class Solver {
    private MinPQ<Node> pq; 
    private MinPQ<Node> pqTwin; 
    private Node solutionNode;
    private int minMoves;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new java.lang.NullPointerException(
                "Null array passed to Solver constructor!");
        // Initialize class members
        solutionNode = null;
        minMoves = -1;
        // Initialize the priority queue for initial board
        pq = new MinPQ<Node>();
        Node initialNode = new Node(initial, initial.hamming(), null);
        // Use a twin board and its own priroity queue
        pqTwin = new MinPQ<Node>();
        Board twin = initial.twin();
        Node twinNode = new Node(twin, twin.hamming(), null);
    
        // First, insert the initial search node into a priority queue. 
        pq.insert(initialNode);
        pqTwin.insert(twinNode);

        int count = 0;
        Board prevBoard = null;
        Board prevTwinBoard = null;
        while (true) {
            // Delete from priority q the search node with the min priority 
            Node previousNode = pq.delMin();
            Node prevTwinNode = pqTwin.delMin();
            if (previousNode.getBoard().isGoal()) {
                // Stop the search node dequeued is a goal
                solutionNode = previousNode;
                return;
            }
            if (prevTwinNode.getBoard().isGoal()) {
                // Stop the search there is no solution
                return;
            }

            count++;
            if (previousNode.previous != null) 
                prevBoard = previousNode.previous.getBoard();
            else
                prevBoard = null;

            if (prevTwinNode.previous != null) 
                prevTwinBoard = prevTwinNode.previous.getBoard();
            else
                prevTwinBoard = null;

            Iterator<Board> iter = previousNode.getBoard().neighbors().iterator();
            while (iter.hasNext()) {
                Board b = (Board) iter.next();
                if (!b.equals(prevBoard)) {
                    Node node = new Node(b, count, previousNode);
                    pq.insert(node);
                }
            }
            iter = prevTwinNode.getBoard().neighbors().iterator();
            while (iter.hasNext()) {
                Board b = (Board) iter.next();
                if (!b.equals(prevTwinBoard)) {
                    Node node = new Node(b, count, prevTwinNode);
                    pqTwin.insert(node);
                }
            }            
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
        if (minMoves != -1)
            return minMoves;
        Iterator<Board> iter = solution().iterator();
        while (iter.hasNext()) {
            minMoves++;
            iter.next();
        }
        // minMoves starts at -1 to exclude initial board
        return minMoves;
    }
    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() { 
        // Create a Stack from algs4 and fill it with neighboring boards
        if (solutionNode == null)
            return null;
        Stack<Board> stack = new Stack<Board>();
        Node solution = solutionNode;
        while (solution != null) {
            Board b = solution.getBoard();
            if (b != null)
                stack.push(b);
            solution = solution.previous;
        }
        return stack;
    }

    private class Node implements Comparable<Node> {
        private final Board board;
        private final int moves;
        private Node previous;
        private final int manhattan;
        private final int hamming;

        public Node(Board b, int m, Node prev) {
            board = b;
            moves = m;
            manhattan = board.manhattan();
            hamming   = board.hamming();
            previous = prev;
        }
        public int compareTo(Node that) {
            int thisPriority = this.moves + this.hamming;
            int thatPriority = that.moves + that.hamming;
            if (thisPriority < thatPriority) 
                return -1;
            else if (thisPriority > thatPriority) 
                return 1;
            else {
                if (this.manhattan < that.manhattan)
                    return -1;
                else if (this.manhattan > that.manhattan)
                    return 1;
                else return 0;
            }
        }
        public Board getBoard() {
            return this.board;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("priority\t= " + (moves+manhattan) + "\n");
            sb.append("moves\t\t= "    +  moves + "\n");
            sb.append("manhattan\t= "+  manhattan + "\n");
            sb.append("hamming\t= "+  hamming + "\n");
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
