import genetic_algorithm as GA


if __name__ == "__main__":
    initial_population = GA.generate_initial_pop(50, 10)
    parent_selection = GA.selection(initial_population, 2)
    o1, o2 = GA.crossover(parent_selection[0], parent_selection[1], 7)