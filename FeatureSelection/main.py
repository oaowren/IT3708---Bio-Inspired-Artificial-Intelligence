from genetic_algorithm import SimpleGenetic
import json
from LinReg import LinReg
import random
import numpy as np
import matplotlib.pyplot as plt
import fitness


class Parameters:
    pass


# Plotting to visualize entropy between generations
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
    with open('FeatureSelection/parameters.json', 'r') as f:
        parameter_dict = json.load(f)
        for attr, value in parameter_dict.items():
            setattr(parameters, attr, value)

    fitness_func = parameters.fitness_function

    print("Fitness value with no feature selection applied: ",
          fitness.get_fitness_no_feat_select())

    # Initialize genetic algorithm without crowding
    ga = SimpleGenetic(parameters)
    exit_threshold = 0.124 if fitness_func == 'dataset' else parameters.exit_threshold
    while ((ga.best_individuals_average < exit_threshold and ga.generation < parameters.max_generations and not fitness_func == 'dataset')
            or (-ga.best_individuals_average > exit_threshold)):
        ga.run_generation()

    # Initialize genetic algorithm with crowding
    ga2 = SimpleGenetic(parameters, use_crowding=True)
    exit_threshold = 0.124 if fitness_func == 'dataset' else parameters.exit_threshold
    while ((ga2.best_individuals_average < exit_threshold and ga2.generation < parameters.max_generations and not fitness_func == 'dataset')
            or (-ga2.best_individuals_average > exit_threshold)):
        ga2.run_generation()

    # Begin: visualize all generations
    ga.visualize_three_generations(
        sine=(fitness_func != "dataset"),
        title="Simple Genetic Algorithm")
    ga2.visualize_three_generations(
        sine=(fitness_func != "dataset"),
        title="Crowding approach")

    plot_entropy(ga.get_entropy(), ga2.get_entropy())

    # Vizualise average fitness per generation
    ga.visualize_generations(
        sine=False if fitness_func == "dataset" else True,
        title="SGA, average of best " + str(parameters.best_n_individuals) +
        " individuals per generation")
    ga2.visualize_generations(
        sine=False if fitness_func == "dataset" else True,
        title="Crowding, average of best " +
        str(parameters.best_n_individuals) + " individuals per generation")
