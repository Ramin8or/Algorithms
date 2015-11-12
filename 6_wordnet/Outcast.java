/******************************************************************************
 *  Compilation:  javac-algs4 WordNet.java
 *  Execution:    java-algs4 Outcast (assumes the following files exists:
 *                test_data/synsets.txt test_data/hypernyms.txt 
 *                test_data/outcast5.txt test_data/outcast8.txt test_data/outcast11.txt)
 * 
 *  Dependencies: WordNet
 * 
 *  Outcast detection. Given a list of wordnet nouns A1, A2, ..., An, which noun 
 *  is the least related to the others? 
 *  To identify an outcast, the sum of the distances between each noun and 
 *  every other one is calculated:
 *  di   =   dist(Ai, A1)   +   dist(Ai, A2)   +   ...   +   dist(Ai, An)
 *  and a noun is returned for which d is maximum.
 * 
 *  For use on Coursera, Algorithms Part II programming assignment.
 *  Specifications: http://coursera.cs.princeton.edu/algs4/assignments/wordnet.html
 *  Written by Ramin Halviatti
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private WordNet W;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        W = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int dMax = Integer.MIN_VALUE;
        int nounMax = Integer.MIN_VALUE;
        for (int i=0; i < nouns.length; i++) {
            int di = 0;
            for (int j=0; j < nouns.length; j++) {
                if (i == j)
                    continue;
                di += W.distance(nouns[i], nouns[j]);
            }
            if (di > dMax) {
                dMax = di;
                nounMax = i;
            }
        }
        if (nounMax >= 0 && nounMax < nouns.length)
            return nouns[nounMax];
        else
            return null;
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

    // Unit test
    public static void main(String[] args) {
        boolean interactive = false;
        WordNet wordnet;
        if (args.length > 2) {
            interactive = true;
            wordnet = new WordNet(args[0], args[1]);
        }
        else {
            wordnet = new WordNet("test_data/synsets.txt", 
                                          "test_data/hypernyms.txt");
        }
        Outcast outcast = new Outcast(wordnet);
        if (interactive) {
            for (int t = 2; t < args.length; t++) {
                In in = new In(args[t]);
                String[] nouns = in.readAllStrings();
                StdOut.println(args[t] + ": " + outcast.outcast(nouns));
            }
        }
        else {
            String[] outcastFiles = { "test_data/outcast5.txt", 
                                      "test_data/outcast8.txt", 
                                      "test_data/outcast11.txt"
                                    };
            String[] answers = { "table",
                                 "bed",
                                 "potato"
                                };
            for (int t = 0; t < outcastFiles.length; t++) {
                In in = new In(outcastFiles[t]);
                String[] nouns = in.readAllStrings();
                String result = outcast.outcast(nouns);
                verify( (result.equals(answers[t])), "Outcast detection.");
            }
        }
    }
}
