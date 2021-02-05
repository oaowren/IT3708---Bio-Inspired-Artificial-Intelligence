from genetic_algorithm import SimpleGenetic
import json
from LinReg import LinReg
import random
import numpy as np
import matplotlib.pyplot as plt


class Parameters:
    pass


def plot_entropy(entropy_dict1, entropy_dict2):
    plt.title("Entropy per generation")
    plt.plot(list(entropy_dict1.keys()), list(entropy_dict1.values()), label="Simple Genetic")
    plt.plot(list(entropy_dict2.keys()), list(entropy_dict2.values()), label="Crowding")
    plt.legend()
    plt.show()


if __name__ == "__main__":
    parameters = Parameters()
    with open('project1/parameters.json', 'r') as f:
        parameter_dict = json.load(f)
        for attr, value in parameter_dict.items():
            setattr(parameters, attr, value)

    ga = SimpleGenetic(parameters)
    while ga.best_individuals_average < parameters.exit_threshold and ga.generation < parameters.max_generations:
        ga.run_generation()

    ga2 = SimpleGenetic(parameters, use_crowding=True)
    while ga2.best_individuals_average < parameters.exit_threshold and ga2.generation < parameters.max_generations:
        ga2.run_generation()

    plot_entropy(ga.get_entropy(), ga2.get_entropy())

    # Plot the generation
    ga2.visualize_three_generations_sine()

    # Plot the average fitness level of the population for each generation
    generational_average_fitness = []
    for i in range(1, ga.generation + 1):
        total_fitness = sum(
            map(lambda individual: individual.fitness, ga.generation_dict[i]))
        generational_average_fitness.append(
            (total_fitness / len(ga.generation_dict[i])) - 1)
    ga2.visualize_generations()

