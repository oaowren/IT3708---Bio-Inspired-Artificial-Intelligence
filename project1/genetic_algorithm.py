import random
import math
from typing import Dict, Tuple
from individual import Individual
import matplotlib.pyplot as plt
import numpy as np


class SimpleGenetic():

    def __init__(self, parameters):
        # Define parameters
        self.parent_cutoff = parameters.parent_selection_cutoff
        self.survivor_cutoff = parameters.survivor_selection_cutoff
        self.minimum_age_replacement = parameters.minimum_age_replacement
        self.children_count = parameters.number_of_children
        self.survivor_age = parameters.survivor_age_cutoff
        self.pop_size = parameters.population_size
        self.dna_length = parameters.dna_length
        self.crossover_length = parameters.crossover_length
        self.mutation_chance = parameters.mutation_chance
        self.interval = parameters.interval
        self.best_n_individuals = parameters.best_n_individuals
        self.survivor_func = (
            self.survivor_selection_age
            if parameters.survivor_func == "age" else
            self.survivor_selection_elitism)
        self.crowding_func = (
            self.crowding_deterministic
            if parameters.crowding_func == "det" else
            self.crowding_probabilistic
        )
        self.use_crowding = parameters.use_crowding

        # Initialize first generation and save in generational dictionary
        self.population = self.generate_initial_pop()
        self.generation_dict = dict()
        self.generation = 1
        self.generation_dict[self.generation] = self.population
        self.best_individuals_average = (sum([x.fitness for x in self.survivor_selection_elitism(
            self.population, self.best_n_individuals)]) / self.best_n_individuals)

    def generate_initial_pop(self):
        pop = []
        for _ in range(self.pop_size):
            dna = ""
            for _ in range(self.dna_length):
                dna += random.choice(["0", "1"])
            pop.append(Individual(dna, self.dna_length, self.interval))
        return tuple(pop)

    def parent_selection(self):
        parents = []
        fitness_sum = sum([x.fitness for x in self.population])
        roulette_wheel = [self.population[0].fitness / fitness_sum]
        for i in range(1, len(self.population)):
            roulette_wheel.append(
                self.population[i].fitness / fitness_sum +
                roulette_wheel[i - 1])
        for i in range(self.parent_cutoff):
            sel = random.random()
            for n in range(len(roulette_wheel)):
                if sel < roulette_wheel[n]:
                    parents.append(self.population[n])
                    break
        return tuple(parents)

    def survivor_selection_elitism(self, population, cutoff):
        return tuple(
            sorted(
                population, key=lambda individual: individual.fitness,
                reverse=True)[: cutoff])

    def survivor_selection_age(self, population):
        filtered_pop = tuple(filter(
            lambda individual: individual.age <= self.survivor_age,
            population))
        # Remove minimum n individuals, take the oldest if not enough individuals too old
        diff = len(population) - len(filtered_pop)
        if diff < self.minimum_age_replacement:
            return tuple(
                sorted(
                    filtered_pop, key=lambda individual: individual.age,
                    reverse=True))[
                self.minimum_age_replacement - diff:]
        return tuple(filtered_pop)

    def crowding_deterministic(self, parents, offspring):
        pop = []
        for parent in parents:
            child = self.get_closest_child(parent, offspring)
            if parent.fitness > child.fitness:
                pop.append(parent)
            else:
                pop.append(child)
        return pop

    def crowding_probabilistic(self, parents, offspring):
        return self.population

    def get_closest_child(self, parent, offspring):
        lowest_dist = 10000
        lowest_child = None
        for o in offspring:
            dist = 0
            for i in range(len(parent.dna)):
                dist += abs(int(parent.dna[i]) - int(o.dna[i]))
            if dist < lowest_dist:
                lowest_dist = dist
                lowest_child = o
        return lowest_child

    def crossover(self, parent1, parent2):
        offspring1 = list(
            parent1.dna[: self.crossover_length] + parent2.dna
            [self.crossover_length:])
        offspring2 = list(
            parent2.dna[: self.crossover_length] + parent1.dna
            [self.crossover_length:])
        mutation_map = {
            "0": "1",
            "1": "0"
        }
        for i in range(len(offspring1)):
            mutation = random.random()
            if mutation > 1 - self.mutation_chance:
                offspring1[i] = mutation_map[offspring1[i]]
            if mutation < self.mutation_chance:
                offspring2[i] = mutation_map[offspring2[i]]
        return Individual(
            "".join(offspring1),
            self.dna_length, self.interval), Individual(
            "".join(offspring2),
            self.dna_length, self.interval)

    def get_total_generation_fitness(self):
        return map(lambda individual: individual.fitness,
                   self.population).sum()

    @staticmethod
    def visualize_generations(generations: Tuple[int]):
        plt.plot(
            [i for i in range(1, len(generations) + 1)],
            generations, marker='o')
        plt.xlabel('Generation')
        plt.ylabel('Average population fitness')
        plt.show()

    def visualize_all_generations_sine(self):
        plt.figure()
        fig, axs = plt.subplots(
            nrows=math.ceil(self.generation / 3),
            ncols=3, sharex=True, sharey=True, squeeze=False)
        for i in range(0, self.generation):
            y = list(map(lambda individual: individual.fitness - 1,
                         self.generation_dict[i + 1]))
            x = list(map(lambda individual: individual.scale(),
                         self.generation_dict[i + 1]))
            lin = np.linspace(0, 128, num=1024)
            axs[i // 3][i % 3].plot(lin, np.sin(lin), color="y")
            axs[i // 3][i % 3].scatter(x, y)
            axs[i // 3][i % 3].set_title('Gen: ' + str(i + 1))

        for ax in axs.flat:
            ax.set(xlabel='Value', ylabel='Sine')

        plt.show()

    def run_generation(self):
        self.generation += 1
        for individual in self.population:
            individual.grow_older()
        parents = self.parent_selection()
        new_pop = []

        # Generate new population based on random pairs of parents
        for i in range(self.children_count):
            par1, par2 = random.choice(parents), random.choice(parents)
            off1, off2 = self.crossover(par1, par2)
            new_pop.append(off1)
            new_pop.append(off2)

        if self.survivor_func == self.survivor_selection_elitism and not self.use_crowding:
            # Select survivors based on elitism
            self.population = self.survivor_func(
                self.population + tuple(new_pop), self.pop_size)
        elif not self.use_crowding:
            # Remove oldest individuals, fill with fittest from new genereation
            old_pop = self.survivor_func(self.population)
            diff = len(self.population) - len(old_pop)
            self.population = (old_pop
                               + self.survivor_selection_elitism(new_pop, diff))
        else:
            self.population = self.crowding_func(self.population, new_pop)
        # Save generation for plots
        self.generation_dict[self.generation] = self.population
        # Calculate new best average
        self.best_individuals_average = (sum([x.fitness for x in self.survivor_selection_elitism(
            self.population, self.best_n_individuals)]) / self.best_n_individuals)
