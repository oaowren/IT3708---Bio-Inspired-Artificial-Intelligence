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
    print(parameters.testParam)

    initial_population = GA.generate_initial_pop(50, 10)
    parent_selection = GA.selection(initial_population, 2)
    o1, o2 = GA.crossover(parent_selection[0], parent_selection[1], 7)
