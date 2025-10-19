import java.util.*;

public class LSystem {
    private String axiom;
    private final List<Rule> rules = new ArrayList<>();

    public LSystem(String axiom) {
        this.axiom = axiom;
    }

    public void addRule(char from, String to) {
        rules.add(new Rule(from, to));
    }

    public String expand(int iterations) {
        String current = axiom;
        for (int i = 0; i < iterations; i++) {
            StringBuilder next = new StringBuilder();
            for (char c : current.toCharArray()) {
                String replacement = null;
                for (Rule r : rules) {
                    if (r.getPredecessor() == c) {
                        replacement = r.getSuccessor();
                        break;
                    }
                }
                if (replacement != null) next.append(replacement);
                else next.append(c);
            }
            current = next.toString();
        }
        return current;
    }

    public static Map<String, Double> computeMetrics(String expanded) {
        Map<String, Double> m = new HashMap<>();
        m.put("length", (double) expanded.length());
        long fCount = expanded.chars().filter(ch -> ch == 'F').count();
        m.put("F_count", (double) fCount);
        long openBr = expanded.chars().filter(ch -> ch == '[').count();
        m.put("branch_count", (double) openBr);
        return m;
    }
}
