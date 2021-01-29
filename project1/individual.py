import genetic_algorithm as GA


class Individual():

    def __init__(self, dna):
        self.dna = dna
        self.fitness = GA.fitness_sine(
            self.dna, len(self.dna),
            tuple([0, 128])) + 1
        self.age = 0

    def generation(self):
        self.age += 1

    def dna_value(self):
        return int(self.dna, 2)
