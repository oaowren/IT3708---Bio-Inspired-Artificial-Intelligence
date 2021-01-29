from genetic_algorithm import SimpleGenetic
import json
import random
import LinReg from LinReg


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
        print(ga.best_individuals_average)
        ga.run_generation()

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

    # Plot the generation
    GA.visualize_all_generations_sine(
        generation,
        generation_dict,
        parameters.interval)

    # Plot the average fitness level of the population for each generation
    generational_average_fitness = []
    for i in range(1, generation + 1):
        total_fitness = sum(
            map(lambda individual: individual.fitness, generation_dict[i]))
        generational_average_fitness.append(
            (total_fitness / len(generation_dict[i])) - 1)
    GA.visualize_generations(generational_average_fitness)
    print([x.fitness for x in generation_dict[1]])
    print("\n\n")
    print([x.fitness for x in ga.generation_dict[ga.generation]])
