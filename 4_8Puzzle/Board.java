/******************************************************************************
 *  Compilation:  javac-algs4 Board.java
 *  Execution:    java-algs4  -ea Board
 *  Dependencies: none
 *  
 *  For use on Coursera, Algorithms Part I programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import java.lang.Math;
import java.util.Queue;
import java.util.LinkedList;

public class Board {
    // internal representation of a Board in a 1d array of bytes
    private byte[] board;
    // dimension N of the 2D Board
    private int N;
    // construct a board from an N-by-N array of blocks
    public Board(int[][] blocks) {
        if (blocks == null)
            throw new java.lang.NullPointerException(
                "Null array passed to Board constructor!");
        // Get the N of 2d array and fill up the internal board
        N = blocks.length;
        assert (N == blocks[0].length);
        this.board = new byte[N*N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.board[i*N + j] = (byte) blocks[i][j];
            }
        }
    }
    // Private constructor, takes array of bytes and size
    private Board(byte[] blocks, int size2D) {
        N = size2D;
        this.board = new byte[N*N];
        for (int i = 0; i < N*N; i++)
            this.board[i] = blocks[i];
    }
    // board dimension N                                   
    public int dimension() {
        return N;
    }                 
    // number of blocks out of place, ignore blank 0 block
    // For each block at index i, goal value of board[i] is i+1
    // except for blank block 0 which is being ignored in calculation
    public int hamming() {
        int count = 0;
        for (int i = 0; i < N*N; i++) {
            if (board[i] != 0 && board[i] != (i+1))
                count++;
        }
        return count;
    }                   
    // sum of Manhattan distances between blocks and goal, ignore 0 block
    public int manhattan() {
        int sum = 0;
        for (int i = 0; i < N*N; i++) {
            if (board[i] != 0)
                sum += manhattanDist(i, board[i]);
        }
        return sum;
    }
    private int manhattanDist(int index, byte actualValue) {
        // For each block at index i, goal value of board[i] is i+1
        // Get goal Row and Column 
        assert (actualValue != 0);
        int goalRow = (actualValue - 1) / N;
        int goalCol = (actualValue - 1) % N;
        int currRow = index / N;  
        int currCol = index % N;
        int distance = Math.abs(goalRow - currRow) + Math.abs(goalCol - currCol);
        return distance;
    }
    // is this board the goal board? 
    public boolean isGoal() {
        for (byte i = 0; i < N*N - 1; i++) {
            if (board[i] != (i + 1))
                return false;
        }
        assert (board[N*N - 1] == 0); // All number are in order, last should be block 0
        return true;
    }       
    // a board that is obtained by exchanging any pair of blocks
    public Board twin() { 
        /*
        int randomIndex = 0;
        int neighborIndex = -1;
        while (neighborIndex == -1) {
            randomIndex = StdRandom.uniform(N*N);
            if (this.board[randomIndex] == 0) 
                // Skip blank block 0
                continue;
            neighborIndex = getIndexOfNeighbor(randomIndex, Direction.RIGHT);
            if (neighborIndex == -1)
                neighborIndex = getIndexOfNeighbor(randomIndex, Direction.LEFT);
            if (neighborIndex != -1 && this.board[neighborIndex] == 0) { 
                // Skip blank block 0
                neighborIndex = -1;
                continue;
            }
        }
        assert (randomIndex < N*N && neighborIndex < N*N);
        assert (neighborIndex != -1 && this.board[neighborIndex] != 0);
        assert (this.board[randomIndex] != 0);
        return createNeighbor(randomIndex, neighborIndex);
        */
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
        return (this.N == that.N && 
                this.toString().equals(that.toString()));
    } 
    // all neighboring boards
    public Iterable<Board> neighbors() {
        // Find index of block 0
        int indexOfBlank = 0;
        while (indexOfBlank < N*N) {
            if (board[indexOfBlank] == 0)
                break;
            ++indexOfBlank;
        }
        assert (indexOfBlank != N*N);
        // Create a queue and fill it with neighboring boards
        Queue<Board> q = new LinkedList<Board>();
        for (Direction dir : Direction.values()) {
            int indexOfNeighbor = getIndexOfNeighbor(indexOfBlank, dir);
            if (indexOfNeighbor == -1)
                continue; // Skip invalid neighbors
            Board neighbor = createNeighbor(indexOfBlank, indexOfNeighbor);
            if (neighbor != null)
                q.add(neighbor);
        }
        return q;
    }
    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sb.append(String.format("%2d ", board[i*N + j])); // TODO Do I need a cast (int)
                //sb.append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    enum Direction {
        ABOVE, RIGHT, BELOW, LEFT
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
        byte[] blocks = new byte[N*N];
        for (int i = 0; i < N*N; i++)
            blocks[i] = this.board[i];
        // Exchange values from index and indexOfNeighbor
        //assert (board[index] == 0);
        blocks[index] = this.board[indexOfNeighbor];
        blocks[indexOfNeighbor] = this.board[index];
        Board neighbor = new Board(blocks, N);
        return neighbor;
    }
    // For unit testing
    // verification helper
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
        verify(twinS.equals(boardS) == false,   "Equals S");
        verify(boardL.equals(boardS) == false,  "Equals L");
        /* TODO
        verify(twinS.hamming()     == 3,        "Hamming Twin");
        verify(twinS.manhattan()   == 4,        "Manhattan Twin");
        */
        for (Board b : boardL.neighbors()) {
            verify(b.equals(boardS) == false,   "Equals neighbors");
        }
    }
}

