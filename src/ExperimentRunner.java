import java.io.*;
import java.util.*;
import java.awt.geom.Rectangle2D;

public class ExperimentRunner {

    public static Map<String, String[]> presets() {
        Map<String, String[]> p = new LinkedHashMap<>();
        p.put("plant1", new String[]{"F", "F -> FF-[-F+F+F]+[+F-F-F]"});
        p.put("fractal1", new String[]{"F", "F -> F[+F]F[-F]F"});
        p.put("bushy", new String[]{"X", "X -> F[+X]F[-X]+X", "F -> FF"});
        return p;
    }

    public static void runFromCSV(String csvPath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(csvPath));
        String line;
    new File("output").mkdirs();
    PrintWriter metrics = new PrintWriter(new FileWriter("output/metrics.csv"));
    metrics.println("id,axiom,ruleList,iterations,angle,step,imageFile,length,F_count,branch_count,bbox_w,bbox_h");

    int id = 0;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            String[] cols = line.split(",", -1);
            if (cols.length < 7) continue;
            String name = cols[0].trim();
            String axiom = cols[1].trim();
            String ruleSpecs = cols[2].trim(); 
            int iters = Integer.parseInt(cols[3].trim());
            double angle = Double.parseDouble(cols[4].trim());
            double step = Double.parseDouble(cols[5].trim());
            String outFile = "output/" + cols[6].trim();

            LSystem ls = new LSystem(axiom);
            String[] rules = ruleSpecs.split(";");
            for (String r : rules) {
                String rr = r.trim();
                if (rr.isEmpty()) continue;
                String[] kv = rr.split(":", 2);
                if (kv.length == 2) {
                    char pred = kv[0].trim().charAt(0);
                    String succ = kv[1].trim();
                    int arrowIdx = succ.indexOf("->");
                    if (arrowIdx >= 0) {
                        succ = succ.substring(arrowIdx + 2).trim();
                    }
                    if (!succ.isEmpty() && succ.charAt(0) == pred) {
                        if (succ.length() == 1 || Character.isWhitespace(succ.charAt(1))) {
                            succ = succ.substring(1).trim();
                        }
                    }
                    ls.addRule(pred, succ);
                }
            }

            String expanded = ls.expand(iters);
            TurtleRenderer tr = new TurtleRenderer(1200, 900);
            double startX = 100;
            double startY = 450;
            Rectangle2D bbox = tr.renderToFile(expanded, startX, startY, -90, step, angle, outFile);

            Map<String, Double> m = LSystem.computeMetrics(expanded);
            metrics.printf(Locale.US, "%d,%s,\"%s\",%d,%.2f,%.2f,%s,%.0f,%.0f,%.0f,%.2f,%.2f\n",
                    id, axiom, ruleSpecs, iters, angle, step, outFile,
                    m.get("length"), m.get("F_count"), m.get("branch_count"),
                    bbox.getWidth(), bbox.getHeight());
            System.out.println("Wrote " + outFile + " (len=" + expanded.length() + ")");
            id++;
        }
        metrics.close();
        br.close();
    }
}
