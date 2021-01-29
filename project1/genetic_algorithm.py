import random
import math
from typing import Tuple
from individual import Individual
import matplotlib.pyplot as plt
import numpy as np


def generate_initial_pop(size, length):
    pop = []
    for _ in range(size):
        dna = ""
        for _ in range(length):
            dna += random.choice(["0", "1"])
        pop.append(Individual(dna))
    return tuple(pop)


def parent_selection(population, count):
    parents = []
    fitness_sum = sum([x.fitness for x in population])
    roulette_wheel = [population[0].fitness/fitness_sum]
    for i in range(1, len(population)):
        roulette_wheel.append(population[i].fitness/fitness_sum + roulette_wheel[i-1])
    for i in range(count):
        sel = random.random()
        for n in range(len(roulette_wheel)):
            if sel < roulette_wheel[n]:
                parents.append(population[n])
                break
    return parents


def survivor_selection_elitism(population, cutoff):
    return tuple(
        sorted(
            population, key=lambda individual: individual.fitness,
            reverse=True)[: cutoff])


def survivor_selection_age(population, age_cutoff):
    return tuple(
        filter(
            lambda individual: individual.age < age_cutoff,
            population))


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


def get_total_generation_fitness(population):
    return map(lambda individual: individual.fitness, population).sum()


def visualize_generations(generations: Tuple[int], population_size):
    plt.plot([i for i in range(len(generations))], generations)
    plt.xlabel('Generation')
    plt.ylabel('Total fitness')
    plt.show()
