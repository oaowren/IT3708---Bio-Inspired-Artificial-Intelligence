import genetic_algorithm as GA
import json


class Parameters:
    default: None


if __name__ == "__main__":
    parameters = Parameters()
    with open('project1/parameters.json', 'r') as f:
        parameter_dict = json.load(f)
        for attr, value in parameter_dict.items():
            setattr(parameters, attr, value)

    initial_population = GA.generate_initial_pop(
        parameters.init_pop_size, parameters.length)
    parent_selection = GA.selection(
        initial_population, parameters.selection_cutoff)
    o1, o2 = GA.crossover(
        parent_selection[0],
        parent_selection[1],
        parameters.crossover_length, parameters.mutation_chance)
