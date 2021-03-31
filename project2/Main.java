class Main{
    public static void main(String[] args){
        long startTime = System.nanoTime();
        DataSetIo dataSet = new DataSetIo();
        dataSet.readDataFile("project2/Data Files/"+Parameters.problem);
        GeneticAlgorithm ga = new GeneticAlgorithm(dataSet);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Population p = ga.getPopulation();
                DataSetIo.writeResults(p.getIndividualByRankAndDeviation(0, true), "project2/Results/"+Parameters.problem);
                DataSetIo.writeGenerations(ga.getGenerationalFitness(), "project2/Generations/"+Parameters.problem);
                long endTime = System.nanoTime();
                System.out.println(String.format("%s completed in %.2f seconds", Parameters.problem, (endTime - startTime) / Math.pow(10,9)));
            }
        });
        ga.run();
        // Population p = ga.getPopulation();
        // DataSetIo.writeResults(p.getIndividualByRankAndDeviation(0, true), "project2/Results/"+Parameters.problem);
        // DataSetIo.writeGenerations(ga.getGenerationalFitness(), "project2/Generations/"+Parameters.problem);
        // long endTime = System.nanoTime();
        // System.out.println(String.format("%s completed in %.2f seconds", Parameters.problem, (endTime - startTime) / Math.pow(10,9)));
        System.exit(0);
    }
}