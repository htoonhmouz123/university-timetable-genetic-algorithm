import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// start with random time table, then try another random timetable if it was bette continue, if no try again
public class HillClimbing {
    private List<Course> courses;
    private List<TimeSlot> timeSlots;
    private List<Room> rooms;
    private int maxIterations;
    private Random random;
    private FitnessCalc fitnessCalc;
    private List<Double> fitnessHistory;

    public HillClimbing(List<Course> courses, List<TimeSlot> timeSlots, List<Room> rooms, int maxIterations, long seed) {
        this.courses = courses;
        this.timeSlots = timeSlots;
        this.rooms = rooms;
        this.maxIterations = maxIterations;
        this.random = new Random(seed);
        this.fitnessCalc = new FitnessCalc();
        this.fitnessHistory = new ArrayList<>();
    }

    public Chromosome run(){
        Chromosome current = createRandChrom();
        current.setFitness(fitnessCalc.evaluate(current).getFitness());
        fitnessHistory.add(current.getFitness());

        for(int i = 0; i < maxIterations; i++){
            Chromosome neighbor = current.deepCopy();
            int geneI = random.nextInt(neighbor.getGenes().size());
            Course course = neighbor.getGene(geneI).getCourse();
            TimeSlot newSlot = timeSlots.get(random.nextInt(timeSlots.size()));
            Room newRoom = rooms.get(random.nextInt(rooms.size()));
            neighbor.setGene(geneI, new Gene(course, newSlot, newRoom));

            FitnessResult neighborResult = fitnessCalc.evaluate(neighbor);
            neighbor.setFitness(neighborResult.getFitness());

            //only go to neighbor if its better
            if(neighbor.getFitness() > current.getFitness()){
                current =  neighbor;
            }
            fitnessHistory.add(current.getFitness());
        }
        return current;
    }

    private Chromosome createRandChrom(){
        Chromosome chromosome = new Chromosome();
        for(int i=0; courses.size() > i; i++){
            Course course = courses.get(i);
            TimeSlot timeSlot = timeSlots.get(random.nextInt(timeSlots.size()));
            Room room = rooms.get(random.nextInt(rooms.size()));
            chromosome.addGene(new Gene(course, timeSlot, room));
        }
        return chromosome;
    }

    public FitnessResult evaluate(Chromosome chromosome){
        return fitnessCalc.evaluate(chromosome);
    }

    public List<Double> getFitnessHistory(){
        return fitnessHistory;
    }




}
