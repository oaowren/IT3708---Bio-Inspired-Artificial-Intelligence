class Main{
    public static void main(String[] args){
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
    }
}