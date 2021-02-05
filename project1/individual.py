import math


class Individual():

    def __init__(self, dna, dna_length, interval, parents=None, children=None):
        self.dna = dna
        self.age = 0
        self.dna_length = dna_length
        self.interval = interval
        self.fitness = self.fitness_sine() + 1
        self.parents = parents
        self.children = children

    def grow_older(self):
        self.age += 1

    def dna_value(self):
        return int(self.dna, 2)

    def scale(self):
        return ((((self.interval[1] - self.interval[0]) * self.dna_value())
                 / (2 ** self.dna_length)) + self.interval[0])

    def fitness_sine(self):
        return math.sin(self.scale())
