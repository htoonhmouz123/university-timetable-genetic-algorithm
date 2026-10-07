import java.util.List;
import java.util.Random;

public class Mutation {
    private Random random;


    public Mutation(Random random) {
        this.random = random;
    }

    // change both timeSlot and room
    public void mutate(Chromosome chromosome, double mutationRate, List<TimeSlot> slots, List<Room> rooms) {
        List<Gene> genes = chromosome.getGenes();
        for(int i = 0; i < genes.size(); i++){
            if(random.nextDouble() < mutationRate){
                TimeSlot newSlot = slots.get(random.nextInt(slots.size()));
                Room newRoom = rooms.get(random.nextInt(rooms.size()));
                chromosome.setGene(i, new Gene(genes.get(i).getCourse(), newSlot, newRoom));
            }
        }
    }

    public void mutateTimeOnly(Chromosome chromosome, double  mutationRate, List<TimeSlot> slots) {
        List<Gene> genes = chromosome.getGenes();
        for(int i = 0; i < genes.size(); i++){
            if(random.nextDouble() < mutationRate){
                Gene current = genes.get(i);
                TimeSlot newSlot = slots.get(random.nextInt(slots.size()));
                chromosome.setGene(i, new Gene(current.getCourse(), newSlot, current.getRoom()));
            }
        }
    }

    public void mutateRoomOnly(Chromosome chromosome, double  mutationRate, List<Room> rooms) {
        List<Gene> genes = chromosome.getGenes();
        for(int i = 0; i < genes.size(); i++){
            if(random.nextDouble() < mutationRate){
                Gene current = genes.get(i);
                Room newRoom = rooms.get(random.nextInt(rooms.size()));
                chromosome.setGene(i, new Gene(current.getCourse(), current.getTimeslot(), newRoom));
            }
        }
    }

    public void swapMutation(Chromosome chromosome, double  mutationRate) {
        List<Gene> genes = chromosome.getGenes();
        if(genes.size() < 2){
            return; // cant swap with less than 2 genes
        }
        for(int i=0; i < genes.size(); i++) {
            if (random.nextDouble() < mutationRate) {
                int j = random.nextInt(genes.size());
                while (j == i) { // dont swap gene with itself
                    j=random.nextInt(genes.size());
                }
                Gene g1 = genes.get(i);
                Gene g2 = genes.get(j);

                Gene newG1 = new Gene(g1.getCourse(), g2.getTimeslot(), g2.getRoom());
                Gene newG2 = new Gene(g2.getCourse(), g1.getTimeslot(), g1.getRoom());
                chromosome.setGene(i, newG1);
                chromosome.setGene(j, newG2);
            }
        }
    }
}
