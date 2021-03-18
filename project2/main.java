class Main{
    public static void main(String[] args){
        long startTime = System.nanoTime();
        DataSetIo dataSet = new DataSetIo();
        dataSet.readDataFile("project2/Data Files/"+Parameters.problem);
        GeneticAlgorithm ga = new GeneticAlgorithm(dataSet);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                DataSetIo.writeResults(ga.getPopulation().getIndividualByRank(0), "project2/Results/"+Parameters.problem);
                DataSetIo.writeGenerations(ga.getGenerationalFitness(), "project2/Generations/"+Parameters.problem);
            }
        });
        ga.run();
        DataSetIo.writeResults(ga.getPopulation().getIndividualByRank(0), "project2/Results/"+Parameters.problem);
        DataSetIo.writeGenerations(ga.getGenerationalFitness(), "project2/Generations/"+Parameters.problem);
        long endTime = System.nanoTime();
        System.out.println(String.format("%s completed in %.2f seconds", Parameters.problem, (endTime - startTime) / Math.pow(10,9)));
        System.exit(0);
    }
}