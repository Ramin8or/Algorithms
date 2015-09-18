
/*
 * Specifications: http://coursera.cs.princeton.edu/algs4/assignments/queues.html
 * 
 * Performance requirements. 
 * Deque implementation must support each deque operation in constant worst-case time. 
 * A deque containing N items must use at most 48N + 192 bytes of memory. 
 * and use space proportional to the number of items currently in the deque. 
 * Additionally, iterator implementation must support each operation (including construction) in constant worst-case time.
 * 
 * Submission cannot call library functions except those in StdIn, StdOut, StdRandom, java.lang, java.util.Iterator, 
 * and java.util.NoSuchElementException. In particular, you may NOT use either java.util.LinkedList or java.util.ArrayList.
 */
import java.util.Iterator;
import edu.princeton.cs.algs4.StdOut;

public class Deque<Item> implements Iterable<Item> {
    private int N;               // number of elements
    private Node<Item> first;    // front of queue
    private Node<Item> last;     // end of queue

    // helper linked list class
    private class Node<Item> {
        private Item item;
        private Node<Item> prev;
        private Node<Item> next;
    }
    // construct an empty deque
    public Deque() {                           
        first = null;
        last  = null;
         N = 0;
    }
    // is the deque empty?
    public boolean isEmpty() {                
        return (N == 0);
    }
    // return the number of items on the deque
    public int size() {                       
        return N;
    }
    // add the item to the front
    public void addFirst(Item item) {         
        if (item == null)
            throw new java.lang.NullPointerException("Cannot add a null item!");
        
        Node<Item> oldFirst = first;
        
        first = new Node<Item>();
        first.item = item;
        first.prev = null;
        first.next = oldFirst;
        
        if (oldFirst != null) {
            oldFirst.prev = first;
        }
        if (isEmpty()) {
          last = first;
        }
        N++;
    }
    // add the item to the end
    public void addLast(Item item)           
    {
        if (item == null)
            throw new java.lang.NullPointerException("Cannot add a null item!");
        
        Node<Item> oldLast = last;
 
        last = new Node<Item>();
        last.item = item;
        last.prev = oldLast;
        last.next = null;

        if (oldLast != null) {
            oldLast.next = last;
        }
        if (isEmpty()) {
            first = last;
        }
        N++;        
    }
    // remove and return the item from the front
    public Item removeFirst() {               
        if (isEmpty() || first == null) 
            throw new java.util.NoSuchElementException(
                "Cannot remove from empty deque!");

        Node<Item> oldFirst = first;
        first = first.next;
        if (first != null)
            first.prev = null; 

        N--;
        if (isEmpty()) {
            first = null; 
            last = null;
        }
        Item item = oldFirst.item;
        oldFirst.prev = null; 
        oldFirst.next = null;
        return item;
    }
    // remove and return the item from the end
    public Item removeLast() {                
        if (isEmpty() || last == null) 
            throw new java.util.NoSuchElementException(
                "Cannot remove from empty deque!");

        Node<Item> oldLast = last;
        last = last.prev; 
        if (last != null)
            last.next = null; 

        N--;
        if (isEmpty()) {
            first = null; 
            last = null;
        }
        Item item = oldLast.item;
        oldLast.prev = null; 
        oldLast.next = null;
        return item;
    }
    // return an iterator over items in order from front to end
    public Iterator<Item> iterator() { 
        return new DequeIterator();
    }
    
    private class DequeIterator implements Iterator<Item> {
        private Node<Item> current = first;
        
        public boolean hasNext() { 
            return current != null; 
        }
        public void remove() {
            throw new java.lang.UnsupportedOperationException(
                "Iterator remove is not supported!");
        }
        public Item next() {
            if (current == null)
                throw new java.util.NoSuchElementException(
                    "There is no next item!");
            Item item = current.item;
            current   = current.next;
            return item;
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
        Deque<Integer> deque = new Deque<Integer>();

        deque.addLast(1000);
        int item = deque.removeFirst();
        verify((item == 1000),     "addLast() followed by removeFirst()");
        verify(deque.isEmpty(),    "deque should be empty");
        deque.addLast(1000);
        verify(!deque.isEmpty(),    "Empty to non-empty");
        item = deque.removeLast();
        verify((item == 1000),     "Item is 1000");
        verify(deque.isEmpty(),    "deque should be empty");

        deque.addFirst(1);
        item = deque.removeLast();
        verify((item == 1),        "addFirst() followed by removeLast()");
        verify(deque.isEmpty(),    "deque should be empty");

        Iterator<Integer> iterator = deque.iterator();
        verify((!iterator.hasNext()), "hasNext() should be false");

        for (int i = 0; i < 5; i++) {
            deque.addLast(i);
        }
        iterator = deque.iterator();
        verify((deque.size() == 5), "Size should be 5");
        int count = 0;
        verify(iterator.hasNext(), "Iterator.hasNext() incorrect");
        while (iterator.hasNext()) {
            item = iterator.next();
            verify((item == count), "Iterator next should increment");
            count++;
        }
        verify((item == 4), "Iterator last item should be 99");
        for (int i = 0; i < 5; i++) {
            item = deque.removeLast();
        }
        verify(deque.isEmpty(), "deque should be empty");
        verify((item == 0), "Item should be 0");
        deque.addFirst(1);
        item = deque.removeFirst();
        verify(deque.isEmpty(), "deque should be empty");
        verify((item == 1), "Item should be 1");
    }
}