// higher final fitness = better timetable
public class FitnessResult {
    private int hardViolations;
    private int softViolations;
    private int penalty;
    private double fitness;

    public FitnessResult(int hardViolations, int softViolations, int penalty,double fitness) {
        this.hardViolations = hardViolations;
        this.softViolations = softViolations;
        this.penalty = penalty;
        this.fitness = fitness;
    }
    public int getHardViolations() {
        return hardViolations;
    }
    public int getSoftViolations() {
        return softViolations;
    }
    public double getFitness() {
        return fitness;
    }
    public int getPenalty() {
        return penalty;
    }
}
