from LinReg import LinReg
import numpy as np


x = None
y = None
lr = LinReg()

"""Process dataset and extract y values"""
with open('project1/resources/dataset.txt', 'r') as f:
    lines = f.readlines()
    x = [line.split(',') for line in lines]
x = np.asarray(x, dtype=np.float64)
y = x[:, [0]]
np.delete(x, 0, axis=1)


def get_fitness_no_feat_select():
    x_no_feat_select = lr.get_columns(x, ['1'] * 101)
    return lr.get_fitness(x_no_feat_select, y)


def get_fitness(bitstring):
    fitness = -lr.get_fitness(lr.get_columns(x, bitstring), y)
    print("bits:", bitstring)
    print("fitness:", fitness)
    return fitness
