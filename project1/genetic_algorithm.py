import random
import math
from typing import Tuple
from individual import Individual
import matplotlib.pyplot as plt
import numpy as np


class SimpleGenetic():

    def __init__(self, parameters, use_crowding=False):
        # Define parameters
        self.parent_cutoff = parameters.parent_selection_cutoff
        self.minimum_age_replacement = parameters.minimum_age_replacement
        self.survivor_age = parameters.survivor_age_cutoff
        self.pop_size = parameters.population_size
        self.dna_length = parameters.dna_length
        self.mutation_chance = parameters.mutation_chance
        self.crossover_chance = parameters.crossover_chance
        self.interval = parameters.interval
        self.best_n_individuals: int = parameters.best_n_individuals
        self.survivor_func = (
            self.survivor_selection_age
            if parameters.survivor_func == "age" else
            self.survivor_selection_elitism)
        self.crowding_func = (
            self.crowding_deterministic
            if parameters.crowding_func == "det" else
            self.crowding_probabilistic
        )
        self.use_crowding = use_crowding
        self.fitness_function = parameters.fitness_function

        # Initialize first generation and save in generational dictionary
        self.population = self.generate_initial_pop()
        self.generation_dict = dict()
        self.generation = 1
        self.generation_dict[self.generation] = self.population
        self.best_individuals_average = self.find_best_individuals_average()

    def find_best_individuals_average(self):
        return (sum([x.fitness for x in self.survivor_selection_elitism(self.population, self.best_n_individuals)]) / self.best_n_individuals)

    def generate_initial_pop(self):
        pop = []
        for _ in range(self.pop_size):
            dna = ""
            for _ in range(self.dna_length):
                dna += random.choice(["0", "1"])
            pop.append(
                Individual(
                    dna, self.dna_length, self.interval,
                    self.fitness_function))
        return tuple(pop)

    def parent_selection(self):
        population_fitness = [x.fitness for x in self.population]
        min_fitness = 0
        if self.fitness_function == "dataset":
            min_fitness = -min(population_fitness)
            for i in range(len(population_fitness)):
                population_fitness[i] += min_fitness

        parents = []
        fitness_sum = sum(population_fitness)
        roulette_wheel = [
            (self.population[0].fitness + min_fitness) / fitness_sum]
        for i in range(1, len(self.population)):
            roulette_wheel.append(
                (self.population[i].fitness + min_fitness) / fitness_sum +
                roulette_wheel[i - 1])
        for i in range(self.parent_cutoff):
            sel = random.random()
            for n in range(len(roulette_wheel)):
                if sel < roulette_wheel[n]:
                    parents.append(self.population[n])
                    break
        return tuple(parents)

    def crossover(self, parent1, parent2):
        crossover_length = random.randint(0, self.dna_length)
        offspring1 = list(
            parent1.dna[: crossover_length] + parent2.dna
            [crossover_length:])
        offspring2 = list(
            parent2.dna[: crossover_length] + parent1.dna
            [crossover_length:])
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
        child1, child2 = Individual(
            "".join(offspring1),
            self.dna_length, self.interval, self.fitness_function,
            parents=[parent1, parent2]), Individual(
            "".join(offspring2),
            self.dna_length, self.interval, self.fitness_function,
            parents=[parent1, parent2])
        parent1.children = [child1, child2]
        parent2.children = [child1, child2]
        return child1, child2

    def survivor_selection_elitism(self, population, cutoff):
        return tuple(
            sorted(
                population, key=lambda individual: individual.fitness,
                reverse=True)[: cutoff])

    def survivor_selection_age(self, population):
        filtered_pop = tuple(filter(
            lambda individual: individual.age <= self.survivor_age,
            population))
        # Remove minimum n individuals, take the oldest if not enough
        diff = len(population) - len(filtered_pop)
        if diff < self.minimum_age_replacement:
            return tuple(
                sorted(
                    filtered_pop, key=lambda individual: individual.age,
                    reverse=True))[
                self.minimum_age_replacement - diff:]
        return tuple(filtered_pop)

    def crowding(self, population, parents, crowding_func):
        pop = population
        visited_parents = []
        for parent in parents:
            if parent.children is not None:
                visited_parents.append(parent)
                children = parent.children
                children_parents = children[0].parents
                if children_parents is not None:
                    if children_parents[0] not in visited_parents or children_parents[1] not in visited_parents:
                        pop = [x for x in pop if x not in children_parents]
                        if (self.distance_func(children[0], children_parents[0]) + self.distance_func(children[1], children_parents[1]) <
                                self.distance_func(children[1], children_parents[0]) + self.distance_func(children[0], children_parents[1])):
                            pop.append(
                                crowding_func(
                                    children[0],
                                    children_parents[0]))
                            pop.append(
                                crowding_func(
                                    children[1],
                                    children_parents[1]))
                        else:
                            pop.append(
                                crowding_func(
                                    children[1],
                                    children_parents[0]))
                            pop.append(
                                crowding_func(
                                    children[0],
                                    children_parents[1]))
        return tuple(pop)

    def crowding_probabilistic(self, parent, child):
        prob = child.fitness / (parent.fitness + child.fitness)
        if random.random() < prob:
            return child
        return parent

    def crowding_deterministic(self, parent, child):
        if parent.fitness > child.fitness:
            return parent
        elif parent.fitness == child.fitness:
            return random.choice([parent, child])
        return child

    def distance_func(self, i1, i2):
        dist = 0
        for i in range(len(i1.dna)):
            dist += abs(int(i1.dna[i]) - int(i2.dna[i]))
        return dist

    def get_total_generation_fitness(self):
        return map(lambda individual: individual.fitness,
                   self.population).sum()

    def visualize_generations(self, signum=1):
        generational_average = []
        for i in range(1, self.generation + 1):
            generation = sorted(
                self.generation_dict[i],
                key=lambda i: i.fitness, reverse=True)
            generation = generation[:self.best_n_individuals]
            generational_average.append(
                sum(map(lambda individual: individual.fitness * signum, generation)) /
                len(generation))
        plt.plot(
            [i for i in range(1, len(generational_average) + 1)],
            generational_average, marker='o')
        plt.xlabel('Generation')
        plt.ylabel('Average best n individuals fitness')
        plt.show()

    def visualize_all_generations(self, sine=False):
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
            if sine:
                axs[i // 3][i % 3].plot(lin, np.sin(lin), color="y")
            axs[i // 3][i % 3].scatter(x, y)
            axs[i // 3][i % 3].set_title('Gen: ' + str(i + 1))

        for ax in axs.flat:
            ax.set(xlabel='Value', ylabel='Sine' if sine else 'Fitness')

        plt.show()

    def visualize_three_generations(self, sine=False):
        plt.figure()
        fig, axs = plt.subplots(
            nrows=1,
            ncols=3, sharex=True, sharey=True, squeeze=False)
        plots = [
            0, math.floor(math.sqrt(self.generation)),
            self.generation - 1]
        for i in range(len(plots)):
            y = list(map(lambda individual: individual.fitness - 1,
                         self.generation_dict[plots[i] + 1]))
            x = list(map(lambda individual: individual.scale(),
                         self.generation_dict[plots[i] + 1]))
            lin = np.linspace(0, 128, num=1024)
            if sine:
                axs[0][i].plot(lin, np.sin(lin), color="y")
            axs[0][i].scatter(x, y)
            axs[0][i].set_title('Gen: ' + str(plots[i] + 1))

        for ax in axs.flat:
            ax.set(xlabel='Value', ylabel='Sine' if sine else 'Fitness')

        plt.show()

    def get_entropy(self):
        entropy_dict = {}
        for n in range(self.generation):
            entropy = 0
            for i in range(self.dna_length):
                p_i = len([x for x in self.generation_dict[n+1] if x.dna[i] == "1"]) / len(self.generation_dict[n+1])
                if p_i == 0:
                    entropy = 0
                    break
                entropy -= p_i * math.log2(p_i)
            entropy_dict[n+1] = entropy
        return entropy_dict

    def run_generation(self):
        self.generation += 1
        for individual in self.population:
            individual.grow_older()
            individual.children = None
        parents = self.parent_selection()
        new_pop = []

        # Generate new population based on pairs of parents
        for i in range(len(parents)):
            for j in range(len(parents)):
                if parents[i].children is None and parents[j].children is None:
                    crossover = random.random()
                    if crossover < self.crossover_chance:
                        off1, off2 = self.crossover(parents[i], parents[j])
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
            # Add the best individuals to replace old ones
            self.population = (
                old_pop + self.survivor_selection_elitism(new_pop, diff))
        else:
            # Use crowding scheme defined by parameters
            self.population = self.crowding(
                self.population, parents, self.crowding_func)
        # Save generation for plots
        self.generation_dict[self.generation] = self.population
        # Calculate new best average
        self.best_individuals_average = self.find_best_individuals_average()
