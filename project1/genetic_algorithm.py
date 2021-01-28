import random
import math
from typing import Tuple


def generate_initial_pop(size, length):
    pop = []
    for _ in range(size):
        individual = ""
        for _ in range(length):
            individual += random.choice(["0", "1"])
        pop.append(individual)
    return tuple(pop)


def parent_selection(population, fitness_function, cutoff):
    return tuple(
        sorted(population, key=fitness_function, reverse=True)
        [: cutoff])


def crossover(parent1, parent2, length, mutation_chance):
    offspring1 = list(parent1[:length] + parent2[length:])
    offspring2 = list(parent2[:length] + parent1[length:])
    mutation_map = {
        "0": "1",
        "1": "0"
    }
    for i in range(len(offspring1)):
        mutation = random.random()
        if mutation > 1 - mutation_chance:
            offspring1[i] = mutation_map[offspring1[i]]
        if mutation < mutation_chance:
            offspring2[i] = mutation_map[offspring2[i]]
    return "".join(offspring1), "".join(offspring2)


def scale(x, length, interval: Tuple[int, int]):
    return (((interval[1] - interval[0]) * x) / (2 ** length)) + interval[0]


def fitness_sine(offspring, length, interval: Tuple[int, int]):
    return math.sin(scale(int(offspring, 2), length, interval))
