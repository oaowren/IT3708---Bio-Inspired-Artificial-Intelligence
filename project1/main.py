from genetic_algorithm import SimpleGenetic
import json


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

    print([x.fitness for x in ga.generation_dict[1]])
    print("\n\n")
    print([x.fitness for x in ga.generation_dict[ga.generation]])
