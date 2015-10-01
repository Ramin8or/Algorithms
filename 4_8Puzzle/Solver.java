/******************************************************************************
 *  Compilation:  javac-algs4 Solver.java
 *  Execution:    java-algs4  Solver
 *  Dependencies: none
 *  
 *  For use on Coursera, Algorithms Part I programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html
 *  Corner cases.  The constructor should throw a java.lang.NullPointerException if passed a null argument.
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;

public class Solver {
    private MinPQ<Node> pq; 
    private MinPQ<Node> pqTwin; 
    private Node solutionNode;
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new java.lang.NullPointerException(
                "Null array passed to Solver constructor!");
        // Initialize the priority queue for initial board
        pq = new MinPQ<Node>();
        Node initialNode = new Node(initial, 0, null);
        // Use a twin board and its own priroity queue
        pqTwin = new MinPQ<Node>();
        Board twin = initial.twin();
        Node twinNode = new Node(twin, 0, null);
        // First, insert the initial search node into a priority queue. 
        pq.insert(initialNode);
        pqTwin.insert(twinNode);
        // Then, delete from the priority queue the search node with the 
        // minimum priority, and insert onto the priority queue all 
        // neighboring search nodes. 
        // Repeat this procedure until the search node dequeued is a goal.
        solutionNode = null;
        int count = 0;
        while (true) {
            Node previousNode = pq.delMin();
            Node prevTwinNode = pqTwin.delMin();
            
            //StdOut.println(previousNode.getBoard());

            if (previousNode.getBoard().isGoal()) {
                StdOut.println("Solved after " + count + " moves!");
                solutionNode = previousNode;
                return;
            }
            if (prevTwinNode.getBoard().isGoal()) {
                StdOut.println("Cannot solve it, but twin was solved after " + count + " moves!");
                return;
            }

            count++;
            StdOut.println(count + ": " + prevTwinNode.manhattan);
            Board prevBoard = null;
            Board prevTwinBoard = null;
            if (previousNode.previous != null) {
                prevBoard = previousNode.previous.getBoard();
            }
            if (prevTwinNode.previous != null) {
                prevTwinBoard = prevTwinNode.previous.getBoard();
            }

            for (Board b : previousNode.getBoard().neighbors()) {
                if (!b.equals(prevBoard)) {
                    Node node = new Node(b, count, previousNode);
                    pq.insert(node);
                }
            }            
            for (Board b : prevTwinNode.getBoard().neighbors()) {
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
        return (solutionNode.moves); // TODO
    }
    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() { //TODO
        return null;
    }

    private class Node implements Comparable<Node> {
        private final Board board;
        public final int moves;
        private Node previous;
        public final int manhattan;
        private final int hamming;

        public Node(Board b, int m, Node prev) {
            board = b;
            moves = m;
            manhattan = board.manhattan();
            hamming   = board.hamming();
            previous = prev;
        }
        public int compareTo(Node that) {
            if ((this.moves+this.manhattan) < (that.moves+that.manhattan)) 
                return -1;
            else if ((this.moves+this.manhattan) > (that.moves+that.manhattan)) 
                return 1;
            else {
                if (this.hamming < that.hamming)
                    return -1;
                else if (this.hamming > that.hamming)
                    return 1;
                else return 0;
            }
        }
        public Board getBoard() {
            return this.board;
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
            /* TODO
            for (Board board : solver.solution())
                StdOut.println(board);
            */
        }
    }
}
