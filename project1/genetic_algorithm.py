import random


def generate_initial_pop(size, length):
    pop = []
    for _ in range(size):
        individual = ""
        for _ in range(length):
            if random.random() >= 0.5:
                individual += "1"
            else:
                individual += "0"
        pop.append(individual)
    return pop


def selection(population, cutoff):
    return sorted(population, reverse=True)[:cutoff]


def crossover(parent1, parent2, length):
    offspring1 = parent1[:length] + parent2[length:]
    offspring2 = parent2[:length] + parent1[length:]
    for i in range(len(offspring1)):
        mutation = random.random()
        if mutation > 0.95:
            if offspring1[i] == "1":
                offspring1 = offspring1[:i] + "0" + offspring1[i + 1:]
            else:
                offspring1 = offspring1[:i] + "1" + offspring1[i + 1:]
        if mutation < 0.05:
            if offspring2[i] == "1":
                offspring2 = offspring2[:i] + "0" + offspring2[i + 1:]
            else:
                offspring2 = offspring2[:i] + "1" + offspring2[i + 1:]
    return offspring1, offspring2
