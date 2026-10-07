import java.util.ArrayList;
import java.util.List;

public class Chromosome {
   private List<Gene> genes;
   private double fitness;

    public  Chromosome() {
        genes = new ArrayList<>();
        fitness = 0;
    }

    public void addGene(Gene gene) {
        genes.add(gene);
    }
    public List<Gene> getGenes() {
        return genes;
    }
    public Gene getGene(int index) {
        return genes.get(index);
    }
    public void setGene(int index, Gene gene) {
        genes.set(index, gene);
    }
    public double getFitness() {
        return fitness;
    }
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /* making deep copy instead of shallow because we dont want shared references, so
    in mutating , the new chromosome dont change the others */
    public Chromosome deepCopy() {
       Chromosome copy = new Chromosome();
       for(int i =0; i< genes.size();i++) {
           copy.addGene(genes.get(i).copy());
       }
       copy.setFitness(fitness);
       return copy;
    }

    // Chromosome: [12, 25, 07, 31, 18, 42, 09, 15, 36, 21]
    public List<Integer> listChromNum() { // to return the chromosome as list of integer slot codes
        List<Integer> slots = new ArrayList<>();
        for(int i =0; i< genes.size();i++) {
            slots.add(genes.get(i).getSlotIndex());
        }
        return slots;
    }
// The chromosome becomes: [(Sun,08,R101), (Sun,09,R102), (Mon,10,R103), (Tue,11,R101)]
    public List<String> listChromDTR() {
        List<String> tuples = new ArrayList<>();
        for(int i =0; i< genes.size();i++) {
            tuples.add(genes.get(i).toString2());
        }
        return tuples;
    }



}
