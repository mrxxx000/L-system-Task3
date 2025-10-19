public class Main {
    public static void main(String[] args) {
        try {
            String csv = "params/experiments.csv";
            if (args.length > 0) csv = args[0];
            ExperimentRunner.runFromCSV(csv);
            System.out.println("Done. Check output/ for PNGs and metrics.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
