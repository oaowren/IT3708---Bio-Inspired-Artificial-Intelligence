import math


class Individual():

    def __init__(self, dna, dna_length, interval):
        self.dna = dna
        self.age = 0
        self.dna_length = dna_length
        self.interval = interval
        self.fitness = self.fitness_sine(
            self.dna) + 1

    def grow_older(self):
        self.age += 1

    def dna_value(self):
        return int(self.dna, 2)

    def scale(self, x):
        return ((((self.interval[1] - self.interval[0]) * x)
                 / (2 ** self.dna_length)) + self.interval[0])

    def fitness_sine(self, individual):
        return math.sin(self.scale(
            int(individual, 2)))
