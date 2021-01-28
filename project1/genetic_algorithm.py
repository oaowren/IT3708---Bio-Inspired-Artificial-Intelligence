import random


def generate_initial_pop(size, length):
    pop = []
    for _ in range(size):
        individual = ""
        for _ in range(length):
            individual += random.choice(["0", "1"])
        pop.append(individual)
    return tuple(pop)


def selection(population, cutoff):
    return tuple(sorted(population, reverse=True)[:cutoff])


def crossover(parent1, parent2, length, mutation_chance):
    offspring1 = list(parent1[:length] + parent2[length:])
    offspring2 = list(parent2[:length] + parent1[length:])
    mutation_map = {
        "0": "1",
        "1": "0"
    }
    for i in range(len(offspring1)):
        mutation = random.random()
        if mutation > 1-mutation_chance:
            offspring1[i] = mutation_map[offspring1[i]]
        if mutation < mutation_chance:
            offspring2[i] = mutation_map[offspring2[i]]
    return "".join(offspring1), "".join(offspring2)
