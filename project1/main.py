from genetic_algorithm import SimpleGenetic
import json
import random
from LinReg import LinReg


class Parameters:
    pass


if __name__ == "__main__":
    parameters = Parameters()
    with open('project1/parameters.json', 'r') as f:
        parameter_dict = json.load(f)
        for attr, value in parameter_dict.items():
            setattr(parameters, attr, value)

    ga = SimpleGenetic(parameters)

    while ga.best_individuals_average < parameters.exit_threshold:
        print(ga.generation)
        ga.run_generation()

    # Plot the generation
    ga.visualize_all_generations_sine()

    # Plot the average fitness level of the population for each generation
    generational_average_fitness = []
    for i in range(1, ga.generation + 1):
        total_fitness = sum(
            map(lambda individual: individual.fitness, ga.generation_dict[i]))
        generational_average_fitness.append(
            (total_fitness / len(ga.generation_dict[i])) - 1)
    SimpleGenetic.visualize_generations(ga.generational_average_fitness)
    print([x.fitness for x in ga.generation_dict[1]])
    print("\n\n")
    print([x.fitness for x in ga.generation_dict[ga.generation]])
