import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// build random timetable(chromosomes)
public class GA {
    private List<Course> courses;
    private List<TimeSlot> timeSlots;
    private List<Room> rooms;
    private int populationSize;
    private int maxGenerations;
    private double crossoverRate;
    private double mutationRate;
    private int elitismCount;
    private int noImprovementLimit =50; // condition C , stop if no improvement
    private int acceptableSoftViol = 5;
    private Random random;
    private FitnessCalc fitnessCalc;
    private Selection selection;
    private boolean useRWselection;
    private Crossover crossover;
    private boolean use1pointCrossover;
    private Mutation mutation;
    private boolean useSwapMutation;
    private List<Double> bestFitnessHistory;
    private String reasonStop;
    private long executionTime;

    public GA (List<Course> courses, List<TimeSlot> timeSlots, List<Room> rooms, int populationSize, int maxGenerations, double crossoverRate, double mutationRate, int elitismCount, long seed) {
        this(courses, timeSlots, rooms, populationSize, maxGenerations,crossoverRate, mutationRate, elitismCount, seed, false, false, false);
    }
    public GA(List<Course> courses, List<TimeSlot> timeSlots,  List<Room> rooms, int populationSize, int maxGenerations, double crossoverRate, double mutationRate, int elitismCount, long seed, boolean useRWselection, boolean use1pointCrossover, boolean useSwapMutation) {
        this.courses = courses;
        this.timeSlots = timeSlots;
        this.rooms = rooms;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismCount = elitismCount;
        random = new Random(seed);
        fitnessCalc = new FitnessCalc();
        selection = new Selection(random, 4);
        this.useRWselection = useRWselection;
        crossover = new Crossover(random);
        this.use1pointCrossover = use1pointCrossover;
        mutation = new Mutation(random);
        this.useSwapMutation = useSwapMutation;
        bestFitnessHistory = new ArrayList<>();
    }

    public Chromosome run() {
        long startTime = System.currentTimeMillis();
        List<Chromosome> population = initializePopulation();
        Chromosome best = null;
        int noImprovement = 0;
        for(int i=0; i<maxGenerations; i++){ // condition B = maxGeneration
            evaluatePop(population);
            //sort chromosome from highest to lowest fitness
            for(int j=0; j<population.size()-1; j++){
                for(int k=j+1; k<population.size(); k++){
                    if(population.get(j).getFitness() < population.get(k).getFitness()){
                        Chromosome temp = population.get(j);
                        population.set(j, population.get(k));
                        population.set(k, temp);
                    }
                }
            }
            Chromosome currentBest = population.get(0);
            if(best == null || currentBest.getFitness() > best.getFitness()){
                best = currentBest.deepCopy();
                noImprovement = 0;
            }else{
                noImprovement++;
            }
            bestFitnessHistory.add(best.getFitness());
            FitnessResult result = fitnessCalc.evaluate(best);

            if(result.getHardViolations() ==0 && result.getSoftViolations() <= acceptableSoftViol){ // condition A = hardViol =0 && acceptable SoftViol
                reasonStop = "Acceptable TimeTable found at generation "+ i;
                break;
            }

            if(noImprovement >= noImprovementLimit){ // condition C = no improvement
                reasonStop = "Stopped because there was no improvement for " +noImprovementLimit +" generation";
                break;
            }
            if(i == maxGenerations-1){ //condition B  = reached max number of generation
                reasonStop = "Reached max generation";
                break;
            }
            List<Chromosome> newPopulation = new ArrayList<>();
            for(int j =0; j< elitismCount && j<population.size(); j++){// elitism: copy best chromosomes to next generations
                newPopulation.add(population.get(j).deepCopy());
            }

            while(newPopulation.size() < populationSize){
                Chromosome parent1 = selectParent(population);
                Chromosome parent2 = selectParent(population);
                Chromosome child1;
                Chromosome child2;

                if(random.nextDouble() < crossoverRate){
                    Chromosome[] children;
                    if(use1pointCrossover){
                        children = crossover.point1Crossover(parent1, parent2);
                    }else{
                        children = crossover.point2Crossover(parent1, parent2);
                    }
                    child1 = children[0];
                    child2 = children[1];
                }else{
                    child1 = parent1.deepCopy();
                    child2 = parent2.deepCopy();
                }
                if(useSwapMutation){
                    mutation.swapMutation(child1, mutationRate);
                    mutation.swapMutation(child2, mutationRate);
                }else {
                    mutation.mutate(child1, mutationRate, timeSlots, rooms);
                    mutation.mutate(child2, mutationRate, timeSlots, rooms);
                }
                newPopulation.add(child1);
                if(newPopulation.size() < populationSize){
                    newPopulation.add(child2);
                }
            }
            population = newPopulation;
        }
        long endTime = System.currentTimeMillis();
        executionTime = endTime - startTime;
        return best;
    }

    private Chromosome selectParent(List<Chromosome> population){
        if(useRWselection){
            return selection.roulette(population);
        }else{
            return selection.tournament(population);
        }
    }

    private List<Chromosome> initializePopulation(){
        List<Chromosome> population = new ArrayList<>();
        for(int i=0; i<populationSize; i++){
            population.add(createRandomChro());
        }
        return population;
    }

    private Chromosome createRandomChro(){
        Chromosome chromosome = new Chromosome();
        for(int i =0; i<courses.size(); i++){
            Course course = courses.get(i);
            TimeSlot slot = timeSlots.get(random.nextInt(timeSlots.size()));
            Room room = rooms.get(random.nextInt(rooms.size()));
            chromosome.addGene(new Gene(course, slot, room));
        }
        return chromosome;
    }
    private void evaluatePop(List<Chromosome> population){
        for(int i=0; i<population.size(); i++){
            Chromosome chromosome = population.get(i);
            FitnessResult result = fitnessCalc.evaluate(chromosome);
            chromosome.setFitness(result.getFitness());
        }
    }
    public List<Double> getBestFitnessHistory() {
        return bestFitnessHistory;
    }
    public FitnessResult evaluate(Chromosome chromosome){
        return fitnessCalc.evaluate(chromosome);
    }

    public void setNoImprovementLimit(int noImprovementLimit) {
        this.noImprovementLimit = noImprovementLimit;
    }
    public void setAcceptableSoftViol(int acceptableSoftViol) {
        this.acceptableSoftViol = acceptableSoftViol;
    }
    public String getReasonStop(){
        return reasonStop;
    }
    public long  getExecutionTime(){
        return executionTime;
    }
}
