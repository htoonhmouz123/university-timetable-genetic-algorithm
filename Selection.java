import java.util.List;
import java.util.Random;

public class Selection {
    private Random random;
    private int tournamentSize;

    public Selection(Random random, int tournamentSize){
        this.random = random;
        this.tournamentSize = tournamentSize;
    }

    //tournament selection:randomly pick N chromosome & return the fittest
    public Chromosome tournament(List<Chromosome> population){
        Chromosome best = null;
        for(int i = 0; i < tournamentSize; i++){
            int index = random.nextInt(population.size());
            Chromosome c = population.get(index);
            if(best == null || c.getFitness() > best.getFitness()){
                best = c;
            }
        }
        return best;
    }

    //roulette selection: probability of selection proportional to fitness
    // more fit --> get bigger slice of wheels
    public Chromosome roulette(List<Chromosome> population){
        double totalFitness = 0;
        for(int i = 0; i < population.size(); i++){
            totalFitness += population.get(i).getFitness();
        }
        if(totalFitness <= 0){
            return population.get(random.nextInt(population.size()));
        }
        double choose = random.nextDouble()*totalFitness; // spin wheel
        double cumulative = 0;
        for(int j = 0; j<population.size(); j++){
            Chromosome c = population.get(j);
            cumulative += c.getFitness();
            if(cumulative >= choose){
                return c;
            }
        }
        return population.get(population.size()-1);
    }
}
