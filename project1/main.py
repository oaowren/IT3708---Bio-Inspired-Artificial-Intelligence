from genetic_algorithm import SimpleGenetic
import json
from LinReg import LinReg
import random
import numpy as np
import matplotlib.pyplot as plt
import fitness


class Parameters:
    pass


def plot_entropy(entropy_dict1, entropy_dict2):
    plt.title("Entropy per generation")
    plt.plot(
        list(entropy_dict1.keys()),
        list(entropy_dict1.values()),
        label="Simple Genetic")
    plt.plot(
        list(entropy_dict2.keys()),
        list(entropy_dict2.values()),
        label="Crowding")
    plt.legend()
    plt.show()


if __name__ == "__main__":
    parameters = Parameters()
    with open('project1/parameters.json', 'r') as f:
        parameter_dict = json.load(f)
        for attr, value in parameter_dict.items():
            setattr(parameters, attr, value)

    print("Fitness no feature selection: ",
          fitness.get_fitness_no_feat_select())
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

    # ga2 = SimpleGenetic(parameters, use_crowding=True)
    # while ga2.best_individuals_average < parameters.exit_threshold and ga2.generation < parameters.max_generations:
    #     ga2.run_generation()
    # plot_entropy(ga.get_entropy(), ga2.get_entropy())

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
