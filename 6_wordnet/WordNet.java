/******************************************************************************
 *  Compilation:  javac-algs4 WordNet.java
 *  Execution:    java-algs4  WordNet (excepts a directory called ./test_data 
                                       with data for synsets and hypernyms)
 *  Dependencies: SAP, algs4.In, algs4.Digraph, algs4.Bag, ArrayList, HashMap
 *  
 *  WordNet is a semantic lexicon for the English language that is used extensively 
 *  by computational linguists and cognitive scientists; for example, it was a key 
 *  component in IBM's Watson. WordNet groups words into sets of synonyms called 
 *  synsets and describes semantic relationships between them. One such relationship 
 *  is the is-a relationship, which connects a hyponym (more specific synset) to a 
 *  hypernym (more general synset). For example, a plant organ is a hypernym of 
 *  carrot and plant organ is a hypernym of plant root.
 *  The WordNet digraph: each vertex v is an integer that represents a synset, 
 *  and each directed edge v→w represents that w is a hypernym of v. The wordnet 
 *  digraph is a rooted DAG: it is acyclic and has one vertex—the root—that is an 
 *  ancestor of every other vertex. A synset can have more than one hypernym. 
 * 
 *  For use on Coursera, Algorithms Part II programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/wordnet.html
 *  Written by Ramin Halviatti
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Bag;
import java.util.ArrayList;
import java.util.HashMap;

public class WordNet {
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new NullPointerException(
                "Null filename specified for synsets or hypernyms.");

        // Create structures for nouns
        nounArray = new ArrayList<String>();
        nounMap = new HashMap<String, Bag<Integer>>();
        // Process synsets file 
        int numOfSynsets = processSynsetsFile(synsets);
        try {
            // Create digraph
            Digraph G = new Digraph(numOfSynsets);
            // Process hypernyms file and populate digraph
            processHypernymsFile(hypernyms, G);
            // Create the Shortest Ancestoral Path
            sap = new SAP(G);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(
                "The input does not correspond to a rooted DAG.");            
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
         if (word == null)
            throw new NullPointerException(
                "Null word specified.");
       return nounMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        // Validate parameters
        if (nounA == null || nounB == null) 
            throw new NullPointerException(
                "Null word specified.");
        if (!isNoun(nounA) || !isNoun(nounB)) 
            throw new NullPointerException(
                "Non-word argument specified.");
        // Map nouns to integers and pass to SAP.length()
        return sap.length(nounMap.get(nounA), nounMap.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        // Validate parameters
        if (nounA == null || nounB == null) 
            throw new NullPointerException(
                "Null word specified.");
        if (!isNoun(nounA) || !isNoun(nounB)) 
            throw new NullPointerException(
                "Non-word argument specified.");
        // Get ancestor id using SAP.ancestor
        int ancestorId = sap.ancestor(nounMap.get(nounA), nounMap.get(nounB));
        // Map id to nount
        return nounArray.get(ancestorId);
    }

    // Shortest ancestral path (SAP) to measure semantic relatedness of nouns
    private SAP sap;    
    
    // nounArray: given an integer id returns nouns corresponding to the id
    // Index of array is first field in synsets file, value is second field
    private ArrayList<String> nounArray; 
    
    // Map an individual noun (from field 2 of synsets file) to all the ids
    // it corresponds to (in a bag if integers)
    private HashMap<String, Bag<Integer>> nounMap; 

    // Add each noun in synonym set to nounMap, by
    // mapping each word to the passed in synsetId 
    private void addNouns(int id, String[] synonymSet) {
        for (String s : synonymSet) {
            if (nounMap.containsKey(s)) {
                Bag<Integer> bag = nounMap.get(s);
                bag.add(id);
            }
            else {
                Bag<Integer> bag = new Bag<Integer>();
                bag.add(id);
                nounMap.put(s, bag);
            }
        }
    }
        
    // Process the synsets and perform mapping for nouns
    private int processSynsetsFile(String synsets) {
        int synsetsCount = 0;

        In synsetsFile = new In(synsets);
        while (!synsetsFile.isEmpty()) {
            String line = synsetsFile.readLine();
            synsetsCount++;
            String[] fields = line.split(",");
            assert fields.length == 3;
            // First field is an integer that denotes the synset Id
            int synsetId = Integer.parseInt(fields[0]);
            // Add the set of synonyms under the id in an resizing array
            nounArray.add(synsetId, fields[1]);

            // Second field is a list of nouns deliminted by space
            String[] synonymSet = fields[1].split(" ");
            // Map each noun to this synonym id
            addNouns(synsetId, synonymSet);
        }
        synsetsFile.close();
        return synsetsCount;
    }

    // Process hypernyms to populate the passed in digraph
    private void processHypernymsFile(String hypernyms, Digraph G) {
        In hypernymsFile = new In(hypernyms);
        while (!hypernymsFile.isEmpty()) {
            String line = hypernymsFile.readLine();
            String[] fields = line.split(",");
            int synsetId = Integer.parseInt(fields[0]);
            for (int i=1; i < fields.length; i++) {
                int w = Integer.parseInt(fields[i]);
                G.addEdge(synsetId, w);
            }
        }
        hypernymsFile.close();
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
        WordNet W; 
        if (args.length == 2)
            W = new WordNet(args[0], args[1]); 
        else {
            W = new WordNet("test_data/synsets.txt",
                            "test_data/hypernyms.txt");
            int distance = W.distance("Black_Plague", "black_marlin");
            verify( (distance == 33), 
                    "Distance between Black_Plague & black_marlin");
            distance = W.distance("worm", "bird");
            verify( (distance == 5),
                    "Distance for worm & bird");
        }
    }
}