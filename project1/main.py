from genetic_algorithm import SimpleGenetic
import json
from LinReg import LinReg
import random
import numpy as np


class Parameters:
    pass


if __name__ == "__main__":
    parameters = Parameters()
    with open('project1/parameters.json', 'r') as f:
        parameter_dict = json.load(f)
        for attr, value in parameter_dict.items():
            setattr(parameters, attr, value)

    ga = SimpleGenetic(parameters)
    current_min = 1000
    exit_threshold = 0.124 if parameters.fitness_function == 'dataset' else parameters.exit_threshold
    while (ga.best_individuals_average < exit_threshold if not parameters.fitness_function == 'dataset' else (-ga.best_individuals_average) > exit_threshold):
        new_min = round(-ga.best_individuals_average, 7)
        if new_min < current_min:
            print(new_min)
            current_min = new_min
        #print(list(map(lambda i: i.fitness, ga.population)))
        ga.run_generation()

    print([x.fitness for x in ga.generation_dict[ga.generation]])
    print("\n\n")
    print(
        [x.dna
         for x in sorted(
             ga.generation_dict[ga.generation],
             key=lambda i: i.fitness, reverse=True)][0])

    # Plot the generations
    ga.visualize_three_generations()

    # Plot the average fitness level of the population for each generation
    generational_average_fitness = []
    for i in range(1, ga.generation + 1):
        total_fitness = sum(
            map(lambda individual: individual.fitness, ga.generation_dict[i]))
        generational_average_fitness.append(
            (total_fitness / len(ga.generation_dict[i])) - 1)
    ga.visualize_generations(signum=-1)
