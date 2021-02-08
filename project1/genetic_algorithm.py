import random
import math
from typing import Tuple
from individual import Individual
import matplotlib.pyplot as plt
import numpy as np
import fitness


class SimpleGenetic():

    def __init__(self, parameters, use_crowding=False):
        # Number of parents selected
        self.parent_cutoff = parameters.parent_selection_cutoff
        # Minimum number of population replaced with age-function
        self.minimum_age_replacement = parameters.minimum_age_replacement
        # Maximum age of an individual with age-function
        self.survivor_age = parameters.survivor_age_cutoff
        # Initial population size, goal to maintain the same size throughout
        self.pop_size = parameters.population_size
        # Number of bits in genome
        self.dna_length = parameters.dna_length

        self.mutation_rate = parameters.mutation_rate
        self.crossover_rate = parameters.crossover_rate
        # Interval for sine-function
        self.interval = [0, 128]
        # Number of individuals used to calculate average
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

    # Helper function
    def find_best_individuals_average(self):
        return (sum([x.fitness for x in self.survivor_selection_elitism(self.population, self.best_n_individuals)]) / self.best_n_individuals)

    # Oppgave a)
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

    # Oppgave b)
    def parent_selection(self):
        population_fitness = [x.fitness for x in self.population]
        min_fitness = 0
        if self.fitness_function == "dataset":
            # Negative fitness values are offset to use in probability-calculation
            min_fitness = -min(population_fitness)
            for i in range(len(population_fitness)):
                population_fitness[i] += min_fitness
        # Roulette wheel-selection
        parents = []
        fitness_sum = sum(population_fitness)
        # Create cumulative probability distribution, 0-1
        roulette_wheel = [
            (self.population[0].fitness + min_fitness) / fitness_sum]
        for i in range(1, len(self.population)):
            roulette_wheel.append(
                (self.population[i].fitness + min_fitness) / fitness_sum +
                roulette_wheel[i - 1])
        # Select n parents based on distribution
        for i in range(self.parent_cutoff):
            sel = random.random()
            for n in range(len(roulette_wheel)):
                if sel < roulette_wheel[n]:
                    parents.append(self.population[n])
                    break
        return tuple(parents)

    # Oppgave c)
    def crossover(self, parent1, parent2):
        # Create a crossover between parents based on random length
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
        # Random mutations on genome
        for i in range(len(offspring1)):
            mutation = random.random()
            # Avoid mutation of the same bit on both offspring
            if mutation > 1 - self.mutation_rate:
                offspring1[i] = mutation_map[offspring1[i]]
            if mutation < self.mutation_rate:
                offspring2[i] = mutation_map[offspring2[i]]
        # Create new individuals with genome, assign parents and children accordingly
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

    # Oppgave d.1)
    def survivor_selection_elitism(self, population, cutoff):
        return tuple(
            sorted(
                population, key=lambda individual: individual.fitness,
                reverse=True)[: cutoff])

    # Oppgave d.2)
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

    # Oppgave g)
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

    # Oppgave g.1)
    def crowding_probabilistic(self, parent, child):
        prob = child.fitness / (parent.fitness + child.fitness)
        if random.random() < prob:
            return child
        return parent

    # Oppgave g.2)
    def crowding_deterministic(self, parent, child):
        if parent.fitness > child.fitness:
            return parent
        elif parent.fitness == child.fitness:
            return random.choice([parent, child])
        return child

    # Hamming distance
    def distance_func(self, i1, i2):
        dist = 0
        for i in range(len(i1.dna)):
            dist += abs(int(i1.dna[i]) - int(i2.dna[i]))
        return dist

    # Oppgave e)
    def run_generation(self):
        # Initialize generation
        self.generation += 1
        for individual in self.population:
            individual.grow_older()
            # Makes it possible to have new children each generation
            individual.children = None

        if not self.use_crowding:
            parents = self.parent_selection()
            new_pop = []
            # Generate new population based on pairs of parents
            for i in range(len(parents)):
                for j in range(len(parents)):
                    crossover = random.random()
                    if crossover < self.crossover_rate:
                        off1, off2 = self.crossover(parents[i], parents[j])
                        new_pop.append(off1)
                        new_pop.append(off2)
            if self.survivor_func == self.survivor_selection_elitism and not self.use_crowding:
                # Select survivors based on elitism
                self.population = self.survivor_func(
                    self.population + tuple(new_pop), self.pop_size)
            else:
                # Remove oldest individuals, fill with fittest from new genereation
                old_pop = self.survivor_func(self.population)
                # Find number of individuals removed
                diff = len(self.population) - len(old_pop)
                # Add the best individuals to replace old ones
                self.population = (
                    old_pop + self.survivor_selection_elitism(new_pop, diff))
        else:
            # Generate new population based on pairs of parents
            for i in range(len(self.population)):
                for j in range(len(self.population)):
                    # Beacuse of implementation of crowding, both parents must not have children
                    if self.population[i].children is None and self.population[j].children is None:
                        crossover = random.random()
                        if crossover < self.crossover_rate:
                            off1, off2 = self.crossover(
                                self.population[i],
                                self.population[j])
            # Use crowding scheme defined by parameters
            self.population = self.crowding(
                self.population, self.population, self.crowding_func)
        # Save generation for plots
        self.generation_dict[self.generation] = self.population
        # Calculate new best average
        self.best_individuals_average = self.find_best_individuals_average()

    # Plotting functions
    def get_total_generation_fitness(self):
        return map(lambda individual: individual.fitness,
                   self.population).sum()

    def visualize_generations(self, sine=False, title="Default"):
        generational_average = []
        for i in range(1, self.generation + 1):
            generation = sorted(
                self.generation_dict[i],
                key=lambda i: i.fitness, reverse=True)
            generation = generation[:self.best_n_individuals]
            generational_average.append(
                sum(map(lambda individual: individual.fitness * -1 if not sine else individual.fitness - 1, generation)) /
                len(generation))
        plt.title(title)
        plt.plot(
            [i for i in range(1, len(generational_average) + 1)],
            generational_average, marker='o')
        if not sine:
            plt.axhline(y=fitness.get_fitness_no_feat_select(), color='r', linestyle='-')
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

    def visualize_three_generations(self, sine=False, title="Default"):
        fig, axs = plt.subplots(
            nrows=1,
            ncols=3, sharex=True, sharey=True, squeeze=False)
        plots = [
            0, math.floor(math.sqrt(self.generation)),
            self.generation - 1]
        for i in range(len(plots)):
            y = list(
                map(
                    lambda individual: individual.fitness - 1
                    if sine else -individual.fitness, self.generation_dict
                    [plots[i] + 1]))
            x = list(map(lambda individual: individual.scale(),
                         self.generation_dict[plots[i] + 1]))
            lin = np.linspace(0, 128, num=1024)
            if sine:
                axs[0][i].plot(lin, np.sin(lin), color="y")
            axs[0][i].scatter(x, y)
            axs[0][i].set_title('Gen: ' + str(plots[i] + 1))

        for ax in axs.flat:
            ax.set(xlabel='Value', ylabel='Sine' if sine else 'Fitness')
        fig.suptitle(title)
        plt.show()

    def get_entropy(self):
        entropy_dict = {}
        for n in range(self.generation):
            entropy = 0
            for i in range(self.dna_length):
                p_i = len([x for x in self.generation_dict[n + 1]
                           if x.dna[i] == "1"]) / len(self.generation_dict[n + 1])
                if p_i != 0:
                    entropy -= p_i * math.log2(p_i)
            entropy_dict[n + 1] = entropy
        return entropy_dict
