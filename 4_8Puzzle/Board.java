/******************************************************************************
 *  Compilation:  javac-algs4 Board.java
 *  Execution:    java-algs4  -ea Board
 *  Dependencies: none
 *  
 *  For use on Coursera, Algorithms Part I programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html
 *  Written by Ramin Halviatti
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.LinkedQueue;

public class Board {
    // internal representation of a Board in a 1d array of chars
    private char[] board;
    // dimension N of the 2D Board
    private int N;
    // Cached values
    private int manhattan;
    private int hamming;
    private boolean goal;
    private LinkedQueue<Board> cachedNeighbors;
    // construct a board from an N-by-N array of blocks
    public Board(int[][] blocks) {
        if (blocks == null)
            throw new java.lang.NullPointerException(
                "Null array passed to Board constructor!");
        // Get the N of 2d array and fill up the internal board
        N = blocks.length;
        cachedNeighbors = null;
        assert (N == blocks[0].length);
        this.board = new char[N*N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.board[i*N + j] = (char) blocks[i][j];
            }
        }
        cacheValues();
    }
    // Private constructor, takes array of chars and size
    private Board(char[] blocks, int size2D) {
        N = size2D;
        cachedNeighbors = null;
        this.board = new char[N*N];
        System.arraycopy(blocks, 0, this.board, 0, N*N);
        cacheValues();
    }
    // Use cached values for these functions                               
    public int dimension() {
        return N;
    }                 
    public int hamming() {
        return hamming;
    }                   
    public int manhattan() {
        return manhattan;
    }
    public boolean isGoal() {
        return goal;
    }       
    // a board that is obtained by exchanging any pair of blocks
    public Board twin() { 
        int rand1 = 0;
        int rand2 = 0;
        while (rand1 == rand2 || board[rand1] == 0 || board[rand2] == 0) {
            rand1 = StdRandom.uniform(N*N);
            rand2 = StdRandom.uniform(N*N);
        }
        return createNeighbor(rand1, rand2);
    }
    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (this.N != that.N) return false;
        for (int i = N*N - 1; i >= 0; i--) {
            if (this.board[i] != that.board[i])
                return false;
        }
        return true;
    } 
    // all neighboring boards
    public Iterable<Board> neighbors() {
        if (cachedNeighbors != null)
            return cachedNeighbors;
        // Find index of block 0
        int indexOfBlank = 0;
        while (indexOfBlank < N*N) {
            if (board[indexOfBlank] == 0)
                break;
            ++indexOfBlank;
        }
        assert (indexOfBlank != N*N);
        // Create a queue and fill it with neighboring boards
        LinkedQueue<Board> q = new LinkedQueue<Board>();
        for (Direction dir : Direction.values()) {
            int indexOfNeighbor = getIndexOfNeighbor(indexOfBlank, dir);
            if (indexOfNeighbor == -1)
                continue; // Skip invalid neighbors
            Board neighbor = createNeighbor(indexOfBlank, indexOfNeighbor);
            if (neighbor != null)
                q.enqueue(neighbor);
        }
        cachedNeighbors = q;
        return cachedNeighbors;
    }
    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sb.append(String.format("%2d ", (int) board[i*N + j]));
                //sb.append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    // Compute cached values for Hamming, Manhattan and Goal
    private void cacheValues() {
        manhattan = 0;
        hamming = 0;
        goal = true;
        for (int i = 0; i < N*N; i++) {
            // Handle 0 block
            if (board[i] == 0) {
                if (goal) {
                    // Goal board has 0 block only as its last element
                    goal = (i == (N*N - 1));
                }
                // Skip check for manhattan and hamming if block is 0
                continue; 
            }
            // Handle Hamming and Goal by checking i vs board[i]
            if (board[i] != (i+1)) {
                hamming++;
                if (goal)
                    goal = false;
            }
            // Compute Manhattan distance
            int rowDistance = Math.abs(((board[i] - 1) / N) - (i / N));
            int colDistance = Math.abs(((board[i] - 1) % N) - (i % N));
            manhattan += (rowDistance + colDistance);
        }
    }
    private enum Direction {
        RIGHT, LEFT, ABOVE, BELOW
    }
    private int getIndexOfNeighbor(int index, Direction direction) {
        // Find index of neighbor based on index
        int indexOfNeighbor = -1;
        switch(direction)
        {
            case ABOVE: if (index >= N)
                            indexOfNeighbor = index - N;
                        break;
            case BELOW: if (index < (N*(N - 1)))
                            indexOfNeighbor = index + N;
                        break;
            case RIGHT: if (index % N != (N - 1))
                            indexOfNeighbor = index + 1;
                        break;
            case LEFT: if (index % N != 0)
                            indexOfNeighbor = index - 1;
                        break;
        }
        return indexOfNeighbor;
    }       
    private Board createNeighbor(int index, int indexOfNeighbor) { //Change name to createBlock
        char[] blocks = new char[N*N];
        System.arraycopy(this.board, 0, blocks, 0, N*N);
        // Exchange values from index and indexOfNeighbor
        //assert (board[index] == 0);
        blocks[index] = this.board[indexOfNeighbor];
        blocks[indexOfNeighbor] = this.board[index];
        Board neighbor = new Board(blocks, N);
        return neighbor;
    }
    // For unit testing verification helper
    private static void verify(boolean b, String message) {
        if (b) {
            StdOut.println("PASSED: " + message);
        }
        else {
            throw new UnsupportedOperationException(
                "FAILED: " + message);
        }
    }
    public static void main(String[] args) {
        final int sizeS = 2;
        final int sizeL = 10;
        int[][] S = { {3, 2}, {1, 0} }; 
        int[][] L = new int[sizeL][sizeL];
        for (int i = 0; i < sizeL; i++)
            for (int j = 0; j < sizeL; j++)
                L[i][j] = i*sizeL + j;
        Board boardS = new Board(S);
        Board boardL = new Board(L);
        verify(boardS.dimension()   == sizeS,   "Dimension S");
        verify(boardL.dimension()   == sizeL,   "Dimension L");
        verify(boardS.hamming()     == 2,       "Hamming S");
        verify(boardS.manhattan()   == 2,       "Manhattan S");
        verify(boardL.hamming()     == 99,      "Hamming L");
        verify(boardL.manhattan()   == 180,     "Manhattan L");
        verify(!boardS.isGoal(),                "isGoal S");
        verify(!boardL.isGoal(),                "isGoal L");
        Board twinS = boardS.twin();
        verify(!twinS.equals(boardS),   "Equals S");
        verify(!boardL.equals(boardS),  "Equals L");
        for (Board b : boardL.neighbors()) {
            verify(!b.equals(boardS),   "Equals neighbors");
        }
    }
}

