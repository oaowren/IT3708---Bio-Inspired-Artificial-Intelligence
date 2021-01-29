import genetic_algorithm as GA
import json
import random


class Parameters:
    pass


if __name__ == "__main__":
    parameters = Parameters()
    with open('project1/parameters.json', 'r') as f:
        parameter_dict = json.load(f)
        for attr, value in parameter_dict.items():
            setattr(parameters, attr, value)

    # Initial population
    population = GA.generate_initial_pop(
        parameters.population_size, parameters.dna_length)
    generation = 1
    # Save population for each generation to use in plots
    generation_dict = dict()
    generation_dict[generation] = population
    # Select n best individuals, used for exit condition
    best_individuals = GA.survivor_selection_elitism(
        population, parameters.best_n_individuals)
    while sum([x.fitness for x in best_individuals]) / parameters.best_n_individuals < parameters.exit_threshold:
        generation += 1
        print(generation)

        # Select parents based on Roulette Wheel
        parents = GA.parent_selection(
            population, parameters.parent_selection_cutoff)
        new_pop = []

        # Generate new population based on random pairs of parents
        for i in range(parameters.number_of_children):
            par1, par2 = random.choice(parents), random.choice(parents)
            off1, off2 = GA.crossover(
                par1, par2, parameters.crossover_length, parameters.mutation_chance)
            new_pop.append(off1)
            new_pop.append(off2)

        # Select survivors based on elitism
        population = GA.survivor_selection_elitism(
            population + tuple(new_pop), parameters.population_size)
        generation_dict[generation] = population
        best_individuals = GA.survivor_selection_elitism(
            population, parameters.best_n_individuals)

    print([x.fitness for x in generation_dict[1]])
    print("\n\n")
    print([x.fitness for x in generation_dict[generation]])
