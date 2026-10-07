import java.util.Random;

public class Crossover {
    private Random random;

    public Crossover(Random random) {
        this.random = random;
    }

    // pick single cut point, swap everything after btw 2 parents to make 2 children
    public Chromosome[] point1Crossover(Chromosome parent1, Chromosome parent2){
        int size = parent1.getGenes().size();
        Chromosome child1 = parent1.deepCopy();
        Chromosome child2 = parent2.deepCopy();
        int cutPoint = random.nextInt(size-1) +1;
        for(int i = cutPoint; i<size; i++){
            child1.setGene(i, parent2.getGene(i).copy());
            child2.setGene(i, parent1.getGene(i).copy());
        }
        return new Chromosome[]{child1, child2};
    }

    //pick 2 cut points , swap btw them
    public Chromosome[] point2Crossover(Chromosome parent1, Chromosome parent2) {
        Chromosome child1 = parent1.deepCopy();
        Chromosome child2 = parent2.deepCopy();
        int size = parent1.getGenes().size();

        int cutPoint1 = random.nextInt(size) ;
        int cutPoint2 = random.nextInt(size);
        if(cutPoint1 > cutPoint2){ // making sur cutPoint1 always smaller
            int temp = cutPoint1;
            cutPoint1 = cutPoint2;
            cutPoint2 = temp;
        }

        for(int i = cutPoint1; i< cutPoint2; i++){
            child1.setGene(i, parent2.getGene(i).copy());
            child2.setGene(i, parent1.getGene(i).copy());
        }
        return new Chromosome[]{child1,child2};
    }
}
