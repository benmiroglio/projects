/**
 * Implements autocomplete on prefixes for a given dictionary of terms and weights.
 */
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collections;
import java.util.TreeSet;
/**
 * Autocomplete Class
 * @author Ben Miroglio
 */
public class Autocomplete {
    /**
     * Initializes required data structures from parallel arrays.
     * @param terms Array of terms.
     * @param weights Array of weights.
     */

  


    private WeightedTrie words;
    private HashMap<Double, String> wordsWithPrefix;
    private String topWord;

    /**
     * @return words WeightedTrie()
     */
    public WeightedTrie getTrie() {
        return words;
    }
    /**
     * @param terms -> array of words
     * @param weights -> correponding word frequency
     * initializes autocomplete // 
     */
    public Autocomplete(String[] terms, double[] weights) {
        if (terms.length != weights.length) {
            throw new IllegalArgumentException();
        }
        words = new WeightedTrie();
        TreeSet<String> checkDups = new TreeSet<String>();
        double topWeight = 0.0;
        topWord = "";
        for (int i = 0; i < terms.length; i++) {
            if (words.find(terms[i], true) || weights[i] <= 0.0) {
                throw new IllegalArgumentException();
            }
            if (weights[i] > topWeight) {
                topWeight = weights[i];
                topWord = terms[i];
            }
            words.insert(terms[i], weights[i]);
            checkDups.add(terms[i]);
        }
        if (checkDups.size() != terms.length) {
            throw new IllegalArgumentException();
        }
        wordsWithPrefix = new HashMap<Double, String>();
    }

    /**
     * Find the weight of a given term. If it is not in the dictionary, return 0.0
     * @param term -> string 
     * @return double
     */
    public double weightOf(String term) {
        return words.get(term).getWeight();
    }

    /**
     * @param n -> the current node, after following all the nodes to the end of 
     * the given prefix in topMatch methods
     * @param sb -> the prefix in SB form
     * builds wordsWithPrefix to contain all words and weights that start with the 
     * given prefix in the sb
     */
    public void getAllWords(Node n, StringBuilder sb) {
        if (!n.equals(words.getRoot())) {
            sb.append(n.getVal());
        }
        if (n.isEnd()) {
            //System.out.println(sb);
            //System.out.println(n.getWeight() + "  " + sb);
            if (wordsWithPrefix.containsKey(n.getWeight())) {
                wordsWithPrefix.put(n.getWeight() + Math.random(), sb.toString());
            } else {
                wordsWithPrefix.put(n.getWeight(), sb.toString());
            }

        } 
        if (n.children().size() == 1) {
            getAllWords(n.children().peekFirst(), sb);
        } 
        if (n.children().size() > 1) {
            for (Node child : n.children()) {
                getAllWords(child, new StringBuilder(sb.toString()));
                
            }
        }
        //System.out.println(wordsWithPrefix.values());
    }

    /**
     * Return the top match for given prefix, or null if there is no matching term.
     * @param prefix Input prefix to match against.
     * @return Best (highest weight) matching string in the dictionary.
     */
    public String topMatch(String prefix) {
        
        if (prefix.equals("")) {
            return topWord;
        }
        Node current = words.getRoot();
        for (char c : prefix.toCharArray()) {
            current = current.childNode(c);
        }

        int ssLength = 0;
        if (prefix.length() > 0) {
            ssLength = prefix.length() - 1;
        }

        getAllWords(current, new StringBuilder(prefix.substring(0, ssLength)));
        String top = wordsWithPrefix.get(Collections.max(wordsWithPrefix.keySet()));
        wordsWithPrefix = new HashMap<Double, String>(); //reset for future operations
        return top;
        
    }

    /**
     * Returns the top k matching terms (in descending order of weight) as an iterable.
     * If there are less than k matches, return all the matching terms.
     * @param prefix somwthign
     * @param k something
     * @return iterableprefic
     */
    public Iterable<String> topMatches(String prefix, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException();
        }
        
        LinkedList<String> it = new LinkedList<String>();
        Node current = words.getRoot();
        for (char c : prefix.toCharArray()) {
            if (current.childNode(c) == null) {
                //System.out.println("Not in Dictionary");
                return new LinkedList<String>();
            }
            current = current.childNode(c);
        }
        int ssLength = 0;
        if (prefix.length() > 0) {
            ssLength = prefix.length() - 1;
        }

        getAllWords(current, new StringBuilder(prefix.substring(0, ssLength)));
        while (k > 0) {
            if (wordsWithPrefix.keySet().isEmpty()) {
                break;
            }

            double max = Collections.max(wordsWithPrefix.keySet());
            //wordsWithPrefix.remove(wordsWithPrefix.lastKey());
            String top = wordsWithPrefix.get(max);
            it.add(top);
            wordsWithPrefix.remove(max);
            k--;
        }
        wordsWithPrefix = new HashMap<Double, String>();
        return it;
    }

    /**
     * Returns the highest weighted matches within k edit distance of the word.
     * If the word is in the dictionary, then return an empty list.
     * @param word The word to spell-check
     * @param dist Maximum edit distance to search
     * @param k    Number of results to return 
     * @return Iterable in descending weight order of the matches
     */
    public Iterable<String> spellCheck(String word, int dist, int k) {
        LinkedList<String> results = new LinkedList<String>();  
        /* YOUR CODE HERE; LEAVE BLANK IF NOT PURSUING BONUS */
        return results;
    }
    /**
     * Test client. Reads the data from the file, 
     * then repeatedly reads autocomplete queries from standard input and prints 
     * out the top k matching terms.
     * @param args takes the name of an input file and an integer k as command-line arguments
     */
    public static void main(String[] args) {
        // initialize autocomplete data structure
        In in = new In(args[0]);
        int N = in.readInt();
        String[] terms = new String[N];
        double[] weights = new double[N];
        for (int i = 0; i < N; i++) {
            weights[i] = in.readDouble();   // read the next weight
            in.readChar();                  // scan past the tab
            terms[i] = in.readLine();       // read the next term
        }

        Autocomplete autocomplete = new Autocomplete(terms, weights);
        //autocomplete.getAllWords(autocomplete.getTrie().getRoot(), new StringBuilder());
        //System.out.println(autocomplete.topMatches("", 10));
        //// process queries from standard input
        int k = Integer.parseInt(args[1]);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            //System.out.println(autocomplete.topMatch(prefix));
            //System.out.println(autocomplete.weightOf(autocomplete.topMatch(prefix)));
            //System.out.println(autocomplete.topMatch(prefix));
            for (String term : autocomplete.topMatches(prefix, k)) {
                StdOut.printf("%14.1f  %s\n", autocomplete.weightOf(term), term);
            }
        }
    }
}
