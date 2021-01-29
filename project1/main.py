import genetic_algorithm as GA
import json


class Parameters:
    pass


if __name__ == "__main__":
    parameters = Parameters()
    with open('project1/parameters.json', 'r') as f:
        parameter_dict = json.load(f)
        for attr, value in parameter_dict.items():
            setattr(parameters, attr, value)

    initial_population = GA.generate_initial_pop(
        parameters.init_pop_size, parameters.length)
    print(initial_population)
    parents = GA.parent_selection(initial_population, 1)
