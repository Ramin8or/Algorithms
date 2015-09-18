/*
 * Performance requirements. Your randomized queue implementation must support each randomized queue operation 
 * (besides creating an iterator) in constant amortized time. That is, any sequence of M randomized queue operations 
 * (starting from an empty queue) should take at most cM steps in the worst case, for some constant c. A randomized queue 
 * containing N items must use at most 48N + 192 bytes of memory. Additionally, your iterator implementation must support 
 * operations next() and hasNext() in constant worst-case time; and construction in linear time; you may (and will need to) 
 * use a linear amount of extra memory per iterator.
 */
import java.util.Iterator;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] queue;        // queue elements
    private int N = 0;           // number of elements on queue

    // construct an empty randomized queue
    public RandomizedQueue() {
        final int SIZE = 10; // Initial size of array 
        // cast needed since no generic array creation in Java
        queue = (Item[]) new Object[SIZE];
    }   
    // is the queue empty?
    public boolean isEmpty() {
        return N == 0;
    }
    // return the number of items on the queue
    public int size() {
        return N;
    }   
    // resize the underlying array
    private void resize(int capacity) {
        assert capacity >= N;
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < N; i++) {
            temp[i] = queue[i];
        }
        queue = temp;
    }
    // add the item   
    public void enqueue(Item item) {
        if (item == null)
            throw new java.lang.NullPointerException("Cannot add a null item!");

        // double size of array if necessary and recopy to front of array
        if (N == queue.length) {
            // double size of array
            resize(2*queue.length);   
        }
        // add item
        queue[N++] = item;                        
    }         
    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) 
            throw new java.util.NoSuchElementException(
                "Queue is empty!");
        // Pick a random item in queue
        int rand = StdRandom.uniform(N);
        Item item = queue[rand];
        // Move last element to this position
        queue[rand] = null;
        queue[rand] = queue[N-1]; 
        // Remove last element without loitering
        queue[N-1] = null; 
        N--;
        // shrink size of array if necessary
        if (N > 0 && N == queue.length/4) 
            resize(queue.length/2);
        return item;
    }                
    // return (but do not remove) a random item
    public Item sample() {
        if (isEmpty()) 
            throw new java.util.NoSuchElementException(
                "Queue is empty!");

        int rand = StdRandom.uniform(N);
        Item item = queue[rand];
        return item;
    }
    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {      
        return new RandomQueueIterator();
    }
    
    private class RandomQueueIterator implements Iterator<Item> {
        private int current;
        private Item[] copyq;
        
        public RandomQueueIterator() {
            copyq = (Item[]) new Object[N];
            current = 0;    
            if (!isEmpty()) {
                StdRandom.shuffle(queue, 0, N-1);
                for (int i = 0; i < N; i++)
                    copyq[i] = queue[i];
            }
        }
        public boolean hasNext() { 
            return current != copyq.length; 
        }
        public void remove() {
            throw new java.lang.UnsupportedOperationException(
                "Iterator remove is not supported!");
        }
        public Item next() {
            if (current == copyq.length)
                throw new java.util.NoSuchElementException(
                    "There is no next item!");
            
            return copyq[current++];
        }
    }
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
    // unit testing
    public static void main(String[] args) {
        RandomizedQueue<Integer> randomq = new RandomizedQueue<Integer>();
        for (int i = 0; i < 20; i++)
            randomq.enqueue(i);
        for (int i : randomq)
            StdOut.println(i);
        verify((randomq.size() == 20),     "Size is 20");
        for (int i = 0; i < 20; i++)
            randomq.dequeue();
        verify(randomq.isEmpty(),    "randomq should be empty");
        Iterator<Integer> it = randomq.iterator();
        int item = 0;
        if (it.hasNext()) {
            item = it.next();
        }
        verify((item == 0),    "Iterator with empty queue");

        randomq.enqueue(1);
        Iterator<Integer> iterator = randomq.iterator();
        item = 0;
        if (iterator.hasNext()) {
            item = iterator.next();
        }
        verify((item == 1),    "Item is 1");
        item = randomq.dequeue();
        verify((item == 1),    "Item is 1");
        verify(randomq.isEmpty(),    "randomq should be empty");
    }
}
