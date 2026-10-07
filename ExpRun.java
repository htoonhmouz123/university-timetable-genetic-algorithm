import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpRun {

    public static void runAllexp(Dataset dataset, String name)throws IOException{
        List<String> resultlines =  new ArrayList<>();
        resultlines.add("Experiment,Configuration,Generations,Fitness,Hard,Soft,RunTime");

        //exp1 population
        int[] populationSizes = {20,50,100,200};
        for(int i=0; i<populationSizes.length; i++) {
            int populationSize = populationSizes[i];
            resultlines.add(runSingleExp(dataset, "Population Size", "Population:" + populationSize, populationSize, 0.05, 0.80, 500, false, false, false));
        }

        //exp2 mutation
        double[] mutationRates = {0.01,0.05,0.10,0.20};
        for(int i=0; i<mutationRates.length; i++) {
            double mutationRate = mutationRates[i];
            resultlines.add(runSingleExp(dataset,"Mutation Rate", "Mutation:"+(mutationRate*100)+"%",100, mutationRate, 0.80, 500, false,false,false));
        }

        //exp3 Crossover
        double[] crossoverRates = {0.60,0.80,0.90};
        for(int i=0; i<crossoverRates.length; i++) {
            double crossoverRate = crossoverRates[i];
            resultlines.add(runSingleExp(dataset,"Crossover Rate", "Crossover:"+(crossoverRate*100)+"%",100, 0.05, crossoverRate, 500, false,false,false));
        }

        //compare selection
        resultlines.add(runSingleExp(dataset,"Selection Method", "Tournament",100, 0.05, 0.80, 500, false,false,false));
        resultlines.add(runSingleExp(dataset,"Selection Method", "Roulette Wheel",100, 0.05, 0.80, 500, true,false,false));

        //compare crossover
        resultlines.add(runSingleExp(dataset,"Crossover Method", "ONE-point",100, 0.05, 0.80, 500, false,true,false));
        resultlines.add(runSingleExp(dataset,"Crossover Method", "TWO-point",100, 0.05, 0.80, 500, false,false,false));

        //compare mutation
        resultlines.add(runSingleExp(dataset,"Mutation Method", "Random",100, 0.05, 0.80, 500, false,false,false));
        resultlines.add(runSingleExp(dataset,"Mutation Method", "Swap",100, 0.05, 0.80, 500, false,false,true));

        //GA&HC
        resultlines.add(runGA_Hill(dataset));

        try(FileWriter writer = new FileWriter(name)) {
            for (int i = 0; i < resultlines.size(); i++) {
                writer.write(resultlines.get(i) + "\n");
            }
        }
    }

    // to see how size affect runtime/fitness/violations
    public static void runDatasetExp(String [] dsFiles, String outputFile) throws IOException{
        List<String> lines = new ArrayList<>();
        lines.add("File,Courses,Generations,Fitness,Hard,Soft,RunTime");

        for(int i=0; i<dsFiles.length; i++) {
            String filename = dsFiles[i];
            Dataset dataset = DataLoader.loadFile(filename);

            long start = System.currentTimeMillis();
            GA ga = new GA(dataset.getCourses(), dataset.getTimeslots(),dataset.getRooms(), 100,500,0.80,0.05, Math.max(1, 100/20),42);
            ga.setAcceptableSoftViol(0);
            Chromosome best = ga.run();
            FitnessResult result = ga.evaluate(best);
            long end = System.currentTimeMillis();

            int generationRun = ga.getBestFitnessHistory().size();
            lines.add(filename +","+dataset.getCourses().size()+","+ga.getBestFitnessHistory().size()+","+result.getFitness()+","+result.getHardViolations()+","+result.getSoftViolations()+","+(end-start));
        }
        try(FileWriter writer = new FileWriter(outputFile)){
            for(int i=0; i<lines.size(); i++) {
                writer.write(lines.get(i) + "\n");
            }
        }
    }

    private static String runSingleExp(Dataset dataset, String expName, String config,int populationSize, double mutationRate, double crossoverRate, int genertions, boolean useRoulette, boolean use1point, boolean useSwap){
        long start = System.currentTimeMillis();
        GA ga = new GA(dataset.getCourses(), dataset.getTimeslots(), dataset.getRooms(),populationSize, genertions, crossoverRate, mutationRate, Math.max(1, populationSize/20),42, useRoulette, use1point, useSwap);
        ga.setAcceptableSoftViol(0);
        Chromosome best = ga.run();
        FitnessResult result = ga.evaluate(best);
        long end = System.currentTimeMillis();
        return expName+","+config+","+ga.getBestFitnessHistory().size()+","+result.getFitness()+"," +result.getHardViolations()+","+result.getSoftViolations()+"," +(end-start);
    }

    public static String runGA_Hill(Dataset dataset){
        long startGA = System.currentTimeMillis();
        GA ga = new GA(dataset.getCourses(), dataset.getTimeslots(), dataset.getRooms(), 100,500,0.80,0.05,5,42);
        ga.setAcceptableSoftViol(0);
        Chromosome bestGA = ga.run();
        FitnessResult resultGA = ga.evaluate(bestGA);
        long endGA = System.currentTimeMillis();
        String line1 = "GA VS HC, GA," +ga.getBestFitnessHistory().size() +","+ resultGA.getFitness() +","+ resultGA.getHardViolations()+","+resultGA.getSoftViolations()+"," +(endGA-startGA);

        long startHC = System.currentTimeMillis();
        HillClimbing hc = new HillClimbing(dataset.getCourses(), dataset.getTimeslots(), dataset.getRooms(), 2500,42);
        Chromosome hcBest = hc.run();
        FitnessResult resultHC = hc.evaluate(hcBest);
        long endHC = System.currentTimeMillis();
        String line2 = "GA VS HC, HC," +hc.getFitnessHistory().size() +","+ resultHC.getFitness() +","+ resultHC.getHardViolations()+","+resultHC.getSoftViolations()+"," +(endHC-startHC);

        return line1+"\n"+line2;
    }

}
