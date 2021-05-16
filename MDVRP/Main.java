class Main{
    public static void main(String[] args){
        long startTime = System.nanoTime();
        DataSetIO dataSet = new DataSetIO();
        dataSet.readDataFile("MDVRP/dataset/"+Parameters.problem);
        GeneticAlgorithm ga = new GeneticAlgorithm(dataSet);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Population p = ga.getPopulation();
                DataSetIO.writeResults(p.getIndividualByRankAndDeviation(0, true), "MDVRP/results/"+Parameters.problem);
                DataSetIO.writeGenerations(ga.getGenerationalFitness(), "MDVRP/generations/"+Parameters.problem);
                long endTime = System.nanoTime();
                System.out.println(String.format("%s completed in %.2f seconds", Parameters.problem, (endTime - startTime) / Math.pow(10,9)));
            }
        });
        ga.run();
        System.exit(0);
    }
}