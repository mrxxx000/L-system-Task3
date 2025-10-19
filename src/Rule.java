public class Rule {
    private final char predecessor;
    private final String successor;

    public Rule(char predecessor, String successor) {
        this.predecessor = predecessor;
        this.successor = successor;
    }

    public char getPredecessor() { return predecessor; }
    public String getSuccessor() { return successor; }
}
